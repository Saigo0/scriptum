package br.com.scriptum.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class OcrServiceInitializationTest {

    @Test
    void inicializaMotorOcrComRecursosDeIdioma() {
        OcrService servico = new OcrService(new DecodificadorImagemBase64());

        assertDoesNotThrow(servico::inicializar);
    }
}
