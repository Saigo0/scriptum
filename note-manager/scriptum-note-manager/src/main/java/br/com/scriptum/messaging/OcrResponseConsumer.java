package br.com.scriptum.messaging;

import br.com.scriptum.dto.OcrResponse;
import br.com.scriptum.service.NoteService;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class OcrResponseConsumer {

    private static final Logger LOG = Logger.getLogger(OcrResponseConsumer.class);

    @Inject
    NoteService noteService;

    @Incoming("ocr-reading-return")
    public void processOcrResult(JsonObject payload) {
        LOG.info("Received OCR result");
        try {
            OcrResponse response = payload.mapTo(OcrResponse.class);
            if (response.paragraphs != null && !response.paragraphs.isEmpty()) {
                String content = String.join("\n", response.paragraphs);
                noteService.updateNoteContent(response.documentId, content);
                LOG.infof("Note %d updated successfully.", response.documentId);
            } else {
                noteService.updateNoteStatus(response.documentId, "COMPLETED_EMPTY");
                LOG.infof("Note %d processed but no text found.", response.documentId);
            }
        } catch (Exception e) {
            LOG.error("Failed to process OCR response", e);
        }
    }
}
