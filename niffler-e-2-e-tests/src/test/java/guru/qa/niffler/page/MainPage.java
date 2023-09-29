package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.SpendJson;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class MainPage extends BasePage {
    // Elements
    private final SelenideElement contentOfTableHistoryOfSpendings = $(".spendings__content tbody");
    private final SelenideElement deleteBtn = $(byText("Delete selected"));
    private final SelenideElement mainPage = $(".main-content__section-stats");
    private final SelenideElement friendsBtn = $("[data-tooltip-id=friends]");
    private final SelenideElement allPeopleBtn = $("[data-tooltip-id=people]");


    // Actions
    @Step("Проверить отображение 'MainPage'")
    public MainPage checkVisibleMainPage() {
        mainPage.should(visible);
        return new MainPage();
    }

    @Step("Выбрать spending's в таблице 'History of spendings'")
    public MainPage selectSpending(SpendJson createdSpend) {
        contentOfTableHistoryOfSpendings
                .$$("tr")
                .find(text(createdSpend.getDescription()))
                .$$("td")
                .first()
                .scrollTo()
                .click();
        return new MainPage();
    }

    @Step("Удалить spending's в таблице 'History of spendings'")
    public MainPage deleteSpending() {
        deleteBtn.click();
        return new MainPage();
    }

    @Step("Проверить удаленные spending's в таблице 'History of spendings'")
    public MainPage checkDeletedSpending() {
        contentOfTableHistoryOfSpendings
                .$$("tr")
                .shouldHave(size(0));
        return new MainPage();
    }

    @Step("Нажать на кнопку 'Друзья'")
    public FriendsPage clickFriendsBtn() {
        friendsBtn.click();
        return new FriendsPage();
    }

    @Step("Нажать на кнопку 'All people'")
    public AllPeoplePage clickAllPeopleBtnBtn() {
        allPeopleBtn.click();
        return new AllPeoplePage();
    }
}
