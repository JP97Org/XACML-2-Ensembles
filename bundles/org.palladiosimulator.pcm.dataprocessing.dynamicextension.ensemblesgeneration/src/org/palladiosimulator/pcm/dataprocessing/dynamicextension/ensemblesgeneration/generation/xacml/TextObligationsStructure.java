package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionsType;

public class TextObligationsStructure implements ObligationsStructure {
    private final List<ObligationStructure> obligations;
    
    //TODO: evtl. noch mit registry machen (allg.)
    
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
                    .filter(x -> !x.isOnlyCalledInSubjects()).collect(Collectors.toList());
        } else {
            return new ArrayList<>(this.obligations);
        }
    }
}
