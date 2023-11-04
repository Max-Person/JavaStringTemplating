package com.github.max_person.templating.expressions;

import com.github.max_person.templating.InterpretationData;

public class ConditionalExpr extends TemplateExpr{
    protected TemplateExpr condition;
    protected TemplateExpr op1;
    protected TemplateExpr op2;
    
    public ConditionalExpr(TemplateExpr condition, TemplateExpr op1, TemplateExpr op2){
        this.condition = condition;
        this.op1 = op1;
        this.op2 = op2;
    }
    
    @Override
    public Object evaluate(InterpretationData data) {
        if(isTruthy(condition.evaluate(data)))
            return op1.evaluate(data);
        else
            return op2.evaluate(data);
    }
}
