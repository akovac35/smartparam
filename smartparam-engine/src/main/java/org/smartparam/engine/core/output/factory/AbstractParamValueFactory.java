/*
 * Copyright 2014 Adam Dubiel.
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
package org.smartparam.engine.core.output.factory;

import java.util.ArrayList;
import java.util.List;
import org.smartparam.engine.core.output.MultiValue;
import org.smartparam.engine.core.prepared.PreparedEntry;
import org.smartparam.engine.core.prepared.PreparedLevel;
import org.smartparam.engine.core.prepared.PreparedParameter;
import org.smartparam.engine.core.type.Type;
import org.smartparam.engine.core.type.ValueHolder;
import org.smartparam.engine.core.type.decode.TypeDecoder;
import org.smartparam.engine.util.EngineUtil;

/**
 *
 * @author Adam Dubiel
 */
abstract class AbstractParamValueFactory<M extends MultiValue> {

    protected abstract M createMultiValue(PreparedParameter parameter, PreparedEntry preparedEntry, Object[] values);

    protected List<M> createRows(PreparedParameter parameter, PreparedEntry[] preparedEntries) {
        int inputLevelCount = parameter.getInputLevelsCount();
        int oputputLevelCount = parameter.getLevelCount() - inputLevelCount;

        List<M> multiValueRows = new ArrayList<M>(preparedEntries.length);
        for (PreparedEntry preparedEntry : preparedEntries) {
            PreparedLevel[] levels = parameter.getLevels();
            Object[] vector = new Object[oputputLevelCount];

            for (int columnIndex = 0; columnIndex < oputputLevelCount; ++columnIndex) {
                String cellText = preparedEntry.getLevel(inputLevelCount + columnIndex);
                PreparedLevel level = levels[inputLevelCount + columnIndex];

                Type<?> cellType = level.getType();
                Object cellValue;

                if (level.isArray()) {
                    cellValue = evaluateStringAsArray(cellText, cellType, ',');
                } else {
                    cellValue = TypeDecoder.decode(cellType, cellText);
                }

                vector[columnIndex] = cellValue;
            }

            multiValueRows.add(createMultiValue(parameter, preparedEntry, vector));
        }

        return multiValueRows;
    }

    private ValueHolder[] evaluateStringAsArray(String value, Type<?> type, char separator) {
        if (EngineUtil.hasText(value)) {
            String[] tokens = EngineUtil.split(value, separator);
            ValueHolder[] array = type.newArray(tokens.length);
            for (int i = 0; i < tokens.length; i++) {
                array[i] = TypeDecoder.decode(type, tokens[i]);
            }
            return array;

        } else {
            return type.newArray(0);
        }
    }
}
