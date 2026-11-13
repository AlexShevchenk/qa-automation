# QA Automation Assignment

UI tests (Selenide) for [SauceDemo](https://www.saucedemo.com/) and API tests (REST Assured)
for [Restful Booker](https://restful-booker.herokuapp.com/), packaged with Docker.

**Stack:** Java 17, Maven, JUnit 5, Selenide, REST Assured, AssertJ.

## Project layout

```
src/test/java/com/example/qa
├── config/TestConfig.java        # settings: -D property > config.properties
├── ui/
│   ├── BaseUiTest.java           # browser setup / teardown, login helper
│   ├── LoginTest.java            # Test 1 (+1 negative)
│   ├── ProductCartTest.java      # Test 2 (+ name sorting)
│   └── pages/                    # Page Objects: LoginPage, ProductsPage, CartPage
└── api/
    ├── BookingApiTest.java       # Test 3 (+ 404 check)
    ├── BookingTestData.java      # random valid booking
    ├── client/BookingClient.java # POST /booking, GET /booking/{id}
    └── model/                    # Booking, BookingDates, BookingCreatedResponse (records)
src/test/resources/config.properties
Dockerfile
```

Tests are tagged `ui` or `api` (JUnit 5 tags) so each group can be run separately.

## Prerequisites (local run)

- JDK 17+
- Maven 3.8+
- Google Chrome (Selenium Manager downloads the matching chromedriver automatically)
- Internet access to saucedemo.com and restful-booker.herokuapp.com

## Local execution

```bash
mvn test                  # all tests
mvn test -Dgroups=ui      # UI tests only
mvn test -Dgroups=api     # API tests only
```

Useful overrides: `-Dheadless=false` (watch the browser), `-Dtimeout.ms=15000`,
`-Dapi.base.url=...`. Any key in `config.properties` can be overridden this way.

Reports: `target/surefire-reports/`; Selenide screenshots and page source for failed
UI tests: `target/selenide-reports/`.

## Docker execution

No local Java, Maven or browser required, only Docker:

```bash
docker build -t qa-automation-tests .

docker run --rm qa-automation-tests                                   # all tests
docker run --rm qa-automation-tests mvn -B test -Dgroups=ui           # UI only
docker run --rm qa-automation-tests mvn -B test -Dgroups=api          # API only

# keep the reports on the host
docker run --rm -v "$(pwd)/target:/app/target" qa-automation-tests
```

The image contains JDK 17, Maven, Chromium and chromedriver. Chrome runs headless with
`--no-sandbox` (needed when running as root in a container). The container exits with a
non-zero code when a test fails, so it can be used directly in CI.

## Test approach

**Locators.** SauceDemo ships `data-test` attributes on its elements, so I use
`[data-test='...']` CSS selectors everywhere. They are meant for automation and are far
less likely to break than CSS classes, generated ids or XPath tied to the DOM structure.
Locators live only in page objects, never in tests. The sort option is chosen by its
`value`, not its visible text.

**Synchronization.** There are no `sleep` calls. Selenide waits and retries on every
action and `should*` assertion up to `timeout.ms`. For sorting, the expected order is
computed in the test (from the data on the page before sorting) and verified with
`exactTexts(...)`, a *waiting* collection condition, so the check passes as soon as
the list re-renders and does not race with the DOM update. The page load strategy is
`eager`, so the browser does not wait for images/fonts. Each test gets a fresh browser
session.

**Structure.** UI: Page Objects expose actions (`loginAs`, `sortBy`, `addToCart`,
`openCart`) and return the next page or the elements to assert on; assertions stay in the
tests. API: `BookingClient` hides URLs/headers/timeouts, request and response bodies are
Java records (typed, immutable, compared with AssertJ's recursive comparison so that any
mismatching field is named in the failure message). Test data is generated per test with
a random suffix; tests are independent and do not rely on existing server data.
Configuration is in one place (`TestConfig`).

**Assumptions and limitations.**
- Both systems are public shared services. Restful Booker data is shared and reset
  periodically, and the Heroku app can be slow on a cold start (hence the 30 s HTTP timeout).
- `GET /booking/{id}` needs an `Accept: application/json` header, otherwise the service
  returns 418. The client sets it.
- Booking dates are compared as the ISO strings the API returns.
- Only Chrome is supported. The Dockerfile installs Chromium from Debian packages, so the
  same image works on amd64 and arm64.
- The Maven/Docker build needs internet access to download dependencies.
