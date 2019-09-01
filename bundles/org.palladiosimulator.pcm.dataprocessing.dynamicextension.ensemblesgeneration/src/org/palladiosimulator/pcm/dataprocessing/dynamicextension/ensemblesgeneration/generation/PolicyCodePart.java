package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaClass;

import java.util.Objects;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.Call;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaBlock;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ValueInitialisation;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml.AttributeExtractor;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml.Category;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml.ComponentCode;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.util.ScalaHelper;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;

/**
 * Represents the code part which represents a XACML policy, i.e. an ensemble in the ensemble system.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class PolicyCodePart implements CodePart {
    private static final String POLICY_PREFIX = ScalaHelper.POLICY_PREFIX;

    private static final String COMPONENTS = ScalaHelper.COMPONENTS;
    private static final String SUBJECT_FIELD_NAME = ScalaHelper.SUBJECT_FIELD_NAME;
    private static final String RESOURCE_FIELD_NAME = ScalaHelper.RESOURCE_FIELD_NAME;
    
    private final PolicyType policy;

    private final String actionName;

    /**
     * Creates a new policy code part for the given policy.
     * 
     * @param policy - the given policy
     */
    public PolicyCodePart(final PolicyType policy) {
        this.policy = Objects.requireNonNull(policy);
        this.actionName = Objects.requireNonNull(this.policy.getPolicyId()).replaceFirst(POLICY_PREFIX, "");
    }

    @Override
    public ScalaBlock getCode() {
        final ScalaBlock ensembleCode = new ScalaBlock();

        final var actionEnsembleClass = new ScalaClass(true, this.actionName, ScalaHelper.KEYWORD_ENSEMBLE);
        ensembleCode.appendPreBlockCode(actionEnsembleClass);

        // resources
        final var resourceExtractor = new AttributeExtractor(this.policy, Category.RESOURCE);
        final String resourceExpression = getExpression(ComponentCode.RESOURCE_CLASS_NAME, resourceExtractor);
        ensembleCode.appendBlockCode(new ValueInitialisation(RESOURCE_FIELD_NAME, resourceExpression));
        
        // subjects and shifts (environment)
        final var subjectExtractor = new AttributeExtractor(this.policy, Category.SUBJECT);
        final String subjectExpression = getExpression(ComponentCode.SUBJECT_CLASS_NAME, subjectExtractor);
        ensembleCode.appendBlockCode(new ValueInitialisation(SUBJECT_FIELD_NAME, subjectExpression));

        // allow call
        ensembleCode.appendBlockCode(new StringBuilder("\n"));
        ensembleCode.appendBlockCode(getAllow(SUBJECT_FIELD_NAME, this.actionName, RESOURCE_FIELD_NAME));
        
        return ensembleCode;
    }

    /**
     * Gets the expression which checks the attributes of the category with the category class name 
     * and the extraction result of the given attribute extractor.
     * 
     * @param categoryClassName - the given category class name
     * @param extractor - the given attribute extractor
     * @return the expression which checks the attributes of the category
     */
    private String getExpression(final String categoryClassName, final AttributeExtractor extractor) {
        final String extractionResult = extractor.extract().toString();
        final String filterChecks = extractionResult.length() == 0 ? "true" : extractionResult;
        final String map = ".map[%s]%s";
        final String cast = "(x => x.getClass().cast(x))";
        final String mapping = String.format(map, categoryClassName, cast);
        return String.format("%s.select[%s].filter(%s => %s)%s", 
                COMPONENTS, categoryClassName, ScalaHelper.VAR_NAME, filterChecks, mapping);
    }

    /**
     * Gets the allow call.
     * 
     * @param subjectsName - the subjects field name
     * @param actionName - the action name
     * @param resourcesName - the resources field name
     * @return the allow call
     */
    private Call getAllow(final String subjectsName, final String actionName, final String resourcesName) {
        return new Call(ScalaHelper.KEYWORD_ALLOW, subjectsName + ", " + "\"" + actionName + "\", " + resourcesName);
    }

    /**
     * Gets the action name.
     * 
     * @return the action name
     */
    public String getActionName() {
        return this.actionName;
    }
}
