/*
 * Copyright 2013 Adam Dubiel, Przemek Hertel.
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
package org.smartparam.serializer.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.StringReader;
import java.util.HashSet;
import org.smartparam.engine.model.Level;
import org.smartparam.engine.model.Parameter;
import org.smartparam.engine.model.ParameterEntry;
import org.smartparam.engine.model.editable.EditableLevel;
import org.smartparam.engine.model.editable.EditableParameter;

/**
 *
 * @author Adam Dubiel
 */
public class JsonParameterConfigDeserializer implements ParameterConfigDeserializer {

    private Class<? extends EditableParameter> parameterInstanceClass;

    private Gson gson;

    public JsonParameterConfigDeserializer(Class<? extends EditableParameter> parameterInstanceClass, Class<? extends EditableLevel> levelInstanceClass) {
        this.parameterInstanceClass = parameterInstanceClass;

        LevelSerializationAdapter levelAdapter = new LevelSerializationAdapter(levelInstanceClass);

        gson = (new GsonBuilder()).registerTypeAdapter(Level.class, levelAdapter).create();

        levelAdapter.setGson(gson);
    }

    @Override
    public Parameter deserialize(String configText) {
        JsonReader reader = new JsonReader(new StringReader(configText));
        reader.setLenient(true);

        EditableParameter parameter = gson.fromJson(reader, parameterInstanceClass);
        parameter.setEntries(new HashSet<ParameterEntry>());
        return parameter;
    }
}
