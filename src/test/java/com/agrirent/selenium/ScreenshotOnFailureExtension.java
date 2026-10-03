package com.agrirent.selenium;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * JUnit 5 extension that automatically captures browser screenshots
 * whenever a Selenium test assertion or step fails.
 */
public class ScreenshotOnFailureExtension implements TestWatcher {

    private static final String SCREENSHOT_DIR = "target/screenshots";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final WebDriver driver;

    public ScreenshotOnFailureExtension(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        if (driver instanceof TakesScreenshot takesScreenshot) {
            String testMethod = context.getTestMethod().map(m -> m.getName()).orElse("unknown_test");
            String timestamp = LocalDateTime.now().format(FORMATTER);
            String filename = String.format("%s_%s_FAILURE.png", testMethod, timestamp);

            try {
                Path dirPath = Paths.get(SCREENSHOT_DIR);
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                }

                File srcFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
                Path destPath = dirPath.resolve(filename);
                Files.copy(srcFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("[FAILURE SCREENSHOT CAPTURED] -> " + destPath.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("Failed to write failure screenshot: " + e.getMessage());
            }
        }
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        // No action needed on successful tests
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        // Optional handling for aborted tests
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        // Optional handling for disabled tests
    }
}
