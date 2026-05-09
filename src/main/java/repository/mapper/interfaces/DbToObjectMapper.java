package repository.mapper.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Interface to convert data from ResultSet to Java object
 */

public interface DbToObjectMapper<T> {
    T map(ResultSet rs) throws SQLException;
}
