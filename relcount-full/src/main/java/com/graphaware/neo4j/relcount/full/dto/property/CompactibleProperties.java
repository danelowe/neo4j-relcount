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

package com.graphaware.neo4j.relcount.full.dto.property;

import com.graphaware.neo4j.dto.common.property.ImmutableProperties;
import com.graphaware.neo4j.dto.string.property.CopyMakingSerializableProperties;
import com.graphaware.neo4j.relcount.full.dto.common.Generalizing;
import com.graphaware.neo4j.relcount.full.dto.common.MutuallyExclusive;
import com.graphaware.neo4j.relcount.full.dto.common.PartiallyComparable;

/**
 * A description of a {@link org.neo4j.graphdb.Relationship}'s properties
 * for the purposes of counting {@link org.neo4j.graphdb.Relationship}s with those properties, caching such counts, etc.
 */
public interface CompactibleProperties extends
        CopyMakingSerializableProperties<CompactibleProperties>,
        PartiallyComparable<ImmutableProperties<String>>,
        Comparable<CompactibleProperties>,
        Generalizing<CompactibleProperties>,
        MutuallyExclusive<ImmutableProperties<String>> {
}