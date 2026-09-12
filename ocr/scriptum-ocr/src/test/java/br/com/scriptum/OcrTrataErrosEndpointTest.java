package br.com.scriptum;

import br.com.scriptum.DTO.request.OcrRequest;
import br.com.scriptum.DTO.response.OcrResponse;
import br.com.scriptum.resource.OcrResource;
import br.com.scriptum.service.OcrService;
import br.com.scriptum.configuracao.UnitTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@ExtendWith(MockitoExtension.class)
class OcrTrataErrosEndpointTest {

    @Mock
    private OcrService ocrService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Emitter<String> returnEmitter;

    @InjectMocks
    private OcrResource resource;

    @Test
    void retornaBadRequestQuandoOProcessamentoRecebeEntradaInvalida() {
        OcrRequest requisicao = new OcrRequest(10L, "por", "aA==");
        when(ocrService.processar(requisicao))
                .thenThrow(new IllegalArgumentException("imagem inválida"));

        var resposta = resource.processar(requisicao);

        assertEquals(400, resposta.getStatus());
        assertEquals("imagem inválida", resposta.getEntity());
        verify(returnEmitter, never()).send(anyString());
    }

    @Test
    void rejeitaMensagemJsonInvalidaRecebidaPelaFila() throws Exception {
        JsonObject carga = new JsonObject().put("documentId", 10);
        when(objectMapper.readValue(carga.encode(), OcrRequest.class))
                .thenThrow(new JsonProcessingException("JSON inválido") { });

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> resource.processarDaFila(carga));

        assertEquals("mensagem inválida na fila de OCR", excecao.getMessage());
        verify(ocrService, never()).processar(any());
        verify(returnEmitter, never()).send(anyString());
    }

    @Test
    void propagaFalhaQuandoNaoConsegueSerializarResposta() throws Exception {
        OcrRequest requisicao = new OcrRequest(10L, "por", "aA==");
        OcrResponse resposta = new OcrResponse(10L, List.of("texto"), 90);
        when(ocrService.processar(requisicao)).thenReturn(resposta);
        when(objectMapper.writeValueAsString(resposta))
                .thenThrow(new JsonProcessingException("falha de serialização") { });

        IllegalStateException excecao = assertThrows(
                IllegalStateException.class,
                () -> resource.processar(requisicao));

        assertEquals("não foi possível serializar a mensagem de OCR",
                excecao.getMessage());
        verify(returnEmitter, never()).send(anyString());
    }
}
