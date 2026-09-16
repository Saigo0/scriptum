package br.com.scriptum.resource;

import br.com.scriptum.dto.NoteRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@QuarkusTest
public class NoteResourceTest {

    @Test
    public void testCriarNotaComSucesso() {
        NoteRequest request = new NoteRequest();
        request.title = "Minha primeira nota via API";
        request.language = "por";
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/notes")
        .then()
            .statusCode(201) 
            .body("id", notNullValue())
            .body("title", is("Minha primeira nota via API"))
            .body("status", is("COMPLETED"));
    }

    @Test
    public void testListarTodasAsNotas() {
        given()
        .when()
            .get("/notes")
        .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    public void testBuscarNotaInexistenteRetorna404() {
        given()
        .when()
            .get("/notes/999999")
        .then()
            .statusCode(404);
    }
}