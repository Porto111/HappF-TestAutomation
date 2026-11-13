package config;

public class TestConfig {

    // ==================== Appium Server / Device ====================
    // Podem ser sobrescritos via -Dappium.url=... -Ddevice.name=... -Ddevice.udid=...
    public static final String APPIUM_URL = value("appium.url", "APPIUM_URL", "http://127.0.0.1:4723");
    public static final String PLATFORM_NAME = value("platform.name", "HAPPFE_PLATFORM", "Android");
    public static final String DEVICE_NAME = value("device.name", "HAPPFE_DEVICE", "Android Emulator");
    public static final String DEVICE_UDID = value("device.udid", "HAPPFE_UDID", "");

    // ==================== HappFé App ====================
    public static final String APP_PACKAGE = value("app.package", "HAPPFE_APP_PACKAGE", "br.com.emeraldindigo.happfe");
    public static final String APP_ACTIVITY = value("app.activity", "HAPPFE_APP_ACTIVITY", "br.com.emeraldindigo.happfe.MainActivity");

    // ==================== Test Configuration ====================
    // false = limpa os dados do app a cada sessão (necessário para validar cadastro/login do zero)
    public static final boolean NO_RESET = booleanValue("no.reset", "HAPPFE_NO_RESET", false);
    public static final boolean AUTO_GRANT_PERMISSIONS = booleanValue("auto.grant.permissions", "HAPPFE_AUTO_GRANT_PERMISSIONS", true);
    public static final long DEFAULT_TIMEOUT = positiveLong("wait.seconds", "HAPPFE_WAIT_SECONDS", 30);
    // Timeout curto para checagens negativas ("o elemento NÃO deve aparecer")
    public static final long SHORT_TIMEOUT = positiveLong("short.wait.seconds", "HAPPFE_SHORT_WAIT_SECONDS", 3);

    // ==================== Test Data ====================
    // Credenciais de uma conta REAL já existente (somente para os cenários de login isolados).
    // Informe via -Dlogin.email=... -Dlogin.senha=... ; sem elas esses cenários são ignorados.
    public static final String TEST_USER_EMAIL = value("login.email", "HAPPFE_TEST_EMAIL", "");
    public static final String TEST_USER_PASSWORD = value("login.senha", "HAPPFE_TEST_PASSWORD", "");

    // O app só mostra lojas/produtos com localização ou endereço. Usado se o GPS não resolver.
    public static final String ENDERECO_ENTREGA =
            value("compra.endereco", "HAPPFE_DELIVERY_ADDRESS", "Avenida Paulista, 1578, São Paulo");

    // Senha usada nos usuários gerados dinamicamente no cadastro
    public static final String NEW_USER_PASSWORD = value("new.user.password", "HAPPFE_NEW_USER_TEST_PASSWORD", "QaMobile@12345");

    /** Domínio reservado para dados sintéticos em ambiente de teste. */
    public static final String TEST_EMAIL_DOMAIN = value(
            "happfe.test.emailDomain", "HAPPFE_TEST_EMAIL_DOMAIN", "example.test");
    public static final String TEST_USER_PHONE = value(
            "happfe.test.phone", "HAPPFE_TEST_PHONE", "");
    public static final String NEW_USER_TEST_PASSWORD = value(
            "happfe.test.newUserPassword", "HAPPFE_NEW_USER_TEST_PASSWORD", NEW_USER_PASSWORD);

    public static String requiredTestUserEmail() {
        return required(TEST_USER_EMAIL, "HAPPFE_TEST_EMAIL");
    }

    public static String requiredTestUserPassword() {
        return required(TEST_USER_PASSWORD, "HAPPFE_TEST_PASSWORD");
    }

    private static String value(String property, String environment, String fallback) {
        String fromProperty = System.getProperty(property);
        if (fromProperty != null && !fromProperty.isBlank()) {
            return fromProperty.trim();
        }
        String fromEnvironment = System.getenv(environment);
        if (fromEnvironment != null && !fromEnvironment.isBlank()) {
            return fromEnvironment.trim();
        }
        return fallback;
    }

    private static boolean booleanValue(String property, String environment, boolean fallback) {
        String configured = value(property, environment, Boolean.toString(fallback));
        if ("true".equalsIgnoreCase(configured)) {
            return true;
        }
        if ("false".equalsIgnoreCase(configured)) {
            return false;
        }
        throw new IllegalArgumentException(
                "A configuração " + property + "/" + environment + " tem de ser true ou false.");
    }

    private static long positiveLong(String property, String environment, long fallback) {
        String configured = value(property, environment, Long.toString(fallback));
        try {
            long parsed = Long.parseLong(configured);
            if (parsed > 0) {
                return parsed;
            }
        } catch (NumberFormatException ignored) {
            // A mensagem final informa a propriedade com valor inválido.
        }
        throw new IllegalArgumentException(
                "A configuração " + property + "/" + environment + " tem de ser um inteiro positivo.");
    }

    private static String required(String configured, String environment) {
        if (configured == null || configured.isBlank()) {
            throw new IllegalStateException(
                    "Defina " + environment + " com credenciais de uma conta de teste/staging.");
        }
        return configured;
    }
}
