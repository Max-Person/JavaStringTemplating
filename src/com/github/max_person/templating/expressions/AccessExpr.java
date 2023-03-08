package com.github.max_person.templating.expressions;

import com.github.max_person.templating.InterpretationData;
import com.github.max_person.templating.TemplatingSafeField;

import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AccessExpr extends TemplateExpr{
    private TemplateExpr op;
    private String identifier;
    
    public AccessExpr(TemplateExpr op, String identifier) {
        this.op = op;
        this.identifier = identifier;
    }
    
    private static class FieldLike{
        private final Field field;
        private final Method getter;
        private final Object owner;
    
        public FieldLike(Field field, Object owner) {
            this.field = field;
            this.getter = null;
            this.owner = owner;
        }
    
        public FieldLike(Method getter, Object owner) {
            this.field = null;
            this.getter = getter;
            this.owner = owner;
        }
    
        public FieldLike(Object obj) {
            this.field = null;
            this.getter = null;
            this.owner = obj;
        }
        
        public Object get() throws IllegalAccessException, InvocationTargetException {
            if(field!=null)
                return field.get(owner);
            else if(getter != null)
                return getter.invoke(owner);
            else
                return owner;
        }
    }
    
    private Map<String, FieldLike> getAvailableFields(Object obj, boolean safe){
        Map<String, FieldLike> fields = new HashMap<>();
        Class<?> c = obj.getClass();
        for(Field f: c.getFields()){
            TemplatingSafeField a = f.getAnnotation(TemplatingSafeField.class);
            if(safe && a == null)
                continue;
            
            String name = a != null && !a.value().isEmpty() ? a.value() : f.getName();
            
            if(fields.containsKey(name)){
                throw new IllegalArgumentException(String.format("Class %s has several fields with templating alias '%s'", c.getName(), name));
            }
            
            f.setAccessible(true);
            fields.put(name, new FieldLike(f, obj));
        }
    
        for(Method m: c.getMethods()){
            TemplatingSafeField a = m.getAnnotation(TemplatingSafeField.class);
            if(safe && a == null)
                continue;
            
            if(m.getParameterCount() != 0){
                if(a != null)
                    throw new AnnotationFormatError(String.format("Method %s cannot be marked as %s it has non-zero parameter count", m.getName(), TemplatingSafeField.class.getSimpleName()));
                else
                    continue;
            }
        
            String name = a != null && !a.value().isEmpty() ? a.value() : m.getName();
        
            if(fields.containsKey(name)){
                throw new IllegalArgumentException(String.format("Class %s has several fields with templating alias '%s'", c.getName(), name));
            }
    
            m.setAccessible(true);
            fields.put(name, new FieldLike(m, obj));
        }
        
        return fields;
    }
    
    @Override
    public Object evaluate(InterpretationData data) {
        FieldLike fieldLike = null;
        if(op == null){
            Object value = data.getVar(identifier);
            if(value != null)
                fieldLike = new FieldLike(value);
            else {
                Object global = data.getGlobalObj();
                if(global != null)
                    fieldLike = getAvailableFields(global, data.getDefaultSafety()).get(identifier);
            
            }
        }
        else {
            fieldLike = getAvailableFields(op.evaluate(data), data.getDefaultSafety()).get(identifier);
        }
    
        if(fieldLike == null)
            throw new IllegalArgumentException(String.format("Access operation in a template could not find a field/variable named %s", identifier)); //TODO позиция
        
        try {
            return fieldLike.get();
        } catch (IllegalAccessException e) { //TODO
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
