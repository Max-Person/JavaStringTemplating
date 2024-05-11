package com.github.max_person.templating;

import com.github.drapostolos.typeparser.TypeParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * A class with static utility methods for reflection purposes,
 * designed to help with interpretation of reflection-based interpolations.
 */
public abstract class ReflectionUtils {
    
    /**
     * Get methods available for calling on an object
     * @param obj the owner object containing methods
     * @param annotationClass if not null, only methods marked with the corresponding annotation are collected
     * @param methodFilter a predicate determining if a method fits the search
     * @param methodNameGetter a function to determine the found alias of a method.
     *                         Only gets called if an annotation is present - by default, the method's name is used
     * @return a map of methods' name to the methods themselves.
     * @throws IllegalArgumentException As no name-based overloading is possible with this setup,
     *          if two suitable methods with the same name are found, an exception is thrown.
     * @param <A> the annotation used in the search
     */
    public static <A extends Annotation> Map<String, Method> getAvailableMethods(
        Object obj,
        Class<A> annotationClass,
        BiPredicate<Method, A> methodFilter,
        BiFunction<Method, A, String> methodNameGetter
    ){
        Map<String, Method> methods = new HashMap<>();
        if(obj == null) return methods;
        
        Class<?> c = obj.getClass();
        boolean needsAnnotation = annotationClass != null;
        for(Method m: c.getMethods()){
            A a = needsAnnotation ? m.getAnnotation(annotationClass) : null;
            if(annotationClass != null && a == null)
                continue;
            
            if(!methodFilter.test(m, a))
                continue;
            
            String name = a != null ? methodNameGetter.apply(m, a) : m.getName();
            if(methods.containsKey(name))
                throw new IllegalArgumentException(String.format(
                    "Class %s has several methods with alias '%s' " +
                        "(method overloading is currently unsupported)",
                    c.getName(),
                    name
                ));
            
            m.setAccessible(true);
            
            methods.put(name, m);
        }
        
        return methods;
    }
    
    
    public static Object invokeMethodWithParsedArguments(
        Object obj,
        String methodName,
        BiFunction<Object, String, Method> methodGetter,
        List<Object> arguments,
        TypeParser typeParser
    ) {
        Method method = methodGetter.apply(obj, methodName);
        
        if(method == null)
            throw new IllegalArgumentException(String.format(
                "Call operation in a template could not find a suitable method '%s'",
                methodName
            )); //TODO позиция
        
        
        if(method.getParameterCount() != arguments.size())
            throw new IllegalArgumentException(String.format(
                "Call operation in a template expected %d arguments for method '%s()', %d found",
                method.getParameterCount(),
                methodName,
                arguments.size()
            )); //TODO позиция
        
        Object[] parsedArguments = new Object[method.getParameterCount()];
        Type[] paramTypes = method.getParameterTypes();
        for(int i = 0; i < method.getParameterCount(); i++){
            Type expectedType = paramTypes[i];
            Object arg = arguments.get(i);
            Class<Integer> integerClass = int.class;
            if(arg != null && !(expectedType.equals(arg.getClass()) || expectedType.equals(getPrimitiveClass(arg)))){
                if(arg instanceof String stringArg)
                    arg = typeParser.parseType(stringArg, expectedType); //TODO ошибки парсинга
                else
                    throw new IllegalArgumentException(String.format(
                        "Type mismatch in a template for a call operation for method '%s()'",
                        methodName
                    )); //TODO позиция и подробнее
            }
            parsedArguments[i] = arg;
        }
        
        try {
            return method.invoke(obj, parsedArguments);
        } catch (IllegalAccessException e) { //TODO
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Class<?> getPrimitiveClass(Object obj){
        if(obj instanceof Integer) return int.class;
        if(obj instanceof Long) return long.class;
        if(obj instanceof Float) return float.class;
        if(obj instanceof Double) return double.class;
        if(obj instanceof Boolean) return boolean.class;
        return null;
    }
}
