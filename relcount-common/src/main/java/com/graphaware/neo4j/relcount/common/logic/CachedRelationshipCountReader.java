package com.graphaware.neo4j.relcount.common.logic;

import com.graphaware.neo4j.dto.common.relationship.HasTypeAndDirection;
import com.graphaware.neo4j.framework.GraphAwareFramework;
import com.graphaware.neo4j.framework.config.FrameworkConfiguration;
import org.neo4j.graphdb.Node;

import java.util.Map;
import java.util.TreeMap;

/**
 * Base-class for {@link RelationshipCountReader} implementations that read relationship counts cached as
 * {@link Node}'s properties, written by a subclass of {@link BaseRelationshipCountCache}.
 *
 * @param <CACHED>      type of cached relationship representation.
 * @param <DESCRIPTION> type of relationship description that can be used to query relationship counts for nodes.
 */
public abstract class CachedRelationshipCountReader<CACHED extends HasTypeAndDirection & Comparable<CACHED>, DESCRIPTION extends HasTypeAndDirection> extends BaseRelationshipCountReader<CACHED, DESCRIPTION> {

    private final String id;
    private final FrameworkConfiguration config;

    /**
     * Construct a new reader.
     *
     * @param id     of the {@link com.graphaware.neo4j.relcount.common.module.RelationshipCountModule} this reader belongs to.
     * @param config of the {@link GraphAwareFramework} that the module is registered with.
     */
    protected CachedRelationshipCountReader(String id, FrameworkConfiguration config) {
        this.id = id;
        this.config = config;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Gets all relationship counts cached as the node's properties. Ignores the description, always returns all cached
     * counts. No aggregation is performed, this is the raw data as stored
     * (as opposed to {@link #getRelationshipCount(com.graphaware.neo4j.dto.common.relationship.HasTypeAndDirection, org.neo4j.graphdb.Node)}).
     * The returned map is sorted so that it can be iterated in order (e.g. specific to general).
     */
    @Override
    public Map<CACHED, Integer> getCandidates(DESCRIPTION description, Node node) {
        Map<CACHED, Integer> result = new TreeMap<>();
        for (String key : node.getPropertyKeys()) {
            if (key.startsWith(config.createPrefix(id))) {
                result.put(newCachedRelationship(key, config.createPrefix(id), config.separator()), (Integer) node.getProperty(key));
            }
        }
        return result;
    }

    /**
     * Create a cached relationship representation from a String representation of the cached relationship, coming from
     * a node's property key.
     *
     * @param string    string representation of the cached relationship.
     * @param prefix    to be removed from the string representation before conversion.
     * @param separator delimiter of information in the string.
     * @return object representation of the cached relationship.
     */
    protected abstract CACHED newCachedRelationship(String string, String prefix, String separator);
}
