package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InvitationReceivedWebTest extends BaseWebTest {

    @BeforeEach
    void doLogin(@User(userType = User.UserType.INVITATION_RECEIVED) UserJson userForTest) {
        welcomePage
                .openWelcomePage()
                .goToLoginPage();
        loginPage
                .signInNiffler(userForTest.getUsername(), userForTest.getPassword());
    }

    @Test
    @AllureId("107")
    void friendInvitationReceivedShouldBeDisplayedInTable0() {
        mainPage
                .clickFriendsBtn();
        friendsPage
                .checkVisibleFriendsPage()
                .checkSizeTableFriends()
                .checkInvitationReceived();
    }

    @Test
    @AllureId("108")
    void friendInvitationReceivedShouldBeDisplayedInTable1() {
        mainPage
                .clickFriendsBtn();
        friendsPage
                .checkVisibleFriendsPage()
                .checkSizeTableFriends()
                .checkInvitationReceived();
    }

    @Test
    @AllureId("109")
    void friendInvitationReceivedShouldBeDisplayedInTable2() {
        mainPage
                .clickFriendsBtn();
        friendsPage
                .checkVisibleFriendsPage()
                .checkSizeTableFriends()
                .checkInvitationReceived();
    }
}