package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import pe.gob.onpe.scebackend.model.orc.entities.VwPrEleccion;


@Repository
public class VwPrDistritalRepository  {
	
	 @PersistenceContext(unitName = "locationEntityManagerFactory")
	 private EntityManager entityManager;


	 public List<VwPrEleccion> findByVista(String nombreVista) {
	     Session session = entityManager.unwrap(Session.class);
	     String queryStr = "SELECT * FROM " + nombreVista + " e ";
	     Query<VwPrEleccion> query = session.createNativeQuery(queryStr, VwPrEleccion.class);
	     return query.getResultList();
	}
	
}
