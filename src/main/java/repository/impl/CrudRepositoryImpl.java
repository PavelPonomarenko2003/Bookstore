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

    protected final Map<Long, T> storageDB;
    private Long idCounter = 1L;

    public CrudRepositoryImpl(List<T> initialData) {
        this.storageDB = new HashMap<>();
        if (initialData != null) {
            for (T entity : initialData) {
                this.storageDB.put(entity.getId(), entity);
                if (entity.getId() >= idCounter) {
                    idCounter = entity.getId() + 1;
                }
            }
        }
    }

    public CrudRepositoryImpl() {
        this.storageDB = new HashMap<>();
    }

    @Override
    public void save(T entity) {
        if (entity.getId() == null) {
            entity.setId(idCounter++);
        }
        storageDB.put(entity.getId(), entity);
    }

    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(storageDB.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storageDB.values());
    }

    @Override
    public void delete(Long id) {
        storageDB.remove(id);
    }
}

