package br.com.scriptum.DTO.response;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OcrResponseTest {

    @Test
    void substituiListaNulaPorListaVazia() {
        OcrResponse resposta = new OcrResponse(1L, null, 0);

        assertEquals(List.of(), resposta.paragraphs());
    }

    @Test
    void copiaListaDeParagrafosParaPreservarImutabilidade() {
        List<String> paragrafos = new ArrayList<>(List.of("parágrafo"));

        OcrResponse resposta = new OcrResponse(1L, paragrafos, 50);
        paragrafos.add("novo parágrafo");

        assertEquals(List.of("parágrafo"), resposta.paragraphs());
        assertThrows(UnsupportedOperationException.class,
                () -> resposta.paragraphs().add("outro parágrafo"));
    }

    @Test
    void aceitaLimitesDeConfianca() {
        assertEquals(0, new OcrResponse(1L, List.of(), 0).confidence());
        assertEquals(100, new OcrResponse(1L, List.of(), 100).confidence());
    }

    @Test
    void rejeitaConfiancaForaDoIntervalo() {
        assertThrows(IllegalArgumentException.class,
                () -> new OcrResponse(1L, List.of(), -0.1));
        assertThrows(IllegalArgumentException.class,
                () -> new OcrResponse(1L, List.of(), 100.1));
        assertThrows(IllegalArgumentException.class,
                () -> new OcrResponse(1L, List.of(), Double.NaN));
    }
}
