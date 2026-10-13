package utils;

import config.TestConfig;

import java.util.concurrent.ThreadLocalRandom;

/** Gera dados únicos para evitar "e-mail já cadastrado" em execuções repetidas. */
public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static Usuario novoUsuario() {
        long agora = System.currentTimeMillis();
        String email = "qa.happfe." + agora + "@email.com";
        // Celular BR com 11 dígitos: DDD 11 + 9 + 8 dígitos
        String celular = "119" + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000));
        return new Usuario("QA Teste " + agora % 100000, email, celular, TestConfig.NEW_USER_PASSWORD);
    }
}
