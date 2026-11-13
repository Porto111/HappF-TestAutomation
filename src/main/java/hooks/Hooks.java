package hooks;

import drivers.DriverFactory;
import io.appium.java_client.android.AndroidDriver;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import utils.ScenarioContext;

public class Hooks {

    @Before(order = 0)
    public void iniciarSessao() {
        ScenarioContext.reset();
        AndroidDriver driver = DriverFactory.getDriver();
        if (driver == null) {
            throw new IllegalStateException("Não foi possível inicializar o AppiumDriver.");
        }
    }

    @After(order = 100)
    public void finalizarSessao(Scenario scenario) {
        try {
            AndroidDriver driver = DriverFactory.peekDriver();
            if (scenario.isFailed() && driver != null) {
                try {
                    scenario.attach(
                            driver.getScreenshotAs(OutputType.BYTES),
                            "image/png",
                            "screenshot-falha-" + scenario.getName());
                    scenario.attach(driver.getPageSource(), "text/xml", "page-source-falha");
                } catch (Exception screenshotException) {
                    System.err.println("Não foi possível anexar screenshot: "
                            + screenshotException.getMessage());
                }
            }
        } finally {
            DriverFactory.quitDriver();
        }
    }

}
