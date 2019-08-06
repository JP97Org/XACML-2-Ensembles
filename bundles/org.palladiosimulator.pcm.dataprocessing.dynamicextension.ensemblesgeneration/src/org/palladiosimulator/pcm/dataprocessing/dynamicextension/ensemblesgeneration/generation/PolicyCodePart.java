package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaClass;
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
    private static final String POLICY_PREFIX = "policy:";
    private static final String MAPPING = ".map[" + ScalaHelper.KEYWORD_COMPONENT + "](x => x.getClass().cast(x))";

    protected static final String COMPONENTS = "components";
    protected static final String SUBJECT_FIELD_NAME = "allowedSubjects";
    protected static final String RESOURCE_FIELD_NAME = "allowedResources";
    protected static final String SITUATION = "situation";
    
    private final PolicyType policy;

    private final String actionName;

    /**
     * Creates a new policy code part for the given policy.
     * 
     * @param policy - the given policy
     */
    public PolicyCodePart(final PolicyType policy) {
        this.policy = policy;
        this.actionName = this.policy.getPolicyId().replaceFirst(POLICY_PREFIX, "");
    }

    @Override
    public ScalaBlock getCode() {
        final ScalaBlock ensembleCode = new ScalaBlock();

        final var actionEnsembleClass = new ScalaClass(true, this.actionName, ScalaHelper.KEYWORD_ENSEMBLE);
        ensembleCode.appendPreBlockCode(actionEnsembleClass);

        // subjects
        final var subjectExtractor = new AttributeExtractor(this.policy, Category.SUBJECT);
        final String subjectExpression = getExpression(ComponentCode.SUBJECT_CLASS_NAME, subjectExtractor);
        ensembleCode.appendBlockCode(new ValueInitialisation(SUBJECT_FIELD_NAME, subjectExpression));

        // resources
        final var resourceExtractor = new AttributeExtractor(this.policy, Category.RESOURCE);
        final String resourceExpression = getExpression(ComponentCode.RESOURCE_CLASS_NAME, resourceExtractor);
        ensembleCode.appendBlockCode(new ValueInitialisation(RESOURCE_FIELD_NAME, resourceExpression));

        // environment
        final var environmentExtractor = new AttributeExtractor(this.policy, Category.ENVIRONMENT);
        ensembleCode.appendBlockCode(situation(environmentExtractor.extract()));

        ensembleCode.appendBlockCode(new StringBuilder("\n"));
        ensembleCode.appendBlockCode(allow(SUBJECT_FIELD_NAME, this.actionName, RESOURCE_FIELD_NAME));

        return ensembleCode;
    }

    private String getExpression(final String categoryClassName, final AttributeExtractor extractor) {
        final StringBuilder extractionResult = extractor.extract();
        return COMPONENTS + ".select[" + categoryClassName + "]." + "filter(" + AttributeExtractor.VAR_NAME + " => "
                + (extractionResult.length() == 0 ? "true" : extractionResult) + ")" + MAPPING;
    }

    private Call allow(final String subjects, final String action, final String resourceName) {
        return new Call(ScalaHelper.KEYWORD_ALLOW, subjects + ", " + "\"" + action + "\", " + resourceName);
    }

    private StringBuilder situation(final StringBuilder expression) {
        if (expression.length() > 0) {
            return expression.insert(0, SITUATION + " {\n").append("\n}");
        }
        return expression;
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
