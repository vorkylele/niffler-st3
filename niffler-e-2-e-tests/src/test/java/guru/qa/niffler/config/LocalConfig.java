package guru.qa.niffler.config;

import com.codeborne.selenide.Configuration;

public class LocalConfig implements Config {

    static final LocalConfig config = new LocalConfig();

    static {
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";
    }

    private LocalConfig() {
    }

    @Override
    public String databaseHost() {
        return "localhost";
    }

    @Override
    public String nifflerSpendUrl() {
        return "http://127.0.0.1:8093";
    }

    @Override
    public String nifflerCategoryUrl() {
        return "http://127.0.0.1:8093";
    }

    @Override
    public String baseUri() {
        return "http://127.0.0.1:3000";
    }
}