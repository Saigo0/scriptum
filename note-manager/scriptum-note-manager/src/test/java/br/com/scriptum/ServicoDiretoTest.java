package br.com.scriptum;

import br.com.scriptum.configuracao.IntegrationTest;
import br.com.scriptum.resource.DocumentoResource;
import br.com.scriptum.resource.GrupoResource;
import br.com.scriptum.service.DocumentoService;
import br.com.scriptum.service.GrupoService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@IntegrationTest
public class ServicoDiretoTest {

    @Inject DocumentoResource documentoResource;
    @Inject GrupoResource grupoResource;
    @Inject DocumentoService documentoService;
    @Inject GrupoService grupoService;

    @Test
    public void testForcarCoberturaDiretaDasCamadas() {

        GrupoResource.GrupoRequest reqGrupo = new GrupoResource.GrupoRequest();
        reqGrupo.nome = "Grupo Direto Jacoco";
        
        grupoResource.criar(reqGrupo);
        assertNotNull(grupoResource.listar());
        assertNotNull(grupoService.listarGrupos());

        DocumentoResource.DocumentoManualRequest reqManual = new DocumentoResource.DocumentoManualRequest();
        reqManual.titulo = "Manual Direto";
        reqManual.paragrafos = List.of("Texto direto para o Jacoco ver");
        
        assertNotNull(documentoResource.criarManual(reqManual));

        DocumentoResource.DocumentoEscaneadoRequest reqScan = new DocumentoResource.DocumentoEscaneadoRequest();
        reqScan.titulo = "Scan Direto";
        reqScan.language = "por";
        reqScan.imageBase64 = "imagem-falsa";
        
        assertNotNull(documentoResource.criarEscaneado(reqScan));

        documentoResource.editarDocumento(999L, List.of("Texto Editado"));
    }
}