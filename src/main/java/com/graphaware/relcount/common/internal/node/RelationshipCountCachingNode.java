package com.graphaware.relcount.common.internal.node;

import com.graphaware.propertycontainer.dto.common.relationship.HasTypeAndDirection;

import java.util.Map;

/**
 * Internal {@link org.neo4j.graphdb.Node} wrapper responsible for caching relationship counts on that node. Changes are
 * only written to the database after {@link #flush()} has been called.
 *
 * @param <CACHED> type of the cached counts.
 */
public interface RelationshipCountCachingNode<CACHED extends HasTypeAndDirection> {

    /**
     * ID of the wrapped Neo4j {@link org.neo4j.graphdb.Node}.
     *
     * @return ID.
     */
    long getId();

    /**
     * Get all relationship counts cached on the node. No aggregation is performed, this is the raw data as stored.
     *
     * @return cached relationship counts (key = relationship representation, value = count).
     */
    Map<CACHED, Integer> getCachedCounts();

    /**
     * Increment a cached relationship count on the node by a delta.
     *
     * @param relationship representation of a relationship.
     * @param delta        by how many to increment.
     */
    void incrementCount(CACHED relationship, int delta);

    /**
     * Decrement a cached relationship count on the node by delta.
     * Delete the cached relationship count if the count is 0 after the decrement.
     *
     * @param relationship representation of a relationship.
     * @param delta        by how many to decrement.
     * @throws com.graphaware.framework.NeedsInitializationException
     *          if a count reaches below 0.
     */
    void decrementCount(CACHED relationship, int delta);

    /**
     * Write all the changes to cached counts to the underlying node.
     */
    void flush();
}
