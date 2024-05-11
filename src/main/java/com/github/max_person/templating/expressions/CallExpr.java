package com.github.max_person.templating.expressions;

import com.github.max_person.templating.InterpretationData;
import com.github.max_person.templating.ReflectionUtils;
import com.github.max_person.templating.TemplatingSafeMethod;

import java.util.List;

public class CallExpr extends TemplateExpr{
    private TemplateExpr op;
    private String identifier;
    private List<TemplateExpr> arguments;
    
    public CallExpr(TemplateExpr op, String identifier, List<TemplateExpr> arguments) {
        this.op = op;
        this.identifier = identifier;
        this.arguments = arguments;
    }
    
    @Override
    public Object evaluate(InterpretationData data) {
        Object owner = op == null ? data.getGlobalObj() : op.evaluate(data);
        
        return ReflectionUtils.invokeMethodWithParsedArguments(
            owner,
            identifier,
            (object, name) -> ReflectionUtils.getAvailableMethods(
                object,
                data.getDefaultSafety() ? TemplatingSafeMethod.class : null,
                (m, a) -> true,
                (m, a) -> a.value().isBlank() ? m.getName() : a.value()
            ).get(name),
            arguments.stream().map(expr -> expr.evaluate(data)).toList(),
            data.getParser()
        );
    }
}