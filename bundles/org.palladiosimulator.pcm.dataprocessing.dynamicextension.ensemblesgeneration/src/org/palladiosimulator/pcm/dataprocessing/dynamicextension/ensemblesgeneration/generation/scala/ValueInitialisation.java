package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala;

import java.util.Objects;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.util.ScalaHelper;

/**
 * Represents a "val" initialisation.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class ValueInitialisation implements ScalaCode {
    private final String name;
    private final String expression;

    /**
     * Creates a new value initialisation with the given name and the assignment expression.
     * 
     * @param name
     *            - the given name
     * @param expression
     *            - the expression which is assigned
     */
    public ValueInitialisation(final String name, final String expression) {
        this.name = ScalaHelper.createIdentifier(name);
        this.expression = Objects.requireNonNull(expression);
    }

    @Override
    public StringBuilder getCodeDefinition() {
        return new StringBuilder(ScalaHelper.KEYWORD_VAL)
                .append(" ")
                .append(this.name)
                .append(" = ")
                .append(this.expression)
                .append("\n");
    }
}
