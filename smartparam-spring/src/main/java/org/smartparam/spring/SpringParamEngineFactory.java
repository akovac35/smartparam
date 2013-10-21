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
package org.smartparam.spring;

import java.util.List;
import org.smartparam.engine.bean.PackageList;
import org.smartparam.engine.config.initialization.MethodScannerInitializer;
import org.smartparam.engine.config.ParamEngineConfig;
import org.smartparam.engine.config.initialization.PostConstructInitializer;
import org.smartparam.engine.config.initialization.TypeScannerInitializer;
import org.smartparam.engine.config.ParamEngineFactory;
import org.smartparam.engine.core.engine.ParamEngine;
import org.smartparam.engine.core.repository.ParamRepository;
import org.smartparam.spring.function.SpringFunctionInvoker;
import org.smartparam.spring.function.SpringFunctionRepository;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author Adam Dubiel
 */
public class SpringParamEngineFactory implements FactoryBean<ParamEngine>, ApplicationContextAware {

    private ApplicationContext appContext;

    private ParamEngineConfig config;

    private ParamRepository paramRepository;

    private boolean scanAnnotations = true;

    private List<String> packagesToScan;

    @Override
    public ParamEngine getObject() {
        if (config == null) {
            config = new ParamEngineConfig();
        }
        if (paramRepository != null) {
            config.getParameterRepositories().add(paramRepository);
        }

        if (scanAnnotations) {
            injectComponentInitializers();
        }

        config.getFunctionInvokers().put(SpringFunctionRepository.FUNCTION_TYPE, new SpringFunctionInvoker(appContext));

        return new ParamEngineFactory().createParamEngine(config);
    }

    private void injectComponentInitializers() {
        PackageList packageList = new PackageList();
        packageList.setPackages(packagesToScan);

        config.getComponentInitializers().add(new PostConstructInitializer());
        config.getComponentInitializers().add(new TypeScannerInitializer(packageList));
        config.getComponentInitializers().add(new MethodScannerInitializer(packageList));
    }

    @Override
    public Class<?> getObjectType() {
        return ParamEngine.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public void setParamRepository(ParamRepository paramRepository) {
        this.paramRepository = paramRepository;
    }

    public void setConfig(ParamEngineConfig config) {
        this.config = config;
    }

    public void setScanAnnotations(boolean scanAnnotations) {
        this.scanAnnotations = scanAnnotations;
    }

    public void setPackagesToScan(List<String> packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    public void setApplicationContext(ApplicationContext appContext) {
        this.appContext = appContext;
    }
}
