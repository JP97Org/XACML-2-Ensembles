package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml;

import java.util.List;

/**
 * Represents a structure which represents all contained obligations of one rule.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface ObligationsListStructure {
    
    /**
     * Gets a list of obligation structures concerning the given category.
     * 
     * @param category - the given category
     * @return a list of obligation structures concerning the given category
     */
    public List<ObligationStructure> getObligations(Category category);
}
