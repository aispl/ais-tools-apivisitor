package pl.ais.tools.apivisitor.test;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.ais.tools.apivisitor.APIWalker;
import pl.ais.tools.apivisitor.CollectingVisitor;
import pl.ais.tools.apivisitor.test.objects.cycle.A;
import pl.ais.tools.apivisitor.test.objects.cycle.B;
import pl.ais.tools.apivisitor.test.objects.cycle.Service;

public class CycleDetectionTest {

    private CycleDetecionVisitor visitor;

    @Before
    public void createVisitor() {
        visitor = new CycleDetecionVisitor();
    }

    @Test(expected = DependencyCycleException.class)
    public void testCycleDetection() {
        APIWalker walker = new APIWalker();
        walker.setVisitor(visitor);
        walker.visit(Service.class);
    }

    @Test
    public void testCycleDetectionWithTemination1() {
        APIWalker walker = new APIWalker();
        walker.setVisitor(visitor);
        walker.addTerminatingTypes(A.class);
        walker.visit(Service.class);
        Set<Type> visitedTypes = visitor.getVisitedTypes();
        Assert.assertEquals("one type visited", 1, visitedTypes.size());
        Assert.assertTrue(visitedTypes.contains(B.class));
    }

    @Test
    public void testCycleDetectionWithTemination2() {
        APIWalker walker = new APIWalker();
        walker.setVisitor(visitor);
        walker.addTerminatingTypes(B.class);
        walker.visit(Service.class);
        Set<Type> visitedTypes = visitor.getVisitedTypes();
        Assert.assertEquals("one type visited", 1, visitedTypes.size());
        Assert.assertTrue(visitedTypes.contains(A.class));
    }

    private class CycleDetecionVisitor extends CollectingVisitor {

        @Override
        public void dependencyCycleDetected(List<Type> path) {
            throw new DependencyCycleException();
        }

    }

    private class DependencyCycleException extends RuntimeException {

    }
}
