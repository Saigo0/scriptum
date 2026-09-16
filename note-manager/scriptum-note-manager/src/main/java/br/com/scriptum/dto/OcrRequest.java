package br.com.scriptum.dto;

public class OcrRequest {
    public Long documentId;
    public String language;
    public String imageBase64;
    
    public OcrRequest(Long documentId, String language, String imageBase64) {
        this.documentId = documentId;
        this.language = language;
        this.imageBase64 = imageBase64;
    }
}
