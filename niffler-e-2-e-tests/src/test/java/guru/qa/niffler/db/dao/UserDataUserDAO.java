package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.UserDataEntity;
import guru.qa.niffler.db.model.UserEntity;

public interface UserDataUserDAO {

    int createUser(UserEntity user);

    void deleteUser(String username);

    UserDataEntity getUser(String username);

    void updateUser(UserDataEntity user);
}
