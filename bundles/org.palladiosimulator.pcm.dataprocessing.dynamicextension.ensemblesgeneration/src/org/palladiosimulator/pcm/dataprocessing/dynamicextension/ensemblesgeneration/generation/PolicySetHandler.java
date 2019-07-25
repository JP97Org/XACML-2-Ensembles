package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaClass;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaCode;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ValueDeclaration;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ValueInitialisation;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.handlers.SampleHandler;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.util.ScalaHelper;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;

public class PolicySetHandler implements CodePart {
	private static final String MODEL_CLASS_NAME = "RunningExample";
	private static final String SUBJECT_CLASS_NAME = "Subject";
	private static final String RESOURCE_CLASS_NAME = "Resource";
	private static final String ENVIRONMENT_CLASS_NAME = "Environment";
	
	private final PolicySetType policySet;
	
	public PolicySetHandler(final PolicySetType policySet) {
		this.policySet = policySet;
	}
	
	public ScalaCode getCode() {
		//TODO constants
		
		final ScalaCode code = new ScalaCode();
		final ScalaClass modelClass = new ScalaClass(false, MODEL_CLASS_NAME, ScalaHelper.KEYWORD_MODEL);
		
		var preBlockCode = new StringBuilder("package scenarios\n")
				.append("import tcof.{Component, _}\n")
				.append(modelClass.getClassDefinition());
		code.appendPreBlockCode(preBlockCode);
		
		// components generation
		code.appendBlockCode(getSubjectComponentCode().append("\n"));
		code.appendBlockCode(getResourceComponentCode().append("\n"));
		code.appendBlockCode(getEnvironmentComponentCode().append("\n"));
		
		final ScalaCode rootEnsemble = new ScalaCode();
		final ScalaClass systemClass = new ScalaClass(false, "System", ScalaHelper.KEYWORD_ENSEMBLE_ROOT);
		
		//TODO adding all component information
		//TODO probably in main into components field
		//systemClass.addAllAttributes(Arrays.asList(new ValueDeclaration("subjects", "List[Subject]"))); 
		
		rootEnsemble.appendPreBlockCode(systemClass.getClassDefinition());
		final List<PolicyType> policies = getPolicies();
		final List<String> rulesNames = new ArrayList<>();
		for (final PolicyType policy : policies) {
			final PolicyCodePart policyCodePart = new PolicyCodePart(policy);
			rulesNames.add(policyCodePart.getActionName());
			rootEnsemble.appendBlockCode(policyCodePart.getCode().getCode());
			rootEnsemble.appendBlockCode(new StringBuilder("\n"));
		}
		rootEnsemble.appendBlockCode(rules(rulesNames));
		code.appendBlockCode(rootEnsemble.getCode());
		code.appendBlockCode(new ValueInitialisation("rootEnsemble", "root(new " + "System" + ")").getDefinition());
		
		code.setNext(getMain());
		
		return code;
	}

	private StringBuilder getSubjectComponentCode() {
		final var subjectClass = new ScalaClass(false, SUBJECT_CLASS_NAME, ScalaHelper.KEYWORD_COMPONENT);
		
		final List<ValueDeclaration> subjectAttributes = new ArrayList<>();
		subjectAttributes.add(new ValueDeclaration("subjectName", ScalaHelper.KEYWORD_STRING)); 
		//TODO: verallg. auf alle subject attr., bzw. dann auch auf alle attr. (in den anderen components)
		subjectAttributes.add(new ValueDeclaration("shiftName", ScalaHelper.KEYWORD_STRING)); 
		subjectClass.addAllAttributes(subjectAttributes);
		
		return subjectClass.getClassDefinition().append("\n");
	}
	
	private StringBuilder getResourceComponentCode() {
		final var resourceClass = new ScalaClass(false, RESOURCE_CLASS_NAME, ScalaHelper.KEYWORD_COMPONENT);
		// TODO Auto-generated method stub
		return resourceClass.getClassDefinition().append("\n");
	}
	
	private StringBuilder getEnvironmentComponentCode() {
		final var environmentClass = new ScalaClass(false, ENVIRONMENT_CLASS_NAME, ScalaHelper.KEYWORD_COMPONENT);
		// TODO Auto-generated method stub
		return environmentClass.getClassDefinition().append("\n");
	}

	private List<PolicyType> getPolicies() {
		final List<PolicyType> policies = new ArrayList<>();
		for (var jaxbObject : this.policySet.getPolicySetOrPolicyOrPolicySetIdReference()) {
			if (jaxbObject.getDeclaredType().equals(PolicyType.class)) {
				policies.add((PolicyType)(jaxbObject.getValue()));
			} else {
				final String error = "illegal type, only policies are supported!";
				SampleHandler.LOGGER.error(error);
				throw new IllegalStateException(error);
			}
		}
		return policies;
	}
	
	private StringBuilder rules(final List<String> rulesNames) {
		final StringBuilder ret = new StringBuilder("rules(");
		for (final String rulesName : rulesNames) {
			ret.append(rulesName).append(", ");
		}
		return ret.delete(ret.length() - 2,ret.length()).append(")\n");
	}
	
	private ScalaCode getMain() {
		final ScalaCode ret = new ScalaCode();
		
		ret.appendPreBlockCode(new StringBuilder("object RunningExample"));
		final ScalaCode main = new ScalaCode();
		main.appendPreBlockCode(new StringBuilder("def main(args: Array[String]): Unit ="));
		
		//TODO noch automatisiert generieren
		main.appendBlockCode(new StringBuilder("val scenario = new RunningExample\n" + 
				"scenario.rootEnsemble.init()\n" + 
				"val subjectA = new scenario.Subject(\"A\", \"Shift 1\")\n" + 
				"val subjectB = new scenario.Subject(\"B\", \"Shift 2\")\n" + 
				"scenario.components = List(subjectA, subjectB)\n" + 
				"scenario.rootEnsemble.solve()"));
		
		ret.appendBlockCode(main.getCode());
		return ret;
	}

}
