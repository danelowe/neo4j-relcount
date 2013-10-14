package com.graphaware.relcount.cache;

import com.graphaware.description.relationship.DetachedRelationshipDescription;
import com.graphaware.tx.executor.single.SimpleTransactionExecutor;
import com.graphaware.tx.executor.single.TransactionExecutor;
import com.graphaware.tx.executor.single.VoidReturningCallback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.graphaware.description.predicate.Predicates.any;
import static com.graphaware.description.predicate.Predicates.equalTo;
import static com.graphaware.description.relationship.RelationshipDescriptionFactory.literal;
import static org.junit.Assert.*;
import static org.neo4j.graphdb.Direction.INCOMING;
import static org.neo4j.graphdb.Direction.OUTGOING;

/**
 * Unit test for {@link com.graphaware.relcount.cache.DegreeCachingStrategy}s.
 */
public abstract class DegreeCachingStrategyTest {

    private GraphDatabaseService database;
    private TransactionExecutor txExecutor;

    @Before
    public void setUp() {
        database = new TestGraphDatabaseFactory().newImpermanentDatabase();
        txExecutor = new SimpleTransactionExecutor(database);
    }

    @After
    public void tearDown() {
        database.shutdown();
    }

    @Test
    public void shouldReadEmptyDegreesWhenNoDegreesHaveBeenWritten() {
        assertTrue(strategy().readDegrees(database.getNodeById(0), "TEST").isEmpty());
    }

    @Test
    public void shouldReadEmptyDegreesWhenEmptyDegreesHaveBeenWritten() {
        txExecutor.executeInTransaction(new VoidReturningCallback() {
            @Override
            protected void doInTx(GraphDatabaseService database) {
                strategy().writeDegrees(database.getNodeById(0), "TEST", new HashMap<DetachedRelationshipDescription, Integer>(), new HashSet<DetachedRelationshipDescription>(), new HashSet<DetachedRelationshipDescription>());
            }
        });

        assertTrue(strategy().readDegrees(database.getNodeById(0), "TEST").isEmpty());
    }

    @Test
    public void readDegreesShouldEqualToWrittenOnes() {
        final Map<DetachedRelationshipDescription, Integer> cachedDegrees = new HashMap<>();
        cachedDegrees.put(literal("TEST", OUTGOING).with("k1", equalTo("v1")), 1);
        cachedDegrees.put(literal("TEST", OUTGOING).with("k1", equalTo("v2")), 2);
        cachedDegrees.put(literal("TEST", INCOMING).with("k2", any()), 3);

        final Set<DetachedRelationshipDescription> updatedDegrees = new HashSet<>();
        updatedDegrees.add(literal("TEST", OUTGOING).with("k1", equalTo("v1")));
        updatedDegrees.add(literal("TEST", OUTGOING).with("k1", equalTo("v2")));
        updatedDegrees.add(literal("TEST", INCOMING).with("k2", any()));

        txExecutor.executeInTransaction(new VoidReturningCallback() {
            @Override
            protected void doInTx(GraphDatabaseService database) {
                strategy().writeDegrees(database.getNodeById(0), "TEST", cachedDegrees, updatedDegrees, new HashSet<DetachedRelationshipDescription>());
            }
        });

        assertEquals(cachedDegrees, strategy().readDegrees(database.getNodeById(0), "TEST"));

        final Map<DetachedRelationshipDescription, Integer> cachedDegrees2 = new HashMap<>();
        cachedDegrees2.put(literal("TEST", OUTGOING).with("k1", equalTo("v1")), 1);
        cachedDegrees2.put(literal("TEST", INCOMING).with("k2", any()), 2);

        final Set<DetachedRelationshipDescription> updatedDegrees2 = new HashSet<>();
        updatedDegrees2.add(literal("TEST", INCOMING).with("k2", any()));

        final Set<DetachedRelationshipDescription> removedDegrees2 = new HashSet<>();
        removedDegrees2.add(literal("TEST", OUTGOING).with("k1", equalTo("v2")));

        txExecutor.executeInTransaction(new VoidReturningCallback() {
            @Override
            protected void doInTx(GraphDatabaseService database) {
                strategy().writeDegrees(database.getNodeById(0), "TEST", cachedDegrees2, updatedDegrees2, removedDegrees2);
            }
        });

        assertEquals(cachedDegrees2, strategy().readDegrees(database.getNodeById(0), "TEST"));
    }

    @Test
    public void strategiesShouldHaveDifferentStringRepresentations() {
        assertNotSame(new SingleNodePropertyDegreeCachingStrategy().asString(), new NodePropertiesDegreeCachingStrategy().asString());
    }

    protected abstract DegreeCachingStrategy strategy();
}
