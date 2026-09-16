package br.com.scriptum.service;

import br.com.scriptum.model.Grupo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class GrupoService {

    @Transactional
    public Grupo criarGrupo(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do grupo não pode ser vazio.");
        }
        
        Grupo grupo = new Grupo();
        grupo.nome = nome;
        grupo.persist();
        
        return grupo;
    }

    public List<Grupo> listarGrupos() {
        return Grupo.listAll();
    }
}