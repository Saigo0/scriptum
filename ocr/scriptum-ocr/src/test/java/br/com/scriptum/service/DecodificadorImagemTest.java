package br.com.scriptum.service;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DecodificadorImagemTest {

    private final DecodificadorImagemBase64 decodificador = new DecodificadorImagemBase64();

    @Test
    void decodificaImagemExistente() throws Exception {
        try (InputStream imagem = getClass().getResourceAsStream("/ocr-test.png")) {
            String imagemBase64 = Base64.getEncoder().encodeToString(imagem.readAllBytes());

            assertNotNull(decodificador.decodificar(imagemBase64));
        }
    }

    @Test
    void rejeitaConteudoQueNaoEImagem() {
        assertThrows(IllegalArgumentException.class,
                () -> decodificador.decodificar("aA=="));
    }
}
