package org.smartparam.engine.core.repository;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.smartparam.engine.annotations.ParamFunctionRepository;
import org.smartparam.engine.annotations.JavaPlugin;
import org.smartparam.engine.model.function.Function;
import org.smartparam.engine.model.function.JavaFunction;

/**
 *
 * @author Adam Dubiel <dubiel.adam@gmail.com>
 */
@ParamFunctionRepository(JavaFunctionRepository.JAVA_FUNCTION_CODE)
public class JavaFunctionRepository extends AbstractJavaFunctionRepository {

    public static final String JAVA_FUNCTION_CODE = "java";

    @Override
    protected Class<? extends Annotation> annotationClass() {
        return JavaPlugin.class;
    }

    @Override
    protected Function createFunction(String functionName, Method method) {
        return new JavaFunction(functionName, JAVA_FUNCTION_CODE, method);
    }

    @Override
    protected Class<? extends Function> functionClass() {
        return JavaFunction.class;
    }
}
