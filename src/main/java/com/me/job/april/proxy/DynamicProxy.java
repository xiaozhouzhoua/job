package com.me.job.april.proxy;

import java.lang.reflect.Proxy;

/**
 * 简单的动态代理
 */
public class DynamicProxy {
    public static void main(String[] args) {
        HelloImpl hello = new HelloImpl();
        HelloInvocationHandler handler = new HelloInvocationHandler(hello);
        // 构造代理实例
        Hello proxiedHello = (Hello) Proxy.newProxyInstance(HelloImpl.class.getClassLoader(),
                HelloImpl.class.getInterfaces(), handler);
        // 调用代理方法
        proxiedHello.sayHello();
    }
}
