package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaBlock;

/**
 * Represents a part of code with one or more code blocks (via next reference in {@code ScalaBlock}).
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface CodePart {

    /**
     * Gets the code block object which represents this code part.
     * 
     * @return the code block object which represents this code part
     */
    ScalaBlock getCode();
}
