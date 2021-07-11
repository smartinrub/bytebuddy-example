package com.sergiomartinrubio.bytebuddyclient;

import net.bytebuddy.implementation.bind.annotation.BindingPriority;

public class FooInterceptor {
    @BindingPriority(1)
    public static String intercept() {
        return "Hello From Foo Interceptor!";
    }

    @BindingPriority(2)
    public static String secondInterceptor() {
        return "Hello from second Interceptor";
    }
}
