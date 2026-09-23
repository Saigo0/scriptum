package br.com.scriptum.dto;

import jakarta.validation.constraints.NotBlank;

public class NoteRequest {
    @NotBlank(message = "Title cannot be blank")
    public String titulo;
    
    public String imageBase64;
    
    public String language = "por"; 
}
