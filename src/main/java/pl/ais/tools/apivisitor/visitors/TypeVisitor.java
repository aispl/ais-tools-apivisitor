package pl.ais.tools.apivisitor.visitors;

import java.lang.reflect.Type;

/**
 * Visitor for service types.
 */
public interface TypeVisitor {

    void beginPropertyProcessing(String name, Type type);

    void finishPropertyProcessing(String name, Type type);

    void beginTypeProcessing(Type type);

    void finishTypeProcessing(Type type);

}
