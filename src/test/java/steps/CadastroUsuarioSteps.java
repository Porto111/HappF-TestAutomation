package steps;

import config.TestConfig;
import drivers.DriverFactory;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.junit.Assert;
import pages.CadastroUsuarioPage;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/** Step definitions do fluxo de cadastro. Os dados positivos são sintéticos e únicos. */
public class CadastroUsuarioSteps {
    private final CadastroUsuarioPage cadastro = new CadastroUsuarioPage(DriverFactory.getDriver());

    @Dado("que o aplicativo está aberto na tela inicial")
    public void aplicativoEstaAbertoNaTelaInicial() {
        cadastro.validarTelaInicial();
    }

    @Dado("que o usuário está na tela de cadastro")
    public void usuarioEstaNaTelaDeCadastro() {
        abrirCadastroSeNecessario();
        Assert.assertTrue("A tela de cadastro não está apresentada.",
                cadastro.telaDeCadastroEstaApresentada());
    }

    @Dado("o botão {string} está disponível")
    public void oBotaoEstaDisponivel(String botao) {
        Assert.assertTrue("O botão não está visível: " + botao, botaoEstaVisivel(botao));
        Assert.assertTrue("O botão não está habilitado: " + botao, botaoEstaHabilitado(botao));
    }

    @Dado("nenhum dado foi informado nos campos de cadastro")
    public void nenhumDadoFoiInformado() {
        abrirCadastroSeNecessario();
        cadastro.limparTodosOsCampos();
    }

    @Quando("o usuário clicar no botão {string}")
    public void usuarioClicarNoBotao(String botao) {
        clicarBotao(botao);
    }

    @Quando("o usuário acessar a tela de cadastro")
    public void usuarioAcessarTelaCadastro() {
        abrirCadastroSeNecessario();
    }

    @Quando("o usuário preencher o campo {string} com {string}")
    public void usuarioPreencherCampo(String campo, String valor) {
        abrirCadastroSeNecessario();
        switch (campo) {
            case "Nome" -> cadastro.preencherNome(valor);
            case "E-mail" -> cadastro.preencherEmailCadastro(valor);
            case "Celular" -> cadastro.preencherCelular(valor);
            case "Senha" -> cadastro.preencherSenhaCadastro(valor);
            case "Confirmar senha" -> cadastro.preencherConfirmacaoSenha(valor);
            default -> Assert.fail("Campo não mapeado: " + campo);
        }
    }

    @Quando("o usuário preencher um cadastro válido com dados exclusivos")
    public void usuarioPreencherCadastroUnico() {
        abrirCadastroSeNecessario();
        String uniqueId = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String domain = TestConfig.TEST_EMAIL_DOMAIN;
        if (domain.startsWith("@")) {
            domain = domain.substring(1);
        }
        String email = "qa-" + uniqueId + "@" + domain;
        String phone = TestConfig.TEST_USER_PHONE.isBlank()
                ? "119" + String.format("%08d", ThreadLocalRandom.current().nextInt(10_000_000, 100_000_000))
                : TestConfig.TEST_USER_PHONE;

        cadastro.preencherNome("QA Mobile " + uniqueId.substring(0, 6));
        cadastro.preencherEmailCadastro(email);
        cadastro.preencherCelular(phone);
        cadastro.preencherSenhaCadastro(TestConfig.NEW_USER_TEST_PASSWORD);
        cadastro.preencherConfirmacaoSenha(TestConfig.NEW_USER_TEST_PASSWORD);
    }

    @Quando("o usuário não preencher nenhum campo")
    public void usuarioNaoPreencherNenhumCampo() {
        abrirCadastroSeNecessario();
        cadastro.limparTodosOsCampos();
    }

    @Quando("o usuário não preencher o campo {string}")
    public void usuarioNaoPreencherCampo(String campo) {
        abrirCadastroSeNecessario();
        switch (campo) {
            case "Nome" -> cadastro.limparCampoNome();
            case "E-mail" -> cadastro.limparCampoEmail();
            case "Celular" -> cadastro.limparCampoCelular();
            case "Senha" -> cadastro.limparCampoSenha();
            case "Confirmar senha" -> cadastro.limparCampoConfirmacaoSenha();
            default -> Assert.fail("Campo não mapeado: " + campo);
        }
    }

    @Quando("o usuário preencher os demais campos obrigatórios com dados válidos")
    public void preencherDemaisCamposValidos() {
        abrirCadastroSeNecessario();
        cadastro.preencherNome("QA Validação");
        if (cadastro.obterEmail().isBlank()) {
            cadastro.preencherEmailCadastro("qa-validacao@example.test");
        }
        if (cadastro.obterCelular().isBlank()) {
            cadastro.preencherCelular("11999999999");
        }
        if (cadastro.obterSenha().isBlank()) {
            cadastro.preencherSenhaCadastro(TestConfig.NEW_USER_TEST_PASSWORD);
        }
        if (cadastro.obterConfirmacaoSenha().isBlank()) {
            cadastro.preencherConfirmacaoSenha(TestConfig.NEW_USER_TEST_PASSWORD);
        }
    }

    @Quando("o usuário tentar concluir o cadastro")
    public void usuarioTentarConcluirCadastro() {
        abrirCadastroSeNecessario();
        cadastro.queroMeCadastrar();
    }

    @Quando("o usuário concluir o cadastro")
    public void usuarioConcluirCadastro() {
        abrirCadastroSeNecessario();
        cadastro.queroMeCadastrar();
    }

    @Então("a tela de cadastro deve ser apresentada")
    public void telaCadastroDeveSerApresentada() {
        Assert.assertTrue("A tela de cadastro não está apresentada.",
                cadastro.telaDeCadastroEstaApresentada());
    }

    @Então("o campo {string} deve estar disponível")
    public void campoDeveEstarDisponivel(String campo) {
        Assert.assertTrue("O campo não está visível: " + campo, campoEstaVisivel(campo));
    }

    @Então("o campo {string} deve estar vazio")
    public void campoDeveEstarVazio(String campo) {
        Assert.assertTrue("O campo deveria estar vazio: " + campo,
                valorDoCampo(campo).isBlank());
    }

    @Então("o campo {string} deve apresentar o valor {string}")
    public void campoDeveApresentarValor(String campo, String esperado) {
        Assert.assertEquals("Valor inesperado no campo: " + campo, esperado, valorDoCampo(campo));
    }

    @Então("o campo {string} deve apresentar o número informado")
    public void campoDeveApresentarNumeroInformado(String campo) {
        Assert.assertFalse("O campo não apresenta o número informado: " + campo,
                valorDoCampo(campo).isBlank());
    }

    @Então("o campo {string} deve estar preenchido")
    public void campoDeveEstarPreenchido(String campo) {
        Assert.assertFalse("O campo deveria estar preenchido: " + campo,
                valorDoCampo(campo).isBlank());
    }

    @Então("o valor da senha não deve ser apresentado em texto aberto")
    public void senhaNaoDeveSerApresentadaEmTextoAberto() {
        Assert.assertTrue("O campo Senha não está mascarado.", cadastro.senhaEstaMascarada("Senha"));
        Assert.assertTrue("O campo Confirmar senha não está mascarado.",
                cadastro.senhaEstaMascarada("Confirmar senha"));
    }

    @Então("os campos {string} e {string} devem possuir valores correspondentes")
    public void camposDevemPossuirValoresCorrespondentes(String campo1, String campo2) {
        Assert.assertEquals("Os campos não possuem valores correspondentes.",
                valorDoCampo(campo1), valorDoCampo(campo2));
    }

    @Então("o aplicativo deve informar que os campos obrigatórios precisam ser preenchidos")
    public void aplicativoDeveInformarCamposObrigatorios() {
        Assert.assertTrue("Não foi apresentada uma mensagem de validação.",
                cadastro.existeMensagemDeErro());
    }

    @Então("o aplicativo deve informar que o campo {string} é obrigatório")
    public void aplicativoDeveInformarCampoObrigatorio(String campo) {
        Assert.assertTrue("Não foi encontrada indicação de obrigatoriedade para: " + campo,
                cadastro.existeMensagem("obrigatório"));
    }

    @Então("o aplicativo deve informar que o e-mail informado é inválido")
    public void aplicativoDeveInformarEmailInvalido() {
        Assert.assertTrue("Não foi apresentada mensagem de e-mail inválido.",
                cadastro.existeMensagem("inválido"));
    }

    @Então("nenhuma mensagem de formato inválido deve ser apresentada")
    public void nenhumaMensagemFormatoInvalido() {
        Assert.assertFalse("Foi apresentada mensagem de formato inválido.",
                cadastro.existeMensagem("inválido"));
    }

    @Então("o campo {string} deve aceitar o valor informado")
    public void campoDeveAceitarValorInformado(String campo) {
        Assert.assertFalse("O campo não aceitou o valor: " + campo, valorDoCampo(campo).isBlank());
    }

    @Então("o campo {string} deve aceitar o número informado")
    public void campoDeveAceitarNumeroInformado(String campo) {
        String valor = valorDoCampo(campo);
        Assert.assertFalse("O campo não aceitou o número: " + campo, valor.isBlank());
        Assert.assertTrue("O campo contém caracteres não numéricos: " + valor,
                valor.matches("\\d+"));
    }

    @Então("o aplicativo deve informar que o número de celular é inválido")
    public void celularInvalido() {
        Assert.assertTrue("Não foi apresentada mensagem de celular inválido.",
                cadastro.existeMensagem("inválido"));
    }

    @Então("o campo {string} não deve aceitar caracteres inválidos")
    public void campoNaoDeveAceitarCaracteresInvalidos(String campo) {
        String valor = valorDoCampo(campo);
        Assert.assertTrue("O campo contém caracteres inválidos: " + valor, valor.matches("\\d*"));
    }

    @Então("o aplicativo deve manter somente o formato permitido para celular")
    public void aplicativoDeveManterFormatoCelular() {
        String valor = cadastro.obterCelular();
        Assert.assertTrue("O celular contém caracteres não numéricos: " + valor,
                valor.matches("\\d*"));
    }

    @Então("o aplicativo deve informar que a senha não atende aos requisitos mínimos")
    public void senhaNaoAtendeRequisitos() {
        Assert.assertTrue("Não foi apresentada mensagem sobre os requisitos da senha.",
                cadastro.existeMensagem("senha"));
    }

    @Então("o aplicativo deve informar que as senhas não são iguais")
    public void senhasNaoSaoIguais() {
        Assert.assertTrue("Não foi apresentada mensagem de divergência de senha.",
                cadastro.existeMensagem("não são iguais"));
    }

    @Então("nenhuma mensagem de divergência de senha deve ser apresentada")
    public void nenhumaMensagemDivergenciaSenha() {
        Assert.assertFalse("Foi apresentada mensagem de divergência de senha.",
                cadastro.existeMensagem("não são iguais"));
    }

    @Então("o cadastro do usuário deve ser realizado com sucesso")
    public void cadastroRealizadoComSucesso() {
        Assert.assertTrue("Não foi encontrada confirmação de sucesso do cadastro.",
                cadastro.existeMensagem("sucesso"));
    }

    @Então("o aplicativo deve apresentar a confirmação do cadastro")
    public void aplicativoApresentarConfirmacaoCadastro() {
        cadastroRealizadoComSucesso();
    }

    @Então("o aplicativo deve apresentar a tela de cadastro")
    public void aplicativoApresentarTelaCadastro() {
        telaCadastroDeveSerApresentada();
    }

    @Então("o aplicativo deve direcionar o usuário para a tela de login")
    public void aplicativoDirecionarParaLogin() {
        Assert.assertTrue("A tela de login não foi apresentada.",
                cadastro.telaDeLoginEstaApresentada());
    }

    @Então("o aplicativo deve abandonar o fluxo de cadastro")
    public void aplicativoAbandonarCadastro() {
        aplicativoDirecionarParaLogin();
    }

    @Então("o aplicativo deve apresentar a tela de login")
    public void aplicativoApresentarTelaLogin() {
        aplicativoDirecionarParaLogin();
    }

    @Então("o botão {string} deve estar visível")
    public void botaoDeveEstarVisivel(String botao) {
        Assert.assertTrue("Botão não está visível: " + botao, botaoEstaVisivel(botao));
    }

    @Então("o botão {string} deve estar habilitado")
    public void botaoDeveEstarHabilitado(String botao) {
        Assert.assertTrue("Botão não está habilitado: " + botao, botaoEstaHabilitado(botao));
    }

    @E("clicar no botão {string}")
    public void clicarNoBotao(String botao) {
        clicarBotao(botao);
    }

    private void abrirCadastroSeNecessario() {
        if (!cadastro.telaDeCadastroEstaApresentada()) {
            cadastro.clicarNaoTemCadastro();
            Assert.assertTrue("Não foi possível abrir a tela de cadastro.",
                    cadastro.telaDeCadastroEstaApresentada());
        }
    }

    private void clicarBotao(String botao) {
        switch (botao) {
            case "Não tem cadastro? Cadastrar" -> cadastro.clicarNaoTemCadastro();
            case "Já tenho cadastro. Fazer login" -> cadastro.clicarJaTenhoCadastro();
            case "Quero me cadastrar" -> cadastro.queroMeCadastrar();
            default -> Assert.fail("Botão não mapeado: " + botao);
        }
    }

    private boolean campoEstaVisivel(String campo) {
        return switch (campo) {
            case "Nome" -> cadastro.nomeEstaVisivel();
            case "E-mail" -> cadastro.emailEstaVisivel();
            case "Celular" -> cadastro.celularEstaVisivel();
            case "Senha" -> cadastro.senhaEstaVisivel();
            case "Confirmar senha" -> cadastro.confirmacaoSenhaEstaVisivel();
            default -> false;
        };
    }

    private String valorDoCampo(String campo) {
        return switch (campo) {
            case "Nome" -> cadastro.obterNome();
            case "E-mail" -> cadastro.obterEmail();
            case "Celular" -> cadastro.obterCelular();
            case "Senha" -> cadastro.obterSenha();
            case "Confirmar senha" -> cadastro.obterConfirmacaoSenha();
            default -> throw new IllegalArgumentException("Campo não mapeado: " + campo);
        };
    }

    private boolean botaoEstaVisivel(String botao) {
        return switch (botao) {
            case "Não tem cadastro? Cadastrar" -> cadastro.botaoCadastroInicialEstaVisivel();
            case "Quero me cadastrar" -> cadastro.botaoQueroMeCadastrarEstaVisivel();
            case "Já tenho cadastro. Fazer login" -> cadastro.botaoJaTenhoCadastroEstaVisivel();
            default -> false;
        };
    }

    private boolean botaoEstaHabilitado(String botao) {
        return switch (botao) {
            case "Não tem cadastro? Cadastrar" -> cadastro.botaoCadastroInicialEstaHabilitado();
            case "Quero me cadastrar" -> cadastro.botaoQueroMeCadastrarEstaHabilitado();
            case "Já tenho cadastro. Fazer login" -> cadastro.botaoJaTenhoCadastroEstaHabilitado();
            default -> false;
        };
    }
}
