package com.github.max_person.grammar.expressions;

import com.github.max_person.grammar.InterpretationData;

public class AndExpr extends BinaryOpExpr{
    public AndExpr(TemplateExpr op1, TemplateExpr op2) {
        super(op1, op2);
    }
    
    @Override
    public Object evaluate(InterpretationData data) {
        Object val1 = op1.evaluate(data);
        if(isFalsy(val1))
            return val1;
        else
            return op2.evaluate(data);
    }
}
