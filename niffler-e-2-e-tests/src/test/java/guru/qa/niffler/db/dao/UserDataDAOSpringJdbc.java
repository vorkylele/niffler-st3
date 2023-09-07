package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.UserDataEntity;
import guru.qa.niffler.db.model.UserEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class UserDataDAOSpringJdbc implements UserDataUserDAO {

    private final TransactionTemplate userDataTtpl;
    private final JdbcTemplate userDataJdbcTemplate;

    public UserDataDAOSpringJdbc() {
        JdbcTransactionManager userDataTm= new JdbcTransactionManager(
                DataSourceProvider.INSTANCE.getDataSource(ServiceDB.USERDATA));

        this.userDataTtpl = new TransactionTemplate(userDataTm);
        this.userDataJdbcTemplate = new JdbcTemplate(userDataTm.getDataSource());
    }


    @Override
    public int createUserFromUserData(UserEntity user) {
        return userDataJdbcTemplate.update(
                "INSERT INTO users (username, currency) VALUES (?, ?)",
                user.getUsername(), CurrencyValues.RUB.name());
    }

    @Override
    public void deleteUserFromUserData(String username) {
        userDataJdbcTemplate.update("DELETE FROM users WHERE username=?", username);
    }

    @Override
    public UserDataEntity getUserFromUserData(String username) {
        return null;
    }

    @Override
    public void updateUserInUserData(UserDataEntity user) {

    }
}
