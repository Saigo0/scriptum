package br.com.scriptum.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Documento extends PanacheEntity {

    @Column(nullable = false)
    public String titulo;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "documento_paragrafos", joinColumns = @JoinColumn(name = "documento_id"))
    @Column(name = "paragrafo", columnDefinition = "TEXT")
    public List<String> conteudo = new ArrayList<>();

    @Column(nullable = false)
    public String status; 

    public Double confiabilidade;

    @ManyToOne
    @JoinColumn(name = "grupo_id")
    public Grupo grupo;

    public LocalDateTime dataCriacao;

    public Documento() {
        this.dataCriacao = LocalDateTime.now();
    }
}