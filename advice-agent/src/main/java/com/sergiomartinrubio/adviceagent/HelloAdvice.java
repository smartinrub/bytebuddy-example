package com.sergiomartinrubio.adviceagent;

import net.bytebuddy.asm.Advice;

public class HelloAdvice {

    @Advice.OnMethodEnter
    static long invokeBeforeEnterMethod(
            @Advice.Origin String method) {
        System.out.println("Method invoked before enter method by: " + method);
        return System.currentTimeMillis();
    }

    @Advice.OnMethodExit
    static void invokeAfterExitMethod(
            @Advice.Origin String method,
            @Advice.Enter long startTime
    ) {
        System.out.println("Method " + method + " took " + (System.currentTimeMillis() - startTime) + "ms");
    }
}
