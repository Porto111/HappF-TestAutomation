package utils;

import config.TestConfig;
import java.util.UUID;

import java.util.concurrent.ThreadLocalRandom;

/** Gera dados únicos para evitar "e-mail já cadastrado" em execuções repetidas. */
public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static Usuario novoUsuario() {
        String id = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String email = "qa.happfe." + id + "@" + TestConfig.TEST_EMAIL_DOMAIN;
        String celular = TestConfig.TEST_USER_PHONE.isBlank() ? gerarTelefone(id) : TestConfig.TEST_USER_PHONE;
        return new Usuario("QA Teste " + id.substring(0, 8), email, celular, TestConfig.NEW_USER_PASSWORD);
    }

    private static String gerarTelefone(String id) {
        String digits = id.replaceAll("\\D", "");
        String suffix = (digits + "00000000").substring(0, 8);
        return "119" + suffix;
    }
}
