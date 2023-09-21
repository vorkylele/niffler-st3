package guru.qa.niffler.test;

import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.jupiter.annotation.DBUser;
import org.junit.jupiter.api.Test;

public class LoginTest extends BaseWebTest {

    @DBUser
    @Test
    void mainPageShouldBeVisibleAfterLogin(AuthUserEntity user) {
        welcomePage
                .openWelcomePage()
                .goToLoginPage();
        loginPage
                .signInNiffler(user.getUsername(), user.getPassword());
        mainPage.
                checkVisibleMainPage();
    }
}