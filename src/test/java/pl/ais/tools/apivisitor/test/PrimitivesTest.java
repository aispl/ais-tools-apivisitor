package pl.ais.tools.apivisitor.test;

import java.lang.reflect.Type;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import pl.ais.tools.apivisitor.APIWalker;
import pl.ais.tools.apivisitor.CollectingVisitor;
import pl.ais.tools.apivisitor.test.objects.primitives.Bean;

public class PrimitivesTest {

    @Test
    public void testPrimitives() {
        APIWalker walker = new APIWalker();
        CollectingVisitor visitor = new CollectingVisitor();
        walker.setVisitor(visitor);
        walker.visitTypes(Bean.class);
        Set<Type> types = visitor.getVisitedTypes();
        Assert.assertNotNull(types);
        Assert.assertEquals(5, types.size());
    }

}
