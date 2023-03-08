package com.github.max_person.grammar.expressions;

import com.github.max_person.grammar.InterpretationData;
import com.github.max_person.grammar.TemplatingSafeMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallExpr extends TemplateExpr{
    private TemplateExpr op;
    private String identifier;
    private List<TemplateExpr> arguments;
    
    public CallExpr(TemplateExpr op, String identifier, List<TemplateExpr> arguments) {
        this.op = op;
        this.identifier = identifier;
        this.arguments = arguments;
    }
    
    private Map<String, Method> getAvailableMethods(Object obj, boolean safe){
        Map<String, Method> methods = new HashMap<>();
        for(Method m: obj.getClass().getMethods()){
            TemplatingSafeMethod a = m.getAnnotation(TemplatingSafeMethod.class);
            if(safe && a == null)
                continue;
            
            String name = a != null && !a.value().isEmpty() ? a.value() : m.getName();
            
            if(methods.containsKey(name)){
                throw new TemplateEvaluationException(); //TODO конфликт имен методов
            }
    
            m.setAccessible(true);
            
            methods.put(name, m);
        }
        
        return methods;
    }
    
    @Override
    public Object evaluate(InterpretationData data) {
        Method m = null;
        Object owner;
        if(op == null)
            owner = data.getGlobalObj();
        else
            owner = op.evaluate(data);
    
        if(owner != null)
            m = getAvailableMethods(owner, data.getDefaultSafety()).get(identifier);
    
        if(m == null)
            throw new TemplateEvaluationException(); //TODO метод не найден
        
        if(m.getParameterCount() != arguments.size())
            throw new TemplateEvaluationException(); //TODO несовпадение количества параметров
    
        Object[] args = new Object[m.getParameterCount()];
        Type[] paramTypes = m.getParameterTypes();
        for(int i = 0; i < m.getParameterCount(); i++){
            Object arg = arguments.get(i).evaluate(data);
            if(!paramTypes[i].equals(arg.getClass())){
                if(arg instanceof String)
                    arg = data.getParser().parseType((String) arg, paramTypes[i]);
                else
                    throw new TemplateEvaluationException(); //TODO несовпадение типов
            }
            args[i] = arg;
        }
    
        try {
            return m.invoke(owner, args);
        } catch (IllegalAccessException e) { //TODO
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}