package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation;

import java.util.Arrays;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaClass;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaCode;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ValueDeclaration;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ValueInitialisation;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml.AttributeExtractor;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.util.ScalaHelper;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;

public class PolicyCodePart implements CodePart {
	private static final String POLICY_PREFIX = "policy:";
	
	private final PolicyType policy;
	
	private final String actionName;
	
	public PolicyCodePart(final PolicyType policy) {
		this.policy = policy;
		this.actionName = this.policy.getPolicyId().replaceFirst(POLICY_PREFIX, "");
	}
	
	@Override
	public ScalaCode getCode() {
		//TODO: constants
		
		final ScalaCode ensembleCode = new ScalaCode();
		
		final var scalaClass = new ScalaClass(true, this.actionName, ScalaHelper.KEYWORD_ENSEMBLE);
		//TODO adding all component information
		scalaClass.addAllAttributes(Arrays.asList(new ValueDeclaration("subjects", "List[Subject]"))); 
		ensembleCode.appendPreBlockCode(scalaClass.getClassDefinition());
		
		//TODO adding all component vals
		final var attributeExtractor = new AttributeExtractor("context:shift:name", "shiftName");
		final String expression = "subjects" + "." 
				+ "filter(" + AttributeExtractor.VAR_NAME +  " => " + attributeExtractor.extract(this.policy) + ")";
		ensembleCode.appendBlockCode(new ValueInitialisation("allowedSubjects", expression).getDefinition());
		
		//TODO adding situation
		
		final String resourceName = "allowedSubjects"; //TODO replace with correct resources list (--> resources match)
		ensembleCode.appendBlockCode(allow("allowedSubjects", this.actionName, resourceName));
		
		return ensembleCode;
	}
	
	public String getActionName() {
		return this.actionName;
	}

	private StringBuilder allow(final String subjects, final String action, final String resourceName) {
		return new StringBuilder(ScalaHelper.KEYWORD_ALLOW)
				.append("(").append(subjects).append(", ")
				.append("\"").append(action).append("\", ")
				.append(resourceName).append(")\n");
	}
}
