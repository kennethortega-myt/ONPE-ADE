package pe.gob.onpe.scebackend.utils;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

@Component
public class AbstractSpec {

  /**
   * The Constant M_ID.
   */
  protected static final String M_ID = "id";

  /**
   * Join fetch. This method takes into account that the initial object could be a Root or a Join. Also, if the query is for counting, the
   * fetch is avoided.
   *
   * @param <T> the generic type
   * @param <U> the generic type
   * @param <V> the value type
   * @param query the query
   * @param object the object
   * @param field the field
   * @param joinType the join type
   * @return the join
   */
  @SuppressWarnings("unchecked")
  public <T, U, V> Join<U, V> joinFetch(CriteriaQuery<?> query, Object object, String field, JoinType joinType) {

    Join<U, V> join;

    // If the query is for counting, the fetch is avoided
    if (isCountQuery(query)) {
      if (object instanceof Root<?>) {
        join = ((Root<U>) object).join(field, joinType);
      } else {
        join = ((Join<T, U>) object).join(field, joinType);
      }
    } else {
      Fetch<U, V> fetch;
      if (object instanceof Root<?>) {
        fetch = ((Root<U>) object).fetch(field, joinType);
      } else {
        fetch = ((Join<T, U>) object).fetch(field, joinType);
      }
      join = (Join<U, V>) fetch;
    }

    return join;
  }

  /**
   * Join. This method takes into account that the initial object could be a Root or a Join.
   *
   * @param <T> the generic type
   * @param <U> the generic type
   * @param <V> the value type
   * @param object the object
   * @param field the field
   * @param joinType the join type
   * @return the join
   */
  @SuppressWarnings("unchecked")
  public <T, U, V> Join<U, V> join(Object object, String field, JoinType joinType) {

    Join<U, V> join;

    if (object instanceof Root<?>) {
      join = ((Root<U>) object).join(field, joinType);
    } else {
      join = ((Join<T, U>) object).join(field, joinType);
    }

    return join;
  }

  /**
   * Fetch. This method takes into account that the initial object could be a Root or a Join. Also, if the query is for counting, the fetch
   * is avoided.
   *
   * @param <T> the generic type
   * @param <U> the generic type
   * @param <V> the value type
   * @param query the query
   * @param object the object
   * @param field the field
   * @param joinType the join type
   * @return the fetch
   */
  @SuppressWarnings("unchecked")
  public <T, U, V> Fetch<U, V> fetch(CriteriaQuery<?> query, Object object, String field, JoinType joinType) {

    Fetch<U, V> fetch = null;

    // If the query is for counting, the fetch is avoided
    if (!isCountQuery(query)) {
      if (object instanceof Root<?>) {
        fetch = ((Root<U>) object).fetch(field, joinType);
      } else {
        fetch = ((Join<T, U>) object).fetch(field, joinType);
      }
    }

    return fetch;
  }

  /**
   * Add to the CriteriaBuilder a list of predicates collected by the AND operator.
   *
   * @param cb the cb
   * @param conditions the conditions
   * @return the predicate
   */
  public Predicate and(CriteriaBuilder cb, List<Predicate> conditions) {
    return cb.and(conditions.toArray(new Predicate[0]));
  }

  /**
   * Add to the CriteriaBuilder a list of predicates collected by the OR operator.
   *
   * @param cb the cb
   * @param conditions the conditions
   * @return the predicate
   */
  public Predicate or(CriteriaBuilder cb, List<Predicate> conditions) {
    return cb.or(conditions.toArray(new Predicate[0]));
  }

  /**
   * Like operation to find a substring in the database.
   *
   * @param cb the cb
   * @param value the value
   * @param pattern the patern
   * @return the predicate
   */
  public Predicate like(CriteriaBuilder cb, Expression<String> value, String pattern) {
    return cb.like(cb.upper(value), "%" + pattern.toUpperCase() + '%');
  }

  /**
   * Checks if is a count query.
   *
   * @param query the query
   * @return true, if is count query
   */
  public boolean isCountQuery(CriteriaQuery<?> query) {
    Class<?> resultType = query.getResultType();
    return resultType.equals(Long.class) || resultType.equals(long.class);
  }

}

