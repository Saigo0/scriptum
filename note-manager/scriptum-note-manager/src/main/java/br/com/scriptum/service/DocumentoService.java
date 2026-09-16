package br.com.scriptum.service;

import br.com.scriptum.dto.OcrRequest;
import br.com.scriptum.model.Documento;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.nio.charset.StandardCharsets;
import java.util.List;

@ApplicationScoped
public class DocumentoService {

    @Inject
    @Channel("ocr-processing")
    Emitter<OcrRequest> ocrEmitter;

    private void validarTamanho(List<String> paragrafos) {
        if (paragrafos == null || paragrafos.isEmpty()) return;
        
        long tamanhoEmBytes = paragrafos.stream()
                .mapToLong(p -> p.getBytes(StandardCharsets.UTF_8).length)
                .sum();
                
        if (tamanhoEmBytes > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("O texto do documento excede o limite de 2MB.");
        }
    }

    @Transactional
    public Documento criarDocumentoManual(String titulo, List<String> paragrafos) {
        validarTamanho(paragrafos);

        Documento doc = new Documento();
        doc.titulo = titulo;
        doc.conteudo = paragrafos;
        doc.status = "DIGITADO_MANUALMENTE";
        doc.persist();
        
        return doc;
    }

    @Transactional
    public Documento criarDocumentoEscaneado(String titulo, String language, String imageBase64) {
        Documento doc = new Documento();
        doc.titulo = titulo;
        doc.status = "EM_ESCANEAMENTO";
        doc.persist();
        
        OcrRequest ocrRequest = new OcrRequest(doc.id, language, imageBase64);
        ocrEmitter.send(ocrRequest);
        
        return doc;
    }

    @Transactional
    public void processarRetornoOcr(Long id, List<String> paragrafos, Double confiabilidade) {
        Documento doc = Documento.findById(id);
        if (doc != null) {
            try {
                validarTamanho(paragrafos);
                doc.conteudo = paragrafos;
                doc.confiabilidade = confiabilidade;
                doc.status = "ESCANEADO";
            } catch (IllegalArgumentException e) {
                doc.status = "ERRO_LIMITE_TAMANHO";
            }
        }
    }

    @Transactional
    public Documento editarDocumento(Long id, List<String> novosParagrafos) {
        Documento doc = Documento.findById(id);
        if (doc != null) {
            validarTamanho(novosParagrafos);
            doc.conteudo = novosParagrafos;
            doc.status = "DIGITADO_MANUALMENTE";
        }
        return doc;
    }
}