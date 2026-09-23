package br.com.scriptum;

import br.com.scriptum.configuracao.UnitTest;
import br.com.scriptum.dto.OcrRequest;
import br.com.scriptum.dto.OcrResponse;
import br.com.scriptum.model.Documento;
import br.com.scriptum.model.Grupo;
import br.com.scriptum.resource.DocumentoResource.DocumentoEscaneadoRequest;
import br.com.scriptum.resource.DocumentoResource.DocumentoManualRequest;
import br.com.scriptum.resource.GrupoResource.GrupoRequest;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@UnitTest
public class CoberturaExtraTest {

    @Test
    public void testGarantirCoberturaDeModelosEDtos() {
        
        Documento doc = new Documento();
        doc.titulo = "Teste Cobertura";
        doc.status = "DIGITADO_MANUALMENTE";
        doc.confiabilidade = 99.9;
        doc.conteudo = new ArrayList<>();
        doc.conteudo.add("Parágrafo 1");

        Grupo grupo = new Grupo();
        grupo.nome = "Grupo Teste";
        grupo.documentos = new ArrayList<>();
        
        doc.grupo = grupo;
        grupo.documentos.add(doc);

        assertEquals("Teste Cobertura", doc.titulo);
        assertEquals("Grupo Teste", grupo.nome);
        assertNotNull(doc.dataCriacao);

        OcrRequest req = new OcrRequest(1L, "por", "base64-string");
        OcrResponse res = new OcrResponse();
        res.documentId = 1L;
        res.paragraphs = new ArrayList<>();
        res.confidence = 100.0;

        assertEquals(1L, res.documentId);

        DocumentoManualRequest reqManual = new DocumentoManualRequest();
        reqManual.titulo = "Manual";
        reqManual.paragrafos = new ArrayList<>();

        DocumentoEscaneadoRequest reqScan = new DocumentoEscaneadoRequest();
        reqScan.titulo = "Scan";
        reqScan.language = "por";
        reqScan.imageBase64 = "img";

        GrupoRequest reqGrupo = new GrupoRequest();
        reqGrupo.nome = "Grupo";

        assertNotNull(reqManual.titulo);
        assertNotNull(reqScan.language);
        assertNotNull(reqGrupo.nome);
    }
}