package pages;

import config.TestConfig;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import utils.Rotulos;

/**
 * Tela inicial do Happ Fé: barra inferior (Início / Promoções / Sacola / Perfil) e o
 * tratamento da localização, que o app exige para listar lojas e produtos.
 *
 * Textos confirmados no app v2.0.0: "Permitir localização", "Permitir acesso", "Informe sua localização",
 * "Digite o endereço", "Confirmar endereço", "Carregando localização...", "Loja Fechada",
 * "Não foram encontradas lojas perto da sua localização", "Entendi", "Agora não".
 */
public class HomePage extends BasePage {

    private final By abaInicio = Rotulos.aba("Início");
    private final By abaSacola = Rotulos.aba("Sacola");

    private final By btnPermitirLocalizacao = Rotulos.contem("Permitir localização", "Permitir acesso");
    private final By btnFecharInformativo = By.xpath("//*[@content-desc='Entendi' or @content-desc='Agora não']");
    private final By campoEndereco = By.xpath("//android.widget.EditText");
    // TODO validar no Inspector: primeira sugestão do autocomplete (Google Places)
    private final By primeiraSugestao = By.xpath(
            "(//*[@clickable='true' and string-length(@content-desc)>12 and not(contains(@content-desc,'Confirmar'))"
                    + " and not(contains(@content-desc,'Tab'))])[1]");
    private final By btnConfirmarEndereco = Rotulos.contem("Confirmar endereço");

    public HomePage(AppiumDriver driver) {
        super(driver);
    }

    /**
     * Leva o app até a home com lojas carregadas: aceita permissão, fecha avisos e, se o GPS não
     * resolver, informa o endereço manualmente (TestConfig.ENDERECO_ENTREGA).
     */
    public void aguardarHomePronta() {
        long limite = System.currentTimeMillis() + 60_000;
        boolean enderecoInformado = false;
        while (System.currentTimeMillis() < limite) {
            if (!driver.findElements(btnFecharInformativo).isEmpty()) {
                driver.findElement(btnFecharInformativo).click();
            } else if (!driver.findElements(btnPermitirLocalizacao).isEmpty()) {
                driver.findElement(btnPermitirLocalizacao).click();
            } else if (precisaDeEndereco() && !enderecoInformado) {
                informarEnderecoManual();
                enderecoInformado = true;
            } else if (!driver.findElements(abaSacola).isEmpty() && !precisaDeEndereco()
                    && !presente("Carregando localização")) {
                return;
            }
            pause(1000);
        }
        throw new AssertionError("A home não ficou pronta em 60s. Verifique GPS/endereço do dispositivo "
                + "(o app não lista lojas sem localização) ou use -Dcompra.endereco=\"Rua, número, cidade\".");
    }

    private boolean precisaDeEndereco() {
        return presente("Informe sua localização") || presente("Ative sua localização")
                || presente("Não foi possível obter sua localização");
    }

    private void informarEnderecoManual() {
        wait.waitForVisible(campoEndereco, 10);
        click(campoEndereco);
        sendKeys(campoEndereco, TestConfig.ENDERECO_ENTREGA);
        pause(3000); // autocomplete
        if (isVisible(primeiraSugestao, 5)) {
            click(primeiraSugestao);
        }
        if (isVisible(btnConfirmarEndereco, 8)) {
            click(btnConfirmarEndereco);
        }
    }

    public boolean lojaFechada() {
        return presente("Loja Fechada");
    }

    public boolean semLojasNaRegiao() {
        return presente("Não foram encontradas lojas") || presente("Não encontramos lojas");
    }

    public void abrirInicio() {
        click(abaInicio);
    }

    /** Abre a aba Sacola; se estiver numa tela sem a barra inferior, volta uma vez antes. */
    public void abrirSacola() {
        if (presente("Minha Sacola")) {
            return;
        }
        if (!isVisible(abaSacola, 3)) {
            driver.navigate().back();
        }
        click(abaSacola);
    }

    public boolean presente(String trecho) {
        return !driver.findElements(Rotulos.contem(trecho)).isEmpty();
    }
}
