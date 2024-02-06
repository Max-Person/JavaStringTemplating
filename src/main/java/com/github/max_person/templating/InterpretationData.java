package com.github.max_person.templating;

import com.github.drapostolos.typeparser.TypeParser;

import java.util.HashMap;
import java.util.Map;

/**
 * The data used in substitution of {@link Template}'s interpolations with values
 * <p>
 * InterpretationData is the data component of the substitution process - it defines the <i>what</i> of interpolation,
 * whereas the {@link Template}s define the <i>how</i>
 * - this allows for separate reuse of both the templates and the data needed to fill them.
 * <p>
 * InterpretationData is passed into the {@link TemplateSection#interpret} method of the interpolation sections,
 * provided by {@link TemplateInterpolationParser}s, and can then be used from there
 * during the evaluation of the language constructs into plain text.
 */
public class InterpretationData {
    
    public InterpretationData(){}
    public InterpretationData(InterpretationData other){
        this.global = other.global;
        this.variables.putAll(other.variables);
        this.defaultSafety = other.defaultSafety;
        this.parser = other.parser;
    }
    
    
    private Object global = null;
    public Object getGlobalObj() {
        return global;
    }
    public InterpretationData withGlobalObj(Object global) {
        this.global = global;
        return this;
    }
    public InterpretationData usingGlobalObj(Object global) {
        InterpretationData copy = new InterpretationData(this);
        copy.withGlobalObj(global);
        return copy;
    }
    
    
    private final Map<String, Object> variables = new HashMap<>();
    public Object getVar(String name) {
        return variables.get(name);
    }
    public InterpretationData withVar(String name, Object value) {
        variables.put(name, value);
        return this;
    }
    public InterpretationData usingVar(String name, Object value) {
        InterpretationData copy = new InterpretationData(this);
        copy.withVar(name, value);
        return copy;
    }
    public InterpretationData removeVar(String name) {
        variables.remove(name);
        return this;
    }
    
    public void clear() {
        global = null;
        variables.clear();
    }
    
    
    private boolean defaultSafety = true;
    public boolean getDefaultSafety() {
        return defaultSafety;
    }
    public InterpretationData withDefaultSafety(boolean defaultSafety) {
        this.defaultSafety = defaultSafety;
        return this;
    }
    
    
    private TypeParser parser = TypeParser.newBuilder().build();
    public TypeParser getParser() {
        return parser;
    }
    public InterpretationData withParser(TypeParser parser) {
        this.parser = parser;
        return this;
    }
    
    public String interpret(Template template){
        return template.interpret(this);
    }
    public String interpret(String str){
        return interpret(new Template(str));
    }
    
}
