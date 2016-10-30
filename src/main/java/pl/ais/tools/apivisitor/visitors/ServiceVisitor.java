package pl.ais.tools.apivisitor.visitors;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Visitor for service main classes.
 */
public interface ServiceVisitor {

    void beginServiceProcessing(Class<?> clazz);

    void finishServiceProcessing(Class<?> clazz);

    void beginMethodProcessing(Method method);

    void finishMethodProcessing(Method method);

    void beginResultProcessing(Method method, Type type);

    void finishResultProcessing(Method method, Type type);

    void beginThrowableProcessing(Method method, Class<? extends Throwable> throwable);

    void finishThrowableProcessing(Method method, Class<? extends Throwable> throwable);

    void beginArgumentProcessing(Method method, int index, Type type);

    void finishArgumentProcessing(Method method, int index, Type type);

}
