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

package com.graphaware.neo4j.relcount.simple.handler;

import com.graphaware.neo4j.relcount.common.handler.RelationshipCountCachingTransactionEventHandler;
import com.graphaware.neo4j.relcount.simple.dto.TypeAndDirectionDescription;
import com.graphaware.neo4j.relcount.simple.dto.TypeAndDirectionDescriptionImpl;
import com.graphaware.neo4j.relcount.simple.manager.SimpleCachingRelationshipCountManager;
import com.graphaware.neo4j.tx.event.strategy.RelationshipInclusionStrategy;
import com.graphaware.neo4j.utils.DirectionUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * {@link org.neo4j.graphdb.event.TransactionEventHandler} responsible for caching relationship counts on nodes.
 * It is simple in the sense that it only cares about {@link org.neo4j.graphdb.RelationshipType}s and
 * {@link org.neo4j.graphdb.Direction}; it completely ignores {@link Relationship} properties.
 */
public class SimpleRelationshipCountTransactionEventHandler extends RelationshipCountCachingTransactionEventHandler {

    private static final Logger LOG = Logger.getLogger(SimpleRelationshipCountTransactionEventHandler.class);

    private final SimpleCachingRelationshipCountManager countManager;

    /**
     * Construct a new event handler.
     *
     * @param countManager      count manager.
     * @param inclusionStrategy strategy for selecting relationships to care about.
     */
    public SimpleRelationshipCountTransactionEventHandler(SimpleCachingRelationshipCountManager countManager, RelationshipInclusionStrategy inclusionStrategy) {
        super(inclusionStrategy);
        this.countManager = countManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleCreatedRelationship(Relationship relationship, Node pointOfView, Direction defaultDirection) {
        TypeAndDirectionDescription createdRelationship = new TypeAndDirectionDescriptionImpl(relationship.getType(), DirectionUtils.resolveDirection(relationship, pointOfView, defaultDirection));

        countManager.incrementCount(createdRelationship, pointOfView);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleDeletedRelationship(Relationship relationship, Node pointOfView, Direction defaultDirection) {
        TypeAndDirectionDescription deletedRelationship = new TypeAndDirectionDescriptionImpl(relationship.getType(), DirectionUtils.resolveDirection(relationship, pointOfView, defaultDirection));

        if (!countManager.decrementCount(deletedRelationship, pointOfView)) {
            LOG.warn(deletedRelationship.toString() + " was out of sync on node " + pointOfView.getId());
        }
    }
}
