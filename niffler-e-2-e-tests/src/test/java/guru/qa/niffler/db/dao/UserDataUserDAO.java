package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.UserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

public interface UserDataUserDAO {

    int createUserInUserData(UserEntity user);

    void deleteUserInUserData(UUID userId);
}
