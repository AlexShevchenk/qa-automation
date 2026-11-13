package com.example.qa.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import java.util.List;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProductsPage {

    public enum SortOption {
        NAME_A_TO_Z("az"),
        NAME_Z_TO_A("za"),
        PRICE_LOW_TO_HIGH("lohi"),
        PRICE_HIGH_TO_LOW("hilo");

        private final String value;

        SortOption(String value) {
            this.value = value;
        }
    }

    private final SelenideElement title = $("[data-test='title']");
    private final SelenideElement sortSelect = $("[data-test='product-sort-container']");
    private final SelenideElement cartLink = $("[data-test='shopping-cart-link']");
    private final SelenideElement cartBadge = $("[data-test='shopping-cart-badge']");
    private final ElementsCollection items = $$("[data-test='inventory-item']");
    private final ElementsCollection names = $$("[data-test='inventory-item-name']");
    private final ElementsCollection prices = $$("[data-test='inventory-item-price']");

    public SelenideElement title() {
        return title;
    }

    public ElementsCollection items() {
        return items;
    }

    public ElementsCollection names() {
        return names;
    }

    public ElementsCollection prices() {
        return prices;
    }

    public ProductsPage sortBy(SortOption option) {
        // Select by value (stable) rather than visible text (could be re-worded/translated)
        sortSelect.selectOptionByValue(option.value);
        return this;
    }

    public ProductsPage addToCart(String productName) {
        items.findBy(text(productName)).$("button").click();
        return this;
    }

    public ProductsPage shouldShowCartCount(int count) {
        cartBadge.shouldHave(exactText(String.valueOf(count)));
        return this;
    }

    public CartPage openCart() {
        cartLink.click();
        return new CartPage();
    }

    /** Snapshot of the currently rendered price texts, e.g. ["$29.99", "$9.99"]. */
    public List<String> priceTexts() {
        return prices.texts();
    }

    /** Snapshot of the currently rendered product names. */
    public List<String> productNames() {
        return names.texts();
    }
}
