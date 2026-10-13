package steps;

import drivers.DriverFactory;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.junit.Assert;
import pages.CadastroUsuarioPage;
import utils.ScenarioContext;
import utils.TestDataFactory;
import utils.Usuario;

/**
 * Step Definitions do cadastro de usuário.
 * O driver é obtido do DriverFactory (criado no hook @Before).
 */
public class CadastroUsuarioSteps {

    private final CadastroUsuarioPage cadastroPage = new CadastroUsuarioPage(DriverFactory.getDriver());

    // ==================== DADO ====================

    @Dado("que o aplicativo está aberto na tela inicial")
    public void aplicativoEstaAbertoNaTelaInicial() {
        // Espera ativa pela tela de login (substitui o pause fixo de 15s)
        cadastroPage.validarTelaInicial();
    }

    @Dado("que o usuário está na tela de cadastro")
    public void usuarioEstaNaTelaDeCadastro() {
        cadastroPage.abrirTelaDeCadastro();
        Assert.assertTrue("A tela de cadastro não está apresentada.",
                cadastroPage.telaDeCadastroEstaApresentada());
    }

    @Dado("o botão {string} está disponível")
    public void botaoEstaDisponivel(String botao) {
        Assert.assertTrue("Botão não está visível: " + botao, botaoEstaVisivel(botao));
        Assert.assertTrue("Botão não está habilitado: " + botao, botaoEstaHabilitado(botao));
    }

    @Dado("nenhum dado foi informado nos campos de cadastro")
    public void nenhumDadoFoiInformado() {
        cadastroPage.limparTodosOsCampos();
    }

    // ==================== QUANDO / E ====================

    @Quando("o usuário clicar no botão {string}")
    public void usuarioClicarNoBotao(String botao) {
        switch (botao) {
            case "Não tem cadastro? Cadastrar" -> cadastroPage.clicarNaoTemCadastro();
            case "Já tenho cadastro. Fazer login" -> cadastroPage.clicarJaTenhoCadastro();
            case "Quero me cadastrar" -> cadastroPage.concluirCadastro();
            default -> Assert.fail("Botão não mapeado: " + botao);
        }
    }

    @Quando("o usuário acessar a tela de cadastro")
    public void usuarioAcessarTelaCadastro() {
        // Antes clicava em "Já tenho cadastro" primeiro, botão que só existe na tela de cadastro
        cadastroPage.abrirTelaDeCadastro();
        Assert.assertTrue("A tela de cadastro não está apresentada.",
                cadastroPage.telaDeCadastroEstaApresentada());
    }

    @Quando("o usuário preencher o campo {string} com {string}")
    public void usuarioPreencherCampo(String campo, String valor) {
        switch (campo) {
            case "Nome" -> cadastroPage.preencherNome(valor);
            case "E-mail" -> cadastroPage.preencherEmail(valor); // antes digitava no campo Nome
            case "Celular" -> cadastroPage.preencherCelular(valor);
            case "Senha" -> cadastroPage.preencherSenha(valor);
            case "Confirmar senha" -> cadastroPage.preencherConfirmacaoSenha(valor);
            default -> Assert.fail("Campo não mapeado: " + campo);
        }
    }

    @Quando("o usuário preencher o cadastro com os dados de um novo usuário único")
    public void usuarioPreencherCadastroUnico() {
        Usuario usuario = TestDataFactory.novoUsuario();
        ScenarioContext.setUsuario(usuario);
        cadastroPage.preencherCadastroCompleto(usuario);
    }

    @Quando("o usuário não preencher nenhum campo")
    public void usuarioNaoPreencherNenhumCampo() {
        cadastroPage.limparTodosOsCampos();
    }

    @Quando("o usuário não preencher o campo {string}")
    public void usuarioNaoPreencherCampo(String campo) {
        switch (campo) {
            case "Nome" -> cadastroPage.limparCampoNome();
            case "E-mail" -> cadastroPage.limparCampoEmail();
            case "Celular" -> cadastroPage.limparCampoCelular();
            case "Senha" -> cadastroPage.limparCampoSenha();
            case "Confirmar senha" -> cadastroPage.limparCampoConfirmacaoSenha();
            default -> Assert.fail("Campo não mapeado: " + campo);
        }
    }

    @Quando("o usuário preencher os demais campos obrigatórios com dados válidos")
    public void preencherDemaisCamposValidos() {
        cadastroPage.preencherNome("João da Silva");
        cadastroPage.preencherCelular("11999999999");
        cadastroPage.preencherSenha("Senha@123");
        cadastroPage.preencherConfirmacaoSenha("Senha@123");
    }

    @Quando("o usuário tentar concluir o cadastro")
    public void usuarioTentarConcluirCadastro() {
        cadastroPage.concluirCadastro();
    }

    @Quando("o usuário concluir o cadastro")
    public void usuarioConcluirCadastro() {
        cadastroPage.concluirCadastro();
    }

    // ==================== ENTÃO ====================

    @Então("a tela de cadastro deve ser apresentada")
    public void telaCadastroDeveSerApresentada() {
        Assert.assertTrue("A tela de cadastro não foi apresentada.",
                cadastroPage.telaDeCadastroEstaApresentada());
    }

    @Então("o campo {string} deve estar disponível")
    public void campoDeveEstarDisponivel(String campo) {
        Assert.assertTrue("O campo não está visível: " + campo, campoEstaVisivel(campo));
    }

    @Então("o campo {string} deve estar vazio")
    public void campoDeveEstarVazio(String campo) {
        Assert.assertTrue("O campo deveria estar vazio: " + campo, valorDoCampo(campo).isBlank());
    }

    @Então("o campo {string} deve apresentar o valor {string}")
    public void campoDeveApresentarValor(String campo, String valorEsperado) {
        Assert.assertEquals("Valor inesperado no campo: " + campo, valorEsperado, valorDoCampo(campo));
    }

    @Então("o campo {string} deve estar preenchido")
    public void campoDeveEstarPreenchido(String campo) {
        Assert.assertFalse("O campo deveria estar preenchido: " + campo, valorDoCampo(campo).isBlank());
    }

    @Então("o campo {string} deve aceitar o valor informado")
    public void campoDeveAceitarValorInformado(String campo) {
        Assert.assertFalse("O campo não aceitou o valor: " + campo, valorDoCampo(campo).isBlank());
    }

    @Então("os campos {string} e {string} devem possuir valores correspondentes")
    public void camposDevemPossuirValoresCorrespondentes(String campo1, String campo2) {
        Assert.assertEquals("Os campos não possuem valores correspondentes.",
                valorDoCampo(campo1), valorDoCampo(campo2));
    }

    @Então("o campo {string} não deve aceitar caracteres inválidos")
    public void campoNaoDeveAceitarCaracteresInvalidos(String campo) {
        String valor = somenteDigitos(valorDoCampo(campo));
        Assert.assertTrue("O campo contém caracteres inválidos: " + valor, valor.matches("\\d*"));
    }

    @Então("o aplicativo deve manter somente o formato permitido para celular")
    public void aplicativoDeveManterFormatoCelular() {
        String valor = cadastroPage.obterCelular();
        // Tolera máscara visual, ex.: (11) 99999-9999
        String semMascara = valor.replaceAll("[()\\s-]", "");
        Assert.assertTrue("O campo celular contém caracteres não numéricos: " + valor,
                semMascara.matches("\\d*"));
    }

    // --- Validações (textos reais do app v2.0.0: "E-mail inválido." e "As senhas não correspondem.") ---
    // O app NÃO possui mensagem genérica de "campo obrigatório"; nesses casos validamos que o
    // cadastro não avança (permanece no formulário).

    @Então("o cadastro não deve ser concluído")
    public void cadastroNaoDeveSerConcluido() {
        Assert.assertTrue("O app avançou mesmo com dados inválidos/incompletos.",
                cadastroPage.cadastroNaoFoiConcluido());
    }

    @Então("o aplicativo deve informar que o e-mail informado é inválido")
    public void aplicativoDeveInformarEmailInvalido() {
        Assert.assertTrue("Mensagem 'E-mail inválido.' não apresentada.", cadastroPage.existeMensagem("E-mail inválido"));
    }

    @Então("nenhuma mensagem de formato inválido deve ser apresentada")
    public void nenhumaMensagemFormatoInvalido() {
        Assert.assertFalse(cadastroPage.mensagemEstaPresenteAgora("E-mail inválido"));
    }

    @Então("o aplicativo deve informar que as senhas não são iguais")
    public void senhasNaoSaoIguais() {
        Assert.assertTrue("Mensagem 'As senhas não correspondem.' não apresentada.",
                cadastroPage.existeMensagem("não correspondem"));
    }

    @Então("nenhuma mensagem de divergência de senha deve ser apresentada")
    public void nenhumaMensagemDivergenciaSenha() {
        Assert.assertFalse(cadastroPage.mensagemEstaPresenteAgora("não correspondem"));
    }

    // --- Resultado do cadastro ---

    @Então("o cadastro do usuário deve ser realizado com sucesso")
    public void cadastroRealizadoComSucesso() {
        CadastroUsuarioPage.ResultadoCadastro resultado = cadastroPage.aguardarResultadoCadastro(25);
        Assert.assertNotEquals("O app exigiu verificação por código SMS. Para automatizar este passo é preciso "
                + "um ambiente de teste com código fixo/número liberado (não é possível ler SMS real).",
                CadastroUsuarioPage.ResultadoCadastro.EXIGE_CODIGO_SMS, resultado);
        Assert.assertEquals("O cadastro não foi concluído (a tela de cadastro permaneceu ou houve erro).",
                CadastroUsuarioPage.ResultadoCadastro.SUCESSO, resultado);
    }

    @Então("o aplicativo deve apresentar a confirmação do cadastro")
    public void aplicativoApresentarConfirmacaoCadastro() {
        cadastroRealizadoComSucesso();
    }

    @Então("o aplicativo deve direcionar o usuário para a tela de login")
    public void aplicativoDirecionarParaLogin() {
        Assert.assertTrue("A tela de login não foi apresentada.", cadastroPage.telaDeLoginEstaApresentada());
    }

    @Então("o aplicativo deve abandonar o fluxo de cadastro")
    public void aplicativoAbandonarCadastro() {
        Assert.assertTrue(cadastroPage.telaDeLoginEstaApresentada());
    }

    @Então("o aplicativo deve apresentar a tela de login")
    public void aplicativoApresentarTelaLogin() {
        Assert.assertTrue(cadastroPage.telaDeLoginEstaApresentada());
    }

    @Então("o botão {string} deve estar visível")
    public void botaoDeveEstarVisivel(String botao) {
        Assert.assertTrue("Botão não está visível: " + botao, botaoEstaVisivel(botao));
    }

    @Então("o botão {string} deve estar habilitado")
    public void botaoDeveEstarHabilitado(String botao) {
        Assert.assertTrue("Botão não está habilitado: " + botao, botaoEstaHabilitado(botao));
    }

    // ==================== helpers ====================

    private boolean campoEstaVisivel(String campo) {
        return switch (campo) {
            case "Nome" -> cadastroPage.nomeEstaVisivel();
            case "E-mail" -> cadastroPage.emailEstaVisivel();
            case "Celular" -> cadastroPage.celularEstaVisivel();
            case "Senha" -> cadastroPage.senhaEstaVisivel();
            case "Confirmar senha" -> cadastroPage.confirmacaoSenhaEstaVisivel();
            default -> false;
        };
    }

    private String valorDoCampo(String campo) {
        return switch (campo) {
            case "Nome" -> cadastroPage.obterNome();
            case "E-mail" -> cadastroPage.obterEmail();
            case "Celular" -> cadastroPage.obterCelular();
            case "Senha" -> cadastroPage.obterSenha();
            case "Confirmar senha" -> cadastroPage.obterConfirmacaoSenha();
            default -> throw new IllegalArgumentException("Campo não mapeado: " + campo);
        };
    }

    private boolean botaoEstaVisivel(String botao) {
        return switch (botao) {
            case "Não tem cadastro? Cadastrar" -> cadastroPage.botaoNaoTemCadastroEstaVisivel();
            case "Quero me cadastrar" -> cadastroPage.botaoQueroMeCadastrarEstaVisivel();
            case "Já tenho cadastro. Fazer login" -> cadastroPage.botaoJaTenhoCadastroEstaVisivel();
            default -> false;
        };
    }

    private boolean botaoEstaHabilitado(String botao) {
        return switch (botao) {
            case "Não tem cadastro? Cadastrar" -> cadastroPage.botaoNaoTemCadastroEstaHabilitado();
            case "Quero me cadastrar" -> cadastroPage.botaoQueroMeCadastrarEstaHabilitado();
            case "Já tenho cadastro. Fazer login" -> cadastroPage.botaoJaTenhoCadastroEstaHabilitado();
            default -> false;
        };
    }

    private String somenteDigitos(String valor) {
        return valor.replaceAll("[()\\s-]", "");
    }
}
