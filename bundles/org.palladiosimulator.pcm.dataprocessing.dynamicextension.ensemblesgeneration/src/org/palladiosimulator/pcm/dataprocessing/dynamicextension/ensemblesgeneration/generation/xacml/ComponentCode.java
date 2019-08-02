package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaBlock;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaClass;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaCode;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ValueDeclaration;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.util.ScalaHelper;

public class ComponentCode implements ScalaCode {
    public static final String SUBJECT_CLASS_NAME = "Subject";
    public static final String RESOURCE_CLASS_NAME = "Resource";
    public static final String ENVIRONMENT_CLASS_NAME = "Environment";

    private final Set<Attribute> existingAttributes;

    public ComponentCode(final Set<Attribute> existingAttributes) {
        this.existingAttributes = existingAttributes;
    }

    @Override
    public StringBuilder getCodeDefinition() {
        final StringBuilder ret = new StringBuilder(getSubjectComponentCode()).append("\n");
        ret.append(getResourceComponentCode()).append("\n");
        return ret;
    }

    private StringBuilder getSubjectComponentCode() {
        return getComponentCode(SUBJECT_CLASS_NAME, "subjectName", false, Category.SUBJECT);
    }

    private StringBuilder getResourceComponentCode() {
        return getComponentCode(RESOURCE_CLASS_NAME, "resourceName", false, Category.RESOURCE);
    }

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
        // TODO scala: aufruf
        // name setting
        classBlock.appendBlockCode(new StringBuilder("name(s\"" + className + " $" + categoryScalaName + "\")"));

        return classBlock.getCodeDefinition().append("\n");
    }
}
