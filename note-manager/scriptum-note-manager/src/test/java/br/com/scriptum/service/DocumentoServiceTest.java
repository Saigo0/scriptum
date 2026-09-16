package br.com.scriptum.service;

import br.com.scriptum.configuracao.UnitTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@UnitTest
public class DocumentoServiceTest {

    private final DocumentoService service = new DocumentoService();

    @Test
    public void deveLancarExcecaoQuandoTextoUltrapassar2MB() {
        
        String textoGigante = "*".repeat(2_100_000); 
        List<String> paragrafos = List.of(textoGigante);

        IllegalArgumentException excecao = assertThrows(
            IllegalArgumentException.class, 
            () -> service.criarDocumentoManual("Título", paragrafos)
        );

        assertEquals("O texto do documento excede o limite de 2MB.", excecao.getMessage());
    }
}