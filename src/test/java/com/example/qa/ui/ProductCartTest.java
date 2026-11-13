package com.example.qa.ui;

import com.example.qa.ui.pages.CartPage;
import com.example.qa.ui.pages.ProductsPage;
import com.example.qa.ui.pages.ProductsPage.SortOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.size;

class ProductCartTest extends BaseUiTest {

    @Test
    @DisplayName("Test 2: sort by price (low to high), add a product, see it in the cart")
    void sortAddToCartAndVerify() {
        ProductsPage products = loginAsStandardUser();

        // --- sorting -------------------------------------------------------------
        // Expected order is computed independently from the page's own data,
        // then asserted with a waiting collection condition (no sleeps needed:
        // Selenide retries until the list re-renders or the timeout expires).
        List<String> expectedPrices = products.priceTexts().stream()
                .sorted(Comparator.comparingDouble(ProductCartTest::toAmount))
                .toList();

        products.sortBy(SortOption.PRICE_LOW_TO_HIGH);
        products.prices().shouldHave(exactTexts(expectedPrices));

        // --- add to cart ---------------------------------------------------------
        String selectedProduct = products.names().first().text(); // cheapest product after sorting
        products.addToCart(selectedProduct)
                .shouldShowCartCount(1);

        // --- cart ----------------------------------------------------------------
        CartPage cart = products.openCart();
        cart.itemNames().shouldHave(size(1));
        cart.itemNames().shouldHave(exactTexts(selectedProduct));
    }

    @Test
    @DisplayName("Extra: sort by name (Z to A)")
    void sortByNameDescending() {
        ProductsPage products = loginAsStandardUser();

        List<String> expectedNames = products.productNames().stream()
                .sorted(Comparator.reverseOrder())
                .toList();

        products.sortBy(SortOption.NAME_Z_TO_A);
        products.names().shouldHave(exactTexts(expectedNames));
    }

    private static double toAmount(String priceText) {
        return Double.parseDouble(priceText.replace("$", "").trim());
    }
}
