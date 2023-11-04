package com.github.max_person.templating.expressions;

import com.github.max_person.templating.InterpretationData;

public abstract class TemplateExpr {
    
    abstract public Object evaluate(InterpretationData data);
    
    public static boolean isTruthy(Object value){
        return !isFalsy(value);
    }
    
    public static boolean isFalsy(Object value){
        if(value == null){
            return true;
        }
        else if(value instanceof Boolean){
            return !(Boolean) value;
        }
        else if(value instanceof Integer ||
                value instanceof Long ||
                value instanceof Float ||
                value instanceof Double){
            return value.equals(0);
        }
        else if(value instanceof String){
            return ((String) value).isEmpty();
        }
        return false;
    }
}
