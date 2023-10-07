package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

@Isolated
public class SpendingWebTest extends BaseWebTest {

    @Category(
            category = "Рыбалка",
            username = "dima"
    )
    @ApiLogin(username = "dima", password = "12345")
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
                .openMainPage()
                .selectSpending(createdSpend)
                .deleteSpending()
                .checkDeletedSpending();
    }
}
