package com.github.max_person.templating;

/**
 * An interface to provide custom interpolation syntax support to the {@link Template}s
 * <p>
 * When creating a {@link Template}, a parsing step is performed, splitting a string into plain string sections
 * and interpolations (denoted with ${}).
 * The contents of the interpolations are then passed as plain text to the TemplateInterpolationParser being used,
 * for it to parse the text in an arbitrary way, producing a {@link TemplateSection},
 * which is then stored in a Template and used during the interpretation step
 * <p>
 * <b>NOTE:</b> It is assumed that the custom interpolation syntaxes are balanced in terms of curly braces { },
 * as the contents of each interpolation are matched accordingly
 * @param <T> Type of objects representing the parsed output of this parser
 */
public interface TemplateInterpolationParser<T extends TemplateSection> {
    
    /**
     * Parse the contents of interpolation into a {@link TemplateSection}
     * @param interpolationContent the contents of interpolation
     * @return a {@link TemplateSection} representing the parsed interpolation context
     */
    T parse(InterpolationContent interpolationContent);
    
    /**
     * Info about the content of the template interpolation
     * @param content plain text content of the interpolation (excluding the surrounding notation, such as ${})
     * @param isSimpleInterpolation flag denoting if the interpolation is simple ('$content' rather than '${content}')
     */
    record InterpolationContent(String content, boolean isSimpleInterpolation){}
}
