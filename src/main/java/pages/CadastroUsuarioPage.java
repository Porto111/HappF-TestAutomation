package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.List;

/** Page Object do fluxo de cadastro de utilizador. */
public class CadastroUsuarioPage extends LoginPage {
    private final By btnConcluirCadastro = AppiumBy.accessibilityId("Quero me cadastrar");
    private final By telaCadastro = By.xpath(
            "//android.view.View[@content-desc=\"Faça seu cadastro para acessar\"]");
    private final By telaInicial = By.xpath(
            "//android.widget.ImageView[contains(@content-desc, 'Faça seu login para acessar')]");

    public CadastroUsuarioPage(AppiumDriver driver) {
        super(driver);
    }

    public void clicarNaoTemCadastro() {
        click(btnNaoTemCad);
    }

    public void clicarJaTenhoCadastro() {
        hideKeyboard();
        try {
            click(btnJatenhoCad);
        } catch (TimeoutException firstLocatorFailed) {
            By textFallback = AppiumBy.androidUIAutomator(
                    "new UiSelector().textContains(\"Já tenho cadastro\")");
            try {
                click(textFallback);
            } catch (TimeoutException textLocatorFailed) {
                By descriptionFallback = AppiumBy.androidUIAutomator(
                        "new UiSelector().descriptionContains(\"Já tenho cadastro\")");
                click(descriptionFallback);
            }
        }
    }

    public void preencherNome(String nome) {
        sendKeys(newUserName, nome);
    }

    public void preencherEmailCadastro(String email) {
        sendKeys(newUserEmail, email);
    }

    public void preencherCelular(String celular) {
        sendKeys(newUserCelular, celular);
    }

    public void preencherSenhaCadastro(String senha) {
        sendKeys(newUserSenha, senha);
    }

    public void preencherConfirmacaoSenha(String senha) {
        sendKeys(newUserConfirSenha, senha);
    }

    public String obterNome() {
        return obterValorCampo(newUserName);
    }

    public String obterEmail() {
        return obterValorCampo(newUserEmail);
    }

    public String obterCelular() {
        return obterValorCampo(newUserCelular);
    }

    public String obterSenha() {
        return obterValorCampo(newUserSenha);
    }

    public String obterConfirmacaoSenha() {
        return obterValorCampo(newUserConfirSenha);
    }

    public boolean nomeEstaVisivel() {
        return isVisible(newUserName);
    }

    public boolean emailEstaVisivel() {
        return isVisible(newUserEmail);
    }

    public boolean celularEstaVisivel() {
        return isVisible(newUserCelular);
    }

    public boolean senhaEstaVisivel() {
        return isVisible(newUserSenha);
    }

    public boolean confirmacaoSenhaEstaVisivel() {
        return isVisible(newUserConfirSenha);
    }

    public void limparTodosOsCampos() {
        clearField(newUserName);
        clearField(newUserEmail);
        clearField(newUserCelular);
        clearField(newUserSenha);
        clearField(newUserConfirSenha);
    }

    public void limparCampoNome() {
        clearField(newUserName);
    }

    public void limparCampoEmail() {
        clearField(newUserEmail);
    }

    public void limparCampoCelular() {
        clearField(newUserCelular);
    }

    public void limparCampoSenha() {
        clearField(newUserSenha);
    }

    public void limparCampoConfirmacaoSenha() {
        clearField(newUserConfirSenha);
    }

    public boolean botaoCadastroInicialEstaVisivel() {
        return isVisible(btnNaoTemCad);
    }

    public boolean botaoCadastroInicialEstaHabilitado() {
        return isEnabled(btnNaoTemCad);
    }

    public boolean botaoQueroMeCadastrarEstaVisivel() {
        return isVisible(btnConcluirCadastro);
    }

    public boolean botaoQueroMeCadastrarEstaHabilitado() {
        return isEnabled(btnConcluirCadastro);
    }

    public boolean botaoJaTenhoCadastroEstaVisivel() {
        return isVisible(btnJatenhoCad);
    }

    public boolean botaoJaTenhoCadastroEstaHabilitado() {
        return isEnabled(btnJatenhoCad);
    }

    public void queroMeCadastrar() {
        aceitarTermosSeNecessario();
        hideKeyboard();
        click(btnConcluirCadastro);
    }

    /**
     * Algumas versões da app apresentam um checkbox de termos e outras não.
     * A ausência do checkbox não deve impedir o submit; quando ele existe,
     * deve ser marcado antes de clicar no botão de cadastro.
     */
    private void aceitarTermosSeNecessario() {
        By checkbox = AppiumBy.androidUIAutomator(
                "new UiSelector().className(\"android.widget.CheckBox\")"
                        + ".descriptionContains(\"Termos\")");
        List<WebElement> elementos = driver.findElements(checkbox);
        if (elementos.isEmpty()) {
            elementos = driver.findElements(AppiumBy.androidUIAutomator(
                    "new UiSelector().className(\"android.widget.CheckBox\")"));
        }
        for (WebElement elemento : elementos) {
            if (elemento.isDisplayed() && !elemento.isSelected() && elemento.isEnabled()) {
                elemento.click();
                return;
            }
        }
    }

    public void validarTelaInicial() {
        wait.waitForVisible(telaInicial);
    }

    public boolean existeMensagemDeErro() {
        return existeMensagem("obrigatório")
                || existeMensagem("inválido")
                || existeMensagem("não são iguais");
    }

    public boolean existeMensagem(String texto) {
        String escaped = texto.replace("\\", "\\\\").replace("\"", "\\\"");
        By locator = AppiumBy.androidUIAutomator(
                "new UiSelector().textContains(\"" + escaped + "\")");
        return !driver.findElements(locator).isEmpty();
    }

    public boolean telaDeLoginEstaApresentada() {
        return !driver.findElements(btnEntrar).isEmpty()
                || !driver.findElements(emailFieldForLogin()).isEmpty();
    }

    public boolean telaDeCadastroEstaApresentada() {
        try {
            wait.waitForVisible(telaCadastro);
            return true;
        } catch (TimeoutException exception) {
            return false;
        }
    }

    public boolean senhaEstaMascarada(String campo) {
        By locator = switch (campo) {
            case "Senha" -> newUserSenha;
            case "Confirmar senha" -> newUserConfirSenha;
            default -> throw new IllegalArgumentException("Campo de senha não mapeado: " + campo);
        };
        WebElement element = wait.waitForVisible(locator);
        return "true".equalsIgnoreCase(element.getAttribute("password"));
    }

    public void campoNomeClick() {
        click(newUserName);
    }

    private By emailFieldForLogin() {
        return AppiumBy.androidUIAutomator(
                "new UiSelector().className(\"android.widget.EditText\").instance(0)");
    }

    private String obterValorCampo(By locator) {
        WebElement element = wait.waitForVisible(locator);
        String value = element.getAttribute("text");
        if (value != null && !value.isBlank()) {
            return value;
        }
        value = element.getText();
        return value == null ? "" : value;
    }


}
