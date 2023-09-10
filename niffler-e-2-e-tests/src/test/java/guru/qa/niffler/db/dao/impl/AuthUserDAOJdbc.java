package guru.qa.niffler.db.dao.impl;

import guru.qa.niffler.db.jdbc.DataSourceProvider;
import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.model.*;
import guru.qa.niffler.db.model.auth.Authority;
import guru.qa.niffler.db.model.auth.AuthorityEntity;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.userdata.UserDataEntity;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthUserDAOJdbc implements AuthUserDAO, UserDataUserDAO {

    private static DataSource authDs = DataSourceProvider.INSTANCE.getDataSource(ServiceDB.AUTh);
    private static DataSource userDataDs = DataSourceProvider.INSTANCE.getDataSource(ServiceDB.USERDATA);

    @Override
    public int createUser(AuthUserEntity user) {
        UUID generatedUserId = null;

        try (Connection conn = authDs.getConnection()) {
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

                usersPs.executeUpdate();

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

        return 0;
    }

    @Override
    public AuthUserEntity updateUser(AuthUserEntity user) {
        try (Connection conn = authDs.getConnection()) {
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

                return user;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteUser(AuthUserEntity user) {
        try (Connection conn = authDs.getConnection()) {
            conn.setAutoCommit(false);
            try (
                    PreparedStatement authorityPs = conn.prepareStatement(
                            "DELETE FROM authorities WHERE user_id=?");
                    PreparedStatement usersPs = conn.prepareStatement(
                            "DELETE FROM users WHERE id=?")
            ) {

                authorityPs.setObject(1, user.getId());
                authorityPs.executeUpdate();

                usersPs.setObject(1, user.getId());
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
    public AuthUserEntity getUserById(UUID userId) {
        AuthUserEntity user = new AuthUserEntity();
        try (Connection conn = authDs.getConnection()) {
            try (PreparedStatement userPs = conn.prepareStatement(
                    "SELECT * FROM users WHERE id = ? ")) {
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
            try (PreparedStatement authPs = conn.prepareStatement(
                    "SELECT * FROM authorities WHERE user_id=?")) {
                authPs.setObject(1, userId);
                authPs.execute();

                ResultSet resultSet = authPs.getResultSet();
                List<AuthorityEntity> authorities = new ArrayList<>();
                while (resultSet.next()) {
                    AuthorityEntity authorityEntity = new AuthorityEntity();
                    authorityEntity.setId(resultSet.getObject("id", UUID.class));
                    authorityEntity.setAuthority(Authority.valueOf(resultSet.getString("authority")));
                    authorityEntity.setUser(user);
                    authorities.add(authorityEntity);
                }
                user.setAuthorities(authorities);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public AuthUserEntity getUserByName(String name) {
        AuthUserEntity user = new AuthUserEntity();
        try (Connection conn = authDs.getConnection()) {
            try (PreparedStatement usersPs = conn.prepareStatement(
                    "SELECT * FROM users WHERE username = ? ")) {
                usersPs.setString(1, name);
                usersPs.execute();

                ResultSet resultSet = usersPs.getResultSet();
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
            try (PreparedStatement authPs = conn.prepareStatement(
                    "SELECT * FROM authorities WHERE user_id=?")) {
                authPs.setObject(1, user.getId());
                authPs.execute();

                ResultSet resultSet = authPs.getResultSet();
                List<AuthorityEntity> authorities = new ArrayList<>();
                while (resultSet.next()) {
                    AuthorityEntity authorityEntity = new AuthorityEntity();
                    authorityEntity.setId(resultSet.getObject("id", UUID.class));
                    authorityEntity.setAuthority(Authority.valueOf(resultSet.getString("authority")));
                    authorityEntity.setUser(user);
                    authorities.add(authorityEntity);
                }
                user.setAuthorities(authorities);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public int createUserInUserData(UserDataEntity user) {
        int createdRows = 0;
        try (Connection conn = userDataDs.getConnection()) {
            try (PreparedStatement usersPs = conn.prepareStatement(
                    "INSERT INTO users (username, currency) " +
                            "VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {

                usersPs.setString(1, user.getUsername());
                usersPs.setString(2, CurrencyValues.RUB.name());

                createdRows = usersPs.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return createdRows;
    }

    @Override
    public void deleteUserFromUserData(String username) {
        try (Connection conn = userDataDs.getConnection()) {
            try (PreparedStatement userPs = conn.prepareStatement(
                    "DELETE FROM users WHERE username=?")) {
                userPs.setString(1, username);
                userPs.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserDataEntity getUserFromUserData(String username) {
        UserDataEntity user = new UserDataEntity();
        try (Connection conn = userDataDs.getConnection()) {
            try (PreparedStatement userPs = conn.prepareStatement(
                    "SELECT * FROM users WHERE username = ? "
            )) {
                userPs.setObject(1, username);
                userPs.execute();

                ResultSet resultSet = userPs.getResultSet();
                while (resultSet.next()) {
                    user.setId(resultSet.getObject("id", UUID.class));
                    user.setUsername(resultSet.getString("username"));
                    user.setCurrency(CurrencyValues.valueOf(resultSet.getString("currency")));
                    user.setFirstname(resultSet.getString("firstname"));
                    user.setSurname(resultSet.getString("surname"));
                    user.setPhoto(resultSet.getBytes("photo"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public void updateUserInUserData(UserDataEntity user) {
        try (Connection conn = userDataDs.getConnection()) {
            try (PreparedStatement userPs = conn.prepareStatement(
                    "UPDATE users SET " +
                            "currency = ?, " +
                            "firstname = ?, " +
                            "surname = ?, " +
                            "photo = ? " +
                            "WHERE id = ? "
            )) {
                userPs.setString(1, user.getCurrency().name());
                userPs.setString(2, user.getFirstname());
                userPs.setString(3, user.getSurname());
                userPs.setObject(4, user.getPhoto());
                userPs.setObject(5, user.getId());
                userPs.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
