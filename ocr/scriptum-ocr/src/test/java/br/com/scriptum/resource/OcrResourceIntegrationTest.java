package br.com.scriptum.resource;

import br.com.scriptum.support.OcrTestFixtures;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusTest
class OcrResourceIntegrationTest {

    @Test
    void rejeitaImagemBase64Invalida() {
        given()
                .contentType("application/json")
                .body("""
                        {"documentId":10,"language":"por","imageBase64":"%%%"}
                        """)
                .when()
                .post("/ocr")
                .then()
                .statusCode(400)
                .body(containsString("imagemBase64 não é um Base64 válido"));
    }

    @Test
    void rejeitaIdiomaNaoSuportado() {
        given()
                .contentType("application/json")
                .body("""
                        {"documentId":10,"language":"spa","imageBase64":"aA=="}
                        """)
                .when()
                .post("/ocr")
                .then()
                .statusCode(400);
    }

    @Test
    void retornaRespostaOcrParaImagemConfigurada() throws Exception {
        given()
                .contentType("application/json")
                .body("""
                        {"documentId":2026,"language":"eng","imageBase64":"%s"}
                        """.formatted(OcrTestFixtures.imagemBase64()))
                .when()
                .post("/ocr")
                .then()
                .statusCode(200)
                .body("documentId", equalTo(2026))
                .body("paragraphs.size()", greaterThan(0))
                .body("confidence", greaterThan(0f));
    }
}
