package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.handlers.SampleHandler;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;

public class AttributeExtractor {
	public static final String VAR_NAME = "x";

	private final PolicyType policy;
	private final Category category;

	public AttributeExtractor(final PolicyType policy, final Category category) {
		this.policy = policy;
		this.category = category;
	}
	
	public Set<Attribute> extractExisitingAttributes() {
		final Set<Attribute> attributes = new HashSet<>();
		
		final List<RuleType> rules = getRules();
		for (final RuleType rule : rules) {
			for (final Category category : Category.values()) {
				attributes.addAll(new RuleAttributeExtractor(rule, category).extractExisitingAttributes());
			}
		}
		
		return attributes;
	}

	public StringBuilder extract() {
		final StringBuilder ret = new StringBuilder("(");

		final List<RuleType> rules = getRules();
		for (final RuleType rule : rules) {
			final StringBuilder extractionResult = new RuleAttributeExtractor(rule, this.category).extract();
			if (extractionResult.length() > 0) {
				ret.append(extractionResult);
				ret.append(" || ");
			}
		}

		if (ret.length() > 1) {
			// deleting last " || " and adding closing parenthesis for starting open parenthesis
			ret.delete(ret.length() - 4, ret.length()).append(")");
		} else {
			// deleting first open parenthesis
			ret.deleteCharAt(0);
		}

		return ret;
	}
	
	private List<RuleType> getRules() {
		if (!this.policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition()
				.stream().allMatch(x -> x.getClass().equals(RuleType.class))) {
			final String error = "all elements of the policy must be rules!";
			SampleHandler.LOGGER.error(error);
			throw new IllegalStateException(error);
		}
		return this.policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().stream()
				.map(x -> (RuleType) x).collect(Collectors.toList());
	}
}
