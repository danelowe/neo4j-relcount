package com.graphaware.relcount.perf;

import com.graphaware.framework.GraphAwareFramework;
import com.graphaware.relcount.full.module.FullRelationshipCountModule;
import org.junit.Ignore;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Relationship;

import java.io.IOException;

@Ignore
public class TwoPropsDeletePerformanceTest extends RelationshipDeletePerformanceTest {

    @Test
    public void plainDatabase() throws IOException {
        System.out.println("Plain Database:");
        measure(new DatabaseModifier() {
            @Override
            public void alterDatabase(GraphDatabaseService database) {
                //do nothing
            }
        }, "twoPropsPlainDatabaseDelete");
    }

    @Test
    public void fullRelcount() throws IOException {
        System.out.println("Full Relcount:");
        measure(new DatabaseModifier() {
            @Override
            public void alterDatabase(GraphDatabaseService database) {
                GraphAwareFramework framework = new GraphAwareFramework(database);
                framework.registerModule(new FullRelationshipCountModule());
                framework.start();
            }
        }, "twoPropsFullRelcountDelete");
    }

    @Override
    protected void createRelPropsIfNeeded(Relationship rel) {
        rel.setProperty("rating", RANDOM.nextInt(2));
        rel.setProperty("another", RANDOM.nextInt(2));
    }
}