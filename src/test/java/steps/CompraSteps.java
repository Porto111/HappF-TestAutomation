package steps;

import drivers.DriverFactory;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.junit.Assert;
import org.junit.Assume;
import pages.CompraPage;
import pages.HomePage;
import pages.LoginPage;
import utils.ScenarioContext;
import utils.Usuario;

/** Fluxo de compra reutilizando o usuário criado no cadastro (ScenarioContext). */
public class CompraSteps {

    private final LoginPage loginPage = new LoginPage(DriverFactory.getDriver());
    private final HomePage homePage = new HomePage(DriverFactory.getDriver());
    private final CompraPage compraPage = new CompraPage(DriverFactory.getDriver());

    @Dado("que o usuário recém-cadastrado está autenticado")
    public void usuarioRecemCadastradoEstaAutenticado() {
        Usuario usuario = ScenarioContext.getUsuario();
        // Se o app já autenticou após o cadastro não há tela de login; senão entra com a conta criada.
        if (loginPage.telaDeLoginEstaApresentada(5)) {
            loginPage.realizarLogin(usuario.email(), usuario.senha());
            loginPage.aguardarLogin();
            Assert.assertTrue("Login com o usuário recém-cadastrado falhou: " + usuario.email(),
                    loginPage.loginBemSucedido());
        }
    }

    @E("o usuário estiver na tela inicial com lojas disponíveis")
    public void usuarioNaTelaInicialComLojas() {
        homePage.aguardarHomePronta();
        Assert.assertFalse("Sem lojas na região do endereço/GPS informado. Ajuste o GPS do aparelho "
                + "ou use -Dcompra.endereco.", homePage.semLojasNaRegiao());
        // Loja fechada é condição do ambiente (horário), não defeito: o cenário é ignorado, não reprovado.
        Assume.assumeFalse("Loja fechada no momento: compra indisponível", homePage.lojaFechada());
    }

    @Quando("o usuário selecionar o primeiro produto disponível no catálogo")
    public void selecionarPrimeiroProduto() {
        String produto = compraPage.abrirPrimeiroProduto();
        ScenarioContext.setProduto(produto);
        System.out.println("Produto selecionado: " + produto);
        Assume.assumeFalse("Loja fechada no momento: compra indisponível", homePage.lojaFechada());
    }

    @E("o usuário adicionar o produto à sacola")
    public void adicionarASacola() {
        compraPage.adicionarASacola();
    }

    @E("o usuário abrir a sacola")
    public void abrirSacola() {
        homePage.abrirSacola();
        Assert.assertTrue("A tela 'Minha Sacola' não foi apresentada.", compraPage.sacolaEstaAberta());
    }

    @E("o usuário fechar o pedido")
    public void fecharPedido() {
        compraPage.fecharPedido();
        Assert.assertFalse("O valor mínimo do pedido não foi atingido com 1 item: escolha um produto de "
                + "maior valor ou adicione mais itens.", compraPage.valorMinimoNaoAtingido());
        compraPage.confirmarEnderecoSeSolicitado();
    }

    @E("o usuário escolher pagamento em dinheiro")
    public void escolherPagamentoEmDinheiro() {
        compraPage.selecionarPagamentoEmDinheiro();
    }

    @E("o usuário finalizar o pedido")
    public void finalizarPedido() {
        compraPage.finalizarPedido();
    }

    @Então("o pedido deve ser recebido com sucesso")
    public void pedidoRecebido() {
        Assert.assertTrue("Confirmação do pedido não apresentada para: " + ScenarioContext.getProduto(),
                compraPage.pedidoFoiConfirmado());
    }
}
