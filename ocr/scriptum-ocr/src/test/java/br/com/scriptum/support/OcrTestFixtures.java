package br.com.scriptum.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public final class OcrTestFixtures {

    private OcrTestFixtures() {
    }

    public static String imagemBase64() throws IOException {
        try (InputStream imagem = OcrTestFixtures.class.getResourceAsStream("/ocr-test.png")) {
            if (imagem == null) {
                throw new IOException("fixture de imagem OCR não encontrado");
            }
            return Base64.getEncoder().encodeToString(imagem.readAllBytes());
        }
    }
}
