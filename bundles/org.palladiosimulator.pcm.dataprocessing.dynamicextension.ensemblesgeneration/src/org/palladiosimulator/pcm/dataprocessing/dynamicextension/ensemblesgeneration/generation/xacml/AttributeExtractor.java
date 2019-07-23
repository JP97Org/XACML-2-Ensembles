package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

//TODO besserer entwurf, mit enum oder konstanten attribute-extractors fuer die einzelnen attribute

public class AttributeExtractor {
	public static final String VAR_NAME = "x";

	private final String attributeId;
	private final String scalaAttributeName;

	public AttributeExtractor(final String attributeId, final String scalaAttributeName) {
		this.attributeId = attributeId;
		this.scalaAttributeName = scalaAttributeName;
	}

	public StringBuilder extract(final PolicyType policy) {
		final StringBuilder ret = new StringBuilder("(");

		// TODO: check if all objects in list are rules (must be the case for correct
		// generations of pcm-2-xacml)
		final List<RuleType> rules = policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().stream()
				.map(x -> (RuleType) x).collect(Collectors.toList());

		for (final RuleType rule : rules) {
			final TargetType target = rule.getTarget();
			if (target.getAnyOf().size() > 0) {
				final var allOfs = target.getAnyOf().get(0).getAllOf();
				if (allOfs.isEmpty()) {
					// TODO EXC
				}
				final List<MatchType> matches = allOfs.get(0).getMatch();

				var tmp = getMatchesConcerningAttributeId(matches);
				if (tmp.count() > 0) {
					tmp = getMatchesConcerningAttributeId(matches);
					ret.append("(");
					final var values = tmp.map(m -> (String) (m.getAttributeValue().getContent().get(0)));
					for (final String value : values.collect(Collectors.toList())) {
						// TODO check with correct check function (may be regex check or time check)
						final StringBuilder check = new StringBuilder("(")
								.append(VAR_NAME)
								.append(".")
								.append(this.scalaAttributeName)
								.append(" == ")
								.append("\"")
								.append(value)
								.append("\"")
								.append(")");
						ret.append(check).append(" && ");
					}
					ret.delete(ret.length() - 4, ret.length()).append(")").append(" || ");
				}
			}
		}

		if (ret.length() > 1) {
			ret.delete(ret.length() - 4, ret.length()).append(")");
		} else {
			ret.deleteCharAt(0);
		}

		return ret;
	}

	private Stream<MatchType> getMatchesConcerningAttributeId(final List<MatchType> matches) {
		// gets only the matches concerning the attributeId
		return matches.stream().filter(m -> m.getAttributeDesignator().getAttributeId().equals(this.attributeId));
	}
}
