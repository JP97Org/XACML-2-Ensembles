package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionsType;

/**
 * Represents the structure which represents all contained text obligations of one rule.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class TextObligationsStructure implements ObligationsStructure {
    private final List<TextObligationStructure> obligations;
    
    /**
     * Creates a new text obligations structure with the given XACML obligations expression type
     * which must all be text obligations.
     * 
     * @param obligations - the XACML obligations expression type.
     */
    public TextObligationsStructure(final ObligationExpressionsType obligations) {
        // creating the obligation structure wrappers for the calls in subjects
        this.obligations = obligations != null ? obligations.getObligationExpression()
                .stream().map(x -> new TextObligationStructure(x, true)).collect(Collectors.toList()) : null;
        if (this.obligations != null) {
            // creating the extension method calls in resources (only the obligations which are at end)
            final List<TextObligationStructure> resourceObligations = obligations.getObligationExpression()
                    .stream()
                    .map(x -> new TextObligationStructure(x, false))
                    .filter(x -> x.isAtEnd())
                    .collect(Collectors.toList());
            this.obligations.addAll(resourceObligations);
        }
    }

    @Override
    public List<ObligationStructure> getObligations(final Category category) {
        if (this.obligations == null || category == Category.ENVIRONMENT) {
            // no obligations or environment category --> no obligations to be added
            return new ArrayList<>();
        } else if (category == Category.RESOURCE) {
            // return the obligations for the resource category
            return this.obligations.stream()
                    .filter(x -> !x.isCalledInSubjects()).collect(Collectors.toList());
        } else {
            // return the obligations for subject category
            return this.obligations.stream()
                    .filter(x -> x.isCalledInSubjects()).collect(Collectors.toList());
        }
    }
}
