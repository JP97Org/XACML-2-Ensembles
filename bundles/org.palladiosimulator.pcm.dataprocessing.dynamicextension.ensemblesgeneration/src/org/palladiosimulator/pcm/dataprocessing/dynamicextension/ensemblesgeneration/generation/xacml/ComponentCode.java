package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.Call;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaBlock;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaClass;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaCode;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ValueDeclaration;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.util.ScalaHelper;

/**
 * Represents the subject and resource component class code.
 * 
 * @author Jonathan Schenkenberger
 * @verison 1.0
 */
public class ComponentCode implements ScalaCode {
    public static final String SUBJECT_CLASS_NAME = ScalaHelper.KEYWORD_SUBJECT;
    public static final String RESOURCE_CLASS_NAME = ScalaHelper.KEYWORD_RESOURCE;

    private final Set<Attribute> existingAttributes;

    /**
     * Creates a new component code with the given existing attributes in a set.
     * 
     * @param existingAttributes
     *          - the given existing attributes in a set
     */
    public ComponentCode(final Set<Attribute> existingAttributes) {
        this.existingAttributes = existingAttributes;
    }

    @Override
    public StringBuilder getCodeDefinition() {
        final StringBuilder ret = new StringBuilder(getSubjectComponentCode()).append("\n");
        ret.append(getResourceComponentCode()).append("\n");
        return ret;
    }

    /**
     * Gets the subject component code.
     * 
     * @return the subject component code
     */
    private StringBuilder getSubjectComponentCode() {
        return getComponentCode(SUBJECT_CLASS_NAME, "subjectName", false, Category.SUBJECT);
    }

    /**
     * Gets the resource component code.
     * 
     * @return the resource component code
     */
    private StringBuilder getResourceComponentCode() {
        return getComponentCode(RESOURCE_CLASS_NAME, "resourceName", false, Category.RESOURCE);
    }

    /**
     * Gets the component code with the given settings.
     * 
     * @param className
     *          - the class name
     * @param categoryScalaName
     *          - the name of the 'name' attribute of the category
     * @param isOptional
     *          - whether the 'name' is optional, i.e. has standard value 'null'
     * @param category
     *          - the category
     * @return the component code
     */
    private StringBuilder getComponentCode(final String className, final String categoryScalaName,
            final boolean isOptional, final Category category) {
        final var componentClass = new ScalaClass(false, className, ScalaHelper.KEYWORD_COMPONENT);

        final List<ValueDeclaration> attributes = new ArrayList<>();
        if (categoryScalaName != null) {
            attributes.add(new ValueDeclaration(categoryScalaName, ScalaHelper.KEYWORD_STRING, isOptional));
        }

        for (final Attribute attribute : Attribute.getCategoryAttributes(category)) {
            if (this.existingAttributes.contains(attribute)) {
                final var declaration = new ValueDeclaration(attribute.getScalaAttributeName(),
                        attribute.getScalaType(), true);
                if (!attributes.contains(declaration)) {
                    attributes.add(declaration);
                }
            }
        }

        componentClass.addAllAttributes(attributes);

        final ScalaBlock classBlock = new ScalaBlock();
        classBlock.appendPreBlockCode(componentClass);
        // name setting
        classBlock.appendBlockCode(new Call("name", "s\"" + className + " $" + categoryScalaName + "\""));

        return classBlock.getCodeDefinition().append("\n");
    }
}
