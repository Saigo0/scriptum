package br.com.scriptum;

import br.com.scriptum.model.Documento;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import br.com.scriptum.configuracao.IntegrationTest;
import java.util.List;
import io.quarkus.narayana.jta.QuarkusTransaction;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@IntegrationTest
public class DocumentoIntegracaoTest {

    @Inject
    @Any
    InMemoryConnector connector;

    @BeforeEach
    public void setup() {
        connector.sink("ocr-processing").clear();
    }

    @Test
    public void testCriarDocumentoManualComSucesso() {
        JsonObject request = new JsonObject()
                .put("titulo", "Anotação da Aula")
                .put("paragrafos", List.of("Primeiro parágrafo", "Segundo parágrafo"));

        given()
            .contentType(ContentType.JSON)
            .body(request.encode())
        .when()
            .post("/documentos/manual")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("status", is("DIGITADO_MANUALMENTE"))
            .body("titulo", is("Anotação da Aula"));
    }

    @Test
    public void testRejeitaDocumentoMaiorQue2MB() {
        String textoGigante = "*".repeat(2_100_000); 
        
        JsonObject request = new JsonObject()
                .put("titulo", "Documento Pesado")
                .put("paragrafos", List.of(textoGigante));

        given()
            .contentType(ContentType.JSON)
            .body(request.encode())
        .when()
            .post("/documentos/manual")
        .then()
            .statusCode(400); 
    }

    @Test
    public void testCriarDocumentoEscaneadoEnviaMensagemParaFila() {
        JsonObject request = new JsonObject()
                .put("titulo", "Foto do Quadro")
                .put("language", "por")
                .put("imageBase64", "base64-falso-aqui");

        given()
            .contentType(ContentType.JSON)
            .body(request.encode())
        .when()
            .post("/documentos/escaneado")
        .then()
            .statusCode(202) 
            .body("status", is("EM_ESCANEAMENTO"));

        InMemorySink<JsonObject> sink = connector.sink("ocr-processing");
        assertEquals(1, sink.received().size());
    }

    @Test
    public void testEditarDocumentoComSucesso() {

        Documento doc = new Documento();
        doc.titulo = "Documento Original";
        doc.status = "DIGITADO_MANUALMENTE";
        QuarkusTransaction.requiringNew().run(() -> doc.persist());

        List<String> novosParagrafos = List.of("Texto atualizado na edição.");

        given()
            .contentType(ContentType.JSON)
            .body(novosParagrafos)
        .when()
            .put("/documentos/" + doc.id)
        .then()
            .statusCode(200) 
            .body("conteudo[0]", is("Texto atualizado na edição."))
            .body("status", is("DIGITADO_MANUALMENTE"));
    }

    @Test
    public void testEditarDocumentoInexistenteRetorna404() {
        List<String> novosParagrafos = List.of("Não importa");

        given()
            .contentType(ContentType.JSON)
            .body(novosParagrafos)
        .when()
            .put("/documentos/9999999") 
        .then()
            .statusCode(404); 
    }
}