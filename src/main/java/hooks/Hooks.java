package hooks;

import drivers.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import utils.ScenarioContext;

public class Hooks {

    @Before
    public void iniciarSessao(Scenario scenario) {
        System.out.println("========== INICIANDO: " + scenario.getName() + " ==========");
        ScenarioContext.reset();
        if (DriverFactory.getDriver() == null) {
            throw new IllegalStateException("Não foi possível inicializar o AppiumDriver.");
        }
    }

    @After
    public void finalizarSessao(Scenario scenario) {
        try {
            if (scenario.isFailed() && DriverFactory.hasDriver()) {
                byte[] print = DriverFactory.getDriver().getScreenshotAs(OutputType.BYTES);
                scenario.attach(print, "image/png", "Screenshot da falha");
                // XML da tela: permite descobrir o locator real de qualquer elemento sem reabrir o Inspector
                String xml = DriverFactory.getDriver().getPageSource();
                scenario.attach(xml.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                        "text/xml", "Page source da falha");
            }
        } catch (Exception e) {
            System.err.println("Não foi possível capturar screenshot: " + e.getMessage());
        } finally {
            DriverFactory.quitDriver();
            ScenarioContext.reset();
            System.out.println("========== FINALIZADO: " + scenario.getStatus() + " ==========");
        }
    }
}
