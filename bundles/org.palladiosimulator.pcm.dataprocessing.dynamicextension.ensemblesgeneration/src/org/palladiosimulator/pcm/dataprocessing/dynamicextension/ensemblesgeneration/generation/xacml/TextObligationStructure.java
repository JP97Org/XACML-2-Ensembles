package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.Call;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.MethodSignature;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaBlock;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ValueDeclaration;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.handlers.SampleHandler;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.util.ScalaHelper;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionType;

/**
 * Represents a structure which represents one text obligation.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class TextObligationStructure implements ObligationStructure {
    private static final String ID_OBLIGATION_PREREQUISITE = "obligation:prerequisite";
    private static final String PREFIX_PREREQUISITE = "prereq_";

    private static final String ID_IS_END = "context:extension:isend";

    private static final String RETURN = "return true";

    private final boolean isPrerequisite;
    private final String methodName;
    private final boolean isAtEnd;

    /**
     * Creates a new text obligation strucure for the given obligation.
     * 
     * @param obligation - the given obligation
     */
    public TextObligationStructure(final ObligationExpressionType obligation) {
        final String obligationId = obligation.getObligationId();
        this.isPrerequisite = obligationId.equals(ID_OBLIGATION_PREREQUISITE);
        final var list = obligation.getAttributeAssignmentExpression();
        
        final var valuesList = getValues(Arrays.asList(list.get(0)), null).collect(Collectors.toList());
        if (valuesList.isEmpty()) {
            final var error = "illegal text obligation structure in obligation \"" + obligationId + "\"";
            SampleHandler.LOGGER.error(error);
            throw new IllegalStateException(error);
        }
        // extracting the text which is always at position 0
        final var extractedValue = valuesList.get(0);
        this.methodName = this.isPrerequisite ? PREFIX_PREREQUISITE + extractedValue : extractedValue;

        // extracting the isAtEnd information, false if this information is not contained
        this.isAtEnd = getValues(list, ID_IS_END).anyMatch(x -> Boolean.parseBoolean(x));
    }

    /**
     * Extracts all the values contained in the list with the given id or all if {@code id == null}.
     * 
     * @param list - the given list
     * @param id - the given id
     * @return all the values contained in the list
     */
    private static Stream<String> getValues(final List<AttributeAssignmentExpressionType> list, final String id) {
        // extracting all the values in the list with the given id or all if id == null
        return list.stream().filter(x -> id == null || x.getAttributeId().equals(id))
                .filter(x -> x.getExpression().getDeclaredType().isAssignableFrom(AttributeValueType.class))
                .map(x -> (AttributeValueType) (x.getExpression().getValue()))
                .map(x -> x.getContent().get(0).toString());
    }

    @Override
    public Call getMethodCall(final String callContent) {
        return new Call(this.methodName, callContent);
    }

    @Override
    public ScalaBlock getMethodBlock() {
        final ScalaBlock returnBlock = new ScalaBlock();

        // signature
        final var component = new ValueDeclaration("component", ScalaHelper.KEYWORD_COMPONENT);
        final var operationSignatureName = new ValueDeclaration("operationSignatureName", ScalaHelper.KEYWORD_STRING);
        final var notAtEndList = this.isPrerequisite ? Arrays.asList(operationSignatureName) : null;
        final var signature = new MethodSignature(this.methodName,
                this.isAtEnd ? Arrays.asList(component) : notAtEndList, ScalaHelper.KEYWORD_BOOLEAN);
        returnBlock.appendPreBlockCode(signature);

        // content
        final String actionName = this.methodName.replaceFirst(Pattern.quote(PREFIX_PREREQUISITE), "");
        final String todo = this.isPrerequisite
                ? "//TODO implement call to operation signature for operationSignature \"" + actionName + "\""
                : "//TODO adapt this function to match the extension you want to implement";
        returnBlock.appendBlockCode(new StringBuilder(todo).append("\n").append(RETURN));

        return returnBlock;
    }

    @Override
    public boolean isOnlyCalledInSubjects() {
        return this.isPrerequisite;
    }

    @Override
    public boolean isAtEnd() {
        return this.isAtEnd;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), this.methodName);
    }

    @Override
    public boolean equals(final Object other) {
        if (other != null && other.getClass().equals(getClass())) {
            return this.methodName.equals(((TextObligationStructure) other).methodName);
        }
        return false;
    }

    @Override
    public int compareTo(ObligationStructure other) {
        return this.methodName.compareTo(other.getMethodCall("").getName());
    }

    @Override
    public String getName() {
        return this.isPrerequisite ? this.methodName.replaceFirst(Pattern.quote(PREFIX_PREREQUISITE), "")
                : this.methodName;
    }
}
