package guru.qa.niffler.jupiter.extension;

import com.github.javafaker.Faker;
import guru.qa.niffler.db.dao.*;
import guru.qa.niffler.db.dao.impl.AuthUserDAOHibernate;
import guru.qa.niffler.db.dao.impl.UserDataDAOHibernate;
import guru.qa.niffler.db.model.auth.Authority;
import guru.qa.niffler.db.model.auth.AuthorityEntity;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.jupiter.annotation.DBUser;
import org.junit.jupiter.api.extension.*;

import java.util.Arrays;

import static guru.qa.niffler.db.model.CurrencyValues.RUB;

public class DBUserExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    public static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(DBUserExtension.class);
//    private AuthUserDAO authUserDAO;
//    private UserDataUserDAO userDataUserDAO;

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AuthUserDAO authUserDAO = new AuthUserDAOHibernate();
        UserDataUserDAO userDataUserDAO = new UserDataDAOHibernate();

        if (context.getRequiredTestMethod().isAnnotationPresent(DBUser.class)) {
            DBUser annotation = context.getRequiredTestMethod().getAnnotation(DBUser.class);
            AuthUserEntity user = convertToUserEntity(annotation);
            authUserDAO.createUser(user);
            AuthUserEntity userAuthFromDb = authUserDAO.getUserByName(user.getUsername());
            userDataUserDAO.createUserInUserData(userAuthFromDb.toUserDataEntity(RUB));

            context.getStore(NAMESPACE).put(context.getUniqueId(), user);
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        AuthUserDAO authUserDAO = new AuthUserDAOHibernate();
        UserDataUserDAO userDataUserDAO = new UserDataDAOHibernate();

        AuthUserEntity user = context.getStore(NAMESPACE).get(context.getUniqueId(), AuthUserEntity.class);
        if (user != null) {
            userDataUserDAO.deleteUserFromUserData(user.getUsername());
            authUserDAO.deleteUser(user);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(AuthUserEntity.class)
                && extensionContext.getRequiredTestMethod().isAnnotationPresent(DBUser.class);
    }

    @Override
    public AuthUserEntity resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext
                .getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), AuthUserEntity.class);
    }

    private AuthUserEntity convertToUserEntity(DBUser dbUser) {
        AuthUserEntity user = new AuthUserEntity();
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
                    ae.setUser(user);
                    return ae;
                }).toList());
        return user;
    }
}
