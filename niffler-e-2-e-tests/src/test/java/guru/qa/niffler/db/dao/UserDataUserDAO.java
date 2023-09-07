package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.UserDataEntity;
import guru.qa.niffler.db.model.UserEntity;

public interface UserDataUserDAO {

    int createUserFromUserData(UserEntity user);

    void deleteUserFromUserData(String username);

    UserDataEntity getUserFromUserData(String username);

    void updateUserInUserData(UserDataEntity user);
}
