package database.neo4j;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

/**
 * I will use neo4j to show user recommendations books
 * the foundation of that task is unweighted, directed graph
 */
public class Neo4jSessionFactoryConfiguration {

    // host, username/password for db neo4j
    // password has to be more than 8 symbols, that's requirement of neo4j
    private static final Driver driver = GraphDatabase.driver(
            "bolt://localhost:7687",
            AuthTokens.basic("neo4j", "root12345")
    );

    public static Driver getDriver() {
        return driver;
    }

    public static void close() {
        driver.close();
    }
}