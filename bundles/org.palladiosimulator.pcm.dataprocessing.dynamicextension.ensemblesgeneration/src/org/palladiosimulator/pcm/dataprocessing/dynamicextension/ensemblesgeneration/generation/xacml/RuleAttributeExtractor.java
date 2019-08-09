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

/**
 * Represents a rule attribute extractor which can extract attributes and checking code.
 * 
 * @author Jonathan Schenkenberger
 * @verison 1.0
 */
public class RuleAttributeExtractor {
    private final RuleType rule;
    private final Category category;
    
    private StringBuilder extractionResult;
    private Set<Attribute> attributeSet;
    private Set<ObligationStructure> obligationsSet;

    /**
     * Creates a new rule attribute extractor for the given rule and the given category.
     * 
     * @param rule - the given rule
     * @param category - the given category
     */
    public RuleAttributeExtractor(final RuleType rule, final Category category) {
        this.rule = rule;
        this.category = category;
        this.extractionResult = null;
        this.attributeSet = null;
        this.obligationsSet = null;
    }

    /**
     * Gets the existing attributes in the defined rule and attribute category.
     * 
     * @return the existing attributes in the defined rule and attribute category
     */
    public Set<Attribute> getExisitingAttributes() {
        if (this.attributeSet == null) {
            extract();
        }
        return this.attributeSet;
    }
    
    /**
     * Gets the existing obligations in the defined rule and category.
     * 
     * @return the existing obligations in the defined rule and category.
     */
    public Set<ObligationStructure> getExisitingObligations() {
        if (this.obligationsSet == null) {
            extract();
        }
        return this.obligationsSet;
    }

    /**
     * Gets the extraction result (extracts the check code if this extractor has not yet extracted it).
     * 
     * @return the extraction result
     */
    public StringBuilder getExtractionResult() {
        if (this.extractionResult == null) {
            extract();
        }
        return this.extractionResult;
    }

    /**
     * Extracts the check code and sets the existing attribute set.
     */
    private void extract() {
        final Set<Attribute> attributeResult = new HashSet<>();
        
        // obligations sets / lists
        final Set<ObligationStructure> obligationResult = new HashSet<>(
                new TextObligationsStructure(this.rule.getObligationExpressions()).getObligations(this.category));
        // extract the start obligations
        final List<ObligationStructure> obligationsStart = obligationResult.stream()
                .filter(x -> !x.isAtEnd()).collect(Collectors.toList());
        // extract the end obligations 
        final List<ObligationStructure> obligationsEnd = obligationResult.stream()
                .filter(x -> x.isAtEnd()).collect(Collectors.toList());
                
        final StringBuilder result = new StringBuilder();
        final TargetType target = this.rule.getTarget();
        if (target.getAnyOf().size() > 0) {
            final var allOfs = target.getAnyOf().get(0).getAllOf();
            if (allOfs.isEmpty()) {
                // TODO EXC
            }
            result.append(calls(obligationsStart));
            
            final List<MatchType> matches = allOfs.get(0).getMatch();
            for (var attribute : Attribute.getCategoryAttributes(this.category)) {
                var tmp = getMatchesConcerningAttribute(matches, attribute);
                final boolean isEmpty = tmp.count() == 0;
                if (!isEmpty) {
                    attributeResult.add(attribute);
                    tmp = getMatchesConcerningAttribute(matches, attribute);
                    final var values = tmp.map(m -> (String) (m.getAttributeValue().getContent().get(0)));
                    for (final String value : values.collect(Collectors.toList())) {
                        result.append(attribute.getCheckCode(value));
                        result.append(" && ");
                    }
                }
            }
            result.append(calls(obligationsEnd));

            if (result.length() > 0) {
                // removing last " && " and parenthesizing
                ScalaHelper.parenthesize(result.delete(result.length() - 4, result.length()));
            }
        }

        this.extractionResult = result;
        this.attributeSet = attributeResult;
        this.obligationsSet = obligationResult;
    }

    private StringBuilder calls(final List<ObligationStructure> obligations) {
        //TODO evtl. noch besser machen mit call-content !
        
        final StringBuilder result = new StringBuilder();
        
        for (var obligation : obligations) {
            final String notAtEndStr = obligation.isOnlyCalledInSubjects() 
                    ? "\"" + obligation.getName() + "\"" : "";
            final String callContent = obligation.isAtEnd() ? AttributeExtractor.VAR_NAME : notAtEndStr;
            result.append(obligation.getMethodCall(callContent).getCodeDefinition());
            result.append(" && ");
        }
        
        return result;
    }

    /**
     * Gets the matches concerning the attribute.
     * 
     * @param matches - the matches
     * @param attribute - the attribute
     * @return the matches concerning the attribute.
     */
    private Stream<MatchType> getMatchesConcerningAttribute(final List<MatchType> matches,
            final Attribute attribute) {
        // gets only the matches concerning the attributeId and matchFunction
        return matches.stream()
                .filter(m -> m.getAttributeDesignator().getAttributeId().equals(attribute.getAttributeId()))
                .filter(m -> m.getMatchId().equals(attribute.getFunction().getMatchId()));
    }
}
