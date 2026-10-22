package pages;

import config.TestConfig;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Tela de login do Happ Fé (Flutter: rótulos expostos em content-desc).
 * Textos confirmados no binário do app v2.0.0: "Faça seu login para acessar", "Entrar",
 * "Esqueceu a senha ?", "E-mail inválido.", "Erro ao efetuar login."
 */
public class LoginPage extends BasePage {

    public LoginPage(AppiumDriver driver) {
        super(driver);
    }

    // ==================== LOCATORS ====================

    By toast = By.className("android.widget.Toast");

    private final By tituloLogin = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"Faça seu login para acessar\")");
    private final By campoEmail = AppiumBy.androidUIAutomator(
            "new UiSelector().className(\"android.widget.EditText\").instance(0)");
    private final By campoSenha = AppiumBy.androidUIAutomator(
            "new UiSelector().className(\"android.widget.EditText\").instance(1)");
    private final By btnEntrar = AppiumBy.accessibilityId("Entrar");
    private final By btnNaoTemCadastro = AppiumBy.accessibilityId("Não tem cadastro? Cadastrar");
    private final By esqueceuSenhaLink = AppiumBy.accessibilityId("Esqueci a senha");
    private final By loadingSpinner = By.id(TestConfig.APP_PACKAGE + ":id/loading_spinner");
    private final By errorMessage = By.id(TestConfig.APP_PACKAGE + ":id/error_text");

    // ==================== ACTIONS ====================

    public void preencherEmail(String email) {
        click(campoEmail);
        sendKeys(campoEmail, email);
    }

    public void preencherSenha(String senha) {
        click(campoSenha);
        sendKeys(campoSenha, senha);
    }

    public void clicarBotaoEntrar() {
        hideKeyboard();
        click(btnEntrar);
    }

    public void clicarCadastrar() {
        click(btnNaoTemCadastro);
    }

    public void clicarEsqueciSenha() {
        click(esqueceuSenhaLink);
    }

    public void realizarLogin(String email, String senha) {
        preencherEmail(email);
        preencherSenha(senha);
        clicarBotaoEntrar();
    }

    // ==================== VALIDATIONS ====================

    public boolean estaCarregando() {
        return driver.findElements(loadingSpinner)
                .stream()
                .anyMatch(WebElement::isDisplayed);
    }


    /** Espera o resultado da tentativa de login (tela de login some ou aparece mensagem de erro). */
    public void aguardarLogin() {
        long deadline =
                System.nanoTime()
                        + java.time.Duration.ofSeconds(15).toNanos();

        while (estaCarregando() && System.nanoTime() < deadline) {
            pause(250);
        }
    }


    /** Login bem-sucedido = botão "Entrar" desapareceu. */
    public boolean loginBemSucedido() {
        return driver.findElements(btnEntrar).stream().noneMatch(WebElement::isDisplayed);
    }

    public boolean permaneceNaTelaDeLogin() {
        return isVisible(btnEntrar, TestConfig.SHORT_TIMEOUT);
    }

    public String obterMensagemErro() {
        List<WebElement> messages = driver.findElements(errorMessage);
        for (WebElement message : messages) {
            String text = textoDoElemento(message);
            if (!text.isBlank()) {
                return text;
            }
        }

        List<String> candidates = new ArrayList<>();
        candidates.addAll(textosVisiveis(By.className("android.widget.Toast")));
        candidates.addAll(textosVisiveis(By.className("android.widget.TextView")));
        candidates.addAll(textosVisiveis(AppiumBy.androidUIAutomator(
                "new UiSelector().descriptionContains(\"erro\")")));
        return candidates.stream()
                .filter(text -> !text.isBlank())
                .filter(this::pareceMensagemDeErro)
                .findFirst()
                .orElse("");
    }

    private boolean pareceMensagemDeErro(String texto) {
        String normalizado = texto.toLowerCase();
        return normalizado.contains("erro")
                || normalizado.contains("inválid")
                || normalizado.contains("invalid")
                || normalizado.contains("credencial")
                || normalizado.contains("obrigat")
                || normalizado.contains("preench");
    }

    private List<String> textosVisiveis(By locator) {
        return driver.findElements(locator).stream()
                .filter(WebElement::isDisplayed)
                .map(this::textoDoElemento)
                .filter(text -> !text.isBlank())
                .toList();
    }

    private String textoDoElemento(WebElement element) {
        String text = element.getText();
        if (text == null || text.isBlank()) {
            text = element.getAttribute("text");
        }
        return text == null ? "" : text.trim();
    }


    private boolean existeMensagemAgora(String trecho) {
        String t = trecho.replace("\"", "\\\"");
        return !driver.findElements(AppiumBy.androidUIAutomator(
                "new UiSelector().descriptionContains(\"" + t + "\")")).isEmpty()
                || !driver.findElements(AppiumBy.androidUIAutomator(
                "new UiSelector().textContains(\"" + t + "\")")).isEmpty();
    }

    /** Tela de login visível (checagem curta: não trava quando o usuário já está logado). */

    public boolean estaVisivel() {

        return  isVisible(btnEntrar) && isVisible(campoEmail);
    }

    public boolean telaDeLoginEstaApresentada(long segundos) {
        return isVisible(tituloLogin, segundos);
    }
}
