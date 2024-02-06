package com.github.max_person.templating;

/**
 * An interface for parsed interpolation sections to use
 */
public interface TemplateSection {
    /**
     * Evaluate this interpolation section to a plain text representation
     * that will then be inserted in the output string in its place
     * @param data the data available for interpretation of the template
     * @return plain text representation of this section
     */
    String interpret(InterpretationData data);
}
