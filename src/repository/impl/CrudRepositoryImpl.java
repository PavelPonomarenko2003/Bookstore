package repository.impl;

import entity.BaseEntity;
import repository.interfaces.CrudRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;

/**
 * That class implement our interface to realize all basic operations in DB
 * @param <T> all classes that will extend from BaseEntity to modify our id
 */

public class CrudRepositoryImpl<T extends BaseEntity>
        implements CrudRepository<T, Long> {

    protected final Map<Long, T> storageDB = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public void save(T entity) {
        if (entity.getId() == null) {
            entity.setId(idCounter++);
        }
        storageDB.put(entity.getId(), entity); // map's method will add or update our data
    }

    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(storageDB.get(id)); // to prevent NullPointerException
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storageDB.values()); // fake list for safety
    }

    @Override
    public void delete(Long id) {
        storageDB.remove(id);
    }
}
