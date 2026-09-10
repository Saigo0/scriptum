package br.com.scriptum.resource;

import br.com.scriptum.DTO.request.OcrRequest;
import br.com.scriptum.DTO.response.OcrResponse;
import br.com.scriptum.service.OcrService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.ws.rs.core.Response;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OcrResourceUnitTest {

    @Mock
    OcrService ocrService;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    Emitter<String> returnEmitter;

    @InjectMocks
    OcrResource resource;

    @Test
    void processaRequisicaoEPublicaRespostaSerializada() throws Exception {
        OcrRequest requisicao = new OcrRequest(10L, "por", "aA==");
        OcrResponse resposta = new OcrResponse(10L, List.of("texto"), 90);
        when(ocrService.processar(requisicao)).thenReturn(resposta);
        when(objectMapper.writeValueAsString(resposta)).thenReturn("{\"documentId\":10}");

        Response httpResponse = resource.processar(requisicao);

        assertEquals(200, httpResponse.getStatus());
        assertEquals(resposta, httpResponse.getEntity());
        verify(returnEmitter).send("{\"documentId\":10}");
    }

    @Test
    void converteErroDeValidacaoEmRespostaBadRequest() {
        OcrRequest requisicao = new OcrRequest(10L, "por", "aA==");
        when(ocrService.processar(requisicao))
                .thenThrow(new IllegalArgumentException("imagem inválida"));

        Response httpResponse = resource.processar(requisicao);

        assertEquals(400, httpResponse.getStatus());
        assertEquals("imagem inválida", httpResponse.getEntity());
        verify(returnEmitter, never()).send(anyString());
    }

    @Test
    void processaMensagemDaFilaEPublicaResposta() throws Exception {
        JsonObject carga = new JsonObject().put("documentId", 10).put("language", "por")
                .put("imageBase64", "aA==");
        OcrRequest requisicao = new OcrRequest(10L, "por", "aA==");
        OcrResponse resposta = new OcrResponse(10L, List.of("texto"), 90);
        when(objectMapper.readValue(carga.encode(), OcrRequest.class)).thenReturn(requisicao);
        when(ocrService.processar(requisicao)).thenReturn(resposta);
        when(objectMapper.writeValueAsString(resposta)).thenReturn("{\"documentId\":10}");

        resource.processarDaFila(carga);

        verify(returnEmitter).send("{\"documentId\":10}");
    }

    @Test
    void rejeitaMensagemDaFilaMalformada() throws Exception {
        JsonObject carga = new JsonObject().put("documentId", 10);
        when(objectMapper.readValue(carga.encode(), OcrRequest.class))
                .thenThrow(new JsonProcessingException("JSON inválido") { });

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> resource.processarDaFila(carga));

        assertEquals("mensagem inválida na fila de OCR", exception.getMessage());
        verify(ocrService, never()).processar(any());
    }
}
