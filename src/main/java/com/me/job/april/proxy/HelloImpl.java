package com.me.job.april.proxy;

public class HelloImpl implements Hello{
    @Override
    public void sayHello() {
        System.out.println("hello proxy");
    }
}
