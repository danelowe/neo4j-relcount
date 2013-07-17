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

package com.graphaware.neo4j.relcount.full.strategy;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import java.util.Map;

import static com.graphaware.neo4j.utils.PropertyContainerUtils.propertiesToStringMap;

/**
 * Properties extraction strategy for {@link org.neo4j.graphdb.Relationship}s.
 */
public interface RelationshipPropertiesExtractionStrategy {

    /**
     * Extract properties from a {@link org.neo4j.graphdb.Relationship}.
     *
     * @param relationship from which to extract properties.
     * @param pointOfView  {@link org.neo4j.graphdb.Node} from whose point of view the relationships is being looked at. This is intended
     *                     to be used for deriving relationship properties from participating nodes' properties and
     *                     labels. Can be <code>null</code> if the strategy doesn't use participating nodes.
     * @return read-only map of extracted properties.
     */
    Map<String, String> extractProperties(Relationship relationship, Node pointOfView);

    public abstract class OtherNodeIncludingAdapter implements RelationshipPropertiesExtractionStrategy {

        protected abstract Map<String, String> extractProperties(Map<String, String> properties, Node otherNode);

        @Override
        public Map<String, String> extractProperties(Relationship relationship, Node pointOfView) {
            if (pointOfView == null) {
                throw new IllegalArgumentException("Strategy for extracting relationship properties expects a node, from whose point of view the relationship is being handled");
            }

            return extractProperties(propertiesToStringMap(relationship), relationship.getOtherNode(pointOfView));
        }
    }

    public abstract class SimpleAdapter implements RelationshipPropertiesExtractionStrategy {

        protected abstract Map<String, String> extractProperties(Map<String, String> properties);

        @Override
        public Map<String, String> extractProperties(Relationship relationship, Node pointOfView) {
            return extractProperties(propertiesToStringMap(relationship));
        }
    }
}
