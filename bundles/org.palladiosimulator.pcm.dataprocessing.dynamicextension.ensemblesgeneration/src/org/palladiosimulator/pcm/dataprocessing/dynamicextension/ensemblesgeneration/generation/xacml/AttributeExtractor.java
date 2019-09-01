package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.handlers.MainHandler;
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
    private static final String OR = ScalaHelper.OR;

    private final PolicyType policy;
    private final Category category;

    /**
     * Creates a new attribute extractor for the given policy and the given category.
     * 
     * @param policy - the given policy
     * @param category - the given category
     */
    public AttributeExtractor(final PolicyType policy, final Category category) {
        this.policy = Objects.requireNonNull(policy);
        this.category = Objects.requireNonNull(category);
    }

    /**
     * Extracts the existing attributes set, i.e. all existing attributes in the rules of this policies.
     * 
     * @return the existing attributes set, i.e. all existing attributes in the rules of this policies
     */
    public Set<Attribute> extractAttributes() {
        final Set<Attribute> attributes = new HashSet<>();

        final List<RuleType> rules = getRules();
        for (final RuleType rule : rules) {
            attributes.addAll(new RuleAttributeExtractor(rule, this.category).getAttributes());
        }

        return attributes;
    }
    
    /**
     * Extracts the existing obligations set, i.e. all existing obligations in the rules of this policies.
     * 
     * @return the existing obligations set, i.e. all existing obligations in the rules of this policies
     */
    public Set<ObligationStructure> extractObligations() {
        final Set<ObligationStructure> obligations = new HashSet<>();

        final List<RuleType> rules = getRules();
        for (final RuleType rule : rules) {
            obligations.addAll(new RuleAttributeExtractor(rule, this.category).getObligations());
        }

        return obligations;
    }

    /**
     * Extracts the check code for the whole policy and the defined category.
     * 
     * @return the check code for the whole policy and the defined category
     */
    public StringBuilder extract() {
        final StringJoiner ret = new StringJoiner(OR);

        final List<RuleType> rules = getRules();
        for (final RuleType rule : rules) {
            final RuleAttributeExtractor extractor = new RuleAttributeExtractor(rule, this.category);
            final StringBuilder extractionResult = extractor.getExtractionResult();
            if (extractionResult.length() > 0) {
                ret.add(extractionResult);
            }
        }

        return ret.length() > 0 ? ScalaHelper.parenthesize(ret) : new StringBuilder();
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
            MainHandler.LOGGER.error(error);
            throw new IllegalStateException(error);
        }
        return this.policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().stream()
                .map(x -> (RuleType) x).collect(Collectors.toList());
    }
}
