package br.com.scriptum.service;

import br.com.scriptum.dto.NoteRequest;
import br.com.scriptum.model.Note;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class NoteServiceTest {

    @Inject
    NoteService noteService;

    @Test
    public void testCreateNoteWithoutImage() {
        NoteRequest req = new NoteRequest();
        req.title = "Test Note";
        
        Note note = noteService.createNote(req);
        
        assertNotNull(note.id);
        assertEquals("Test Note", note.title);
        assertEquals("COMPLETED", note.status);
    }
}
