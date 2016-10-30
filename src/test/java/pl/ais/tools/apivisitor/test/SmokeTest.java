package pl.ais.tools.apivisitor.test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.ais.tools.apivisitor.APIWalker;
import pl.ais.tools.apivisitor.CollectingVisitor;
import pl.ais.tools.apivisitor.test.objects.smoke.A;
import pl.ais.tools.apivisitor.test.objects.smoke.B;
import pl.ais.tools.apivisitor.test.objects.smoke.C;
import pl.ais.tools.apivisitor.test.objects.smoke.D;
import pl.ais.tools.apivisitor.test.objects.smoke.E;
import pl.ais.tools.apivisitor.test.objects.smoke.F;
import pl.ais.tools.apivisitor.test.objects.smoke.G;
import pl.ais.tools.apivisitor.test.objects.smoke.Service;

public class SmokeTest {

    private CollectingVisitor visitor;

    @Before
    public void createVisitor() {
        visitor = new CollectingVisitor();
    }

    @Test
    public void smokeTest() {
        APIWalker walker = new APIWalker();
        walker.setVisitor(visitor);
        walker.visit(Service.class);

        {
            Set<Type> types = visitor.getVisitedTypes();
            Assert.assertEquals(11, types.size());
            Assert.assertTrue(types.contains(A.class));
            Assert.assertTrue(types.contains(B.class));
            Assert.assertTrue(types.contains(C.class));
            Assert.assertTrue(types.contains(D.class));
            Assert.assertTrue(types.contains(E.class));
            Assert.assertTrue(types.contains(F.class));
            Assert.assertTrue(types.contains(G.class));
            Assert.assertTrue(types.contains(List.class));
            Assert.assertTrue(types.contains(void.class));
            Assert.assertTrue(types.stream().anyMatch(type -> type instanceof ParameterizedType));
            Assert.assertTrue(types.stream().anyMatch(type -> type instanceof Class && ((Class<?>) type).isArray()));
        }
        {
            Set<Class<?>> classes = visitor.getVisitedServices();
            Assert.assertEquals(1, classes.size());
            Assert.assertTrue(classes.contains(Service.class));
        }
    }

}
