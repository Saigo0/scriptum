package br.com.scriptum.service;

import br.com.scriptum.dto.NoteRequest;
import br.com.scriptum.dto.OcrRequest;
import br.com.scriptum.model.Note;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import java.util.List;

@ApplicationScoped
public class NoteService {

    @Inject
    @Channel("ocr-processing")
    Emitter<OcrRequest> ocrEmitter;

    @Transactional
    public Note createNote(NoteRequest request) {
        Note note = new Note();
        note.title = request.title;
        
        if (request.imageBase64 != null && !request.imageBase64.isEmpty()) {
            note.status = "PROCESSING_IMAGE";
            note.persist();
            
            OcrRequest ocrRequest = new OcrRequest(note.id, request.language, request.imageBase64);
            ocrEmitter.send(ocrRequest);
        } else {
            note.status = "COMPLETED";
            note.persist();
        }
        
        return note;
    }

    public List<Note> getAllNotes() {
        return Note.listAll();
    }
    
    public Note getNote(Long id) {
        return Note.findById(id);
    }

    @Transactional
    public void updateNoteContent(Long id, String content) {
        Note note = Note.findById(id);
        if (note != null) {
            note.content = content;
            note.status = "COMPLETED";
        }
    }
    
    @Transactional
    public void updateNoteStatus(Long id, String status) {
        Note note = Note.findById(id);
        if (note != null) {
            note.status = status;
        }
    }
}
