package com.example.qa.ui;

import com.codeborne.selenide.WebDriverConditions;
import com.example.qa.config.TestConfig;
import com.example.qa.ui.pages.LoginPage;
import com.example.qa.ui.pages.ProductsPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Selenide.webdriver;

class LoginTest extends BaseUiTest {

    @Test
    @DisplayName("Test 1: standard user can log in and sees the product list")
    void userCanLogIn() {
        ProductsPage products = loginAsStandardUser();

        webdriver().shouldHave(WebDriverConditions.urlContaining("inventory.html"));
        products.title().shouldHave(exactText("Products"));
        products.items().shouldHave(sizeGreaterThan(0));
    }

    @Test
    @DisplayName("Extra: wrong password shows an error and stays on the login page")
    void invalidPasswordShowsError() {
        LoginPage login = new LoginPage().open();
        login.loginAs(TestConfig.username(), "wrong_password");

        login.errorMessage().shouldHave(text("Username and password do not match"));
        webdriver().shouldNotHave(WebDriverConditions.urlContaining("inventory.html"));
    }
}
