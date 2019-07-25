package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaClass;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaCode;
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
		
		final var actionEnsembleClass = new ScalaClass(true, this.actionName, ScalaHelper.KEYWORD_ENSEMBLE);
		ensembleCode.appendPreBlockCode(actionEnsembleClass.getClassDefinition());
		
		//TODO adding all component vals
		final var attributeExtractor = new AttributeExtractor("context:shift:name", "shiftName");
		final String mapping = ".map[Component](x => x.getClass().cast(x))";
		final String expression = "components" + ".select[Subject]."
				+ "filter(" + AttributeExtractor.VAR_NAME +  " => " + attributeExtractor.extract(this.policy) + ")"
				+ mapping;
		
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
