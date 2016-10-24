package pl.ais.tools.apivisitor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.function.Predicate;

/**
 * Service API structure walker.
 *
 * APIWalker Visitor visits through service class methods, allowing callers to verify
 * structure of API, document it, verify argument or return types etc.
 *
 * While walking through class methods and it's methods (arguments, returns, exceptions)
 * it calls methods on supplied visitor.
 *
 * Let's assume, we've following service:
 * <pre>
 * {@code
 * public class Service {
 *   public A doSomething(B b) {
 *     return new A();
 *   }
 * }
 *
 * public class A {
 *   private String name;
 *
 *   public void setName(String name) {
 *     this.name = name;
 *   }
 *
 *   public String getName() {
 *     return name;
 *   }
 * }
 *
 * public class B {
 *   private long id;
 *
 *   public void setId(long id) {
 *     this.id = id;
 *   }
 *
 *   public long getId() {
 *     return id;
 *   }
 * }
 * </pre>
 *
 * Then we may use <code>APIWalker</code>:
 * <pre>
 * {@code
 * public class PrintingVisitor extends BaseVisitor {
 *
 *   @Override
 *   public void beginServiceProcessing(Class<?> clazz) {
 *     log("beginServiceProcessing " + clazz.getSimpleName());
 *   }
 *
 *   @Override
 *   public void finishServiceProcessing(Class<?> clazz) {
 *     log("finishServiceProcessing " + clazz.getSimpleName());
 *   }
 *   ...
 * }
 * APIWalker walker = new APIWalker();
 * walker.setVisitor(new PrintingVisitor());
 * walker.visit(Service.class);
 * </pre>
 *
 * will produce the following output:
 * <pre>
 * beginServiceProcessing Service
 *   beginMethodProcessing doSomething
 *     beginResultProcessing pl.ais.tools.apivisittor.test.example.A
 *       beginTypeProcessing pl.ais.tools.apivisittor.test.example.A
 *         beginPropertyProcessing (name) java.lang.String
 *           beginTypeProcessing java.lang.String
 *           finishTypeProcessing java.lang.String
 *         finishPropertyProcessing (name) java.lang.String
 *       finishTypeProcessing pl.ais.tools.apivisittor.test.example.A
 *     finishResultProcessing pl.ais.tools.apivisittor.test.example.A
 *     beginArgumentProcessing (0) pl.ais.tools.apivisittor.test.example.B
 *       beginTypeProcessing pl.ais.tools.apivisittor.test.example.B
 *         beginPropertyProcessing (id) long
 *           beginTypeProcessing long
 *           finishTypeProcessing long
 *         finishPropertyProcessing (id) long
 *       finishTypeProcessing pl.ais.tools.apivisittor.test.example.B
 *     finishArgumentProcessing (0) pl.ais.tools.apivisittor.test.example.B
 *   finishMethodProcessing doSomething
 * finishServiceProcessing Service
 * </pre>
 *
 * Please, see {@link Visitor#unsupportedType(java.util.List, Type)} for a list
 * of supported types.
 *
 * <em>Implementation note</em>: this class is not thread-safe.
 */
public class APIWalker {

    /**
     * Always <code>true</code>.
     */
    public static final Predicate<Method> DEFAULT_METHOD_ACCEPTOR = (any) -> true;

    /**
     * Always <code>true</code>.
     */
    public static final Predicate<Type> DEFAULT_TYPE_ACCEPTOR = (any) -> true;

    private final Collection<Class<?>> terminatingServices = createTerminatingServices();

    private final Collection<Type> terminatingTypes = createTerminatingTypes();

    private final Collection<Type> primitiveTypes = createPrimitiveTypes();

    private Predicate<Method> methodAcceptor = DEFAULT_METHOD_ACCEPTOR;

    private Predicate<Type> typeAcceptor = DEFAULT_TYPE_ACCEPTOR;

    private Visitor visitor;

    /**
     * Visit service classes.
     *
     * Iterates through service class methods and visits them.
     */
    public void visit(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            if (clazz != null && !terminatingServices.contains(clazz)) {
                visitor.beginServiceProcessing(clazz);
                Arrays.stream(clazz.getDeclaredMethods()).filter(methodAcceptor).forEach(this::visit);
                visit(clazz.getSuperclass());
                visitor.finishServiceProcessing(clazz);
            }
        }
    }

    public void visit(Type ... types) {
        for (Type type : types) {
            visit(new Stack<>(), type);
        }
    }

    private void visit(Method method) {
        visitor.beginMethodProcessing(method);
        Type returnType = method.getGenericReturnType();
        visitor.beginResultProcessing(method, returnType);
        visit(returnType);
        visitor.finishResultProcessing(method, returnType);
        Type[] argTypes = method.getGenericParameterTypes();
        for (int i = 0; i < argTypes.length; i++) {
            visitor.beginArgumentProcessing(method, i, argTypes[i]);
            visit(argTypes[i]);
            visitor.finishArgumentProcessing(method, i, argTypes[i]);
        }
        for (Class<?> exceptionType : method.getExceptionTypes()) {
            visitor.beginThrowableProcessing(method, (Class<? extends Throwable>) exceptionType);
            visit(exceptionType);
            visitor.finishThrowableProcessing(method, (Class<? extends Throwable>) exceptionType);
        }
        visitor.finishMethodProcessing(method);
    }

    private void visit(Stack<Type> path, Type type) {
        if (typeAcceptor.test(type) && !terminatingTypes.contains(type)) {
            if (path.contains(type)) {
                visitor.dependencyCycleDetected(Collections.unmodifiableList(path));
            } else {
                path.push(type);
                visitor.beginTypeProcessing(type);
                if (type instanceof ParameterizedType) {
                    ParameterizedType pType = (ParameterizedType) type;
                    visit(path, pType.getRawType());
                    for (Type argType : pType.getActualTypeArguments()) {
                        visit(path, argType);
                    }
                } else if (type instanceof Class && !primitiveTypes.contains(type)) {
                    Class<?> cType = (Class<?>) type;
                    if (cType.isArray()) {
                        visit(path, cType.getComponentType());
                    } else {
                        Field[] fields = cType.getDeclaredFields();
                        for (Field field : fields) {
                            visitor.beginPropertyProcessing(field.getName(), field.getGenericType());
                            visit(path, field.getGenericType());
                            visitor.finishPropertyProcessing(field.getName(), field.getGenericType());
                        }
                    }
                } else {
                    visitor.unsupportedType(Collections.unmodifiableList(path), type);
                }
                visitor.finishTypeProcessing(type);
                path.pop();
            }
        }
    }

    public void setMethodAcceptor(Predicate<Method> methodAcceptor) {
        this.methodAcceptor = methodAcceptor;
    }

    public void setTypeAcceptor(Predicate<Type> typeAcceptor) {
        this.typeAcceptor = typeAcceptor;
    }

    public void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }

    public void addTerminatingServices(Class<?>... terminating) {
        for (Class<?> terminatingClass : terminating) {
            terminatingServices.add(terminatingClass);
        }
    }

    public void addTerminatingTypes(Type ... terminating) {
        for (Type terminatingType : terminating) {
            terminatingTypes.add(terminatingType);
        }
    }

    private Collection<Class<?>> createTerminatingServices() {
        Set<Class<?>> result = new HashSet<>();
        result.add(Object.class);
        return result;
    }

    private Collection<Type> createTerminatingTypes() {
        Set<Type> result = new HashSet<>();
        result.add(Object.class);
        return result;
    }

    private Collection<Type> createPrimitiveTypes() {
        Set<Type> result = new HashSet<>();
        result.add(String.class);
        result.add(Byte.class);
        result.add(Short.class);
        result.add(Integer.class);
        result.add(Long.class);
        result.add(Double.class);
        result.add(Float.class);
        result.add(BigDecimal.class);
        result.add(BigInteger.class);
        return result;
    }

}
