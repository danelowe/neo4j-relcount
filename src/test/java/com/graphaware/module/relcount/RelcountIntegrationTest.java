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

package com.graphaware.module.relcount;

import com.graphaware.test.integration.CommunityNeoServerIntegrationTest;
import com.graphaware.test.integration.NeoServerIntegrationTest;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;

import java.io.IOException;

import static com.graphaware.test.util.TestUtils.*;

/**
 * {@link NeoServerIntegrationTest} for {@link RelationshipCountModule}.
 */
public class RelcountIntegrationTest extends CommunityNeoServerIntegrationTest {

    @Test
    public void relationshipCountsShouldBeCachedWhenRuntimeAndRelcountAreEnabled() throws InterruptedException, IOException {
        httpClient.post(baseUrl() + "/db/data/transaction/commit", jsonAsString("create"), HttpStatus.OK_200);

        assertJsonEquals(httpClient.post(baseUrl() + "/db/data/transaction/commit", jsonAsString("query"), HttpStatus.OK_200),
                "{\"results\":[{\"columns\":[\"one._GA_relcount_\"],\"data\":[{\"row\":[[17,1,1,43,2,3,1,33,4,18,5,0,12,6,7,82,-79,2,2]]}]}],\"errors\":[]}");

        assertJsonEquals(httpClient.post(baseUrl() + "/db/data/transaction/commit", jsonAsString("query2"), HttpStatus.OK_200),
                "{\"results\":[{\"columns\":[\"two._GA_relcount_\"],\"data\":[{\"row\":[[17,1,1,43,2,3,2,33,4,18,5,0,12,6,7,82,-79,2,2]]}]}],\"errors\":[]}");
    }
}
