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
    private final List<ObligationStructure> obligations;
    
    /**
     * Creates a new text obligations structure with the given XACML obligations expression type
     * which must all be text obligations.
     * 
     * @param obligations - the XACML obligations expression type.
     */
    public TextObligationsStructure(final ObligationExpressionsType obligations) {
        // creating the obligation structure wrappers
        this.obligations = obligations != null ? obligations.getObligationExpression()
                .stream().map(x -> new TextObligationStructure(x)).collect(Collectors.toList()) : null;
    }

    @Override
    public List<ObligationStructure> getObligations(final Category category) {
        if (this.obligations == null || category == Category.ENVIRONMENT) {
            return new ArrayList<>();
        } else if (category != Category.SUBJECT) {
            return this.obligations.stream()
                    .filter(x -> !x.isPrerequisite()).collect(Collectors.toList());
        } else {
            return new ArrayList<>(this.obligations);
        }
    }
}
