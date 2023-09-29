package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import static guru.qa.niffler.jupiter.annotation.User.UserType.WITH_FRIENDS;

@Isolated
public class SpendingWebTest extends BaseWebTest {

    @BeforeEach
    void doLogin(@User(userType = WITH_FRIENDS) UserJson userForTest) {
        welcomePage
                .openWelcomePage()
                .goToLoginPage();
        loginPage
                .signInNiffler(userForTest.getUsername(), userForTest.getPassword());
    }

    @Category(
            category = "Рыбалка",
            username = "dima"
    )
    @Spend(
            username = "dima",
            description = "Рыбалка на Ладоге",
            category = "Рыбалка",
            amount = 14000.00,
            currency = CurrencyValues.RUB
    )
    @Test
    @AllureId("100")
    void spendingShouldBeDeletedAfterDeleteAction(SpendJson createdSpend) {
        mainPage
                .selectSpending(createdSpend)
                .deleteSpending()
                .checkDeletedSpending();
    }
}
