package pl.ais.tools.apivisitor.test;

import org.junit.Test;

import pl.ais.tools.apivisitor.APIWalker;
import pl.ais.tools.apivisitor.test.example.A;
import pl.ais.tools.apivisitor.test.example.Service;

public class APIWalkerTest {

    @Test(expected = NullPointerException.class)
    public void testNPEWhenVisitorIsNotProvidedForServices() {
        APIWalker walker = new APIWalker();
        walker.visit(Service.class);
    }

    @Test(expected = NullPointerException.class)
    public void testNPEWhenVisitorIsNotProvidedForTypes() {
        APIWalker walker = new APIWalker();
        walker.visit(A.class);
    }

}
