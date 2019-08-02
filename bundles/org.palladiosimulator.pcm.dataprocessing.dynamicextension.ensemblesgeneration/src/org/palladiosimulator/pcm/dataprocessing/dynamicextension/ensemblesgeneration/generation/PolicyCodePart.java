package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaClass;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaBlock;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ValueInitialisation;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml.AttributeExtractor;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml.Category;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml.ComponentCode;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.util.ScalaHelper;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;

public class PolicyCodePart implements CodePart {
    private static final String POLICY_PREFIX = "policy:";

    private static final String MAPPING = ".map[" + ScalaHelper.KEYWORD_COMPONENT + "](x => x.getClass().cast(x))";

    private final PolicyType policy;

    private final String actionName;

    public PolicyCodePart(final PolicyType policy) {
        this.policy = policy;
        this.actionName = this.policy.getPolicyId().replaceFirst(POLICY_PREFIX, "");
    }

    @Override
    public ScalaBlock getCode() {
        // TODO: constants

        final ScalaBlock ensembleCode = new ScalaBlock();

        final var actionEnsembleClass = new ScalaClass(true, this.actionName, ScalaHelper.KEYWORD_ENSEMBLE);
        ensembleCode.appendPreBlockCode(actionEnsembleClass);

        // subjects
        final var subjectExtractor = new AttributeExtractor(this.policy, Category.SUBJECT);
        final String subjectExpression = getExpression(ComponentCode.SUBJECT_CLASS_NAME, subjectExtractor);
        final String subjectFieldName = "allowedSubjects";
        ensembleCode.appendBlockCode(new ValueInitialisation(subjectFieldName, subjectExpression));

        // resources
        final var resourceExtractor = new AttributeExtractor(this.policy, Category.RESOURCE);
        final String resourceExpression = getExpression(ComponentCode.RESOURCE_CLASS_NAME, resourceExtractor);
        final String resourceFieldName = "allowedResources";
        ensembleCode.appendBlockCode(new ValueInitialisation(resourceFieldName, resourceExpression));

        // environment
        final var environmentExtractor = new AttributeExtractor(this.policy, Category.ENVIRONMENT);
        ensembleCode.appendBlockCode(situation(environmentExtractor.extract()));

        ensembleCode.appendBlockCode(new StringBuilder("\n"));
        ensembleCode.appendBlockCode(allow(subjectFieldName, this.actionName, resourceFieldName));

        return ensembleCode;
    }

    private String getExpression(final String categoryClassName, final AttributeExtractor extractor) {
        final StringBuilder extractionResult = extractor.extract();
        return "components.select[" + categoryClassName + "]." + "filter(" + AttributeExtractor.VAR_NAME + " => "
                + (extractionResult.length() == 0 ? "true" : extractionResult) + ")" + MAPPING;
    }

    private StringBuilder allow(final String subjects, final String action, final String resourceName) {
        return new StringBuilder(ScalaHelper.KEYWORD_ALLOW).append("(").append(subjects).append(", ").append("\"")
                .append(action).append("\", ").append(resourceName).append(")");
    }

    private StringBuilder situation(final StringBuilder expression) {
        if (expression.length() > 0) {
            return expression.insert(0, "situation {\n").append("\n}");
        }
        return expression;
    }

    public String getActionName() {
        return this.actionName;
    }
}
