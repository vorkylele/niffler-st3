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

public class InvitationSentWebTest extends BaseWebTest {

    SelenideElement userTable = $(".people-content").$(".table");

    @BeforeEach
    void doLogin(@User(userType = User.UserType.INVITATION_SENT) UserJson userForTest) {
        Selenide.open("http://127.0.0.1:3000/main");
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(userForTest.getUsername());
        $("input[name='password']").setValue(userForTest.getPassword());
        $("button[type='submit']").click();
    }

    @Test
    @AllureId("104")
    void friendInviteShouldBeDisplayedInTable0() {
        $("[data-tooltip-id=people]").click();
        userTable.shouldBe(visible);
        userTable.$("tbody").$$("tr")
                .filterBy(text("Pending invitation"))
                .shouldHave(CollectionCondition.size(1));
    }

    @Test
    @AllureId("105")
    void friendInviteShouldBeDisplayedInTable1() {
        $("[data-tooltip-id=people]").click();
        userTable.shouldBe(visible);
        userTable.$("tbody").$$("tr")
                .filterBy(text("Pending invitation"))
                .shouldHave(CollectionCondition.size(1));
    }
}