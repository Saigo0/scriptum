package br.com.scriptum.service;

import br.com.scriptum.DTO.request.OcrRequest;
import br.com.scriptum.DTO.response.OcrResponse;
import br.com.scriptum.support.OcrTestFixtures;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class OcrServiceImageIntegrationTest {

    @Inject
    OcrService servicoOcr;

    @Test
    void leImagemBase64Configurada() throws Exception {
        OcrRequest requisicao = new OcrRequest(
                2026L, "eng", OcrTestFixtures.imagemBase64());

        OcrResponse resposta = servicoOcr.processar(requisicao);
        String texto = String.join(" ", resposta.paragraphs()).toUpperCase();

        assertFalse(resposta.paragraphs().isEmpty());
        assertTrue(texto.contains("SCRIPTUM"));
        assertTrue(resposta.confidence() > 0);
    }
}
