package guru.qa.niffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class WelcomePage extends BasePage {
    // Elements
    private final SelenideElement loginBtn = $("a[href*='redirect']");
    private final SelenideElement registerBtn = $("a[href*='refister']");

    // Actions
    @Step("Открыть 'WelcomePage'")
    public WelcomePage openWelcomePage() {
        Selenide.open(config.nifflerFrontUrl());
        return new WelcomePage();
    }

    @Step("Открыть 'LoginPage'")
    public LoginPage goToLoginPage() {
        loginBtn.click();
        return new LoginPage();
    }

    @Step("Открыть 'RegisterPage'")
    public RegisterPage goToRegisterPage() {
        registerBtn.click();
        return new RegisterPage();
    }
}
