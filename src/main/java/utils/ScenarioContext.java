package utils;

/**
 * Compartilha dados entre as classes de steps dentro do mesmo cenário
 * (ex.: usuário criado no cadastro é reutilizado no login e na compra).
 */
public final class ScenarioContext {

    private static final ThreadLocal<Usuario> USUARIO = new ThreadLocal<>();
    private static final ThreadLocal<String> PRODUTO = new ThreadLocal<>();

    private ScenarioContext() {
    }

    public static void setUsuario(Usuario usuario) {
        USUARIO.set(usuario);
    }

    public static Usuario getUsuario() {
        Usuario u = USUARIO.get();
        if (u == null) {
            throw new IllegalStateException("Nenhum usuário foi cadastrado neste cenário.");
        }
        return u;
    }

    public static void setProduto(String produto) {
        PRODUTO.set(produto);
    }

    public static String getProduto() {
        return PRODUTO.get();
    }

    public static void reset() {
        USUARIO.remove();
        PRODUTO.remove();
    }
}
