package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml;

import java.util.List;
import java.util.stream.Collectors;

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

	public StringBuilder extract() {
		final StringBuilder ret = new StringBuilder("(");

		// TODO: check if all objects in list are rules (must be the case for correct
		// generations of pcm-2-xacml)
		final List<RuleType> rules = this.policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().stream()
				.map(x -> (RuleType) x).collect(Collectors.toList());

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
}
