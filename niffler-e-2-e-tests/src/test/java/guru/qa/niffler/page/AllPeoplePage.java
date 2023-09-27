package guru.qa.niffler.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class AllPeoplePage extends BasePage {
    // Elements
    private final SelenideElement userTable = $(".people-content").$(".table");

    // Actions
    @Step("Проверить отображение таблицы 'Пользователи'")
    public AllPeoplePage checkVisibleAllPeoplePage() {
        userTable.shouldBe(visible);
        return new AllPeoplePage();
    }

    @Step("Проверить наличие 1 отправленного приглашения дружбы")
    public AllPeoplePage checkVisibleInvitationSent() {
        userTable.$("tbody").$$("tr")
                .filterBy(text("Pending invitation"))
                .shouldHave(CollectionCondition.size(1));
        return new AllPeoplePage();
    }
}
