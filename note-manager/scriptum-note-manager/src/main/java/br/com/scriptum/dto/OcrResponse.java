package br.com.scriptum.dto;

import java.util.List;

public class OcrResponse {
    public Long documentId;
    public List<String> paragraphs;
    public double confidence;
}
