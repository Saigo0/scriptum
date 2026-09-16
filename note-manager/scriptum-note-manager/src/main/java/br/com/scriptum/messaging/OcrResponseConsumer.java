package br.com.scriptum.messaging;

import br.com.scriptum.dto.OcrResponse;
import br.com.scriptum.service.DocumentoService;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class OcrResponseConsumer {

    private static final Logger LOG = Logger.getLogger(OcrResponseConsumer.class);

    @Inject
    DocumentoService documentoService;

    @Incoming("ocr-reading-return")
    public void processOcrResult(JsonObject payload) {
        LOG.info("Recebendo resultado do OCR via RabbitMQ");
        try {
            OcrResponse response = payload.mapTo(OcrResponse.class);
            
            documentoService.processarRetornoOcr(
                response.documentId, 
                response.paragraphs, 
                response.confidence
            );
            
            LOG.infof("Documento %d atualizado com sucesso pelo OCR.", response.documentId);
        } catch (Exception e) {
            LOG.error("Falha ao processar o retorno do OCR", e);
        }
    }
}