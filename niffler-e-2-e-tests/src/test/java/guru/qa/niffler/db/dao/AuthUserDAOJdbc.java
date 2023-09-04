package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.UserDataEntity;
import guru.qa.niffler.db.model.UserEntity;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthUserDAOJdbc implements AuthUserDAO {

    private static DataSource ds = DataSourceProvider.INSTANCE.getDataSource(ServiceDB.AUTh);

    @Override
    public int createUser(UserEntity user) {
        int createdRows = 0;

        try (Connection conn = ds.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement usersPs = conn.prepareStatement(
                    "INSERT INTO users (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                            "VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);


                 PreparedStatement authorityPs = conn.prepareStatement(
                         "INSERT INTO authorities (user_id, authority) " +
                                 "VALUES (?, ?)");) {

                usersPs.setString(1, user.getUsername());
                usersPs.setString(2, pe.encode(user.getPassword()));
                usersPs.setBoolean(3, user.getEnabled());
                usersPs.setBoolean(4, user.getAccountNonExpired());
                usersPs.setBoolean(5, user.getAccountNonLocked());
                usersPs.setBoolean(6, user.getCredentialsNonExpired());

                createdRows = usersPs.executeUpdate();
                UUID generatedUserId;

                try (ResultSet generatedKeys = usersPs.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedUserId = UUID.fromString(generatedKeys.getString("id"));
                    } else {
                        throw new IllegalStateException("Can't obtain id from given ResultSet");
                    }
                }

                for (Authority authority : Authority.values()) {
                    authorityPs.setObject(1, generatedUserId);
                    authorityPs.setString(2, authority.name());
                    authorityPs.addBatch();
                    authorityPs.clearParameters();
                }

                authorityPs.executeBatch();
                user.setId(generatedUserId);
                conn.commit();
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return createdRows;
    }

    @Override
    public void deleteUser(UUID userId) {
        try (Connection conn = ds.getConnection()) {
            conn.setAutoCommit(false);
            try (
                    PreparedStatement authorityPs = conn.prepareStatement(
                            "DELETE FROM authorities WHERE user_id=?");
                    PreparedStatement usersPs = conn.prepareStatement(
                            "DELETE FROM users WHERE id=?")
            ) {

                usersPs.setObject(1, userId);
                authorityPs.setObject(1, userId);
                authorityPs.executeUpdate();
                usersPs.executeUpdate();
                conn.commit();
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(true);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserEntity getUser(UUID userId) {
        UserEntity user = new UserEntity();
        try (Connection conn = ds.getConnection()) {
            try (PreparedStatement userPs = conn.prepareStatement(
                    "SELECT * FROM users WHERE id = ? "
            )) {
                userPs.setObject(1, userId);
                userPs.execute();

                ResultSet resultSet = userPs.getResultSet();
                while (resultSet.next()) {
                    user.setId(resultSet.getObject("id", UUID.class));
                    user.setUsername(resultSet.getString("username"));
                    user.setPassword(resultSet.getString("password"));
                    user.setEnabled(resultSet.getBoolean("enabled"));
                    user.setAccountNonExpired(resultSet.getBoolean("account_non_expired"));
                    user.setAccountNonLocked(resultSet.getBoolean("account_non_locked"));
                    user.setCredentialsNonExpired(resultSet.getBoolean("credentials_non_expired"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
        return user;
    }

    @Override
    public void updateUser(UserEntity user) {
        try (Connection conn = ds.getConnection()) {
            try (PreparedStatement userPs = conn.prepareStatement(
                    "UPDATE users SET " +
                            "password = ?" +
                            "enabled = ?" +
                            "account_non_expired = ?, " +
                            "account_non_locked = ? , " +
                            "credentials_non_expired = ? " +
                            "WHERE id = ? "
            )) {
                userPs.setString(1, pe.encode(user.getPassword()));
                userPs.setBoolean(2, user.getEnabled());
                userPs.setBoolean(3, user.getAccountNonExpired());
                userPs.setBoolean(4, user.getAccountNonLocked());
                userPs.setBoolean(5, user.getCredentialsNonExpired());
                userPs.setObject(6, user.getId());
                userPs.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
