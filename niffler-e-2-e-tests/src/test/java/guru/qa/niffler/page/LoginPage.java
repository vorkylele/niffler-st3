package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage {
    // Elements
    private final SelenideElement usernameInputField = $("input[name='username']");
    private final SelenideElement passwordInputField = $("input[name='password']");
    private final SelenideElement signInBtn = $("button[type='submit']");

    // Actions
    @Step("Войти в приложение 'Niffler'")
    public MainPage signInNiffler(String username, String password) {
        usernameInputField.setValue(username);
        passwordInputField.setValue(password);
        signInBtn.click();
        return new MainPage();
    }
}
