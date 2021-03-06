/*
 * Copyright 2013 Adam Dubiel, Przemek Hertel.
 *
 * Licensed under the Apache License, VeresultSetion 2.0 (the "License");
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
package org.smartparam.repository.jdbc.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.polyjdbc.core.query.mapper.ObjectMapper;
import org.smartparam.repository.jdbc.config.DefaultJdbcConfig;
import org.smartparam.repository.jdbc.model.JdbcParameterEntry;

/**
 *
 * @author Adam Dubiel
 */
public class JdbcParameterEntryMapper implements ObjectMapper<JdbcParameterEntry> {

    private final DefaultJdbcConfig configuration;

    public JdbcParameterEntryMapper(DefaultJdbcConfig configuration) {
        this.configuration = configuration;
    }

    @Override
    public JdbcParameterEntry createObject(ResultSet resultSet) throws SQLException {
        List<String> levels = new ArrayList<String>();
        String currentLevelValue;
        boolean doneBeforeLimit = false;
        for (int levelIndex = 0; levelIndex < configuration.levelColumnCount() - 1; ++levelIndex) {
            currentLevelValue = resultSet.getString("level" + levelIndex);
            if (currentLevelValue == null) {
                doneBeforeLimit = true;
                break;
            }
            levels.add(currentLevelValue);
        }

        if (!doneBeforeLimit) {
            String lastLevelValue = resultSet.getString("level" + (configuration.levelColumnCount() - 1));
            if (lastLevelValue != null) {
                levels.addAll(Arrays.asList(lastLevelValue.split("\\" + configuration.excessLevelsSeparator())));
            }
        }

        String[] levelValues = levels.toArray(new String[levels.size()]);
        JdbcParameterEntry entry = new JdbcParameterEntry(resultSet.getLong("id"), levelValues);

        return entry;
    }
}
