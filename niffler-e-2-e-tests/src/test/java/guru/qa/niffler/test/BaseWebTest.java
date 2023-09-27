package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.annotation.WebTest;
import guru.qa.niffler.page.*;

@WebTest
public abstract class BaseWebTest {
    protected final WelcomePage welcomePage = new WelcomePage();
    protected final LoginPage loginPage = new LoginPage();
    protected final MainPage mainPage = new MainPage();
    protected final FriendsPage friendsPage = new FriendsPage();
    protected final AllPeoplePage allPeoplePage = new AllPeoplePage();
}