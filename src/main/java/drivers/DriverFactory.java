package drivers;

import config.TestConfig;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.net.URL;

/** Cria e gere a sessão Android utilizada pelo cenário Cucumber atual. */
public final class DriverFactory {
    private static AndroidDriver driver;

    private DriverFactory() {
    }

    public static synchronized AndroidDriver getDriver() {
        if (driver == null) {
            createDriver();
        }
        return driver;
    }

    /** Permite aos hooks recolher evidências sem criar uma nova sessão. */
    public static synchronized AndroidDriver peekDriver() {
        return driver;
    }

    private static void createDriver() {
        System.out.println("A iniciar sessão Appium em " + TestConfig.APPIUM_URL
                + " | dispositivo=" + TestConfig.DEVICE_NAME
                + " | app=" + TestConfig.APP_PACKAGE
                + " | noReset=" + TestConfig.NO_RESET);

        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName(TestConfig.PLATFORM_NAME)
                .setDeviceName(TestConfig.DEVICE_NAME)
                .setAutomationName("UIAutomator2")
                .setAppPackage(TestConfig.APP_PACKAGE)
                .setAppActivity(TestConfig.APP_ACTIVITY)
                .setNoReset(TestConfig.NO_RESET)
                .setAutoGrantPermissions(TestConfig.AUTO_GRANT_PERMISSIONS)
                .setAppWaitForLaunch(true);
        if (!TestConfig.DEVICE_UDID.isBlank()) {
            options.setUdid(TestConfig.DEVICE_UDID);
        }

        try {
            driver = new AndroidDriver(new URL(TestConfig.APPIUM_URL), options);
            System.out.println("Sessão Appium iniciada. Session ID: " + driver.getSessionId());
        } catch (Exception exception) {
            driver = null;
            throw new IllegalStateException(
                    "Não foi possível iniciar o Appium Driver. Confirme APPIUM_URL, dispositivo, "
                            + "Appium/UiAutomator2 e APP_PACKAGE/APP_ACTIVITY.",
                    exception);
        }
    }

    public static synchronized void quitDriver() {
        if (driver == null) {
            return;
        }
        try {
            driver.quit();
        } finally {
            driver = null;
        }
    }
}
