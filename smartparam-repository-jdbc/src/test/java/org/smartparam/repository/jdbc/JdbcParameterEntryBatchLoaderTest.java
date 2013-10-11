/*
 * Copyright 2013 Adam Dubiel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartparam.repository.jdbc;

import org.smartparam.repository.jdbc.dao.JdbcParameterEntryBatchLoader;
import java.util.Collection;
import java.util.LinkedList;
import org.smartparam.engine.core.exception.ParamBatchLoadingException;
import org.smartparam.engine.model.ParameterEntry;
import org.smartparam.repository.jdbc.dao.ParameterEntryDAO;
import org.smartparam.repository.jdbc.integration.DatabaseTest;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 *
 * @author Adam Dubiel
 */
public class JdbcParameterEntryBatchLoaderTest extends DatabaseTest {

    @Test
    public void shouldAlwaysRunAtLeastOneLoop() {
        // given
        ParameterEntryDAO parameterEntryDAO = get(ParameterEntryDAO.class);
        JdbcParameterEntryBatchLoader loader = new JdbcParameterEntryBatchLoader(transactionManager(), parameterEntryDAO, 1);

        // when
        boolean initialCheck = loader.hasMore();
        loader.close();

        // then
        assertThat(initialCheck).isTrue();
    }

    @Test
    public void shouldReturnFalseWhenNoMoreEntriesToLoad() throws ParamBatchLoadingException {
        // given
        ParameterEntryDAO parameterEntryDAO = get(ParameterEntryDAO.class);
        JdbcParameterEntryBatchLoader loader = new JdbcParameterEntryBatchLoader(transactionManager(), parameterEntryDAO, 1);

        // when
        loader.nextBatch(10);
        boolean hasMore = loader.hasMore();
        loader.close();

        // then
        assertThat(hasMore).isFalse();
    }

    @Test
    public void shouldLoadBatchOfGivenSizeWhenLotOfEntriesAvailable() throws ParamBatchLoadingException {
        // given
        database().withParameter(1).withParameterEntries(1, 100).build();
        ParameterEntryDAO parameterEntryDAO = get(ParameterEntryDAO.class);
        JdbcParameterEntryBatchLoader loader = new JdbcParameterEntryBatchLoader(transactionManager(), parameterEntryDAO, 1);

        // when
        Collection<ParameterEntry> entries = loader.nextBatch(50);
        loader.close();

        // then
        assertThat(entries).hasSize(50);
    }

    @Test
    public void shouldLoadBatchSmallerThanDefinedWhenNoMoreEntriesToLoad() throws ParamBatchLoadingException {
        // given
        database().withParameter(1).withParameterEntries(1, 20).build();
        ParameterEntryDAO parameterEntryDAO = get(ParameterEntryDAO.class);
        JdbcParameterEntryBatchLoader loader = new JdbcParameterEntryBatchLoader(transactionManager(), parameterEntryDAO, 1);

        // when
        Collection<ParameterEntry> entries = loader.nextBatch(50);
        loader.close();

        // then
        assertThat(entries).hasSize(20);
    }

    @Test
    public void shouldLoadAllEntriesInMultipleBatchesUntileNoneAvailable() throws ParamBatchLoadingException {
        // given
        database().withParameter(1).withParameterEntries(1, 110).build();
        ParameterEntryDAO parameterEntryDAO = get(ParameterEntryDAO.class);
        JdbcParameterEntryBatchLoader loader = new JdbcParameterEntryBatchLoader(transactionManager(), parameterEntryDAO, 1);

        // when
        Collection<ParameterEntry> entries = new LinkedList<ParameterEntry>();
        while (loader.hasMore()) {
            entries.addAll(loader.nextBatch(50));
        }
        loader.close();

        // then
        assertThat(entries).hasSize(110);
    }
}