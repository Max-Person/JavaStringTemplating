package com.github.max_person.templating.expressions;

import com.github.max_person.templating.InterpretationData;
import com.github.max_person.templating.TemplateSection;

public abstract class TemplateExpr implements TemplateSection {
    
    abstract public Object evaluate(InterpretationData data);
    
    @Override
    public String interpret(InterpretationData data) {
        return this.evaluate(data).toString();
    }
    
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
        else if(value instanceof Number){
            return value.equals(0);
        }
        else if(value instanceof String){
            return ((String) value).isEmpty();
        }
        return false;
    }
}
