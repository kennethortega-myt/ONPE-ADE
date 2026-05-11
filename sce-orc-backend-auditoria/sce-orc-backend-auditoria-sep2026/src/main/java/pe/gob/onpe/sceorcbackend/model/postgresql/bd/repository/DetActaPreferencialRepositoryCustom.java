package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.stereotype.Repository;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaPreferencial;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Repository
public class DetActaPreferencialRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    public DetActaPreferencialRepositoryCustom(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<DetActaPreferencial> getListDetActaPreferencialByIdDetActa(Long idDetActa) {
        // Utilizamos una consulta JPQL para seleccionar los datos necesarios
        String jpql = "SELECT d FROM DetActaPreferencial d WHERE d.id = :idDetActa";
        TypedQuery<DetActaPreferencial> query = entityManager.createQuery(jpql, DetActaPreferencial.class);
        query.setParameter("idDetActa", idDetActa);
        return query.getResultList();
    }
    
}
