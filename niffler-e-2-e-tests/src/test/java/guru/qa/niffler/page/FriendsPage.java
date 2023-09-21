package guru.qa.niffler.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage extends BasePage {
    // Elements
    private final SelenideElement friendsTable = $(".people-content").$(".table");


    // Actions
    @Step("Проверить отображение таблицы 'Друзья'")
    public FriendsPage checkVisibleFriendsPage() {
        friendsTable.shouldBe(visible);
        return new FriendsPage();
    }

    @Step("Проверить, что размер таблицы 'Друзья' == 1")
    public FriendsPage checkSizeTableFriends() {
        friendsTable.$("tbody").$$("tr")
                .shouldHave(CollectionCondition.size(1));
        return new FriendsPage();
    }

    @Step("Проверить наличие 1 друга в таблице 'Друзья'")
    public FriendsPage checkVisibleFriend() {
        friendsTable.$("tbody").$("tr").$("td:nth-child(4)").
                shouldHave(text("You are friends"));
        return new FriendsPage();
    }

    @Step("Проверить наличие 1 друга в таблице 'Друзья'")
    public FriendsPage checkInvitationReceived() {
        friendsTable.$("tbody").$("tr")
                .$("td:nth-child(4)")
                .$("[data-tooltip-id='submit-invitation']")
                .shouldHave(visible);
        return new FriendsPage();
    }
}
