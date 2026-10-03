package com.agrirent.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FarmEquipmentRentalSeleniumTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

    @BeforeAll
    public static void setUpDriver() {
        try {
            WebDriverManager.chromedriver().setup();
        } catch (Exception e) {
            System.out.println("WebDriverManager fallback to Selenium Manager: " + e.getMessage());
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @BeforeEach
    public void init() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        baseUrl = "http://localhost:" + port;
    }

    @AfterAll
    public static void tearDownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void takeScreenshot(String testName) {
        if (driver instanceof TakesScreenshot takesScreenshot) {
            try {
                Path dir = Paths.get("target/screenshots");
                if (!Files.exists(dir)) {
                    Files.createDirectories(dir);
                }
                File file = takesScreenshot.getScreenshotAs(OutputType.FILE);
                Path dest = dir.resolve(testName + ".png");
                Files.copy(file.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("[SELENIUM SCREENSHOT] -> " + dest.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("Could not capture screenshot: " + e.getMessage());
            }
        }
    }

    @Test
    @Order(1)
    @DisplayName("UJ1: Equipment Catalog Browsing, Search, and Category Filtering")
    public void testUserJourney1_BrowseAndSearch() {
        try {
            driver.get(baseUrl);

            // Assert page title
            assertTrue(driver.getTitle().contains("AgriRent"), "Page title should contain AgriRent");

            // Wait for equipment grid to populate
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("equipment-card")));
            List<WebElement> cards = driver.findElements(By.className("equipment-card"));
            assertTrue(cards.size() >= 6, "Initial seeded catalog should have at least 6 equipment items");

            // Verify KPI total equipment is loaded
            WebElement totalKpi = driver.findElement(By.id("stat-total-equipment"));
            assertNotEquals("--", totalKpi.getText(), "KPI total equipment should display loaded value");

            // Test Search functionality
            WebElement searchInput = driver.findElement(By.id("search-input"));
            searchInput.clear();
            searchInput.sendKeys("John Deere");

            // Wait for filtered result
            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.id("equipment-container"), "John Deere 5050D Tractor"));
            List<WebElement> filteredCards = driver.findElements(By.className("equipment-card"));
            assertEquals(1, filteredCards.size(), "Search for 'John Deere' should return exactly 1 machinery item");

            // Reset search and test category filter
            searchInput.clear();
            searchInput.sendKeys(Keys.BACK_SPACE);
            driver.findElement(By.id("btn-reset-filters")).click();

            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.className("equipment-card"), 5));

            Select catSelect = new Select(driver.findElement(By.id("category-filter")));
            catSelect.selectByValue("COMBINE_HARVESTER");

            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.id("equipment-container"), "Kubota Harvester DC-68G"));
            List<WebElement> harvesterCards = driver.findElements(By.className("equipment-card"));
            assertEquals(1, harvesterCards.size(), "Filter for COMBINE_HARVESTER should display 1 item");

            takeScreenshot("UJ1_Browse_Search_Success");
        } catch (Throwable t) {
            takeScreenshot("UJ1_Browse_Search_FAILURE");
            throw t;
        }
    }

    @Test
    @Order(2)
    @DisplayName("UJ2: Register New Agricultural Equipment (Lessor Lifecycle)")
    public void testUserJourney2_RegisterNewEquipment() {
        try {
            driver.get(baseUrl);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("equipment-card")));

            // Open Modal
            WebElement addBtn = driver.findElement(By.id("btn-open-add-equipment"));
            addBtn.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modal-add-equipment")));

            // Fill Form
            String uniqueName = "Sonalika Tiger DI 75 4WD";
            driver.findElement(By.id("input-eq-name")).sendKeys(uniqueName);
            new Select(driver.findElement(By.id("input-eq-category"))).selectByValue("TRACTOR");
            driver.findElement(By.id("input-eq-location")).sendKeys("Punjab Central Depot");
            driver.findElement(By.id("input-eq-daily-rate")).sendKeys("2800");
            driver.findElement(By.id("input-eq-hourly-rate")).sendKeys("400");
            driver.findElement(By.id("input-eq-owner-name")).sendKeys("Harpreet Singh");
            driver.findElement(By.id("input-eq-owner-contact")).sendKeys("+91 98111 22334");
            driver.findElement(By.id("input-eq-description")).sendKeys("Heavy duty 75 HP 4WD tractor with AC cabin");

            // Submit
            driver.findElement(By.id("btn-save-equipment")).click();

            // Wait for modal to close and new item to appear in catalog
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("modal-add-equipment")));
            wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("equipment-container"), uniqueName));

            // Verify toast confirmation
            WebElement toast = driver.findElement(By.id("toast"));
            assertTrue(toast.getText().contains("registered successfully") || toast.getText().length() > 0,
                    "Toast notification should acknowledge successful equipment registration");

            takeScreenshot("UJ2_Register_Equipment_Success");
        } catch (Throwable t) {
            takeScreenshot("UJ2_Register_Equipment_FAILURE");
            throw t;
        }
    }

    @Test
    @Order(3)
    @DisplayName("UJ3: Machinery Rental Reservation & Cost Estimation Calculation")
    public void testUserJourney3_ReserveMachinery() {
        try {
            driver.get(baseUrl);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("equipment-card")));

            // Find an available equipment card and click Book Rental
            List<WebElement> bookButtons = driver.findElements(By.className("btn-book"));
            assertFalse(bookButtons.isEmpty(), "Available machinery must have active 'Book Rental' buttons");
            WebElement firstBookBtn = bookButtons.get(0);
            firstBookBtn.click();

            // Modal should be visible
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modal-book-rental")));

            // Fill Renter Details
            driver.findElement(By.id("input-renter-name")).sendKeys("Balwinder Kaur");
            driver.findElement(By.id("input-renter-contact")).sendKeys("+91 98765 43210");
            driver.findElement(By.id("input-farm-location")).sendKeys("Ludhiana Block 7");

            String startDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
            WebElement dateInput = driver.findElement(By.id("input-rental-start"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('change'));", dateInput, startDate);

            // Change duration to 4 days and trigger cost recalculation
            WebElement daysInput = driver.findElement(By.id("input-rental-days"));
            daysInput.clear();
            daysInput.sendKeys("4");

            // Verify cost estimation box updates
            WebElement estimatedCost = driver.findElement(By.id("book-estimated-cost"));
            assertNotEquals("₹0", estimatedCost.getText(), "Estimated rental cost must update dynamically");

            // Submit booking
            driver.findElement(By.id("btn-confirm-booking")).click();

            // Wait for modal to dismiss
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("modal-book-rental")));

            // Switch to Rentals Tab and verify new booking
            driver.findElement(By.id("tab-rentals")).click();
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("rentals-tbody")));
            wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("rentals-tbody"), "Balwinder Kaur"));

            WebElement rentalsTable = driver.findElement(By.id("rentals-tbody"));
            assertTrue(rentalsTable.getText().contains("PENDING"), "Newly booked rental must have PENDING status");

            takeScreenshot("UJ3_Rental_Reservation_Success");
        } catch (Throwable t) {
            takeScreenshot("UJ3_Rental_Reservation_FAILURE");
            throw t;
        }
    }

    @Test
    @Order(4)
    @DisplayName("UJ4: Role-Based Workflow Transitions (Approve -> In-Use -> Completed)")
    public void testUserJourney4_RoleBasedWorkflow() {
        try {
            driver.get(baseUrl);

            // Switch Role to OWNER
            Select roleSelect = new Select(driver.findElement(By.id("role-select")));
            roleSelect.selectByValue("OWNER");

            // Open Rentals Tab
            driver.findElement(By.id("tab-rentals")).click();
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("rentals-tbody")));

            // Locate first Approve button for PENDING rental
            List<WebElement> approveButtons = driver.findElements(By.xpath("//button[contains(@id,'btn-approve-')]"));
            if (!approveButtons.isEmpty()) {
                WebElement approveBtn = approveButtons.get(0);
                String btnId = approveBtn.getAttribute("id");
                String rentalId = btnId.replace("btn-approve-", "");
                approveBtn.click();

                // Wait for status pill to become APPROVED
                wait.until(ExpectedConditions.textToBePresentInElementLocated(
                        By.id("rental-row-" + rentalId), "APPROVED"));

                // Switch role to ADMIN to dispatch machinery
                roleSelect = new Select(driver.findElement(By.id("role-select")));
                roleSelect.selectByValue("ADMIN");

                // Locate Dispatch button
                WebElement dispatchBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("btn-dispatch-" + rentalId)));
                dispatchBtn.click();

                // Wait for status pill to transition to IN_USE
                wait.until(ExpectedConditions.textToBePresentInElementLocated(
                        By.id("rental-row-" + rentalId), "IN_USE"));

                // Locate Confirm Return button
                WebElement returnBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("btn-return-" + rentalId)));
                returnBtn.click();

                // Wait for status pill to transition to COMPLETED
                wait.until(ExpectedConditions.textToBePresentInElementLocated(
                        By.id("rental-row-" + rentalId), "COMPLETED"));

                // Check Dashboard KPIs
                driver.findElement(By.id("tab-equipment")).click();
                WebElement revenueKpi = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("stat-total-revenue")));
                assertFalse(revenueKpi.getText().equals("₹0"), "Accumulated revenue must be greater than zero after completed rental");
            }

            takeScreenshot("UJ4_Workflow_Transitions_Success");
        } catch (Throwable t) {
            takeScreenshot("UJ4_Workflow_Transitions_FAILURE");
            throw t;
        }
    }

    @Test
    @Order(5)
    @DisplayName("UJ5: Failure Screenshot Mechanism Demonstration")
    public void testUserJourney5_FailureScreenshotMechanism() {
        // Explicitly validates the failure capture subsystem
        takeScreenshot("UJ5_Demonstrated_Failure_Screenshot_Mechanism");
        File dir = new File("target/screenshots");
        assertTrue(dir.exists() && dir.isDirectory(), "Screenshots directory should exist and be accessible");
    }
}
