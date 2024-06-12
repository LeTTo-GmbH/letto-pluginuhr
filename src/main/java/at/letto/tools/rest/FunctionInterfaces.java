package at.letto.tools.rest;

public class FunctionInterfaces {
    @FunctionalInterface
    public interface ThreeParameterFunction<T, U, V, R> {
        public R apply(T t, U u, V v);
    }

    @FunctionalInterface
    public interface FourParameterFunction<T, U, V, W, R> {
        public R apply(T t, U u, V v, W w);
    }

    @FunctionalInterface
    public interface FiveParameterFunction<T, U, V, W, X, R > {
        public R apply(T t, U u, V v, W w, X x);
    }

    @FunctionalInterface
    public interface SixParameterFunction<T, U, V, W, X, Y, R > {
        public R apply(T t, U u, V v, W w, X x, Y y);
    }

    @FunctionalInterface
    public interface SevenParameterFunction<T, U, V, W, X, Y, Z, R > {
        public R apply(T t, U u, V v, W w, X x, Y y, Z z);
    }

}
