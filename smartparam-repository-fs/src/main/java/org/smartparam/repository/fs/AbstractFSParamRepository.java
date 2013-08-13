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
package org.smartparam.repository.fs;

import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartparam.engine.config.InitializableComponent;
import org.smartparam.engine.core.batch.ParameterBatchLoader;
import org.smartparam.engine.core.batch.ParameterEntryUnbatchUtil;
import org.smartparam.engine.core.repository.ParamRepository;
import org.smartparam.engine.model.Parameter;
import org.smartparam.engine.model.ParameterEntry;
import org.smartparam.engine.model.editable.SimpleEditableLevel;
import org.smartparam.engine.model.editable.SimpleEditableParameter;
import org.smartparam.engine.model.editable.SimpleEditableParameterEntry;
import org.smartparam.serializer.ParamDeserializer;
import org.smartparam.serializer.StandardParamDeserializer;
import org.smartparam.serializer.StandardSerializationConfig;

/**
 *
 * @author Adam Dubiel <dubiel.adam@gmail.com>
 */
public abstract class AbstractFSParamRepository implements ParamRepository, InitializableComponent {

    private static final Logger logger = LoggerFactory.getLogger(AbstractFSParamRepository.class);

    private static final int DEFAULT_BATCH_LOADER_SIZE = 2000;

    private String basePath;

    private String filePattern;

    private ParamDeserializer deserializer;

    private ResourceResolver resourceResolver;

    private Map<String, String> parameters;

    public AbstractFSParamRepository(String basePath, String filePattern) {
        this(basePath, filePattern, null);
    }

    public AbstractFSParamRepository(String basePath, String filePattern, ParamDeserializer deserializer) {
        this.basePath = basePath;
        this.filePattern = filePattern;
        this.deserializer = deserializer;
    }

    @Override
    public void initialize() {
        if (deserializer == null) {
            logger.debug("no custom deserializer provided, using {}", StandardParamDeserializer.class.getSimpleName());
            this.deserializer = new StandardParamDeserializer(new StandardSerializationConfig(),
                    SimpleEditableParameter.class, SimpleEditableLevel.class, SimpleEditableParameterEntry.class);
        }

        resourceResolver = createResourceResolver(basePath, filePattern, deserializer);
        parameters = resourceResolver.findParameterResources();

        logger.info("found {} parameters after scanning resources at {}", parameters.size(), basePath);
    }

    protected abstract ResourceResolver createResourceResolver(String basePath, String filePattern, ParamDeserializer deserializer);

    @Override
    public Parameter load(String parameterName) {
        ParameterBatchLoader parameterBatch = batchLoad(parameterName);
        if(parameterBatch != null) {
            ParameterEntryUnbatchUtil.loadEntriesIntoParameter(parameterBatch.getMetadata(), parameterBatch.getEntryLoader(), DEFAULT_BATCH_LOADER_SIZE);
            return parameterBatch.getMetadata();
        }
        return null;
    }

    @Override
    public ParameterBatchLoader batchLoad(String parameterName) {
        String parameterResourceName = parameters.get(parameterName);
        if (parameterResourceName != null) {
            return resourceResolver.loadParameterFromResource(parameterResourceName);
        }
        return null;
    }

    @Override
    public Set<ParameterEntry> findEntries(String parameterName, String[] levelValues) {
        logger.info("trying to load parameter {}, but {} does not support non-cacheable parameters", parameterName, getClass().getSimpleName());
        return null;
    }

    @Override
    public Set<String> listParameters() {
        return parameters.keySet();
    }

}