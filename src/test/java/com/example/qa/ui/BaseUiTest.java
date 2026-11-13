package com.example.qa.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.example.qa.config.TestConfig;
import com.example.qa.ui.pages.LoginPage;
import com.example.qa.ui.pages.ProductsPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;
import java.util.Map;

@Tag("ui")
public abstract class BaseUiTest {

    @BeforeAll
    static void configureSelenide() {
        Configuration.browser = "chrome";
        Configuration.headless = TestConfig.headless();
        Configuration.browserSize = TestConfig.browserSize();
        Configuration.timeout = TestConfig.timeoutMs();          // implicit waits for every should*/click
        Configuration.pageLoadStrategy = "eager";                  // don't wait for images/fonts
        Configuration.reportsFolder = "target/selenide-reports";   // screenshots + page source on failure

        TestConfig.chromeBinary().ifPresent(path -> Configuration.browserBinary = path);
        TestConfig.chromeDriverBinary().ifPresent(path -> System.setProperty("webdriver.chrome.driver", path));

        ChromeOptions options = new ChromeOptions();
        // Required when Chrome runs as root inside a container
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu");
        // Suppress Chrome's "change your password" popup that SauceDemo credentials can trigger
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.password_manager_leak_detection", false));
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
        Configuration.browserCapabilities = options;
    }

    @AfterEach
    void closeBrowser() {
        Selenide.closeWebDriver(); // fresh session per test = isolated tests
    }

    protected ProductsPage loginAsStandardUser() {
        return new LoginPage()
                .open()
                .loginAs(TestConfig.username(), TestConfig.password());
    }
}
