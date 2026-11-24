import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// 1. 目标接口
interface GreetingService {
    String greet(String name);
}

// 2. 目标接口的实现（被Hook的真实对象）
class GreetingServiceImpl implements GreetingService {
    @Override
    public String greet(String name) {
        return "Hello, " + name;
    }
}

// 3. 实现 InvocationHandler，编写 Hook 逻辑
class LoggingHandler implements InvocationHandler {
    private final Object target; // 持有真实对象

    public LoggingHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // --- Hook 前置逻辑 ---
        System.out.println("[Hook] 方法 " + method.getName() + " 开始执行，参数：" + args[0]);
        long start = System.nanoTime();

        // --- 调用真实方法 ---
        Object result = method.invoke(target, args);

        // --- Hook 后置逻辑 ---
        long cost = System.nanoTime() - start;
        System.out.println("[Hook] 方法 " + method.getName() + " 执行结束，耗时 " + cost + " ns，返回值：" + result);

        return result;
    }
}

// 4. 使用动态代理创建 Hook 对象
public class DynamicProxyHookDemo {
    public static void main(String[] args) {
        GreetingService realService = new GreetingServiceImpl();

        // 创建代理对象
        GreetingService proxyService = (GreetingService) Proxy.newProxyInstance(
                realService.getClass().getClassLoader(),
                new Class[]{GreetingService.class},
                new LoggingHandler(realService)
        );

        // 通过代理对象调用方法，会自动触发 Hook
        String message = proxyService.greet("World");
        System.out.println("最终返回：" + message);
    }
}
