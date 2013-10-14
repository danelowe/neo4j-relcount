/*
 * Copyright (c) 2013 GraphAware
 *
 * This file is part of GraphAware.
 *
 * GraphAware is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received a copy of
 * the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.graphaware.relcount.count;

import com.graphaware.description.relationship.LazyRelationshipDescription;
import com.graphaware.description.relationship.RelationshipDescription;
import com.graphaware.relcount.module.RelationshipCountStrategies;
import com.graphaware.relcount.module.RelationshipCountStrategiesImpl;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import static org.neo4j.graphdb.Direction.BOTH;

/**
 * A naive {@link FullRelationshipCounter} that counts matching relationships by inspecting all {@link org.neo4j.graphdb.Node}'s {@link org.neo4j.graphdb.Relationship}s.
 * <p/>
 * <b>Full</b> relationship counter means that it inspects relationship types, directions, and properties.
 * <p/>
 * Because relationships are counted on the fly (no caching performed), this can be used without the
 * {@link com.graphaware.framework.GraphAwareFramework} and/or any {@link com.graphaware.framework.GraphAwareModule}s.
 * <p/>
 * This counter always returns a count, never throws {@link UnableToCountException}.
 */
public class NaiveRelationshipCounter implements RelationshipCounter {

    private final RelationshipCountStrategies relationshipCountStrategies;

    /**
     * Construct a new relationship counter with default strategies.
     */
    public NaiveRelationshipCounter() {
        this(RelationshipCountStrategiesImpl.defaultStrategies());
    }

    /**
     * Construct a new relationship counter. Use when custom {@link com.graphaware.relcount.module.RelationshipCountStrategies} have been used for the
     * {@link com.graphaware.relcount.module.RelationshipCountModule}. Alternatively, it might be easier
     * use {@link com.graphaware.relcount.module.RelationshipCountModule#naiveCounter(org.neo4j.graphdb.RelationshipType, org.neo4j.graphdb.Direction)}.
     *
     * @param relationshipCountStrategies strategies, of which only {@link com.graphaware.relcount.strategy.RelationshipPropertiesExtractionStrategy} is used.
     */
    public NaiveRelationshipCounter(RelationshipCountStrategies relationshipCountStrategies) {
        this.relationshipCountStrategies = relationshipCountStrategies;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int count(Node node, RelationshipDescription description) {
        //optimization - don't load properties if it is unnecessary
//        if (description.getPropertiesDescription().isEmpty() && relationshipCountStrategies.getRelationshipPropertiesExtractionStrategy().equals(ExtractAllRelationshipProperties.getInstance())) {
//            return new FullNaiveRelationshipCountingNode(node, ExtractNoRelationshipProperties.getInstance(), relationshipCountStrategies.getRelationshipWeighingStrategy()).getRelationshipCount(new WildcardRelationshipQueryDescription(this));
//        }                              //todo put back

        int result = 0;

        for (Relationship candidateRelationship : node.getRelationships(description.getDirection(), description.getType())) {
            RelationshipDescription candidate = new LazyRelationshipDescription(candidateRelationship, node);

            if (candidate.isMoreSpecificThan(description)) {
                int relationshipWeight = relationshipCountStrategies.getWeighingStrategy().getRelationshipWeight(candidateRelationship, node);
                result = result + relationshipWeight;

                //double count loops if looking for BOTH
                if (BOTH.equals(description.getDirection()) && BOTH.equals(candidate.getDirection())) {
                    result = result + relationshipWeight;
                }
            }
        }

        return result;
    }
}
