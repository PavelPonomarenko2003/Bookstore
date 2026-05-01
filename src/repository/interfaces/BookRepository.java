package repository.interfaces;

import entity.BookEntity;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends CrudRepository<BookEntity, Long>{

    Optional<BookEntity> findByTitle(String title);

}
