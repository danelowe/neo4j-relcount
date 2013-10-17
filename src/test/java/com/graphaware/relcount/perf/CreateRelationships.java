package com.graphaware.relcount.perf;

import com.graphaware.framework.GraphAwareFramework;
import com.graphaware.performance.EnumParameter;
import com.graphaware.performance.Exponential;
import com.graphaware.performance.Parameter;
import com.graphaware.relcount.module.RelationshipCountModule;
import com.graphaware.relcount.module.RelationshipCountStrategiesImpl;
import com.graphaware.test.TestUtils;
import com.graphaware.tx.event.improved.strategy.IncludeNoRelationshipProperties;
import com.graphaware.tx.executor.NullItem;
import com.graphaware.tx.executor.batch.BatchTransactionExecutor;
import com.graphaware.tx.executor.batch.NoInputBatchTransactionExecutor;
import com.graphaware.tx.executor.batch.UnitOfWork;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Performance test for creating relationships.
 */
public class CreateRelationships extends RelcountPerformanceTest {

    private static final String BATCH_SIZE = "batchSize";

    private static final int NO_NODES = 100;
    private static final int NO_RELATIONSHIPS = 1000;

    enum FrameworkInvolvement {
        NO_FRAMEWORK,
        EMPTY_FRAMEWORK,
        RELCOUNT_NO_PROPS,
        FULL_RELCOUNT
    }

    private enum Properties {
        NO_PROPS,
        TWO_PROPS_NO_COMPACT,
        TWO_PROPS_COMPACT,
        FOUR_PROPS
    }

    @Override
    public String shortName() {
        return "createThousandRelationships";
    }

    @Override
    public String longName() {
        return "Create 1,000 Relationships Between Random Pairs of 100 Nodes";
    }

    @Override
    public List<Parameter> parameters() {
        List<Parameter> result = new LinkedList<>();

        result.add(new EnumParameter(PROPS, Properties.class));
        result.add(new EnumParameter(FW, FrameworkInvolvement.class));
        result.add(new Exponential(BATCH_SIZE, 10, 0, 3, 0.25));

        return result;
    }

    @Override
    public int dryRuns(Map<String, Object> stringObjectMap) {
        return 1;
    }

    @Override
    public int measuredRuns() {
        return 20;
    }

    @Override
    public Map<String, String> databaseParameters(Map<String, Object> params) {
        return null;
    }

    @Override
    public void prepareDatabase(GraphDatabaseService database, Map<String, Object> params) {
        FrameworkInvolvement frameworkInvolvement = (FrameworkInvolvement) params.get(FW);

        switch (frameworkInvolvement) {
            case EMPTY_FRAMEWORK:
                GraphAwareFramework framework = new GraphAwareFramework(database);
                framework.start();
                break;
            case RELCOUNT_NO_PROPS:
                framework = new GraphAwareFramework(database);
                framework.registerModule(new RelationshipCountModule(RelationshipCountStrategiesImpl.defaultStrategies().with(IncludeNoRelationshipProperties.getInstance())));
                framework.start();
                break;
            case FULL_RELCOUNT:
                framework = new GraphAwareFramework(database);
                framework.registerModule(new RelationshipCountModule());
                framework.start();
                break;
            default:
                //nothing
        }

        new NoInputBatchTransactionExecutor(database, 1000, NO_NODES, new UnitOfWork<NullItem>() {
            @Override
            public void execute(GraphDatabaseService database, NullItem input, int batchNumber, int stepNumber) {
                database.createNode();
            }
        }).execute();
    }

    @Override
    public long run(GraphDatabaseService database, final Map<String, Object> params) {
        final BatchTransactionExecutor executor = new NoInputBatchTransactionExecutor(database, (int) params.get(BATCH_SIZE), NO_RELATIONSHIPS, new UnitOfWork<NullItem>() {
            @Override
            public void execute(GraphDatabaseService database, NullItem input, int batchNumber, int stepNumber) {
                final Node node1 = randomNode(database, NO_NODES);
                final Node node2 = randomNode(database, NO_NODES);
                Relationship relationship = node1.createRelationshipTo(node2, randomType());

                if (params.get(PROPS).equals(Properties.TWO_PROPS_NO_COMPACT)) {
                    relationship.setProperty("rating", RANDOM.nextInt(2));
                    relationship.setProperty("another", RANDOM.nextInt(2));
                }

                if (params.get(PROPS).equals(Properties.TWO_PROPS_COMPACT) || params.get(PROPS).equals(Properties.FOUR_PROPS)) {
                    relationship.setProperty("rating", RANDOM.nextInt(4) + 1);
                    relationship.setProperty("timestamp", RANDOM.nextLong());
                }

                if (params.get(PROPS).equals(Properties.FOUR_PROPS)) {
                    relationship.setProperty("3", RANDOM.nextLong());
                    relationship.setProperty("4", RANDOM.nextLong());
                }

            }
        });

        return TestUtils.time(new TestUtils.Timed() {
            @Override
            public void time() {
                executor.execute();
            }
        });
    }

    @Override
    public RebuildDatabase rebuildDatabase() {
        return RebuildDatabase.AFTER_EVERY_RUN;
    }

    @Override
    public boolean rebuildDatabase(Map<String, Object> stringObjectMap) {
        return false;
    }
}
