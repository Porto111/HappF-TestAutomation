package steps;

import config.TestConfig;
import drivers.DriverFactory;
import io.cucumber.java.pt.*;
import org.junit.Assert;
import org.junit.Assume;
import pages.LoginPage;

import java.text.Normalizer;

public class AutenticacaoSteps {


    private final LoginPage loginPage =
            new LoginPage(DriverFactory.getDriver());

    @Dado("que o aplicativo HappFé foi aberto")
    public void queOAplicativoHappFeFoiAberto() {
        // App is already started by DriverFactory
        // Wait for login screen to be visible

        loginPage.pause(2000);
        Assert.assertTrue("Tela de login não está visível", loginPage.estaVisivel());
    }

    @Dado("que estou autenticado com uma conta de teste")
    public void queEstouAutenticadoComUmaContaDeTeste() {
        if (!loginPage.estaVisivel()) {
            return; // Already logged in
        }

        loginPage.realizarLogin(
                TestConfig.requiredTestUserEmail(),
                TestConfig.requiredTestUserPassword()
        );

        loginPage.aguardarLogin();
        Assert.assertTrue("Login falhou", loginPage.loginBemSucedido());
    }

    @Quando("eu preencho a senha de teste configurada")
    public void euPreenchoSenhaDeTesteConfigurada() {
        loginPage.preencherSenha(TestConfig.requiredTestUserPassword());
    }

    @Quando("eu preencho o email de teste configurado")
    public void euPreenchoEmailDeTesteConfigurado() {
        loginPage.preencherEmail(TestConfig.requiredTestUserEmail());
    }

    @Quando("eu preencho o email {string}")
    public void euPreenchoOEmail(String email) {
        loginPage.preencherEmail(email);
    }

    @Quando("eu preencho a senha {string}")
    public void euPreenchoASenha(String senha) {
        loginPage.preencherSenha(senha);
    }

    @Quando("eu clico no botão entrar")
    public void euClicoNoBotaoEntrar() {
        loginPage.clicarBotaoEntrar();
        loginPage.aguardarLogin();
    }

    @Então("devo ver a tela principal do aplicativo")
    public void devoVerATelaPrincipalDoAplicativo() {
        Assert.assertTrue("Login não foi bem-sucedido", loginPage.loginBemSucedido());
    }

    private String normalizar(String texto) {
        return Normalizer.normalize(texto == null ? "" : texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("-", "")
                .toLowerCase();
    }

    @Então("devo ver a mensagem de erro {string}")
    public void devoVerAMensagemDeErro(String mensagemEsperada) {
        String mensagemAtual = loginPage.obterMensagemErro();
        Assert.assertTrue(
                "Mensagem de erro incorreta. Esperado: '" + mensagemEsperada + "', Atual: '" + mensagemAtual + "'",
                normalizar(mensagemAtual).contains(normalizar(mensagemEsperada))
        );
    }

    @Então("devo permanecer na tela de login")
    public void devoPermanecerNaTelaDeLogin() {
        Assert.assertTrue("O app saiu da tela de login com campos vazios", loginPage.permaneceNaTelaDeLogin());
    }

    @Quando("eu informo as credenciais válidas de teste")
    public void euInformoAsCredenciaisValidasDeTeste() {
        Assume.assumeFalse("Defina -Dlogin.email e -Dlogin.senha com uma conta real para este cenário",
                TestConfig.TEST_USER_EMAIL.isBlank() || TestConfig.TEST_USER_PASSWORD.isBlank());
        loginPage.preencherEmail(TestConfig.TEST_USER_EMAIL);
        loginPage.preencherSenha(TestConfig.TEST_USER_PASSWORD);
    }

    @Quando("eu clico em cadastrar")
    public void euClicoEmCadastrar() {
        loginPage.clicarCadastrar();
    }

    @Quando("eu clico em esqueci minha senha")
    public void euClicoEmEsqueciMinhaSenha() {
        loginPage.clicarEsqueciSenha();
    }
}