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

package com.graphaware.neo4j.relcount.full.manager;

import com.graphaware.neo4j.dto.common.property.ImmutableProperties;
import com.graphaware.neo4j.dto.common.relationship.ImmutableDirectedRelationship;
import com.graphaware.neo4j.relcount.common.manager.BaseNaiveRelationshipCountManager;
import com.graphaware.neo4j.relcount.common.manager.RelationshipCountManager;
import com.graphaware.neo4j.relcount.full.dto.relationship.GenerallyCountableRelationship;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * Default production implementation of {@link com.graphaware.neo4j.relcount.full.manager.FullNaiveRelationshipCountManager}.
 */
public class FullNaiveRelationshipCountManager extends BaseNaiveRelationshipCountManager<ImmutableDirectedRelationship<String, ? extends ImmutableProperties<String>>, GenerallyCountableRelationship> implements RelationshipCountManager<ImmutableDirectedRelationship<String, ? extends ImmutableProperties<String>>, GenerallyCountableRelationship> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean candidateMatchesDescription(GenerallyCountableRelationship candidate, ImmutableDirectedRelationship<String, ? extends ImmutableProperties<String>> description) {
        return candidate.isMoreSpecificThan(description);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean continueAfterFirstLookupMatch() {
        //need to continue, there might be other more general matches
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected GenerallyCountableRelationship newCandidate(Relationship relationship, Node pointOfView) {
        return new GenerallyCountableRelationship(relationship, pointOfView);
    }
}
