package br.com.scriptum.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@QuarkusTest
public class GrupoResourceTest {

    @Test
    public void testCriarGrupoComSucesso() {
        JsonObject request = new JsonObject().put("nome", "Matérias da Faculdade");

        given()
            .contentType(ContentType.JSON)
            .body(request.encode())
        .when()
            .post("/grupos")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("nome", is("Matérias da Faculdade"));
    }

    @Test
    public void testRejeitaGrupoSemNome() {
        JsonObject request = new JsonObject().put("nome", "");

        given()
            .contentType(ContentType.JSON)
            .body(request.encode())
        .when()
            .post("/grupos")
        .then()
            .statusCode(400);
    }

    @Test
    public void testListarGrupos() {
        given()
        .when()
            .get("/grupos")
        .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(0));
    }
}