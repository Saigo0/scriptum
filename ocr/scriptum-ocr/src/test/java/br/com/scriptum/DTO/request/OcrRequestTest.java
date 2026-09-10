package br.com.scriptum.DTO.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OcrRequestTest {

    @Test
    void normalizaIdiomaParaMinusculas() {
        OcrRequest requisicao = new OcrRequest(1L, "POR", "aA==");

        assertEquals("por", requisicao.language());
    }

    @Test
    void rejeitaIdentificadorNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> new OcrRequest(-1L, "por", "aA=="));
    }
}
