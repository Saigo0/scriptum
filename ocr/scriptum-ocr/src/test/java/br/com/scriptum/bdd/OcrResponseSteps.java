package br.com.scriptum.bdd;

import br.com.scriptum.DTO.response.OcrResponse;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OcrResponseSteps {

    private long documentId;
    private List<String> paragraphs;
    private OcrResponse response;
    private IllegalArgumentException exception;

    @Given("um documento {long} com os parágrafos {string}")
    public void umDocumentoComOsParagrafos(long documentId, String paragraphs) {
        this.documentId = documentId;
        this.paragraphs = List.of(paragraphs.split("\\|"));
    }

    @When("a resposta OCR é criada com confiança {string}")
    public void aRespostaOcrECriadaComConfianca(String confidence) {
        response = new OcrResponse(documentId, paragraphs, parseConfidence(confidence));
    }

    @When("uma resposta OCR é criada com confiança inválida {string}")
    public void umaRespostaOcrECriadaComConfiancaInvalida(String confidence) {
        exception = assertThrows(IllegalArgumentException.class,
                () -> new OcrResponse(documentId, paragraphs, parseConfidence(confidence)));
    }

    @Then("o identificador da resposta é {long}")
    public void oIdentificadorDaResposta(long expectedDocumentId) {
        assertEquals(expectedDocumentId, response.documentId());
    }

    @Then("a resposta contém {int} parágrafos")
    public void aRespostaContemParagrafos(int expectedParagraphCount) {
        assertEquals(expectedParagraphCount, response.paragraphs().size());
    }

    @Then("a confiança da resposta é {string}")
    public void aConfiancaDaResposta(String expectedConfidence) {
        assertEquals(parseConfidence(expectedConfidence), response.confidence());
    }

    @Then("a resposta é rejeitada por confiança inválida")
    public void aRespostaERejeitadaPorConfiancaInvalida() {
        assertEquals("confidence deve estar entre 0 e 100", exception.getMessage());
    }

    private static double parseConfidence(String confidence) {
        return "NaN".equalsIgnoreCase(confidence)
                ? Double.NaN
                : Double.parseDouble(confidence.replace(',', '.'));
    }
}
