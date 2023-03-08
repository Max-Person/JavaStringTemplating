package com.github.max_person.templating.expressions;

import com.github.max_person.templating.InterpretationData;

public class OrExpr  extends BinaryOpExpr {
    public OrExpr(TemplateExpr op1, TemplateExpr op2) {
        super(op1, op2);
    }
    
    @Override
    public Object evaluate(InterpretationData data) {
        Object val1 = op1.evaluate(data);
        if (isTruthy(val1))
            return val1;
        else
            return op2.evaluate(data);
    }
}
