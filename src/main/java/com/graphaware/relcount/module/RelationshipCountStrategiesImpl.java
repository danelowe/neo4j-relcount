package com.graphaware.relcount.module;


import com.graphaware.relcount.cache.DegreeCachingStrategy;
import com.graphaware.relcount.cache.NodePropertiesDegreeCachingStrategy;
import com.graphaware.relcount.compact.CompactionStrategy;
import com.graphaware.relcount.compact.ThresholdBasedCompactionStrategy;
import com.graphaware.relcount.count.OneForEach;
import com.graphaware.relcount.count.WeighingStrategy;
import com.graphaware.tx.event.improved.strategy.*;

/**
 * {@link RelationshipCountStrategies}, providing static factory method for a default configuration and "with"
 * methods for fluently overriding these with custom strategies.
 */
public class RelationshipCountStrategiesImpl extends BaseInclusionStrategies<RelationshipCountStrategiesImpl> implements RelationshipCountStrategies {

    private static final int DEFAULT_COMPACTION_THRESHOLD = 20;

    private final DegreeCachingStrategy degreeCachingStrategy;
    private final CompactionStrategy compactionStrategy;
    private final WeighingStrategy weighingStrategy;

    /**
     * Create default strategies.
     *
     * @return default strategies.
     */
    public static RelationshipCountStrategiesImpl defaultStrategies() {
        return new RelationshipCountStrategiesImpl(
                IncludeNoNodes.getInstance(),
                IncludeNoNodeProperties.getInstance(),
                IncludeAllRelationships.getInstance(),
                IncludeAllRelationshipProperties.getInstance(),
                new NodePropertiesDegreeCachingStrategy(),
                new ThresholdBasedCompactionStrategy(DEFAULT_COMPACTION_THRESHOLD),
                OneForEach.getInstance()
        );
    }

    /**
     * Constructor.
     *
     * @param nodeInclusionStrategy         strategy.
     * @param nodePropertyInclusionStrategy strategy.
     * @param relationshipInclusionStrategy strategy.
     * @param relationshipPropertyInclusionStrategy
     *                                      strategy.
     * @param weighingStrategy              strategy.
     */
    private RelationshipCountStrategiesImpl(NodeInclusionStrategy nodeInclusionStrategy, NodePropertyInclusionStrategy nodePropertyInclusionStrategy, RelationshipInclusionStrategy relationshipInclusionStrategy, RelationshipPropertyInclusionStrategy relationshipPropertyInclusionStrategy, DegreeCachingStrategy degreeCachingStrategy, CompactionStrategy compactionStrategy, WeighingStrategy weighingStrategy) {
        super(nodeInclusionStrategy, nodePropertyInclusionStrategy, relationshipInclusionStrategy, relationshipPropertyInclusionStrategy);
        this.degreeCachingStrategy = degreeCachingStrategy;
        this.compactionStrategy = compactionStrategy;
        this.weighingStrategy = weighingStrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RelationshipCountStrategiesImpl newInstance(NodeInclusionStrategy nodeInclusionStrategy, NodePropertyInclusionStrategy nodePropertyInclusionStrategy, RelationshipInclusionStrategy relationshipInclusionStrategy, RelationshipPropertyInclusionStrategy relationshipPropertyInclusionStrategy) {
        return new RelationshipCountStrategiesImpl(nodeInclusionStrategy, nodePropertyInclusionStrategy, relationshipInclusionStrategy, relationshipPropertyInclusionStrategy, getDegreeCachingStrategy(), getCompactionStrategy(), getWeighingStrategy());
    }

    /**
     * Reconfigure this instance to use a custom degree caching strategy.
     *
     * @param degreeCachingStrategy to use.
     * @return reconfigured strategies.
     */
    public RelationshipCountStrategiesImpl with(DegreeCachingStrategy degreeCachingStrategy) {
        return new RelationshipCountStrategiesImpl(getNodeInclusionStrategy(), getNodePropertyInclusionStrategy(), getRelationshipInclusionStrategy(), getRelationshipPropertyInclusionStrategy(), degreeCachingStrategy, getCompactionStrategy(), getWeighingStrategy());
    }

    /**
     * Reconfigure this instance to use a custom compaction strategy.
     *
     * @param compactionStrategy to use.
     * @return reconfigured strategies.
     */
    public RelationshipCountStrategiesImpl with(CompactionStrategy compactionStrategy) {
        return new RelationshipCountStrategiesImpl(getNodeInclusionStrategy(), getNodePropertyInclusionStrategy(), getRelationshipInclusionStrategy(), getRelationshipPropertyInclusionStrategy(), getDegreeCachingStrategy(), compactionStrategy, getWeighingStrategy());
    }

    /**
     * Reconfigure this instance to use a {@link ThresholdBasedCompactionStrategy} with a different threshold.
     *
     * @param threshold to use.
     * @return reconfigured strategies.
     */
    public RelationshipCountStrategiesImpl withThreshold(int threshold) {
        return new RelationshipCountStrategiesImpl(getNodeInclusionStrategy(), getNodePropertyInclusionStrategy(), getRelationshipInclusionStrategy(), getRelationshipPropertyInclusionStrategy(), getDegreeCachingStrategy(), new ThresholdBasedCompactionStrategy(threshold), getWeighingStrategy());
    }

    /**
     * Reconfigure this instance to use a custom relationship weighing strategy.
     *
     * @param weighingStrategy to use.
     * @return reconfigured strategies.
     */
    public RelationshipCountStrategiesImpl with(WeighingStrategy weighingStrategy) {
        return new RelationshipCountStrategiesImpl(getNodeInclusionStrategy(), getNodePropertyInclusionStrategy(), getRelationshipInclusionStrategy(), getRelationshipPropertyInclusionStrategy(), getDegreeCachingStrategy(), getCompactionStrategy(), weighingStrategy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DegreeCachingStrategy getDegreeCachingStrategy() {
        return degreeCachingStrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompactionStrategy getCompactionStrategy() {
        return compactionStrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WeighingStrategy getWeighingStrategy() {
        return weighingStrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String asString() {
        return super.asString() + ";"
                + degreeCachingStrategy.asString() + ";"
                + compactionStrategy.asString() + ";"
                + weighingStrategy.asString();
    }
}
