package com.github.max_person.grammar.expressions;

import com.github.max_person.grammar.InterpretationData;

import java.util.ArrayList;
import java.util.List;

public class StringConcatExpr extends TemplateExpr{
    private List<TemplateExpr> concat = new ArrayList<>();
    public StringConcatExpr(LiteralExpr<String> string){
        concat.add(string);
    }
    public void addInterpolation(TemplateExpr interpolation, LiteralExpr<String> closingString){
        concat.add(interpolation);
        if(!closingString.value.isEmpty()){
            concat.add(closingString);
        }
    }
    
    @Override
    public Object evaluate(InterpretationData data) {
        StringBuilder b = new StringBuilder();
        for(TemplateExpr e: concat){
            b.append(e.evaluate(data).toString());
        }
        return b.toString();
    }
}
