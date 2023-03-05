package com.github.max_person.grammar.expressions;

import com.github.max_person.grammar.TemplateInterpreter;

abstract public class NumericExpr extends BinaryOpExpr {
    public NumericExpr(TemplateExpr op1, TemplateExpr op2) {
        super(op1, op2);
    }
    
    @Override
    public Object evaluate(TemplateInterpreter data) {
        Object val1 = op1.evaluate(data);
        Object val2 = op2.evaluate(data);
        if(!(val1 instanceof Double || val1 instanceof Integer ) ||
                !(val2 instanceof Double || val2 instanceof Integer ))
            throw new TemplateEvaluationException(); //TODO
        
        if(val1 instanceof Integer && val2 instanceof Integer){
            return evaluate((Integer) val1, (Integer) val2);
        }
        else {
            val1 = ((Number) val1).doubleValue();
            val2 = ((Number) val2).doubleValue();
            return evaluate((Double) val1, (Double) val2);
        }
    }
    
    abstract public Object evaluate(Double val1, Double val2);
    abstract public Object evaluate(Integer val1, Integer val2);
}
