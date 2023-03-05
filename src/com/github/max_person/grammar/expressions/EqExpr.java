package com.github.max_person.grammar.expressions;

import com.github.max_person.grammar.TemplateInterpreter;

import java.util.Objects;

public class EqExpr extends BinaryOpExpr{
    public EqExpr(TemplateExpr op1, TemplateExpr op2) {
        super(op1, op2);
    }
    
    @Override
    public Object evaluate(TemplateInterpreter data) {
        Object val1 = op1.evaluate(data);
        Object val2 = op2.evaluate(data);
        return Objects.equals(val1, val2) ||
                val1 instanceof Number && val2 instanceof Number && ((Number) val1).doubleValue() == ((Number) val2).doubleValue();
    }
}
