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

    /** Domínio reservado para dados sintéticos em ambiente de teste. */
    public static final String TEST_EMAIL_DOMAIN = value(
            "happfe.test.emailDomain", "HAPPFE_TEST_EMAIL_DOMAIN", "example.test");
    public static final String TEST_USER_PHONE = value(
            "happfe.test.phone", "HAPPFE_TEST_PHONE", "");
    public static final String NEW_USER_TEST_PASSWORD = value(
            "happfe.test.newUserPassword", "HAPPFE_NEW_USER_TEST_PASSWORD", "QaMobile@12345");

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
