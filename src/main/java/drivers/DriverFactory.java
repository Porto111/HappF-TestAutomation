package drivers;

import config.TestConfig;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class DriverFactory {

    private static AndroidDriver driver;

    public static AndroidDriver getDriver() {
        if (driver == null) {
            createDriver();
        }
        return driver;
    }

    public static boolean hasDriver() {
        return driver != null;
    }

    private static void createDriver() {
        System.out.println("INICIANDO SESSÃO APPIUM -> " + TestConfig.APPIUM_URL
                + " | device=" + TestConfig.DEVICE_NAME
                + " | package=" + TestConfig.APP_PACKAGE);

        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName(TestConfig.DEVICE_NAME)
                .setPlatformName(TestConfig.PLATFORM_NAME)
                .setAutomationName("UiAutomator2")
                .setAppPackage(TestConfig.APP_PACKAGE)
                .setAppActivity(TestConfig.APP_ACTIVITY)
                // Apps com splash/trampolim podem trocar de Activity logo após abrir
                .setAppWaitActivity("*")
                // Antes estava fixo em true, ignorando TestConfig.NO_RESET:
                // o app mantinha a sessão logada e o fluxo de cadastro nunca partia do zero.
                .setNoReset(TestConfig.NO_RESET)
                .setAutoGrantPermissions(TestConfig.AUTO_GRANT_PERMISSIONS)
                .setAppWaitForLaunch(true)
                .setNewCommandTimeout(Duration.ofSeconds(120));

        if (!TestConfig.DEVICE_UDID.isBlank()) {
            options.setUdid(TestConfig.DEVICE_UDID);
        }

        try {
            driver = new AndroidDriver(new URL(TestConfig.APPIUM_URL), options);
            System.out.println("APPIUM DRIVER CRIADO - Session ID: " + driver.getSessionId());
        } catch (MalformedURLException e) {
            throw new IllegalStateException("URL do Appium inválida: " + TestConfig.APPIUM_URL, e);
        } catch (Exception e) {
            throw new IllegalStateException("Não foi possível iniciar o Appium Driver", e);
        }
    }

    public static void quitDriver() {
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                driver = null;
            }
        }
    }
}
