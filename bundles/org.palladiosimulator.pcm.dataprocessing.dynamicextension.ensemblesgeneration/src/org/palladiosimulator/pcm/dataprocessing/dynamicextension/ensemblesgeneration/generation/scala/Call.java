package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala;

import java.util.List;

/**
 * Represents a scala method call.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class Call implements ScalaCode {
    private final String name;
    private final String callContent;

    /**
     * Creates a new call with the given method name and the given call content.
     * 
     * @param name
     *            - the given method name
     * @param callContent
     *            - the call content (the argument(s) of the method call)
     */
    public Call(final String name, final String callContent) {
        this.name = name;
        this.callContent = callContent;
    }

    /**
     * Creates a new call with the given method name and the given arguments as a list of
     * {@code ValueDeclaration}.
     * 
     * @param name
     *            - the given method name
     * @param arguments
     *            - the given arguments
     */
    public Call(final String name, final List<ValueDeclaration> arguments) {
        this.name = name;
        if (!arguments.isEmpty()) {
            final StringBuilder builder = new StringBuilder();
            for (var val : arguments) {
                builder.append(val.getName()).append(", ");
            }
            this.callContent = builder.substring(0, builder.length() - 2);
        } else {
            this.callContent = "";
        }
    }
    
    /**
     * Gets the name of the called method.
     * 
     * @return the name of the called method
     */
    public String getName() {
        return this.name;
    }
    
    @Override
    public StringBuilder getCodeDefinition() {
        return new StringBuilder(this.name).append("(").append(this.callContent).append(")");
    }
}
