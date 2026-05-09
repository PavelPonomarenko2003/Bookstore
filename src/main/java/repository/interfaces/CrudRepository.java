package repository.interfaces;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * That interface for all CRUD operations to comply with the principle DRY
 */

public interface CrudRepository<T, ID> {

    void save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    void delete(ID id);
}