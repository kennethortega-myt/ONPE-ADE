package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.spec.usuario;

import java.util.ArrayList;
import java.util.List;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Usuario;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.AbstractSpec;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UsuarioSpecImpl extends AbstractSpec implements UsuarioSpec {

  @Override
  public Specification<Usuario> filter(UsuarioFilter filter) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> conditions = new ArrayList<>();

      // Proceso
      if (StringUtils.isNotBlank(filter.getAcronimoProceso())) {
        conditions.add(
            criteriaBuilder.equal(root.get("acronimoProceso"), filter.getAcronimoProceso()));
      }

      // Centro cómputo
      if (StringUtils.isNotBlank(filter.getCentroComputo())) {
        conditions.add(
            criteriaBuilder.equal(root.get("centroComputo"), filter.getCentroComputo()));
      }

      // Usuario
      addLikeUpper(criteriaBuilder, root, conditions, "usuario", filter.getUsuario());

      // Número documento
      addLikeUpper(criteriaBuilder, root, conditions, "documento", filter.getDocumento());

      // Apellido Paterno
      addLikeUpper(criteriaBuilder, root, conditions, "apellidoPaterno", filter.getApellidoPaterno());

      // Apellido Materno
      addLikeUpper(criteriaBuilder, root, conditions, "apellidoMaterno", filter.getApellidoMaterno());

      // Nombres
      addLikeUpper(criteriaBuilder, root, conditions, "nombres", filter.getNombres());

      // Perfil
      if (StringUtils.isNotBlank(filter.getPerfil())) {
        conditions.add(
            criteriaBuilder.equal(root.get("perfil"), filter.getPerfil().toUpperCase()));
      }

      // Persona asignada
      personaAsignada(criteriaBuilder, root, conditions, filter.getPersonaAsignada());

      // Desincronizado SASA
      if (filter.getDesincronizadoSaza() != null) {
        Boolean desincronizado = false;
        if (filter.getDesincronizadoSaza().equals(1)) {
          desincronizado = true;
        }
        conditions.add(
            criteriaBuilder.equal(root.get("desincronizadoSasa"), desincronizado));
      }

      return and(criteriaBuilder, conditions);
    };
  }

  @Override
  public Specification<Usuario> filterSesionActiva(UsuarioFilter filter) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> conditions = new ArrayList<>();
      conditions.add(criteriaBuilder.equal(root.get("sesionActiva"), ConstantesComunes.SESION_ACTIVO));
      addLikeUpper(criteriaBuilder, root, conditions, "usuario", filter.getUsuario());
      return and(criteriaBuilder, conditions);
    };
  }

  private void addLikeUpper(
      CriteriaBuilder cb,
      Root<Usuario> root,
      List<Predicate> conditions,
      String field,
      String value) {

    if (StringUtils.isNotBlank(value)) {
      conditions.add(
          cb.like(root.get(field), "%" + value.toUpperCase() + "%"));
    }
  }

  private void personaAsignada(CriteriaBuilder cb, Root<Usuario> root, List<Predicate> conditions,
      Integer filtroPersonaAsignada) {

    if (filtroPersonaAsignada == null) {
      return;
    }

    conditions.add(
        cb.equal(root.get("personaAsignada"), filtroPersonaAsignada));
  }
}
