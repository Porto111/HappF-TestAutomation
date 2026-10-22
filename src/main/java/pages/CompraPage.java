package pages;

import config.TestConfig;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import utils.Rotulos;

/**
 * Fluxo de compra do Happ Fé: produto -> Sacola -> Fechar Pedido -> pagamento -> Finalizar Pedido.
 *
 * Rótulos CONFIRMADOS no binário do app v2.0.0: "Minha Sacola", "Fechar Pedido (", "Adicionar (",
 * "Confirmar endereço", "Forma de pagamento", "Dinheiro - Preciso de troco para", "Finalizar Pedido",
 * "Pedido recebido", "Acompanhe seu pedido", "Ver Pedido", "Valor mínimo do pedido em produtos é".
 * A ESTRUTURA (qual elemento é clicável, ordem das telas) é inferida: ao falhar, o relatório anexa o
 * page source da tela para ajustar o locator.
 */
public class CompraPage extends BasePage {

    /** Card de produto: primeiro item clicável com preço. */
    private final By primeiroProduto = By.xpath(
            "(//*[@clickable='true' and contains(@content-desc,'R$')])[1]");

    /** Botão "Adicionar (R$ ...)" da tela do produto (não confundir com "Adicionar cupom/itens"). */
    private final By btnAdicionar = By.xpath(
            "//*[starts-with(@content-desc,'Adicionar') and not(contains(@content-desc,'cupom'))"
                    + " and not(contains(@content-desc,'itens'))]");

    private final By btnFecharPedido = Rotulos.comecaCom("Fechar Pedido");
    private final By btnConfirmarEndereco = Rotulos.comecaCom("Confirmar endereço");
    private final By btnAlterarPagamento = Rotulos.contem("Alterar forma de pagamento",
            "Forma de pagamento", "Método de pagamento");
    private final By opcaoDinheiro = Rotulos.contem("Dinheiro");
    private final By btnFinalizarPedido = Rotulos.comecaCom("Finalizar Pedido");
    private final By confirmacaoPedido = Rotulos.contem(
            "Pedido recebido", "Acompanhe seu pedido", "Ver Pedido", "N° do pedido");

    public CompraPage(AppiumDriver driver) {
        super(driver);
    }

    /** Abre o primeiro produto do catálogo e devolve seu rótulo para o relatório. */
    public String abrirPrimeiroProduto() {
        scrollToElement(primeiroProduto, 5);
        String rotulo = driver.findElement(primeiroProduto).getAttribute("content-desc");
        click(primeiroProduto);
        return rotulo == null ? "" : rotulo.replace('\n', ' ').trim();
    }

    public void adicionarASacola() {
        scrollToElement(btnAdicionar, 5);
        click(btnAdicionar);
    }

    public boolean sacolaEstaAberta() {
        return isVisible(Rotulos.contem("Minha Sacola"), TestConfig.DEFAULT_TIMEOUT);
    }

    public void fecharPedido() {
        scrollToElement(btnFecharPedido, 5);
        click(btnFecharPedido);
    }

    public boolean valorMinimoNaoAtingido() {
        return isVisible(Rotulos.contem("Valor mínimo do pedido"), TestConfig.SHORT_TIMEOUT);
    }

    /** Confirma o endereço de entrega apenas se o app pedir (pode já vir confirmado). */
    public void confirmarEnderecoSeSolicitado() {
        if (isVisible(btnConfirmarEndereco, TestConfig.SHORT_TIMEOUT)) {
            click(btnConfirmarEndereco);
        }
    }

    /**
     * Pagamento em Dinheiro: não cobra cartão real nem gera Pix (os demais métodos exigem
     * Stripe/Pix e não são automatizáveis de ponta a ponta).
     */
    public void selecionarPagamentoEmDinheiro() {
        if (!isVisible(opcaoDinheiro, TestConfig.SHORT_TIMEOUT) && isVisible(btnAlterarPagamento, 5)) {
            click(btnAlterarPagamento);
        }
        scrollToElement(opcaoDinheiro, 4);
        click(opcaoDinheiro);
    }

    public void finalizarPedido() {
        hideKeyboard();
        scrollToElement(btnFinalizarPedido, 5);
        click(btnFinalizarPedido);
    }

    public boolean pedidoFoiConfirmado() {
        return isVisible(confirmacaoPedido, TestConfig.DEFAULT_TIMEOUT);
    }
}
