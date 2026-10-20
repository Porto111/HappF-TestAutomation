package pages;

import config.TestConfig;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;

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

    private final By tituloLogin = AppiumBy.androidUIAutomator(
            "new UiSelector().descriptionContains(\"Faça seu login para acessar\")");
    private final By campoEmail = AppiumBy.androidUIAutomator(
            "new UiSelector().className(\"android.widget.EditText\").instance(0)");
    private final By campoSenha = AppiumBy.androidUIAutomator(
            "new UiSelector().className(\"android.widget.EditText\").instance(1)");
    private final By btnEntrar = AppiumBy.accessibilityId("Entrar");
    private final By btnNaoTemCadastro = AppiumBy.accessibilityId("Não tem cadastro? Cadastrar");
    private final By esqueceuSenhaLink = AppiumBy.accessibilityId("Esqueci a senha");
    private final By btnCadastrar = AppiumBy.accessibilityId("Quero me cadastrar");
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

    /** Espera o resultado da tentativa de login (tela de login some ou aparece mensagem de erro). */
    public void aguardarLogin() {
        long limite = System.currentTimeMillis() + 15_000;
        while (System.currentTimeMillis() < limite) {
            if (driver.findElements(btnEntrar).isEmpty() || existeMensagemAgora("Erro ao efetuar")
                    || existeMensagemAgora("inválid")) {
                return;
            }
            pause(500);
        }
    }

    /** Login bem-sucedido = botão "Entrar" desapareceu. */
    public boolean loginBemSucedido() {
        return wait.waitForInvisible(btnEntrar, TestConfig.DEFAULT_TIMEOUT);
    }

    public boolean permaneceNaTelaDeLogin() {
        return isVisible(btnEntrar, TestConfig.SHORT_TIMEOUT);
    }

    /** Mensagem (content-desc ou text) contendo o trecho, aguardando alguns segundos. */
    public boolean existeMensagem(String trecho) {
        long limite = System.currentTimeMillis() + 5_000;
        while (System.currentTimeMillis() < limite) {
            if (existeMensagemAgora(trecho)) {
                return true;
            }
            pause(500);
        }
        return false;
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
