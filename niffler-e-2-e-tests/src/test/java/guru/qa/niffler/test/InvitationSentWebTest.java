package guru.qa.niffler.test;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;

public class InvitationSentWebTest extends BaseWebTest {

    SelenideElement userTable = $(".people-content").$(".table");

    @BeforeEach
    void doLogin(@User(userType = User.UserType.INVITATION_SENT) UserJson userForTest) {
        welcomePage
                .openWelcomePage()
                .goToLoginPage();
        loginPage
                .signInNiffler(userForTest.getUsername(), userForTest.getPassword());
    }

    @Test
    @AllureId("104")
    void friendInviteShouldBeDisplayedInTable0() {
        mainPage
                .clickAllPeopleBtnBtn();
        allPeoplePage
                .checkVisibleAllPeoplePage()
                .checkVisibleInvitationSent();
    }

    @Test
    @AllureId("105")
    void friendInviteShouldBeDisplayedInTable1() {
        mainPage
                .clickAllPeopleBtnBtn();
        allPeoplePage
                .checkVisibleAllPeoplePage()
                .checkVisibleInvitationSent();
    }

    @Test
    @AllureId("106")
    void friendInviteShouldBeDisplayedInTable2() {
        mainPage
                .clickAllPeopleBtnBtn();
        allPeoplePage
                .checkVisibleAllPeoplePage()
                .checkVisibleInvitationSent();
    }
}