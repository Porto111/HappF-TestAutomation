package pages;

import config.TestConfig;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import utils.Usuario;

/**
 * Page Object do fluxo de cadastro de novo usuário (tela de login -> tela de cadastro).
 *
 * Os textos do app são expostos via content-desc (e não via text), por isso as buscas
 * por mensagem consideram os dois atributos.
 */
public class CadastroUsuarioPage extends BasePage {

    // ==================== LOCATORS ====================

    private final By tituloLogin = descricaoContem("Faça seu login para acessar");
    private final By tituloCadastro = descricaoContem("Faça seu cadastro para acessar");

    private final By campoNome = campoTexto(0);
    private final By campoEmail = campoTexto(1);
    private final By campoCelular = campoTexto(2);
    private final By campoSenha = campoTexto(3);
    private final By campoConfirmarSenha = campoTexto(4);

    private final By btnNaoTemCadastro = By.xpath(
            "//android.widget.Button[@content-desc=\"Não tem cadastro? Cadastrar\"]");
    private final By btnJaTenhoCadastro = By.xpath(
            "//android.widget.Button[@content-desc=\"Já tenho cadastro. Fazer login\"]");
    private final By btnQueroMeCadastrar = By.xpath(
            "//android.widget.Button[@content-desc=\"Quero me cadastrar\"]");

    // "Li e concordo com Termos e Politicas" (texto confirmado no app). Checkbox nativo do Flutter.
    private final By checkboxTermos = By.xpath(
            "//android.widget.CheckBox[contains(@content-desc,'concordo') or contains(@content-desc,'Termos')]");
    private final By qualquerCheckbox = By.xpath("//android.widget.CheckBox");

    /** Resultado do envio do formulário de cadastro. */
    public enum ResultadoCadastro { SUCESSO, ERRO_VALIDACAO, EXIGE_CODIGO_SMS, TIMEOUT }

    public CadastroUsuarioPage(AppiumDriver driver) {
        super(driver);
    }

    // ==================== NAVEGAÇÃO ====================

    /** Espera a tela inicial (login) carregar, no lugar do antigo pause(15000). */
    public void validarTelaInicial() {
        wait.waitForVisible(tituloLogin, TestConfig.DEFAULT_TIMEOUT);
    }

    public void clicarNaoTemCadastro() {
        click(btnNaoTemCadastro);
    }

    public void clicarJaTenhoCadastro() {
        click(btnJaTenhoCadastro);
    }

    /** Garante que a tela de cadastro está aberta, sem clicar de novo se já estiver nela. */
    public void abrirTelaDeCadastro() {
        if (!isVisible(tituloCadastro, TestConfig.SHORT_TIMEOUT)) {
            clicarNaoTemCadastro();
        }
    }

    // ==================== PREENCHIMENTO ====================

    public void preencherNome(String valor) {
        preencher(campoNome, valor);
    }

    public void preencherEmail(String valor) {
        preencher(campoEmail, valor);
    }

    public void preencherCelular(String valor) {
        preencher(campoCelular, valor);
    }

    public void preencherSenha(String valor) {
        preencher(campoSenha, valor);
    }

    public void preencherConfirmacaoSenha(String valor) {
        preencher(campoConfirmarSenha, valor);
    }

    public void preencherCadastroCompleto(Usuario u) {
        preencherNome(u.nome());
        preencherEmail(u.email());
        preencherCelular(u.celular());
        preencherSenha(u.senha());
        preencherConfirmacaoSenha(u.senha());
    }

    public void limparTodosOsCampos() {
        limparCampoNome();
        limparCampoEmail();
        limparCampoCelular();
        limparCampoSenha();
        limparCampoConfirmacaoSenha();
    }

    public void limparCampoNome() { limpar(campoNome); }
    public void limparCampoEmail() { limpar(campoEmail); }
    public void limparCampoCelular() { limpar(campoCelular); }
    public void limparCampoSenha() { limpar(campoSenha); }
    public void limparCampoConfirmacaoSenha() { limpar(campoConfirmarSenha); }

    public void concluirCadastro() {
        hideKeyboard();
        aceitarTermosSeNecessario();
        scrollToElement(btnQueroMeCadastrar, 3);
        click(btnQueroMeCadastrar);
    }

    /**
     * Marca "Li e concordo com Termos e Politicas" quando o checkbox existir e estiver desmarcado.
     * Não faz nada se a tela não tiver o checkbox (não toca no texto, que abriria os Termos).
     */
    public void aceitarTermosSeNecessario() {
        scrollToElement(checkboxTermos, 2);
        By alvo = !driver.findElements(checkboxTermos).isEmpty() ? checkboxTermos : null;
        if (alvo == null && driver.findElements(qualquerCheckbox).size() == 1) {
            alvo = qualquerCheckbox; // único checkbox da tela: só pode ser o dos termos
        }
        if (alvo != null) {
            WebElement caixa = driver.findElement(alvo);
            if (!"true".equals(caixa.getAttribute("checked"))) {
                caixa.click();
            }
        }
    }

    // ==================== LEITURA ====================

    public String obterNome() { return obterValor(campoNome); }
    public String obterEmail() { return obterValor(campoEmail); }
    public String obterCelular() { return obterValor(campoCelular); }
    public String obterSenha() { return obterValor(campoSenha); }
    public String obterConfirmacaoSenha() { return obterValor(campoConfirmarSenha); }

    // ==================== VALIDAÇÕES ====================

    public boolean nomeEstaVisivel() { return estaVisivel(campoNome); }
    public boolean emailEstaVisivel() { return estaVisivel(campoEmail); }
    public boolean celularEstaVisivel() { return estaVisivel(campoCelular); }
    public boolean senhaEstaVisivel() { return estaVisivel(campoSenha); }
    public boolean confirmacaoSenhaEstaVisivel() { return estaVisivel(campoConfirmarSenha); }

    public boolean botaoNaoTemCadastroEstaVisivel() { return estaVisivel(btnNaoTemCadastro); }
    public boolean botaoNaoTemCadastroEstaHabilitado() { return estaHabilitado(btnNaoTemCadastro); }
    public boolean botaoJaTenhoCadastroEstaVisivel() { return estaVisivel(btnJaTenhoCadastro); }
    public boolean botaoJaTenhoCadastroEstaHabilitado() { return estaHabilitado(btnJaTenhoCadastro); }

    public boolean botaoQueroMeCadastrarEstaVisivel() {
        hideKeyboard();
        return isVisible(btnQueroMeCadastrar, TestConfig.DEFAULT_TIMEOUT);
    }

    public boolean botaoQueroMeCadastrarEstaHabilitado() { return estaHabilitado(btnQueroMeCadastrar); }

    public boolean telaDeCadastroEstaApresentada() {
        return isVisible(tituloCadastro, TestConfig.DEFAULT_TIMEOUT);
    }

    public boolean telaDeLoginEstaApresentada() {
        return isVisible(tituloLogin, TestConfig.DEFAULT_TIMEOUT);
    }

    public boolean existeMensagemDeErro() {
        return existeMensagem("E-mail inválido") || existeMensagem("não correspondem")
                || existeMensagem("Informe o número");
    }

    /** Aguarda até 3s por uma mensagem (content-desc ou text) que contenha o trecho informado. */
    public boolean existeMensagem(String trecho) {
        return aguardarMensagem(trecho, TestConfig.SHORT_TIMEOUT);
    }

    public boolean aguardarMensagem(String trecho, long segundos) {
        return isVisible(descricaoContem(trecho), segundos) || isVisible(textoContem(trecho), 1);
    }

    /** Checagem imediata (sem espera) para asserções negativas. */
    public boolean mensagemEstaPresenteAgora(String trecho) {
        return !driver.findElements(descricaoContem(trecho)).isEmpty()
                || !driver.findElements(textoContem(trecho)).isEmpty();
    }

    /**
     * Aguarda o resultado do envio do cadastro.
     * SUCESSO: saiu da tela de cadastro sem erro nem pedido de SMS.
     * EXIGE_CODIGO_SMS: o app pediu verificação por SMS (o app v2.0.0 possui esse fluxo).
     * ERRO_VALIDACAO: apareceu mensagem de validação e a tela de cadastro permaneceu.
     */
    public ResultadoCadastro aguardarResultadoCadastro(long segundos) {
        long limite = System.currentTimeMillis() + segundos * 1000;
        while (System.currentTimeMillis() < limite) {
            if (pediuCodigoSms()) {
                return ResultadoCadastro.EXIGE_CODIGO_SMS;
            }
            if (driver.findElements(tituloCadastro).isEmpty()) {
                pause(1000); // a verificação por SMS pode abrir logo após a troca de tela
                return pediuCodigoSms() ? ResultadoCadastro.EXIGE_CODIGO_SMS : ResultadoCadastro.SUCESSO;
            }
            if (mensagemEstaPresenteAgora("E-mail inválido") || mensagemEstaPresenteAgora("não correspondem")
                    || mensagemEstaPresenteAgora("Informe o número") || mensagemEstaPresenteAgora("já cadastrad")
                    || mensagemEstaPresenteAgora("já existe")) {
                return ResultadoCadastro.ERRO_VALIDACAO;
            }
            pause(500);
        }
        return ResultadoCadastro.TIMEOUT;
    }

    /** Verdadeiro se o app avançou para a etapa de telefone/SMS. */
    public boolean pediuCodigoSms() {
        return mensagemEstaPresenteAgora("código enviado por SMS")
                || mensagemEstaPresenteAgora("Qual o número do seu celular")
                || mensagemEstaPresenteAgora("Reenviar o código");
    }

    /** O cadastro NÃO foi concluído: permaneceu no formulário (com ou sem mensagem). */
    public boolean cadastroNaoFoiConcluido() {
        ResultadoCadastro r = aguardarResultadoCadastro(5);
        return r == ResultadoCadastro.ERRO_VALIDACAO || r == ResultadoCadastro.TIMEOUT;
    }

    // ==================== INTERNOS ====================

    private static By campoTexto(int instancia) {
        return AppiumBy.androidUIAutomator(
                "new UiSelector().className(\"android.widget.EditText\").instance(" + instancia + ")");
    }

    private static By descricaoContem(String trecho) {
        return AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"" + escapar(trecho) + "\")");
    }

    private static By textoContem(String trecho) {
        return AppiumBy.androidUIAutomator("new UiSelector().textContains(\"" + escapar(trecho) + "\")");
    }

    private static String escapar(String texto) {
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private void preencher(By locator, String valor) {
        WebElement elemento = wait.waitForVisible(locator);
        elemento.click();
        elemento.clear();
        elemento.sendKeys(valor);
    }

    private void limpar(By locator) {
        WebElement elemento = wait.waitForVisible(locator);
        elemento.click();
        elemento.clear();
    }

    private String obterValor(By locator) {
        WebElement elemento = wait.waitForVisible(locator);
        String text = elemento.getText();
        if (text != null && !text.isBlank()) {
            return text;
        }
        String value = elemento.getAttribute("text");
        return value == null ? "" : value;
    }

    private boolean estaVisivel(By locator) {
        try {
            return wait.waitForVisible(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean estaHabilitado(By locator) {
        try {
            return wait.waitForVisible(locator).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
}
