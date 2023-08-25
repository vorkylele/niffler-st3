package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.User;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;

public class FriendsWebTest extends BaseWebTest {

    @BeforeEach
    void doLogin(@User(userType = User.UserType.WITH_FRIENDS) UserJson userForTest) {
        Selenide.open("http://127.0.0.1:3000/main");
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(userForTest.getUsername());
        $("input[name='password']").setValue(userForTest.getPassword());
        $("button[type='submit']").click();
    }

    @Test
    @AllureId("101")
    void friendShouldBeDisplayedInTable0(@User(userType = User.UserType.WITH_FRIENDS) UserJson userForTest) {
        System.out.println(1);
    }

    @Test
    @AllureId("102")
    void friendShouldBeDisplayedInTable1(@User(userType = User.UserType.WITH_FRIENDS) UserJson userForTest) {
        System.out.println(2);

    }

    @Test
    @AllureId("103")
    void friendShouldBeDisplayedInTable2(@User(userType = User.UserType.WITH_FRIENDS) UserJson userForTest) {
        System.out.println(3);

    }
}
