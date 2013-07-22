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

package com.graphaware.neo4j.relcount.full.logic;

import com.graphaware.neo4j.relcount.common.logic.NaiveRelationshipCountReader;
import com.graphaware.neo4j.relcount.common.logic.RelationshipCountReader;
import com.graphaware.neo4j.relcount.full.dto.relationship.LiteralRelationshipDescription;
import com.graphaware.neo4j.relcount.full.dto.relationship.RelationshipDescription;
import com.graphaware.neo4j.relcount.full.strategy.RelationshipCountStrategiesImpl;
import com.graphaware.neo4j.relcount.full.strategy.RelationshipPropertiesExtractionStrategy;
import com.graphaware.neo4j.relcount.full.strategy.RelationshipWeighingStrategy;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import java.util.Map;

/**
 * {@link com.graphaware.neo4j.relcount.common.logic.RelationshipCountReader} that counts relationships by traversing them (performs no caching). It is "full" in
 * the sense that it cares about {@link org.neo4j.graphdb.RelationshipType}s, {@link org.neo4j.graphdb.Direction}s, and properties.
 */
public class FullNaiveRelationshipCountReader extends NaiveRelationshipCountReader<RelationshipDescription> implements RelationshipCountReader<RelationshipDescription> {

    private final RelationshipPropertiesExtractionStrategy extractionStrategy;
    private final RelationshipWeighingStrategy weighingStrategy;

    /**
     * Construct a new counter with default strategies for extracting properties and weighing relationships.
     */
    public FullNaiveRelationshipCountReader() {
        this(RelationshipCountStrategiesImpl.defaultStrategies().getRelationshipPropertiesExtractionStrategy());
    }

    /**
     * Construct a new counter with default properties extraction strategy.
     *
     * @param weighingStrategy strategy for weighing each relationship.
     */
    public FullNaiveRelationshipCountReader(RelationshipWeighingStrategy weighingStrategy) {
        this(RelationshipCountStrategiesImpl.defaultStrategies().getRelationshipPropertiesExtractionStrategy(), weighingStrategy);
    }

    /**
     * Construct a new counter with default relationship weighing strategy.
     *
     * @param extractionStrategy strategy for extracting properties from relationships.
     */
    public FullNaiveRelationshipCountReader(RelationshipPropertiesExtractionStrategy extractionStrategy) {
        this(extractionStrategy, RelationshipCountStrategiesImpl.defaultStrategies().getRelationshipWeighingStrategy());
    }

    /**
     * Construct a new counter.
     *
     * @param extractionStrategy strategy for extracting properties from relationships.
     * @param weighingStrategy   strategy for weighing each relationship.
     */
    public FullNaiveRelationshipCountReader(RelationshipPropertiesExtractionStrategy extractionStrategy, RelationshipWeighingStrategy weighingStrategy) {
        this.extractionStrategy = extractionStrategy;
        this.weighingStrategy = weighingStrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean candidateMatchesDescription(RelationshipDescription candidate, RelationshipDescription description) {
        return candidate.isMoreSpecificThan(description);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean continueAfterFirstLookupMatch() {
        return true; //it is naive => need to traverse all relationship to find all matches.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RelationshipDescription newCandidate(Relationship relationship, Node pointOfView) {
        Map<String, String> extractedProperties = extractionStrategy.extractProperties(relationship, pointOfView);
        return new LiteralRelationshipDescription(relationship, pointOfView, extractedProperties);   //direction can resolve to both, but that's ok for non-cached relationships
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int relationshipWeight(Relationship relationship, Node pointOfView) {
        return weighingStrategy.getRelationshipWeight(relationship, pointOfView);
    }
}
