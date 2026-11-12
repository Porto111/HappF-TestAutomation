package utils;

import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class TestDataFactoryTest {

    @Test
    public void deveGerarUsuarioComDadosValidos() {
        Usuario usuario = TestDataFactory.novoUsuario();

        assertTrue(usuario.nome().startsWith("QA Teste "));
        assertTrue(usuario.email().matches("qa\\.happfe\\.[a-f0-9]{16}@.+"));
        assertTrue(usuario.celular().matches("\\d{11}"));
        assertTrue(usuario.senha().length() >= 8);
    }

    @Test
    public void deveGerarEmailsDiferentesEmExecucoesSucessivas() {
        Usuario primeiro = TestDataFactory.novoUsuario();
        Usuario segundo = TestDataFactory.novoUsuario();

        assertNotEquals(primeiro.email(), segundo.email());
    }
}
