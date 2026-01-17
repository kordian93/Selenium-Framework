package support.context;

public class ContextHolder {
    private static final ThreadLocal<TestContext> ctx = new ThreadLocal<>();

    public static void set(TestContext testContext) {
        ctx.set(testContext);
    }

    public static TestContext get() {
        return ctx.get();
    }

    public static void clear() {
        ctx.remove();
    }
}