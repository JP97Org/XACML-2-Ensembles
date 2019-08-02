package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala;

/**
 * Represents a part of code with a code definition.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface ScalaCode {

    /**
     * Gets the code definition as a {@code StringBuilder}.
     * 
     * @return the code definition as a {@code StringBuilder}
     */
    StringBuilder getCodeDefinition();
}
