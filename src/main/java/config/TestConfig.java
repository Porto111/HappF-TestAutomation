package config;

public class TestConfig {

    // ==================== Appium Server / Device ====================
    // Podem ser sobrescritos via -Dappium.url=... -Ddevice.name=... -Ddevice.udid=...
    public static final String APPIUM_URL = System.getProperty("appium.url", "http://127.0.0.1:4723");
    public static final String PLATFORM_NAME = "Android";
    public static final String DEVICE_NAME = System.getProperty("device.name", "Xiaomi14");
    public static final String DEVICE_UDID = System.getProperty("device.udid", "");

    // ==================== HappFé App ====================
    public static final String APP_PACKAGE = "br.com.emeraldindigo.happfe";
    public static final String APP_ACTIVITY = "br.com.emeraldindigo.happfe.MainActivity";

    // ==================== Test Configuration ====================
    // false = limpa os dados do app a cada sessão (necessário para validar cadastro/login do zero)
    public static final boolean NO_RESET = false;
    public static final boolean AUTO_GRANT_PERMISSIONS = true;
    public static final long DEFAULT_TIMEOUT = 30;
    // Timeout curto para checagens negativas ("o elemento NÃO deve aparecer")
    public static final long SHORT_TIMEOUT = 3;

    // ==================== Test Data ====================
    // Credenciais de uma conta REAL já existente (somente para os cenários de login isolados).
    // Informe via -Dlogin.email=... -Dlogin.senha=... ; sem elas esses cenários são ignorados.
    public static final String TEST_USER_EMAIL = System.getProperty("login.email", "");
    public static final String TEST_USER_PASSWORD = System.getProperty("login.senha", "");

    // O app só mostra lojas/produtos com localização ou endereço. Usado se o GPS não resolver.
    public static final String ENDERECO_ENTREGA =
            System.getProperty("compra.endereco", "Avenida Paulista, 1578, São Paulo");

    // Senha usada nos usuários gerados dinamicamente no cadastro
    public static final String NEW_USER_PASSWORD = "Senha@123";

}
