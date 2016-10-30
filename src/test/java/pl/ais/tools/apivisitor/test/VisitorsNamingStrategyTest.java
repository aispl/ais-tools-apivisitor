package pl.ais.tools.apivisitor.test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import pl.ais.tools.apivisitor.APIWalker;
import pl.ais.tools.apivisitor.BaseVisitor;
import pl.ais.tools.apivisitor.visitors.ServiceVisitor;
import pl.ais.tools.apivisitor.visitors.TypeVisitor;

/**
 * Eating our own dog's food - making sure, that {@link TypeVisitor} and {@link ServiceVisitor} have
 * consisting method names.
 *
 * Each <em>begin</em> method must have its <em>finish</em> method and vice-versa, each <em>finish</em>
 * method must have its <em>begin</em> method.
 */
@RunWith(Parameterized.class)
public class VisitorsNamingStrategyTest {

    @Parameters(name = "{0}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[] { TypeVisitor.class }, new Object[] { ServiceVisitor.class });
    }

    private final Class<?> serviceClass;

    public VisitorsNamingStrategyTest(Class<?> serviceClass) {
        this.serviceClass = serviceClass;
    }

    @Test
    public void testNaming() {
        APIWalker walker = new APIWalker();
        VisitorNamingEnforcingVisitor visitor = new VisitorNamingEnforcingVisitor();
        walker.setVisitor(visitor);
        walker.visit(serviceClass);
    }

    class VisitorNamingEnforcingVisitor extends BaseVisitor {

        private Set<Method> beginMethods;
        private Set<Method> finishMethods;

        @Override
        public void beginServiceProcessing(Class<?> clazz) {
            beginMethods = new HashSet<>();
            finishMethods = new HashSet<>();
        }

        @Override
        public void beginMethodProcessing(Method method) {
            Assert.assertEquals("method " + method  + " must return void", void.class, method.getReturnType());
            if (method.getName().startsWith("begin")) {
                beginMethods.add(method);
                return;
            }
            if (method.getName().startsWith("finish")) {
                finishMethods.add(method);
                return;
            }
            throw new IllegalArgumentException("method " + method + " does not have allowed name");
        }

        @Override
        public void finishServiceProcessing(Class<?> clazz) {
            Assert.assertEquals(beginMethods.size(), finishMethods.size());
            matchMethods("begin", "finish", beginMethods, finishMethods);
            matchMethods("finish", "begin", finishMethods, beginMethods);
        }

        private void matchMethods(final String leftPrefix, final String rightPrefix, final Set<Method> left, final Set<Method> right) {
            Assert.assertTrue(left.stream().allMatch(lmethod -> {
                final String rightMethodName = rightPrefix + lmethod.getName().substring(leftPrefix.length());
                final Method rmethod = right.stream().filter(rm -> rightMethodName.contentEquals(rm.getName())).findFirst().get();
                Assert.assertArrayEquals("comparing arguments of " + lmethod.getName() + " and " + rmethod.getName(),
                        lmethod.getParameterTypes(), rmethod.getParameterTypes());
                Assert.assertEquals(lmethod.getParameterCount(), rmethod.getParameterCount());
                return true;
            }));
        }
    }
}
