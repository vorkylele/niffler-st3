package guru.qa.niffler.jupiter;

import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.AuthUserDAOJdbc;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.AuthorityEntity;
import guru.qa.niffler.db.model.UserEntity;
import org.junit.jupiter.api.extension.*;

import java.util.Arrays;

public class DBUserExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    public static ExtensionContext.Namespace NAMESPACEDBUSER = ExtensionContext.Namespace.create(DBUserExtension.class);
    private AuthUserDAO authUserDAO = new AuthUserDAOJdbc();
    private UserDataUserDAO userDataUserDAO = new AuthUserDAOJdbc();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        if (context.getRequiredTestMethod().isAnnotationPresent(DBUser.class)) {
            DBUser annotation = context.getRequiredTestMethod().getAnnotation(DBUser.class);
            UserEntity user = convertToUserEntity(annotation);
            context.getStore(NAMESPACEDBUSER).put(context.getUniqueId(), user);
            authUserDAO.createUser(user);
            userDataUserDAO.createUserFromUserData(user);
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

        user.setUsername(dbUser.username());
        user.setPassword(dbUser.password());
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
