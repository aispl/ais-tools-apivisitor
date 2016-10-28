package pl.ais.tools.apivisitor.test.example;

import org.junit.Test;

import pl.ais.tools.apivisitor.APIWalker;

public class Example {

    @Test
    public void doExample() {
        APIWalker walker = new APIWalker();
        walker.setVisitor(new PrintingVisitor());
        walker.visit(Service.class);
    }
}
