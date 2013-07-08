package com.graphaware.neo4j.relcount.simple.api;

import com.graphaware.neo4j.dto.common.relationship.HasTypeAndDirection;
import com.graphaware.neo4j.relcount.common.manager.RelationshipCountManager;
import com.graphaware.neo4j.relcount.simple.handler.SimpleNaiveRelationshipCountManager;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.RelationshipType;

/**
 * A naive {@link com.graphaware.neo4j.relcount.full.api.FullRelationshipCounter} that counts matching relationships by inspecting all
 * {@link org.neo4j.graphdb.Node}'s {@link org.neo4j.graphdb.Relationship}s. It delegates the work to {@link com.graphaware.neo4j.relcount.full.manager.FullNaiveRelationshipCountManager}.
 * <p/>
 * Because relationships are counted on the fly (no caching performed), this can be used without any {@link org.neo4j.graphdb.event.TransactionEventHandler}s
 * and on already existing graphs.
 */
public class SimpleNaiveRelationshipCounter extends BaseSimpleRelationshipCounter {

    /**
     * Construct a new relationship counter.
     *
     * @param type      type of the relationships to count.
     * @param direction direction of the relationships to count.
     */
    public SimpleNaiveRelationshipCounter(RelationshipType type, Direction direction) {
        super(type, direction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RelationshipCountManager<HasTypeAndDirection> getRelationshipCountManager() {
        return new SimpleNaiveRelationshipCountManager();
    }
}
