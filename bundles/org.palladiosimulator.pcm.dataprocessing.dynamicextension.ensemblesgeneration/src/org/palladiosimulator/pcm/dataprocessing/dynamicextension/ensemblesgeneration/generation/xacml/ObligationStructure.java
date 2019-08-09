package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.Call;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaBlock;

public interface ObligationStructure extends Comparable<ObligationStructure> {
    public String getName();
    public Call getMethodCall(final String callContent);
    public boolean isOnlyCalledInSubjects();
    public boolean isAtEnd();
    public ScalaBlock getMethodBlock();
}
