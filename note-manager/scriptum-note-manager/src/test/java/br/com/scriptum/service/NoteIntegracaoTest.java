package br.com.scriptum.service;

import br.com.scriptum.dto.NoteRequest;
import br.com.scriptum.model.Note;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class NoteIntegracaoTest {

    @Inject
    @Any
    InMemoryConnector connector;

    @Inject
    NoteService noteService;

    @BeforeEach
    public void setup() {
        connector.sink("ocr-processing").clear();
    }

    @Test
    public void testCriarNotaComImagemEnviaParaFila() {
        NoteRequest req = new NoteRequest();
        req.title = "Nota com Imagem";
        req.imageBase64 = "base64dummy"; 
        req.language = "por";

        Note note = noteService.createNote(req);

        assertEquals("PROCESSING_IMAGE", note.status);

        InMemorySink<Object> sink = connector.sink("ocr-processing");
        assertEquals(1, sink.received().size());
    }

    @Test
    public void testReceberRetornoOcrAtualizaNota() {

        NoteRequest req = new NoteRequest();
        req.title = "Aguardando OCR";
        req.imageBase64 = "base64dummy";
        Note note = noteService.createNote(req);

        InMemorySource<JsonObject> source = connector.source("ocr-reading-return");
        
        JsonObject payload = new JsonObject();
        payload.put("documentId", note.id);
        payload.put("paragraphs", List.of("Texto", "Extraído", "Pelo OCR"));
        payload.put("confidence", 95.5);

        source.send(payload);

        Note updatedNote = noteService.getNote(note.id);
        assertEquals("COMPLETED", updatedNote.status);
        assertEquals("Texto\nExtraído\nPelo OCR", updatedNote.content);
    }
}