package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.UserEntity;

import java.util.UUID;

public class AuthUserDAOHibernate implements AuthUserDAO {
    @Override
    public int createUser(UserEntity user) {
        return 0;
    }

    @Override
    public void deleteUser(UUID userId) {

    }

    @Override
    public UserEntity getUser(UUID userId) {
        return null;
    }

    @Override
    public void updateUser(UserEntity user) {
    }
}
