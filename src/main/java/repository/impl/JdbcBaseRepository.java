package repository.impl;

import database.my_sql.DataSourceHikariConfiguration;
import entity.BaseEntity;
import exception.sql_exception.DataStorageException;
import repository.interfaces.CrudRepository;
import repository.mapper.interfaces.DbToObjectMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JdbcBaseRepository class will take all jdbc-logic with Connection, PreparedStatement and ResultSet
 * following DRY-principle
 * Hikari - create certain number pools of connections and then sharing it with our users
 * so, that is preventing overload our server from a lot of requests
 * PreparedStatement working to prevent basic SQL-injection
 * ResultSet - container that take care about our RAM, to prevent outOfMemoryException
 * consist filtered data from db and sending it one by one to our OS, that is reduce
 * the load on our db and prevent outOfMemoryException
 */
public abstract class JdbcBaseRepository<T extends BaseEntity, ID>
        implements CrudRepository<T, ID> {

    // universal SELECT
    protected <R> List<R> executeQuery(String sql, DbToObjectMapper<R> mapper, Object... params) {
        try (Connection connection = DataSourceHikariConfiguration.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            setParameters(preparedStatement, params);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<R> results = new ArrayList<>();
                while (resultSet.next()) {
                    results.add(mapper.map(resultSet));
                }
                return results;
            }
        } catch (SQLException exception) {
            throw new DataStorageException("Exception executing data select: " + sql, exception);
        }
    }


    // universal UPDATE / DELETE
    protected void executeUpdate(String sql, Object... params) {
        try (Connection conn = DataSourceHikariConfiguration.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setParameters(ps, params);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataStorageException("Exception executing data update: " + sql, e);
        }
    }

    // universal INSERT with ID returning (RETURN_GENERATED_KEYS)
     protected ID executeInsert(String sql, Object... params) {
        try (Connection conn = DataSourceHikariConfiguration.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setParameters(ps, params);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    Object key = keys.getObject(1);
                    if (key instanceof Number) {
                        return (ID) Long.valueOf(((Number) key).longValue());
                    }
                    return (ID) key;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DataStorageException("Exception executing data insert: " + sql, e);
        }
    }

    // Helper method for counting our "?" and insert data instead
    private void setParameters(PreparedStatement preparedStatement, Object... params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
        }
    }

}