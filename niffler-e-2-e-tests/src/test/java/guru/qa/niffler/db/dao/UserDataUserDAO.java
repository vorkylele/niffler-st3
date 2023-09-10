package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.userdata.UserDataEntity;

public interface UserDataUserDAO {

    int createUserInUserData(UserDataEntity user);

    void deleteUserFromUserData(String username);

    UserDataEntity getUserFromUserData(String username);

    void updateUserInUserData(UserDataEntity user);
}
