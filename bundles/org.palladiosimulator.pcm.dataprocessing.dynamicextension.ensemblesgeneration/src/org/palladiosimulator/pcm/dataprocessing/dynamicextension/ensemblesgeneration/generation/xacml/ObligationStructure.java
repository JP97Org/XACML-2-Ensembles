package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.Call;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaBlock;

/**
 * Represents a structure which represents one obligation.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface ObligationStructure extends Comparable<ObligationStructure> {
    
    /**
     * Gets the name of the obligation.
     * 
     * @return the name of the obligation
     */
    public String getName();
    
    /**
     * Gets the method call with the given call content.
     * 
     * @param callContent - the given call content
     * @return the method call with the given call content
     */
    public Call getMethodCall(final String callContent);
    
    /**
     * Determines whether this obligation is only called in subjects.
     * 
     * @return whether this obligation is only called in subjects
     */
    public boolean isOnlyCalledInSubjects();
    
    /**
     * Determines whether this obligation is to be fulfilled at the end of the checks.
     * 
     * @return whether this obligation is to be fulfilled at the end of the checks
     */
    public boolean isAtEnd();
    
    /**
     * Gets the method block of the obligation.
     * 
     * @return the method block of the obligation
     */
    public ScalaBlock getMethodBlock();
}
