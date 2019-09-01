package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.handlers.MainHandler;
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
    private static final String AND = ScalaHelper.AND;

    private final RuleType rule;
    private final Category category;

    private StringBuilder extractionResult;
    private Set<Attribute> attributeSet;
    private Set<ObligationStructure> obligationsSet;

    private boolean isCalledInCombineCheck;

    /**
     * Creates a new rule attribute extractor for the given rule and the given category.
     * 
     * @param rule
     *            - the given rule
     * @param category
     *            - the given category
     */
    public RuleAttributeExtractor(final RuleType rule, final Category category) {
        this.rule = Objects.requireNonNull(rule);
        this.category = Objects.requireNonNull(category);
        this.extractionResult = null;
        this.attributeSet = null;
        this.obligationsSet = null;
        this.isCalledInCombineCheck = false;
    }

    /**
     * Gets the existing attributes in the defined rule and attribute category.
     * 
     * @return the existing attributes in the defined rule and attribute category
     */
    public Set<Attribute> getAttributes() {
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
    public Set<ObligationStructure> getObligations() {
        if (this.obligationsSet == null) {
            extract();
        }
        return this.obligationsSet;
    }

    /**
     * Gets the extraction result (extracts the check code (i.e. the code which is used to check the attribute values saved in the match
     * against the actual value in the subject's or resource's respective attribute) if this extractor has not yet extracted
     * it).
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
     * Extracts the match check code (i.e. the code which is used to check the attribute values saved in the match
     * against the actual value in the subject's or resource's respective attribute) 
     * and sets the existing attribute and obligation set.
     */
    private void extract() {
        this.attributeSet = new HashSet<>();

        // obligations sets / lists
        this.obligationsSet = new HashSet<>(
                new TextObligationsListStructure(this.rule.getObligationExpressions()).getObligations(this.category));
        // extract the start obligations
        final List<ObligationStructure> obligationsStart = this.obligationsSet.stream().filter(x -> !x.isAtEnd())
                .collect(Collectors.toList());
        // extract the end obligations
        final List<ObligationStructure> obligationsEnd = this.obligationsSet.stream().filter(x -> x.isAtEnd())
                .collect(Collectors.toList());

        extract(obligationsStart, obligationsEnd);
    }

    /**
     * Does the main extraction work.
     * 
     * @param obligationsStart
     *            - the obligations which should be added at the start
     * @param obligationsEnd
     *            - the obligations which should be added at the end
     */
    private void extract(final List<ObligationStructure> obligationsStart, final List<ObligationStructure> obligationsEnd) {
        final StringJoiner result = new StringJoiner(AND);
        final TargetType target = this.rule.getTarget();
        if (target.getAnyOf().size() > 0) {
            final var allOfs = target.getAnyOf().get(0).getAllOf();
            if (allOfs.isEmpty()) {
                final String error = "empty rule \"" + this.rule.getRuleId() + "\" (illegal input file format)";
                MainHandler.LOGGER.error(error);
                throw new IllegalStateException(error);
            }
            result.merge(createCombineCheck());
            result.merge(createCalls(obligationsStart));

            final List<MatchType> matches = allOfs.get(0).getMatch();
            for (var attribute : Attribute.getCategoryAttributes(this.category)) {
                var tmp = getMatchesConcerningAttribute(matches, attribute);
                final boolean isEmpty = tmp.count() == 0;
                if (!isEmpty) {
                    this.attributeSet.add(attribute);
                    tmp = getMatchesConcerningAttribute(matches, attribute);
                    final var values = tmp.map(m -> (String) (m.getAttributeValue().getContent().get(0)));
                    for (final String value : values.collect(Collectors.toList())) {
                        result.add(attribute.getCheckCode(value));
                    }
                }
            }
            
            if (this.category == Category.SUBJECT) {
                // also extract environment attributes
                final var shiftChecks = new RuleAttributeExtractor(this.rule, Category.ENVIRONMENT)
                        .getExtractionResult();
                if (shiftChecks.length() > 0) {
                    result.add(shiftChecks);
                }
            }
            result.merge(createCalls(obligationsEnd));            
        }
        this.extractionResult = result.length() > 0 ? ScalaHelper.parenthesize(result) : new StringBuilder();
    }

    /**
     * Creates the check which combines the rule's resource and subject checks, so that the XACML
     * rule is transformed correctly.
     * 
     * @return the check which combines the rule's resource and subject checks
     */
    private StringJoiner createCombineCheck() {
        final StringJoiner ret = new StringJoiner(AND);
        if (!this.isCalledInCombineCheck) {
            final String varRegex = ScalaHelper.VAR_NAME + "\\.";
            final String varReplacement;
            final RuleAttributeExtractor extractor;
            final StringBuilder extractionResult;
            
            switch (this.category) {
            case RESOURCE:
                final var accessSubject = ScalaHelper.VAR_NAME + "." + ScalaHelper.ACCESS_SUBJECT;
                varReplacement =  accessSubject + ".";
                final var nullCheck = accessSubject + " != null" + AND;
                extractor = new RuleAttributeExtractor(this.rule, Category.SUBJECT);
                extractor.isCalledInCombineCheck = true;
                extractionResult = extractor.getExtractionResult();
                if (extractionResult.length() > 0) {
                    ret.add(new StringBuilder(nullCheck)
                                   .append(replaceAll(extractionResult, varRegex, varReplacement)));
                }
                break;
            case SUBJECT:
                final String varNew = "y";
                varReplacement = varNew + ".";
                extractor = new RuleAttributeExtractor(this.rule, Category.RESOURCE);
                extractor.isCalledInCombineCheck = true;
                extractionResult = extractor.getExtractionResult();
                final String forall = ScalaHelper.RESOURCE_FIELD_NAME + ".forall(" + varNew + " => ";
                if (extractionResult.length() > 0) {
                    ret.add(new StringBuilder(forall)
                                    .append(replaceAll(extractionResult, varRegex, varReplacement))
                                    .append(")"));
                }
                break;
            default:
                break;
            }

        }
        return ret;
    }
    
    private static StringBuilder replaceAll(StringBuilder sb, String find, String replace){
        return new StringBuilder(Pattern.compile(find).matcher(sb).replaceAll(replace));
    }

    /**
     * Create the calls for all the given obligations. The calls are sorted by whether they are
     * prerequisites or not. Prerequisites are called earlier.
     * 
     * @param obligations
     *            - the given obligations
     * @return the calls for all the given obligations
     */
    private StringJoiner createCalls(final List<ObligationStructure> obligations) {
        final StringJoiner result = new StringJoiner(AND);
        if (!this.isCalledInCombineCheck) {
            final List<StringBuilder> obligationsListFirstPrerequisites = new ArrayList<>();

            for (var obligation : obligations) {
                final boolean isPrerequisite = obligation.isPrerequisite();
                final String notAtEndStr = isPrerequisite ? "\"" + obligation.getName() + "\"" : "";
                final String callContent = obligation.isAtEnd() ? ScalaHelper.VAR_NAME : notAtEndStr;
                if (isPrerequisite) {
                    obligationsListFirstPrerequisites.add(0, obligation.getMethodCall(callContent).getCodeDefinition());
                } else {
                    obligationsListFirstPrerequisites.add(obligation.getMethodCall(callContent).getCodeDefinition());
                }
            }

            for (var obligationStr : obligationsListFirstPrerequisites) {
                result.add(obligationStr);
            }
        }
        return result;
    }

    /**
     * Gets the matches concerning the attribute.
     * 
     * @param matches
     *            - the matches
     * @param attribute
     *            - the attribute
     * @return the matches concerning the attribute.
     */
    private Stream<MatchType> getMatchesConcerningAttribute(final List<MatchType> matches, final Attribute attribute) {
        // gets only the matches concerning the attributeId and matchFunction
        return matches.stream()
                .filter(m -> m.getAttributeDesignator().getAttributeId().equals(attribute.getAttributeId()))
                .filter(m -> m.getMatchId().equals(attribute.getFunction().getMatchId()));
    }
}
