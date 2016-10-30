package pl.ais.tools.apivisitor;

import java.lang.reflect.Type;
import java.util.List;

import pl.ais.tools.apivisitor.visitors.ServiceVisitor;
import pl.ais.tools.apivisitor.visitors.TypeVisitor;

/**
 * Visitor provides methods invoked by {@link APIWalker} while traversing API.
 */
public interface Visitor extends ServiceVisitor, TypeVisitor {

    /**
     * Invoked, when there's a dependency cycle in type.
     *
     * Dependency cycle means, that bean contains a reference to itself as a part of its field.
		 *
		 * @param path path to cycle of dependencies.
     */
    void dependencyCycleDetected(List<Type> path);

    /**
     * Invoked, when there's an unsupported type.
     *
     * API visitor supports:
     * <ul>
     *   <li>parametrized types</li>
     *   <li>arrays</li>
     *   <li>classes</li>
     * </ul>
     * In case of code, that cause API Walker to call this method please, submit
     * a ticket to API https://github.com/aispl/ais-tools-apivisitor with a sample code.
		 *
		 * @param path path to unsupported type.
		 * @param type unsupported type.
     */
    void unsupportedType(List<Type> path, Type type);

}
