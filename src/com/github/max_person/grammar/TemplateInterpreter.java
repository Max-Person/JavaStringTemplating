package com.github.max_person.grammar;

import com.github.drapostolos.typeparser.TypeParser;

import java.util.HashMap;
import java.util.Map;

public class TemplateInterpreter {
    private final Map<String, Object> variables = new HashMap<>();
    private Object global;
    
    public TemplateInterpreter(Object global) {
        this.global = global;
    }
    public TemplateInterpreter() {
        this.global = null;
    }
    public TemplateInterpreter(TemplateInterpreter other){
        this.global = other.global;
        this.variables.putAll(other.variables);
    }
    
    public TemplateInterpreter setGlobalObj(Object global) {
        this.global = global;
        return this;
    }
    public Object getGlobalObj() {
        return global;
    }
    
    public Object getVar(String name) {
        return variables.get(name);
    }
    public TemplateInterpreter setVar(String name, Object value) {
        variables.put(name, value);
        return this;
    }
    public TemplateInterpreter removeVar(String name) {
        variables.remove(name);
        return this;
    }
    
    public void clear() {
        global = null;
        variables.clear();
    }
    
    
    public TemplateInterpreter usingGlobalObj(Object global) {
        TemplateInterpreter copy = new TemplateInterpreter(this);
        copy.setGlobalObj(global);
        return copy;
    }
    public TemplateInterpreter usingVar(String name, Object value) {
        TemplateInterpreter copy = new TemplateInterpreter(this);
        copy.setVar(name, value);
        return copy;
    }
    
    
    private boolean defaultSafety = true;
    public boolean getDefaultSafety() {
        return defaultSafety;
    }
    public void setDefaultSafety(boolean defaultSafety) {
        this.defaultSafety = defaultSafety;
    }
    
    
    public TypeParser parser = TypeParser.newBuilder().build(); //TODO возможность добавлять парсеры
    
    public String intepret(Template template){
        return template.expr.evaluate(this).toString();
    }
    public String intepret(String str){
        return new Template(str).expr.evaluate(this).toString();
    }
    
}
