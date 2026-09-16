package br.com.scriptum.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

@Entity
public class Note extends PanacheEntity {

    @Column(nullable = false)
    public String title;
    
    @Column(columnDefinition = "TEXT")
    public String content;

    @Column(nullable = false)
    public String status;

    public LocalDateTime createdAt;
    
    public Note() {
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
    }
}
