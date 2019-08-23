package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.handlers.SampleHandler;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.util.ScalaHelper;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;

/**
 * Represents a policy attribute extractor which can extract attributes and checking code.
 * 
 * @author Jonathan Schenkenberger
 * @verison 1.0
 */
public class AttributeExtractor {
    public static final String VAR_NAME = "x";
    private static final String OR = " || ";

    private final PolicyType policy;
    private final Category category;

    /**
     * Creates a new attribute extractor for the given policy and the given category.
     * 
     * @param policy - the given policy
     * @param category - the given category
     */
    public AttributeExtractor(final PolicyType policy, final Category category) {
        this.policy = policy;
        this.category = category;
    }

    /**
     * Extracts the existing attributes set, i.e. all existing attributes in the rules of this policies.
     * 
     * @return the existing attributes set, i.e. all existing attributes in the rules of this policies
     */
    public Set<Attribute> extractExisitingAttributes() {
        final Set<Attribute> attributes = new HashSet<>();

        final List<RuleType> rules = getRules();
        for (final RuleType rule : rules) {
            attributes.addAll(new RuleAttributeExtractor(rule, this.category).getExisitingAttributes());
        }

        return attributes;
    }
    
    /**
     * Extracts the existing obligations set, i.e. all existing obligations in the rules of this policies.
     * 
     * @return the existing obligations set, i.e. all existing obligations in the rules of this policies
     */
    public Set<ObligationStructure> extractExisitingObligations() {
        final Set<ObligationStructure> obligations = new HashSet<>();

        final List<RuleType> rules = getRules();
        for (final RuleType rule : rules) {
            obligations.addAll(new RuleAttributeExtractor(rule, this.category).getExisitingObligations());
        }

        return obligations;
    }

    /**
     * Extracts the check code for the whole policy and the defined category.
     * 
     * @return the check code for the whole policy and the defined category
     */
    public StringBuilder extract() {
        final StringBuilder ret = new StringBuilder();

        final List<RuleType> rules = getRules();
        for (final RuleType rule : rules) {
            final RuleAttributeExtractor extractor = new RuleAttributeExtractor(rule, this.category);
            final StringBuilder extractionResult = extractor.getExtractionResult();
            if (extractionResult.length() > 0) {
                ret.append("(").append(extractionResult);
                if (this.category == Category.SUBJECT) {
                    // also extract environment attributes
                    final var shiftChecks = new RuleAttributeExtractor(rule, Category.ENVIRONMENT).getExtractionResult();
                    if (shiftChecks.length() > 0) {
                        ret.append(RuleAttributeExtractor.AND).append(shiftChecks);
                    }
                }
                ret.append(")").append(OR);
            }
        }

        if (ret.length() > 1) {
            // deleting last " || " and parenthesize
            ScalaHelper.parenthesize(ret.delete(ret.length() - OR.length(), ret.length()));
        }

        return ret;
    }

    /**
     * Gets the rules of this policy.
     * 
     * @return the rules of this policy
     */
    private List<RuleType> getRules() {
        if (!this.policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().stream()
                .allMatch(x -> x.getClass().equals(RuleType.class))) {
            final String error = "all elements of the policy must be rules!";
            SampleHandler.LOGGER.error(error);
            throw new IllegalStateException(error);
        }
        return this.policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().stream()
                .map(x -> (RuleType) x).collect(Collectors.toList());
    }
}
