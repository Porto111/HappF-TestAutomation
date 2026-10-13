package utils;

import config.TestConfig;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;

public class WaitUtils {
    private final AppiumDriver driver;

    public WaitUtils(AppiumDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("AppiumDriver não pode ser null no WaitUtils");
        }
        this.driver = driver;
    }

    private FluentWait<AppiumDriver> getWait(long seconds) {
        return new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(seconds))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
    }

    public WebElement waitForVisible(By locator) {
        return waitForVisible(locator, TestConfig.DEFAULT_TIMEOUT);
    }

    public WebElement waitForVisible(By locator, long seconds) {
        try {
            return getWait(seconds).until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            throw new TimeoutException("Elemento não ficou visível em " + seconds + "s: " + locator, e);
        }
    }

    public void waitForClickable(By locator) {
        try {
            getWait(TestConfig.DEFAULT_TIMEOUT).until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            throw new TimeoutException("Elemento não ficou clicável dentro do timeout: " + locator, e);
        }
    }

    /** Retorna true se o elemento ficar visível dentro do tempo informado (nunca lança exceção). */
    public boolean isVisibleWithin(By locator, long seconds) {
        try {
            waitForVisible(locator, seconds);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Retorna true se o elemento sumir (ou já não existir) dentro do tempo informado. */
    public boolean waitForInvisible(By locator, long seconds) {
        try {
            return getWait(seconds).until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            return false;
        }
    }
}
