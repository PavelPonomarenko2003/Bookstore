package repository.interfaces;

import entity.BookEntity;

import java.sql.SQLException;
import java.util.Optional;

public interface BookRepository extends CrudRepository<BookEntity, Long>{

    Optional<BookEntity> findByTitle(String title);
}
