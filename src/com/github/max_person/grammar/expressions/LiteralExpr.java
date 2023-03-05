package com.github.max_person.grammar.expressions;

import com.github.max_person.grammar.TemplateInterpreter;

public class LiteralExpr<ValueType> extends TemplateExpr {
    public final ValueType value;
    public LiteralExpr(ValueType value){
        this.value = value;
    }
    
    @Override
    public Object evaluate(TemplateInterpreter data) {
        return value;
    }
}
