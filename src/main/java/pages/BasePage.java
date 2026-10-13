package pages;

import drivers.DriverFactory;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import utils.WaitUtils;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class BasePage {

    protected final AppiumDriver driver;
    protected final WaitUtils wait;

    public BasePage(AppiumDriver driver) { if (driver == null) {
        throw new IllegalArgumentException( "AppiumDriver não pode ser null" ); }

        this.driver = driver; this.wait = new WaitUtils(driver);
    }



    /**
     * Click on element using locator
     */
    protected void click(By locator) {
        wait.waitForClickable(locator);
        driver.findElement(locator).click();
    }

    /**
     * Type text into element (clears first)
     */
    protected void sendKeys(By locator, String text) {
        wait.waitForVisible(locator);
        WebElement element = driver.findElement(locator);
        element.clear();
        element.sendKeys(text);
    }

    /**
     * Type text without clearing first
     */
    protected void typeText(By locator, String text) {
        wait.waitForVisible(locator);
        driver.findElement(locator).sendKeys(text);
    }

    /**
     * Clear text field
     */
    protected void clearField(By locator) {
        wait.waitForVisible(locator);
        driver.findElement(locator).clear();
    }

    // ==================== ELEMENT QUERIES ====================

    /**
     * Check if element is visible
     */
    protected boolean isVisible(By locator) {
        return wait.isVisibleWithin(locator, config.TestConfig.DEFAULT_TIMEOUT);
    }

    /**
     * Check if element becomes visible within the given seconds (use for negative checks / scroll loops)
     */
    protected boolean isVisible(By locator, long seconds) {
        return wait.isVisibleWithin(locator, seconds);
    }

    /**
     * Check if element exists in DOM (may not be visible)
     */
    protected boolean exists(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Check if element is enabled
     */
    protected boolean isEnabled(By locator) {
        try {
            return driver.findElement(locator).isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Check if element is selected (checkboxes, radio buttons)
     */
    protected boolean isSelected(By locator) {
        try {
            return driver.findElement(locator).isSelected();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // ==================== TEXT OPERATIONS ====================

    /**
     * Get text from element
     */
    protected String getText(By locator) {
        wait.waitForVisible(locator);
        return driver.findElement(locator).getText();
    }

    /**
     * Get attribute value from element
     */
    protected String getAttribute(By locator, String attribute) {
        wait.waitForVisible(locator);
        return driver.findElement(locator).getAttribute(attribute);
    }

    /**
     * Check if element contains specific text
     */
    protected boolean containsText(By locator, String expectedText) {
        try {
            String actualText = getText(locator);
            return actualText.contains(expectedText);
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== LIST OPERATIONS ====================

    /**
     * Get all elements matching locator
     */
    protected List<WebElement> getElements(By locator) {
        wait.waitForVisible(locator);
        return driver.findElements(locator);
    }

    /**
     * Get count of elements
     */
    protected int getElementCount(By locator) {
        return driver.findElements(locator).size();
    }

    /**
     * Find element by text in a list
     */
    protected WebElement findElementByText(By listLocator, String text) {
        List<WebElement> elements = getElements(listLocator);
        return elements.stream()
                .filter(e -> e.getText().equals(text))
                .findFirst()
                .orElse(null);
    }

    // ==================== TOUCH ACTIONS - COORDINATES ====================

    /**
     * Tap at specific coordinates
     */
    public void tap(int x, int y) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence seq = new Sequence(finger, 1);
        seq.addAction(finger.createPointerMove(Duration.ZERO,
                PointerInput.Origin.viewport(), x, y));
        seq.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        seq.addAction(new Pause(finger, Duration.ofMillis(100)));
        seq.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(seq));
    }

    /**
     * Tap at Point
     */
    protected void tap(Point p) {
        tap(p.getX(), p.getY());
    }

    /**
     * Tap using percentage of screen (device-independent)
     * @param xPercent 0.0 to 1.0 (e.g., 0.5 = center)
     * @param yPercent 0.0 to 1.0 (e.g., 0.5 = center)
     */
    public void tapByPercentage(double xPercent, double yPercent) {
        Dimension size = driver.manage().window().getSize();
        int x = (int) (size.width * xPercent);
        int y = (int) (size.height * yPercent);
        tap(x, y);
    }

    /**
     * Long press on element
     */
    protected void longPress(By locator, Duration duration) {
        WebElement element = driver.findElement(locator);
        Point location = element.getLocation();
        Dimension size = element.getSize();
        int centerX = location.getX() + (size.getWidth() / 2);
        int centerY = location.getY() + (size.getHeight() / 2);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence longPress = new Sequence(finger, 1);
        longPress.addAction(finger.createPointerMove(Duration.ZERO,
                PointerInput.Origin.viewport(), centerX, centerY));
        longPress.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        longPress.addAction(new Pause(finger, duration));
        longPress.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(longPress));
    }

    // ==================== SCROLL ACTIONS ====================

    /**
     * Scroll down on screen
     */
    public void scrollDown() {
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.8);
        int endY = (int) (size.height * 0.2);
        swipe(startX, startY, startX, endY, Duration.ofMillis(1000));
    }

    /**
     * Scroll up on screen
     */
    public void scrollUp() {
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.2);
        int endY = (int) (size.height * 0.8);
        swipe(startX, startY, startX, endY, Duration.ofMillis(1000));
    }

    /**
     * Scroll to element until visible
     */
    protected void scrollToElement(By locator, int maxScrolls) {
        for (int i = 0; i < maxScrolls; i++) {
            if (isVisible(locator, config.TestConfig.SHORT_TIMEOUT)) {
                return;
            }
            scrollDown();
        }
        throw new RuntimeException("Element not found after " + maxScrolls + " scrolls: " + locator);
    }

    /**
     * Scroll horizontally (left to right)
     */
    public void scrollRight() {
        Dimension size = driver.manage().window().getSize();
        int startX = (int) (size.width * 0.8);
        int endX = (int) (size.width * 0.2);
        int y = size.height / 2;
        swipe(startX, y, endX, y, Duration.ofMillis(800));
    }

    /**
     * Scroll horizontally (right to left)
     */
    public void scrollLeft() {
        Dimension size = driver.manage().window().getSize();
        int startX = (int) (size.width * 0.2);
        int endX = (int) (size.width * 0.8);
        int y = size.height / 2;
        swipe(startX, y, endX, y, Duration.ofMillis(800));
    }

    /**
     * Generic swipe action
     */
    private void swipe(int startX, int startY, int endX, int endY, Duration duration) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ZERO,
                PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(duration,
                PointerInput.Origin.viewport(), endX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(swipe));
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Wait/pause execution
     */
    public void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Hide keyboard if visible
     */
    protected void hideKeyboard() {
        try {
            // Só esconde se o teclado estiver aberto: pressionar BACK sem teclado
            // navega para a tela anterior (ou fecha o app) e quebra o fluxo.
            Object shown = driver.executeScript("mobile: isKeyboardShown");
            if (Boolean.TRUE.equals(shown)) {
                driver.executeScript("mobile: hideKeyboard");
            }
        } catch (Exception ignored) {
            // Comando indisponível: segue o fluxo sem esconder o teclado
        }
    }

    /**
     * Get screen dimensions
     */
    protected Dimension getScreenSize() {
        return driver.manage().window().getSize();
    }

    /**
     * Take screenshot
     */
    protected byte[] takeScreenshot() {
        return driver.getScreenshotAs(org.openqa.selenium.OutputType.BYTES);
    }

    /**
     * Navigate back (Android back button)
     */
    protected void navigateBack() {
        driver.navigate().back();
    }
}