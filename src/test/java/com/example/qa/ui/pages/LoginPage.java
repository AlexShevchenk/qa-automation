package com.example.qa.ui.pages;

import com.codeborne.selenide.SelenideElement;
import com.example.qa.config.TestConfig;

import static com.codeborne.selenide.Selenide.$;
import com.codeborne.selenide.Selenide;

public class LoginPage {

    // SauceDemo exposes dedicated data-test attributes: they are meant for automation
    // and do not change when styling or layout does.
    private final SelenideElement usernameInput = $("[data-test='username']");
    private final SelenideElement passwordInput = $("[data-test='password']");
    private final SelenideElement loginButton = $("[data-test='login-button']");
    private final SelenideElement errorMessage = $("[data-test='error']");

    public LoginPage open() {
        Selenide.open(TestConfig.uiBaseUrl());
        return this;
    }

    public ProductsPage loginAs(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        loginButton.click();
        return new ProductsPage();
    }

    public SelenideElement errorMessage() {
        return errorMessage;
    }
}
