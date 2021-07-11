package com.sergiomartinrubio.bytebuddyclient;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MyByteBuddyExampleTest {

    @Test
    void fixedValue() throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<?> dynamicType = new ByteBuddy()
                .subclass(Object.class)
                .name("com.sergiomartinrubio.bytebuddyexample.NewClass")
                .method(ElementMatchers.named("toString"))
                .intercept(FixedValue.value("Hello Fixed Value!"))
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();

        Assertions.assertEquals("Hello Fixed Value!", dynamicType.getDeclaredConstructor().newInstance().toString());
    }

    @Test
    void methodDelegation() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<? extends Foo> dynamicType = new ByteBuddy()
                .subclass(Foo.class)
                .method(ElementMatchers.named("myFooMethod")
                        .and(ElementMatchers.isDeclaredBy(Foo.class))
                        .and(ElementMatchers.returns(String.class))
                )
                .intercept(MethodDelegation.to(FooInterceptor.class))
                .make()
                .load(getClass().getClassLoader())
                .getLoaded();

        assertEquals("Hello from second Interceptor", dynamicType.getDeclaredConstructor().newInstance().myFooMethod());
    }

    @Test
    void newMethod() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> dynamicType = new ByteBuddy()
                .subclass(Object.class)
                .name("com.sergiomartinrubio.bytebuddyexample.NewClassWithNewMethod")
                .defineMethod("invokeMyMethod", String.class, Modifier.PUBLIC)
                .intercept(FixedValue.value("Hello from new method!"))
                .make()
                .load(getClass().getClassLoader())
                .getLoaded();

        Method method = dynamicType.getMethod("invokeMyMethod");

        Assertions.assertEquals(
                "Hello from new method!",
                method.invoke(dynamicType.getDeclaredConstructor().newInstance())
        );
    }

    @Test
    void overrideFieldValue() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        Class<? extends Foo> dynamicType = new ByteBuddy()
                .redefine(Foo.class)
                .name("com.sergiomartinrubio.bytebuddyexample.NewFooClass")
                .field(ElementMatchers.named("myField"))
                .value("World")
                .make()
                .load(getClass().getClassLoader())
                .getLoaded();


        Field field = dynamicType.getDeclaredField("myField");
        Assertions.assertEquals(String.class, field.getGenericType());
        Assertions.assertEquals("World", field.get(dynamicType.getDeclaredConstructor().newInstance()));
    }

    @Test
    void newField() throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> dynamicType = new ByteBuddy()
                .subclass(Object.class)
                .name("com.sergiomartinrubio.bytebuddyexample.NewClassWithNewField")
                .defineField("newField", String.class, Modifier.PUBLIC | Modifier.FINAL | Modifier.STATIC)
                .value("Hello From New Field!")
                .make()
                .load(getClass().getClassLoader())
                .getLoaded();

        Field field = dynamicType.getDeclaredField("newField");
        Assertions.assertEquals(String.class, field.getGenericType());
        Assertions.assertEquals("Hello From New Field!", field.get(dynamicType.getDeclaredConstructor().newInstance()));
    }

    @Test
    void newNonStaticField() throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> dynamicType = new ByteBuddy()
                .subclass(Object.class)
                .name("com.sergiomartinrubio.bytebuddyexample.NewClassWithNewMethod")
                .defineConstructor(Modifier.PUBLIC)
                .withParameters(String.class)
                .intercept(MethodCall
                        .invoke(Object.class.getConstructor())
                        .andThen(FieldAccessor
                                .ofField("newField")
                                .setsArgumentAt(0)
                        )
                )
                .defineField("newField", String.class, Modifier.PUBLIC)
                .make()
                .load(getClass().getClassLoader())
                .getLoaded();

        Object newInstance = dynamicType.getConstructor(String.class)
                .newInstance("Hello From New non Static Field!");
        Field field = dynamicType.getDeclaredField("newField");
        field.setAccessible(true);
        Assertions.assertEquals("Hello From New non Static Field!", field.get(newInstance));
    }

}
