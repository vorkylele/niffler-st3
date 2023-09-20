package guru.qa.niffler.test;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class FriendsWebTest extends BaseWebTest {

    SelenideElement friendsTable = $(".people-content").$(".table");

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
    void friendShouldBeDisplayedInTable0() {
        $("[data-tooltip-id=friends]").click();
        friendsTable.shouldBe(visible);
        friendsTable.$("tbody").$$("tr")
                .shouldHave(CollectionCondition.size(1));
        friendsTable.$("tbody").$("tr").$("td:nth-child(4)").
                shouldHave(text("You are friends"));
    }

    @Test
    @AllureId("102")
    void friendShouldBeDisplayedInTable1() {
        $("[data-tooltip-id=friends]").click();
        friendsTable.shouldBe(visible);
        friendsTable.$("tbody").$$("tr")
                .shouldHave(CollectionCondition.size(1));
        friendsTable.$("tbody").$("tr").$("td:nth-child(4)").
                shouldHave(text("You are friends"));
    }

    @Test
    @AllureId("103")
    void friendShouldBeDisplayedInTable2() {
        $("[data-tooltip-id=friends]").click();
        friendsTable.shouldBe(visible);
        friendsTable.$("tbody").$$("tr")
                .shouldHave(CollectionCondition.size(1));
        friendsTable.$("tbody").$("tr").$("td:nth-child(4)").
                shouldHave(text("You are friends"));
    }
}
