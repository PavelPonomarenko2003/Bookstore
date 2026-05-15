package bookstore.service.impl;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.neo4j.driver.Values.parameters;

/**
 * That is book recommendations class building on unweighted, directed graph
 * using graph db neo4j (Launch using Docker)
 * instead of SQL-lang, neo4j use Cypher-lang
 * advantages of graph logic in here:
 * fast search through a huge amount of data, which significantly reduces the load on the system,
 * clear image in the form of a graph
 */

@Service
public class BookRecommendationService {

    private final Driver driver;

    public BookRecommendationService(Driver driver) {
        this.driver = driver;
    }

    public void addPurchase(Long userId, String bookTitle) {
        try (Session session = driver.session()) {
            session.run(
                    "MERGE (u:User {id: $uId}) " +
                            "MERGE (b:Book {title: $title}) " +
                            "MERGE (u)-[:PURCHASED]->(b)",
                    parameters("uId", userId, "title", bookTitle)
            );
        }
    }

    public List<String> getRecommendations(Long userId) {
        try (Session session = driver.session()) {
            String cypher =
                    "MATCH (u:User {id: $uId})-[:PURCHASED]->(b:Book)<-[:PURCHASED]-(others:User) " +
                            "MATCH (others)-[:PURCHASED]->(reco:Book) " +
                            "WHERE NOT (u)-[:PURCHASED]->(reco) " +
                            "RETURN reco.title AS title, count(*) AS strength " +
                            "ORDER BY strength DESC LIMIT 5";

            Result result = session.run(cypher, parameters("uId", userId));

            List<String> recommendations = result.list().stream()
                    .map(record -> record.get("title").asString())
                    .collect(Collectors.toList());

            System.out.println("For user: with id:  " + userId + " recommendations: " + recommendations);

            return recommendations;
        }
    }
}