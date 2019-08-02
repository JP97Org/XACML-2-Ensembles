package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.util.ScalaHelper;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

public class RuleAttributeExtractor {
    private final RuleType rule;
    private final Category category;
    
    private StringBuilder extractionResult;
    private Set<Attribute> attributeSet;

    public RuleAttributeExtractor(final RuleType rule, final Category category) {
        this.rule = rule;
        this.category = category;
        this.extractionResult = null;
        this.attributeSet = null;
    }

    public Set<Attribute> getExisitingAttributes() {
        if (this.attributeSet == null) {
            extract();
        }
        return this.attributeSet;
    }

    public StringBuilder getExtractionResult() {
        if (this.extractionResult == null) {
            extract();
        }
        return this.extractionResult;
    }

    private void extract() {
        final Set<Attribute> setRet = new HashSet<Attribute>();
        final StringBuilder ret = new StringBuilder();
        final TargetType target = this.rule.getTarget();
        if (target.getAnyOf().size() > 0) {
            final var allOfs = target.getAnyOf().get(0).getAllOf();
            if (allOfs.isEmpty()) {
                // TODO EXC
            }
            final List<MatchType> matches = allOfs.get(0).getMatch();
            for (var attribute : Attribute.getCategoryAttributes(this.category)) {
                var tmp = getMatchesConcerningAttributeId(matches, attribute);
                final boolean isEmpty = tmp.count() == 0;
                if (!isEmpty) {
                    setRet.add(attribute);
                    tmp = getMatchesConcerningAttributeId(matches, attribute);
                    final var values = tmp.map(m -> (String) (m.getAttributeValue().getContent().get(0)));
                    for (final String value : values.collect(Collectors.toList())) {
                        ret.append(attribute.getCheckCode(value));
                        ret.append(" && ");
                    }
                }
            }

            if (ret.length() > 0) {
                // removing last " && " and parenthesizing
                ScalaHelper.parenthesize(ret.delete(ret.length() - 4, ret.length()));
            }
        }

        this.extractionResult = ret;
        this.attributeSet = setRet;
    }

    private Stream<MatchType> getMatchesConcerningAttributeId(final List<MatchType> matches,
            final Attribute attribute) {
        // gets only the matches concerning the attributeId and matchFunction
        return matches.stream()
                .filter(m -> m.getAttributeDesignator().getAttributeId().equals(attribute.getAttributeId()))
                .filter(m -> m.getMatchId().equals(attribute.getFunction().getMatchId()));
    }
}
