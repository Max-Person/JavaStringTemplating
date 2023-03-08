package com.github.max_person.templating;

import com.github.drapostolos.typeparser.TypeParser;

import java.util.HashMap;
import java.util.Map;

public class InterpretationData {
    
    public InterpretationData(){}
    public InterpretationData(InterpretationData other){
        this.global = other.global;
        this.variables.putAll(other.variables);
    }
    
    
    private Object global = null;
    public InterpretationData setGlobalObj(Object global) {
        this.global = global;
        return this;
    }
    public InterpretationData usingGlobalObj(Object global) {
        InterpretationData copy = new InterpretationData(this);
        copy.setGlobalObj(global);
        return copy;
    }
    public Object getGlobalObj() {
        return global;
    }
    
    
    private final Map<String, Object> variables = new HashMap<>();
    public Object getVar(String name) {
        return variables.get(name);
    }
    public InterpretationData setVar(String name, Object value) {
        variables.put(name, value);
        return this;
    }
    public InterpretationData removeVar(String name) {
        variables.remove(name);
        return this;
    }
    public InterpretationData usingVar(String name, Object value) {
        InterpretationData copy = new InterpretationData(this);
        copy.setVar(name, value);
        return copy;
    }
    
    public void clear() {
        global = null;
        variables.clear();
    }
    
    
    private boolean defaultSafety = true;
    public boolean getDefaultSafety() {
        return defaultSafety;
    }
    public void setDefaultSafety(boolean defaultSafety) {
        this.defaultSafety = defaultSafety;
    }
    
    
    private TypeParser parser = TypeParser.newBuilder().build();
    public TypeParser getParser() {
        return parser;
    }
    public void setParser(TypeParser parser) {
        this.parser = parser;
    }
    
    public String interpret(Template template){
        return template.expr.evaluate(this).toString();
    }
    public String interpret(String str){
        return interpret(new Template(str));
    }
    
}
