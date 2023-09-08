package guru.qa.niffler.jupiter;

import com.github.javafaker.Faker;
import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.AuthUserDAOSpringJdbc;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.AuthorityEntity;
import guru.qa.niffler.db.model.UserEntity;
import org.junit.jupiter.api.extension.*;

import java.util.Arrays;
import java.util.UUID;

public class DBUserExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    public static ExtensionContext.Namespace NAMESPACEDBUSER = ExtensionContext.Namespace.create(DBUserExtension.class);
    private AuthUserDAO authUserDAO = new AuthUserDAOSpringJdbc();
    private UserDataUserDAO userDataUserDAO = new AuthUserDAOSpringJdbc();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        if (context.getRequiredTestMethod().isAnnotationPresent(DBUser.class)) {
            DBUser annotation = context.getRequiredTestMethod().getAnnotation(DBUser.class);
            UserEntity user = convertToUserEntity(annotation);
            UUID uuid = authUserDAO.createUser(user);
            user.setId(uuid);
            userDataUserDAO.createUserFromUserData(user);

            context.getStore(NAMESPACEDBUSER).put(context.getUniqueId(), user);
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        UserEntity user = context.getStore(NAMESPACEDBUSER).get(context.getUniqueId(), UserEntity.class);
        userDataUserDAO.deleteUserFromUserData(user.getUsername());
        authUserDAO.deleteUser(user.getId());
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter()
                .getType()
                .isAssignableFrom(UserEntity.class);
    }

    @Override
    public UserEntity resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext
                .getStore(NAMESPACEDBUSER)
                .get(extensionContext.getUniqueId(), UserEntity.class);
    }

    private UserEntity convertToUserEntity(DBUser dbUser) {
        UserEntity user = new UserEntity();
        Faker faker = Faker.instance();

        user.setUsername(dbUser.username().isBlank() ? faker.name().username() : dbUser.username());
        user.setPassword(dbUser.password().isBlank() ? faker.internet().password() : dbUser.password());
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setAuthorities(Arrays.stream(Authority.values())
                .map(a -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setAuthority(a);
                    return ae;
                }).toList());
        return user;
    }
}
