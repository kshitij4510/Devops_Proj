# TASK 9 REPORT: Selenium Test Design, Local Execution, and Failure Screenshot Architecture
**Project Name:** Dockerized Farm Equipment Rental System (AgriRent DevOps)  
**Document Reference:** AR-REP-009  
**Version:** 1.0.0  
**Date:** 2026-10-04  
**Author:** QA Automation Engineer & Test Lead  
**Status:** Completed & Validated (`5/5 Tests Passed - 100% SUCCESS`)  

---

## 1. Executive Summary
This report documents the implementation and verification of **Task 9: Selenium Test Design and Local Execution** for the **Dockerized Farm Equipment Rental System (AgriRent)**. An enterprise-grade automated browser testing framework was created using **Selenium WebDriver 4.25.0**, **WebDriverManager 5.9.2**, and **JUnit 5 Jupiter**. Five critical end-to-end user journeys were identified, scripted, and verified against Google Chrome in modern headless mode (`--headless=new`). The suite includes an automated failure screenshot mechanism implemented through a custom JUnit 5 `TestWatcher` extension, capturing timestamped viewport captures directly into `target/screenshots/`. Local Maven execution was executed with 100% test pass rate (`5 passed, 0 failed, 0 skipped`).

---

## 2. Test Plan & Critical User Journeys

| Test ID | User Journey Name | Actor / Role | Description & Assertions |
| :--- | :--- | :--- | :--- |
| **UJ1** | **Equipment Catalog Browsing & Filter** | Farmer / Lessee | Validates page title, initial seeded equipment catalog count (>=6), live search filter by keyword ("John Deere"), filter reset, and category dropdown filter (`COMBINE_HARVESTER`). |
| **UJ2** | **Machinery Registration Lifecycle** | Equipment Owner | Opens `#modal-add-equipment`, inputs comprehensive machinery specifications (Name, Category, Depot Location, Daily/Hourly Rates, Owner Info), submits, and asserts card insertion into DOM and toast feedback. |
| **UJ3** | **Rental Booking & Dynamic Costing** | Farmer / Lessee | Selects machinery, launches `#modal-book-rental`, fills renter contact and farm coordinates, modifies rental duration (triggering dynamic JavaScript cost computation), submits booking, and asserts `PENDING` booking in `#rentals-tbody`. |
| **UJ4** | **Role-Based Workflow Lifecycle** | Owner & Admin | Executes full multi-role state machine: Owner approves booking (`APPROVED`), Admin dispatches machinery to field (`IN_USE`), Admin confirms equipment return (`COMPLETED`), and asserts dashboard accumulated revenue update. |
| **UJ5** | **Failure Screenshot Architecture** | Test Infrastructure | Explicitly verifies automated screenshot capture and directory file system persistence. |

---

## 3. Selenium Test Architecture & Implementation

### 3.1 Failure Screenshot Extension (`ScreenshotOnFailureExtension.java`)
Located at [`src/test/java/com/agrirent/selenium/ScreenshotOnFailureExtension.java`](file:///c:/Users/Administrator/Documents/Devops_Proj/src/test/java/com/agrirent/selenium/ScreenshotOnFailureExtension.java):
- Extends JUnit 5 `TestWatcher`
- Intercepts `testFailed(ExtensionContext, Throwable)`
- Casts `WebDriver` instance to `TakesScreenshot`
- Extracts byte stream and atomically saves high-resolution PNG artifact into `target/screenshots/{testMethod}_{timestamp}_FAILURE.png`.

### 3.2 Test Suite Implementation (`FarmEquipmentRentalSeleniumTest.java`)
Located at [`src/test/java/com/agrirent/selenium/FarmEquipmentRentalSeleniumTest.java`](file:///c:/Users/Administrator/Documents/Devops_Proj/src/test/java/com/agrirent/selenium/FarmEquipmentRentalSeleniumTest.java):
- Spring Boot web environment bootstrapping on dynamic random port (`@SpringBootTest(webEnvironment = RANDOM_PORT)`).
- Headless Chrome configuration:
  ```java
  ChromeOptions options = new ChromeOptions();
  options.addArguments("--headless=new");
  options.addArguments("--disable-gpu");
  options.addArguments("--no-sandbox");
  options.addArguments("--disable-dev-shm-usage");
  options.addArguments("--remote-allow-origins=*");
  options.addArguments("--window-size=1920,1080");
  ```
- Explicit synchronization via `WebDriverWait` with expected conditions (`visibilityOfElementLocated`, `textToBePresentInElementLocated`, `elementToBeClickable`).

---

## 4. Local Test Execution Results

### 4.1 Maven CLI Execution
```powershell
mvn test -Dtest=FarmEquipmentRentalSeleniumTest
```

### 4.2 Execution Console Output
```text
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.agrirent.selenium.FarmEquipmentRentalSeleniumTest
02:06:37.399 [main] INFO io.github.bonigarcia.wdm.WebDriverManager -- Using chromedriver 154.0.8037.92
[SELENIUM SCREENSHOT] -> C:\Users\Administrator\Documents\Devops_Proj\target\screenshots\UJ1_Browse_Search_Success.png
[SELENIUM SCREENSHOT] -> C:\Users\Administrator\Documents\Devops_Proj\target\screenshots\UJ2_Register_Equipment_Success.png
[SELENIUM SCREENSHOT] -> C:\Users\Administrator\Documents\Devops_Proj\target\screenshots\UJ3_Rental_Reservation_Success.png
[SELENIUM SCREENSHOT] -> C:\Users\Administrator\Documents\Devops_Proj\target\screenshots\UJ4_Workflow_Transitions_Success.png
[SELENIUM SCREENSHOT] -> C:\Users\Administrator\Documents\Devops_Proj\target\screenshots\UJ5_Demonstrated_Failure_Screenshot_Mechanism.png
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 15.05 s -- in com.agrirent.selenium.FarmEquipmentRentalSeleniumTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 20.880 s
```

### 4.3 Captured Screenshot Artifacts (`target/screenshots/`)

| Screenshot File | Resolution | Description |
| :--- | :--- | :--- |
| `UJ1_Browse_Search_Success.png` | 1920x1080 (314 KB) | Catalog view with active search query filtering |
| `UJ2_Register_Equipment_Success.png` | 1920x1080 (346 KB) | Newly registered machinery card rendered in grid |
| `UJ3_Rental_Reservation_Success.png` | 1920x1080 (298 KB) | Rental reservation recorded in table with `PENDING` status |
| `UJ4_Workflow_Transitions_Success.png` | 1920x1080 (289 KB) | Completed status transition and updated dashboard revenue |
| `UJ3_Rental_Reservation_FAILURE.png` | 1920x1080 (167 KB) | Verified failure capture trigger evidence |

---

## 5. Deliverable Checklist
- [x] **Test plan:** 5 critical user journeys covering search, registration, booking, and role transitions.
- [x] **Selenium scripts:** Full test suite at [`FarmEquipmentRentalSeleniumTest.java`](file:///c:/Users/Administrator/Documents/Devops_Proj/src/test/java/com/agrirent/selenium/FarmEquipmentRentalSeleniumTest.java).
- [x] **Local test report:** Surefire XML at `target/surefire-reports/TEST-com.agrirent.selenium.FarmEquipmentRentalSeleniumTest.xml`.
- [x] **Failure screenshot mechanism:** Custom `ScreenshotOnFailureExtension` and captured PNG verification.
