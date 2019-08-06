package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala;

import java.util.List;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.util.ScalaHelper;

/**
 * Represents a scala method signature.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class MethodSignature implements ScalaCode {
    private final StringBuilder methodSignature;

    /**
     * Creates a new method signature with the given method name and the given arguments as a list of
     * {@code ValueDeclaration}.
     * 
     * @param name
     *            - the given method name
     * @param arguments
     *            - the given arguments
     * @param type
     *            - the return type of the method
     */
    public MethodSignature(final String name, final List<ValueDeclaration> arguments, final String type) {
        this.methodSignature = new StringBuilder(ScalaHelper.KEYWORD_DEF).append(" ")
                .append(ScalaHelper.createIdentifier(name)).append("(");
        if (arguments != null && !arguments.isEmpty()) {
            final StringBuilder builder = new StringBuilder();
            for (var val : arguments) {
                builder.append(val.getCodeDefinition().toString().replaceFirst(ScalaHelper.KEYWORD_VAL + " ", "")).append(", ");
            }
            this.methodSignature.append(builder.substring(0, builder.length() - 2));
        }
        this.methodSignature.append(")").append(" : ").append(type).append(" =");
    }

    @Override
    public StringBuilder getCodeDefinition() {
        return new StringBuilder(this.methodSignature);
    }
}
