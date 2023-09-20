package guru.qa.niffler.db.dao.impl;

import guru.qa.niffler.db.jdbc.DataSourceProvider;
import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.springjdbc.AuthorityEntityRowMapper;
import guru.qa.niffler.db.springjdbc.UserDataEntityRowMapper;
import guru.qa.niffler.db.springjdbc.UserEntityRowMapper;
import guru.qa.niffler.db.model.auth.Authority;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.userdata.UserDataEntity;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class AuthUserDAOSpringJdbc implements AuthUserDAO, UserDataUserDAO {

    private final TransactionTemplate authTtpl;
    private final JdbcTemplate authJdbcTemplate;
    private final TransactionTemplate userDataTtpl;
    private final JdbcTemplate userDataJdbcTemplate;

    public AuthUserDAOSpringJdbc() {
        JdbcTransactionManager authTm = new JdbcTransactionManager(
                DataSourceProvider.INSTANCE.getDataSource(ServiceDB.AUTh));
        JdbcTransactionManager userDataTm = new JdbcTransactionManager(
                DataSourceProvider.INSTANCE.getDataSource(ServiceDB.USERDATA));

        this.authTtpl = new TransactionTemplate(authTm);
        this.authJdbcTemplate = new JdbcTemplate(authTm.getDataSource());
        this.userDataTtpl = new TransactionTemplate(userDataTm);
        this.userDataJdbcTemplate = new JdbcTemplate(userDataTm.getDataSource());
    }

    @Override
    @SuppressWarnings("unchecked")
    public int createUser(AuthUserEntity user) {
        return authTtpl.execute(status -> {
            KeyHolder kh = new GeneratedKeyHolder();

            authJdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement("INSERT INTO users (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                        "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getUsername());
                ps.setString(2, pe.encode(user.getPassword()));
                ps.setBoolean(3, user.getEnabled());
                ps.setBoolean(4, user.getAccountNonExpired());
                ps.setBoolean(5, user.getAccountNonLocked());
                ps.setBoolean(6, user.getCredentialsNonExpired());
                return ps;
            }, kh);
            final UUID userId = (UUID) kh.getKeyList().get(0).get("id");
            user.setId(userId);
            authJdbcTemplate.batchUpdate("INSERT INTO authorities (user_id, authority) VALUES (?, ?)", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setObject(1, userId);
                    ps.setObject(2, Authority.values()[i].name());
                }

                @Override
                public int getBatchSize() {
                    return Authority.values().length;
                }
            });
            return 0;
        });
    }

    @Override
    public AuthUserEntity updateUser(AuthUserEntity user) {
        authJdbcTemplate.update("UPDATE users SET " +
                        "password = ?, " +
                        "enabled = ?, " +
                        "account_non_expired = ?, " +
                        "account_non_locked = ?, " +
                        "credentials_non_expired = ? " +
                        "WHERE id = ?",

                pe.encode(user.getPassword()),
                user.getEnabled(),
                user.getAccountNonExpired(),
                user.getAccountNonLocked(),
                user.getCredentialsNonExpired(),
                user.getId());

        return user;
    }

    @Override
    public void deleteUser(AuthUserEntity user) {
        authTtpl.executeWithoutResult(status -> {
            authJdbcTemplate.update("DELETE FROM authorities WHERE user_id=?", user.getId());
            authJdbcTemplate.update("DELETE FROM users WHERE id=?", user.getId());
        });
    }

    @Override
    public AuthUserEntity getUserById(UUID userId) {
        AuthUserEntity user = authJdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE id = ?",
                UserEntityRowMapper.instance,
                userId
        );

        user.setAuthorities(
                authJdbcTemplate.query(
                        "SELECT * FROM authorities WHERE user_id=?",
                        AuthorityEntityRowMapper.instance,
                        userId
                ));
        user.getAuthorities().forEach(
                authorityEntity -> authorityEntity.setUser(user)
        );
        return user;
    }

    @Override
    public AuthUserEntity getUserByName(String name) {
        AuthUserEntity user = authJdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE username = ?",
                UserEntityRowMapper.instance,
                name
        );

        user.setAuthorities(
                authJdbcTemplate.query(
                        "SELECT * FROM authorities WHERE user_id=?",
                        AuthorityEntityRowMapper.instance,
                        user.getId()
                ));
        user.getAuthorities().forEach(
                authorityEntity -> authorityEntity.setUser(user)
        );
        return user;
    }

    @Override
    public int createUserInUserData(UserDataEntity user) {
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
        UserDataEntity userDataEntity = userDataJdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE username = ?",
                UserDataEntityRowMapper.instance,
                username
        );
        return userDataEntity;
    }

    @Override
    public void updateUserInUserData(UserDataEntity user) {
        userDataJdbcTemplate.update("UPDATE users SET " +
                        "currency = ?, " +
                        "firstname = ?, " +
                        "surname = ?, " +
                        "photo = ? " +
                        "WHERE id = ? ",

                user.getCurrency().name(),
                user.getFirstname(),
                user.getSurname(),
                user.getPhoto(),
                user.getId());
    }
}
