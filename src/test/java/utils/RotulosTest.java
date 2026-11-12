package utils;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RotulosTest {

    @Test
    public void deveCriarLocalizadorComTextoSeguroParaXpath() {
        String xpath = Rotulos.contem("Já tenho cadastro. Fazer login").toString();

        assertTrue(xpath.contains("Já tenho cadastro. Fazer login"));
    }

    @Test
    public void deveAceitarTextoComAspas() {
        String literal = Rotulos.xpathLiteral("Forma d'água \"teste\"");

        assertTrue(literal.startsWith("concat("));
    }
}
