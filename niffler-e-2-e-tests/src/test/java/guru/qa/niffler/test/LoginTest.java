package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.DBUser;
import org.junit.jupiter.api.Test;

public class LoginTest extends BaseWebTest {

    @DBUser
    @ApiLogin
    @Test
    void mainPageShouldBeVisibleAfterLogin() {
        mainPage
                .openMainPage()
                .checkVisibleMainPage();
    }
}