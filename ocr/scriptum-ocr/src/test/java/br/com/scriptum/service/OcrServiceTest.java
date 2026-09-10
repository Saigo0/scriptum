package br.com.scriptum.service;

import br.com.scriptum.DTO.request.OcrRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OcrServiceTest {

    @Mock
    private DecodificadorImagem decodificadorImagem;

    @Test
    void divideTextoEmParagrafosNaoVazios() {
        assertEquals(List.of("First paragraph", "Second paragraph"),
                OcrService.dividirParagrafos("\n First paragraph \n\n Second paragraph \n"));
    }

    @Test
    void propagaErroAoDecodificarImagem() {
        OcrService servico = new OcrService(decodificadorImagem);
        OcrRequest requisicao = new OcrRequest(42L, "eng", "not-base64");
        when(decodificadorImagem.decodificar(requisicao.imageBase64()))
                .thenThrow(new IllegalArgumentException("imagem inválida"));

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class, () -> servico.processar(requisicao));

        assertEquals("imagem inválida", excecao.getMessage());
        verify(decodificadorImagem).decodificar(requisicao.imageBase64());
    }
}
