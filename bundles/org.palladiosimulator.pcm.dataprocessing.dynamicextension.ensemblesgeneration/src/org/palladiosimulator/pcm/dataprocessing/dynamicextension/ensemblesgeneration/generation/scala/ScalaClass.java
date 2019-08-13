package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.util.ScalaHelper;

/**
 * Represents a scala class definition.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class ScalaClass implements ScalaCode {
    private final boolean isSingleton;
    private final String name;
    private final String superClass;

    private final List<ValueDeclaration> attributes;

    /**
     * Creates a new scala class definition with the given settings.
     * 
     * @param isSingleton
     *            - whether the class should be a singleton
     * @param name
     *            - the name of the class
     * @param superClass
     *            - the super class of the class
     */
    public ScalaClass(final boolean isSingleton, final String name, final String superClass) {
        this.isSingleton = isSingleton;
        this.name = ScalaHelper.createIdentifier(name);
        this.superClass = superClass;
        this.attributes = new ArrayList<>();
    }

    /**
     * Adds all the given attributes to the attribute list.
     * 
     * @param attributes
     *            - the given attributes
     */
    public void addAllAttributes(final List<ValueDeclaration> attributes) {
        this.attributes.addAll(attributes);
    }

    @Override
    public StringBuilder getCodeDefinition() {
        return new StringBuilder(this.isSingleton ? ScalaHelper.KEYWORD_OBJECT : ScalaHelper.KEYWORD_CLASS)
                .append(" ")
                .append(this.name)
                .append(getAttributeList())
                .append(" ")
                .append(ScalaHelper.KEYWORD_EXTENDS)
                .append(" ")
                .append(this.superClass);
    }

    /**
     * Gets the attribute list of this scala class.
     * 
     * @return the attribute list of this scala class
     */
    private StringBuilder getAttributeList() {
        if (!this.attributes.isEmpty()) {
            final StringBuilder builder = new StringBuilder("(");
            for (final ValueDeclaration attribute : this.attributes) {
                builder.append(attribute.getCodeDefinition()).append(", ");
            }
            return builder.delete(builder.length() - 2, builder.length()).append(")");
        }
        return new StringBuilder();
    }
}
