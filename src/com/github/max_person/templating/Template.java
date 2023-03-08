package com.github.max_person.templating;

import com.github.max_person.templating.expressions.TemplateExpr;

import java.io.IOException;
import java.io.StringReader;

public class Template {
    final TemplateExpr expr;
    public Template(String str){
        TemplateParser p = new TemplateParser(new TemplateScanner(new StringReader(str)));
        try {
            this.expr = p._parse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
