package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala;

import java.util.Objects;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.util.ScalaHelper;

/**
 * Represents a "val" declaration.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class ValueDeclaration implements ScalaCode {
    private final String name;
    private final String type;
    private final boolean isOptional;

    /**
     * Creates a new value declaration with the given name and type. This attribute has no standard
     * value.
     * 
     * @param name
     *            - the given name
     * @param type
     *            - the given type
     */
    public ValueDeclaration(final String name, final String type) {
        this.name = ScalaHelper.createIdentifier(name);
        this.type = type;
        this.isOptional = false;
    }

    /**
     * Creates a new value declaration with the given name and type. If isOptional is set, this
     * attribute has a standard value.
     * 
     * @param name
     *            - the given name
     * @param type
     *            - the given type
     * @param isOptional
     *            - whether the attribute setting is optional
     */
    public ValueDeclaration(final String name, final String type, final boolean isOptional) {
        this.name = name;
        this.type = type;
        this.isOptional = isOptional;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    @Override
    public StringBuilder getCodeDefinition() {
        return new StringBuilder(ScalaHelper.KEYWORD_VAL).append(" ").append(this.name).append(": ").append(this.type)
                .append(this.isOptional ? getStandardValue() : "");
    }

    private String getStandardValue() {
        return this.type.equals(ScalaHelper.KEYWORD_INT) || this.type.equals(ScalaHelper.KEYWORD_DOUBLE) 
                ? " = 0" : " = null";
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), this.name);
    }

    @Override
    public boolean equals(final Object other) {
        if (other != null) {
            if (other.getClass().equals(this.getClass())) {
                final ValueDeclaration otherVal = (ValueDeclaration) other;
                return this.name.equals(otherVal.name);
            }
        }
        return false;
    }
}
