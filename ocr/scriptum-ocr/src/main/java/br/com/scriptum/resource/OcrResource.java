package br.com.scriptum.resource;

import br.com.scriptum.DTO.request.OcrRequest;
import br.com.scriptum.DTO.response.OcrResponse;
import br.com.scriptum.service.OcrService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@Path("/ocr")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OcrResource {

    private static final Logger LOG = Logger.getLogger(OcrResource.class);

    @Inject
    OcrService ocrService;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    @Channel("ocr-reading-return")
    Emitter<String> returnEmitter;

    /**
     * Processa uma solicitação de OCR de forma síncrona e publica sua resposta.
     *
     * @param requisicao solicitação com identificador, idioma e imagem do documento
     * @return resposta do OCR ou HTTP 400 quando a imagem for inválida
     */
    @POST
    public Response processar(@Valid OcrRequest requisicao) {
        LOG.infof("Iniciando processamento OCR do documento %d no idioma %s",
                requisicao.documentId(), requisicao.language());
        try {
            OcrResponse resposta = ocrService.processar(requisicao);
            enviar(returnEmitter, resposta);
            LOG.infof("Processamento OCR concluído para o documento %d: %d parágrafo(s), confiança %.2f",
                    resposta.documentId(), resposta.paragraphs().size(), resposta.confidence());
            return Response.ok(resposta).build();
        } catch (IllegalArgumentException exception) {
            LOG.warnf("Solicitação OCR inválida para o documento %d: %s",
                    requisicao.documentId(), exception.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(exception.getMessage())
                    .build();
        }
    }

    /**
     * Processa uma solicitação de OCR recebida pelo RabbitMQ.
     *
     * @param carga mensagem JSON contendo a solicitação de OCR
     */
    @Incoming("ocr-processing")
    @Blocking
    public void processarDaFila(JsonObject carga) {
        LOG.info("Mensagem recebida na fila ocr-processing");
        try {
            OcrRequest requisicao = objectMapper.readValue(carga.encode(), OcrRequest.class);
            OcrResponse resposta = ocrService.processar(requisicao);
            enviar(returnEmitter, resposta);
            LOG.infof("Mensagem da fila processada para o documento %d",
                    resposta.documentId());
        } catch (JsonProcessingException exception) {
            LOG.warn("Mensagem inválida recebida na fila ocr-processing", exception);
            throw new IllegalArgumentException("mensagem inválida na fila de OCR", exception);
        }
    }

    /**
     * Serializa e publica uma mensagem no canal RabbitMQ configurado.
     *
     * @param emissor emissor de mensagens de destino
     * @param carga objeto que será serializado
     */
    private void enviar(Emitter<String> emissor, Object carga) {
        try {
            emissor.send(objectMapper.writeValueAsString(carga));
        } catch (JsonProcessingException exception) {
            LOG.error("Não foi possível serializar a resposta OCR", exception);
            throw new IllegalStateException(
                    "não foi possível serializar a mensagem de OCR", exception);
        }
    }
}
