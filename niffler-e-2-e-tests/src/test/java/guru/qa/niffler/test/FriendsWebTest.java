package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FriendsWebTest extends BaseWebTest {

    @BeforeEach
    void doLogin(@User(userType = User.UserType.WITH_FRIENDS) UserJson userForTest) {
        welcomePage
                .openWelcomePage()
                .goToLoginPage();
        loginPage
                .signInNiffler(userForTest.getUsername(), userForTest.getPassword());
    }

    @Test
    @AllureId("101")
    void friendShouldBeDisplayedInTable0() {
        mainPage
                .clickFriendsBtn();
        friendsPage
                .checkVisibleFriendsPage()
                .checkSizeTableFriends()
                .checkVisibleFriend();
    }

    @Test
    @AllureId("102")
    void friendShouldBeDisplayedInTable1() {
        mainPage
                .clickFriendsBtn();
        friendsPage
                .checkVisibleFriendsPage()
                .checkSizeTableFriends()
                .checkVisibleFriend();
    }

    @Test
    @AllureId("103")
    void friendShouldBeDisplayedInTable2() {
        mainPage
                .clickFriendsBtn();
        friendsPage
                .checkVisibleFriendsPage()
                .checkSizeTableFriends()
                .checkVisibleFriend();
    }
}
