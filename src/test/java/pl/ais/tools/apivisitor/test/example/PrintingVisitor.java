package pl.ais.tools.apivisitor.test.example;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import pl.ais.tools.apivisitor.BaseVisitor;

public class PrintingVisitor extends BaseVisitor {

    private final int INDENT = 2;
    private int deep;

    @Override
    public void beginServiceProcessing(Class<?> clazz) {
        deep = 0;
        log("beginServiceProcessing " + clazz.getSimpleName());
    }

    @Override
    public void finishServiceProcessing(Class<?> clazz) {
        log("finishServiceProcessing " + clazz.getSimpleName());
    }

    @Override
    public void beginMethodProcessing(Method method) {
        deep = deep + INDENT;
        log("beginMethodProcessing " + method.getName());
    }

    @Override
    public void finishMethodProcessing(Method method) {
        log("finishMethodProcessing " + method.getName());
        deep = deep - INDENT;
    }

    @Override
    public void beginResultProcessing(Method method, Type type) {
        deep = deep + INDENT;
        log("beginResultProcessing " + type.getTypeName());
    }

    @Override
    public void finishResultProcessing(Method method, Type type) {
        log("finishResultProcessing " + type.getTypeName());
        deep = deep - INDENT;
    }

    @Override
    public void beginArgumentProcessing(Method method, int index, Type type) {
        deep = deep + INDENT;
        log("beginArgumentProcessing (" + index + ") " + type.getTypeName());
    }

    @Override
    public void finishArgumentProcessing(Method method, int index, Type type) {
        log("finishArgumentProcessing (" + index + ") " + type.getTypeName());
        deep = deep - INDENT;
    }

    @Override
    public void beginPropertyProcessing(String name, Type type) {
        deep = deep + INDENT;
        log("beginPropertyProcessing (" + name + ") " + type.getTypeName());
    }

    @Override
    public void finishPropertyProcessing(String name, Type type) {
        log("finishPropertyProcessing (" + name + ") " + type.getTypeName());
        deep = deep - INDENT;
    }

    @Override
    public void beginTypeProcessing(Type type) {
        deep = deep + INDENT;
        log("beginTypeProcessing " + type.getTypeName());
    }

    @Override
    public void finishTypeProcessing(Type type) {
        log("finishTypeProcessing " + type.getTypeName());
        deep = deep - INDENT;
    }

    @Override
    public void beginThrowableProcessing(Method method, Class<? extends Throwable> exception) {
        deep = deep + INDENT;
        log("beginThrowableProcessing " + exception.getTypeName());
    }

    @Override
    public void finishThrowableProcessing(Method method, Class<? extends Throwable> exception) {
        log("finishThrowableProcessing " + exception.getTypeName());
        deep = deep - INDENT;
    }

    @Override
    public void dependencyCycleDetected(List<Type> path) {
    }

    @Override
    public void unsupportedType(List<Type> path, Type type) {
    }

    private void log(String message) {
        StringBuilder msg = new StringBuilder(deep * 2 + message.length());
        for (int i = 0; i < deep; i++) {
            msg.append(" ");
        }
        msg.append(message);
        System.out.println(msg);
    }

}
