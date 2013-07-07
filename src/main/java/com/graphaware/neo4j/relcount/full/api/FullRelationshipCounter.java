package com.graphaware.neo4j.relcount.full.api;

import com.graphaware.neo4j.dto.common.property.MakesCopyWithProperty;
import com.graphaware.neo4j.dto.common.relationship.DirectedRelationship;
import com.graphaware.neo4j.dto.string.property.CopyMakingSerializableProperties;
import com.graphaware.neo4j.relcount.common.api.RelationshipCounter;

/**
 * A {@link RelationshipCounter} capable of counting relationships based on their "full" description, i.e. type, direction,
 * and an arbitrary number of properties (key-value pairs).
 * <p/>
 * The fluent interface of this counter allows such "description" to be constructed by calling the constructor and then
 * successively {@link #with(String, Object)} to add properties. Finally, by calling {@link #count(org.neo4j.graphdb.Node)},
 * relationship count for the specified relationship on the node is returned.
 */
public interface FullRelationshipCounter extends RelationshipCounter, DirectedRelationship<String, CopyMakingSerializableProperties>, MakesCopyWithProperty<FullRelationshipCounter> {
}