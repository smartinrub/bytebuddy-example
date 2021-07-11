package com.sergiomartinrubio.adviceagent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

class Agent {
    public static void premain(String arguments, Instrumentation instrumentation) {
        new AgentBuilder.Default()
                .type(ElementMatchers.any())
                .transform((builder, typeDescription, classLoader, module) -> builder
                        .method(ElementMatchers.nameContainsIgnoreCase("custom"))
                        .intercept(Advice.to(HelloAdvice.class)))
                .installOn(instrumentation);
    }
}
