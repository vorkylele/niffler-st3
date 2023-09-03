package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.UserDataEntity;
import guru.qa.niffler.db.model.UserEntity;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserDataDAOJdbc implements UserDataUserDAO {

    private static DataSource ds = DataSourceProvider.INSTANCE.getDataSource(ServiceDB.USERDATA);


    @Override
    public int createUser(UserEntity user) {
        int createdRows = 0;
        try (Connection conn = ds.getConnection()) {
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
    public void deleteUser(String username) {
        try (Connection conn = ds.getConnection()) {
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
    public UserDataEntity getUser(String username) {
        UserDataEntity user = new UserDataEntity();
        try (Connection conn = ds.getConnection()) {
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
    public void updateUser(UserDataEntity user) {
        try (Connection conn = ds.getConnection()) {
            try (PreparedStatement userPs = conn.prepareStatement(
                    "UPDATE users SET " +
                            "currency = ?" +
                            "firstname = ?" +
                            "surname = ?, " +
                            "photo = ? , " +
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
