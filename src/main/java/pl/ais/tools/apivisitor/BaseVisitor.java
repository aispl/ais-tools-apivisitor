package pl.ais.tools.apivisitor;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Empty (no-op) implementation of {@link Visitor}.
 *
 * This class should be used as a superclass for implementing {@link Visitor}s.
 */
public class BaseVisitor implements Visitor {

    @Override
    public void beginServiceProcessing(Class<?> clazz) {
    }

    @Override
    public void finishServiceProcessing(Class<?> clazz) {
    }

    @Override
    public void beginMethodProcessing(Method method) {
    }

    @Override
    public void finishMethodProcessing(Method method) {
    }

    @Override
    public void beginResultProcessing(Method method, Type type) {
    }

    @Override
    public void finishResultProcessing(Method method, Type type) {
    }

    @Override
    public void beginArgumentProcessing(Method method, int index, Type type) {
    }

    @Override
    public void finishArgumentProcessing(Method method, int index, Type type) {
    }

    @Override
    public void beginPropertyProcessing(String name, Type type) {
    }

    @Override
    public void finishPropertyProcessing(String name, Type type) {
    }

    @Override
    public void beginTypeProcessing(Type type) {
    }

    @Override
    public void dependencyCycleDetected(List<Type> path) {
    }

    @Override
    public void unsupportedType(List<Type> path, Type type) {
    }

    @Override
    public void finishTypeProcessing(Type type) {
    }

    @Override
    public void beginThrowableProcessing(Method method, Class<? extends Throwable> exception) {
    }

    @Override
    public void finishThrowableProcessing(Method method, Class<? extends Throwable> exception) {
    }

}
