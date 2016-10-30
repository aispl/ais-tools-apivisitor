package pl.ais.tools.apivisitor;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Simple implementation of {@link Visitor}, that collects visited service classes, their methods and visited types.
 */
public class CollectingVisitor extends BaseVisitor {

    private Set<Type> visitedTypes = new HashSet<>();
    private Set<Class<?>> visitedServices = new HashSet<>();
    private Set<Method> visitedMethods = new HashSet<>();

    @Override
    public void beginServiceProcessing(Class<?> clazz) {
        visitedServices.add(clazz);
    }

    @Override
    public void beginTypeProcessing(Type type) {
        visitedTypes.add(type);
    }

    @Override
    public void beginMethodProcessing(Method method) {
        visitedMethods.add(method);
    }

    /**
     * @return Unmodifiable set, containing all visited types.
     */
    public Set<Type> getVisitedTypes() {
        return Collections.unmodifiableSet(visitedTypes);
    }

    /**
     * @return Unmodifiable set, containing all visited service classes.
     */
    public Set<Class<?>> getVisitedServices() {
        return Collections.unmodifiableSet(visitedServices);
    }

    /**
     * @return Unmodifiable set, containing all visited service methods.
     */
    public Set<Method> getVisitedMethods() {
        return Collections.unmodifiableSet(visitedMethods);
    }

}
