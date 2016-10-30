package pl.ais.tools.apivisitor.test;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.ais.tools.apivisitor.APIWalker;
import pl.ais.tools.apivisitor.CollectingVisitor;
import pl.ais.tools.apivisitor.test.objects.acceptors.A;
import pl.ais.tools.apivisitor.test.objects.acceptors.AcceptedMethod;
import pl.ais.tools.apivisitor.test.objects.acceptors.AcceptedType;
import pl.ais.tools.apivisitor.test.objects.acceptors.MethodsService;
import pl.ais.tools.apivisitor.test.objects.acceptors.TypesService;

public class AcceptorsTest {

    private CollectingVisitor visitor;

    @Before
    public void createVisitor() {
        visitor = new CollectingVisitor();
    }

    @Test
    public void testAcceptingMethods() {
        APIWalker walker = new APIWalker();
        walker.setVisitor(visitor);
        walker.setMethodAcceptor(method -> method.isAnnotationPresent(AcceptedMethod.class));
        walker.visit(MethodsService.class);
        Set<Method> visitedMethods = visitor.getVisitedMethods();
        Assert.assertEquals(1, visitedMethods.size());
        Assert.assertTrue(visitedMethods.stream().anyMatch(method -> "acceptedMethod".equals(method.getName())));
    }

    @Test
    public void testAcceptingTypes() {
        APIWalker walker = new APIWalker();
        walker.setVisitor(visitor);
        walker.setTypeAcceptor(type -> (type instanceof Class) && ((Class<?>) type).isAnnotationPresent(AcceptedType.class));
        walker.visit(TypesService.class);
        Set<Type> visitedTypes = visitor.getVisitedTypes();
        Assert.assertEquals(1, visitedTypes.size());
        Assert.assertTrue(visitedTypes.stream().anyMatch(method -> A.class.getName().equals(method.getTypeName())));
    }

}
