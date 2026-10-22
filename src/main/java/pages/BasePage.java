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



    protected void click(By locator) {
        wait.waitForClickable(locator);
        driver.findElement(locator).click();
    }


    protected void sendKeys(By locator, String text) {
        wait.waitForVisible(locator);
        WebElement element = driver.findElement(locator);
        element.clear();
        element.sendKeys(text);
    }


    protected void typeText(By locator, String text) {
        wait.waitForVisible(locator);
        driver.findElement(locator).sendKeys(text);
    }


    protected void clearField(By locator) {
        wait.waitForVisible(locator);
        driver.findElement(locator).clear();
    }

    // ==================== ELEMENT QUERIES ====================


    protected boolean isVisible(By locator) {
        return wait.isVisibleWithin(locator, config.TestConfig.DEFAULT_TIMEOUT);
    }


    protected boolean isVisible(By locator, long seconds) {
        return wait.isVisibleWithin(locator, seconds);
    }


    protected boolean exists(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    protected boolean isEnabled(By locator) {
        try {
            return driver.findElement(locator).isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Checagem de elementos selecionados/flegados (checkboxes, radio buttons)
     */
    protected boolean isSelected(By locator) {
        try {
            return driver.findElement(locator).isSelected();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // ==================== TEXT OPERATIONS ====================


    protected String getText(By locator) {
        wait.waitForVisible(locator);
        return driver.findElement(locator).getText();
    }


    protected String getAttribute(By locator, String attribute) {
        wait.waitForVisible(locator);
        return driver.findElement(locator).getAttribute(attribute);
    }

    /**
     * Checagem de texto específico
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

    protected List<WebElement> getElements(By locator) {
        wait.waitForVisible(locator);
        return driver.findElements(locator);
    }


    protected int getElementCount(By locator) {

        return driver.findElements(locator).size();
    }


    protected WebElement findElementByText(By listLocator, String text) {
        List<WebElement> elements = getElements(listLocator);
        return elements.stream()
                .filter(e -> e.getText().equals(text))
                .findFirst()
                .orElse(null);
    }

    // ==================== TOUCH ACTIONS - COORDINATES ====================

    /**
     * Toque através de coordenadas
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
     * Toque por ponto ordenado
     */
    protected void tap(Point p) {
        tap(p.getX(), p.getY());
    }

    /**
     * Toque usando uma porcentagem da tela (independente do dispositivo)
     * @param xPercent 0.0 to 1.0 (e.g., 0.5 = center)
     * @param yPercent 0.0 to 1.0 (e.g., 0.5 = center)
     */
    public void tapByPercentage(double xPercent, double yPercent) {
        Dimension size = driver.manage().window().getSize();
        int x = (int) (size.width * xPercent);
        int y = (int) (size.height * yPercent);
        tap(x, y);
    }


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


    public void scrollDown() {
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.8);
        int endY = (int) (size.height * 0.2);
        swipe(startX, startY, startX, endY, Duration.ofMillis(1000));
    }


    public void scrollUp() {
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.2);
        int endY = (int) (size.height * 0.8);
        swipe(startX, startY, startX, endY, Duration.ofMillis(1000));
    }


    protected void scrollToElement(By locator, int maxScrolls) {
        for (int i = 0; i < maxScrolls; i++) {
            if (isVisible(locator, config.TestConfig.SHORT_TIMEOUT)) {
                return;
            }
            scrollDown();
        }
        throw new RuntimeException("Element not found after " + maxScrolls + " scrolls: " + locator);
    }


    public void scrollRight() {
        Dimension size = driver.manage().window().getSize();
        int startX = (int) (size.width * 0.8);
        int endX = (int) (size.width * 0.2);
        int y = size.height / 2;
        swipe(startX, y, endX, y, Duration.ofMillis(800));
    }


    public void scrollLeft() {
        Dimension size = driver.manage().window().getSize();
        int startX = (int) (size.width * 0.2);
        int endX = (int) (size.width * 0.8);
        int y = size.height / 2;
        swipe(startX, y, endX, y, Duration.ofMillis(800));
    }


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


    public void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    protected void hideKeyboard() {
        try {
            driver.executeScript(
                    "mobile: pressKey",
                    java.util.Map.of("keycode", 4)
            );
        }  catch (Exception ignored) {
            // Comando indisponível: segue o fluxo sem esconder o teclado
        }
    }


    protected Dimension getScreenSize() {

        return driver.manage().window().getSize();
    }

    protected byte[] takeScreenshot() {

        return driver.getScreenshotAs(org.openqa.selenium.OutputType.BYTES);
    }

    protected void navigateBack() {

        driver.navigate().back();
    }
}