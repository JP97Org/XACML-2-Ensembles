package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaClass;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ValueDeclaration;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.Call;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.MethodSignature;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaBlock;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ValueInitialisation;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml.Attribute;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml.AttributeExtractor;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml.Category;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml.ComponentCode;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.handlers.SampleHandler;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.util.ScalaHelper;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;

/**
 * Represents the code part which represents the XACML policy set, i.e. the whole ensemble system.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class PolicySetHandler implements CodePart {
    private static final String MODEL_CLASS_NAME = "RunningExample";
    private static final ValueDeclaration NOW = new ValueDeclaration(ScalaHelper.KEYWORD_NOW, ScalaHelper.KEYWORD_TIME);
    private static final String PACKAGE = "package scenarios";
    private static final StringBuilder IMPORTS = new StringBuilder("import tcof.{Component, _}\n")
            .append("import java.time._\n").append("import java.time.format._\n")
            .append("import java.util.Collection\n").append("import java.util.ArrayList\n");
    private static final String SYSTEM = "System";
    private static final String ROOT_ENSEMBLE_NAME = "rootEnsemble";
    private static final String RULE_SUFFIX = "Rule";

    private final PolicySetType policySet;
    private final String mainCode;

    /**
     * Creates a new policy set code part for the given policy set.
     * 
     * @param policySet
     *            - the given policy set
     * @param mainCode
     *            - the code contained in the main method or null (if standard code)
     */
    public PolicySetHandler(final PolicySetType policySet, final String mainCode) {
        this.policySet = policySet;
        this.mainCode = mainCode;
    }

    @Override
    public ScalaBlock getCode() {
        final ScalaBlock code = new ScalaBlock();

        final ScalaClass modelClass = new ScalaClass(false, MODEL_CLASS_NAME, ScalaHelper.KEYWORD_MODEL);
        modelClass.addAllAttributes(Arrays.asList(NOW));

        // package, imports and model class definition
        var preBlockCode = new StringBuilder(PACKAGE).append("\n").append(IMPORTS).append("\n")
                .append(modelClass.getCodeDefinition());
        code.appendPreBlockCode(preBlockCode);

        // components
        final Set<Attribute> existingAttributes = new HashSet<>();
        final List<PolicyType> policies = getPolicies();
        for (final PolicyType policy : policies) {
            for (final Category category : Category.values()) {
                existingAttributes.addAll(new AttributeExtractor(policy, category).extractExisitingAttributes());
            }
        }
        code.appendBlockCode(new ComponentCode(existingAttributes));

        // root ensemble
        final ScalaBlock rootEnsemble = new ScalaBlock();
        final ScalaClass systemClass = new ScalaClass(false, SYSTEM, ScalaHelper.KEYWORD_ENSEMBLE_ROOT);

        rootEnsemble.appendPreBlockCode(systemClass);

        // ensembles
        final List<String> rulesNames = new ArrayList<>();
        for (final PolicyType policy : policies) {
            final PolicyCodePart policyCodePart = new PolicyCodePart(policy);
            rulesNames.add(policyCodePart.getActionName());
            rootEnsemble.appendBlockCode(policyCodePart.getCode());
            rootEnsemble.appendBlockCode(new StringBuilder("\n"));
        }

        // rules
        rootEnsemble.appendBlockCode(rules(rulesNames));
        code.appendBlockCode(rootEnsemble);
        final String expression = new Call("root", "new " + SYSTEM).getCodeDefinition().toString();
        code.appendBlockCode(new ValueInitialisation(ROOT_ENSEMBLE_NAME, expression));

        // helper-method(s) and main
        code.setNext(getMain());

        return code;
    }

    private List<PolicyType> getPolicies() {
        final List<PolicyType> policies = new ArrayList<>();
        for (var jaxbObject : this.policySet.getPolicySetOrPolicyOrPolicySetIdReference()) {
            if (jaxbObject.getDeclaredType().equals(PolicyType.class)) {
                policies.add((PolicyType) (jaxbObject.getValue()));
            } else {
                final String error = "illegal type, only policies are supported!";
                SampleHandler.LOGGER.error(error);
                throw new IllegalStateException(error);
            }
        }
        return policies;
    }

    private StringBuilder rules(final List<String> rulesNames) {
        final StringBuilder ret = new StringBuilder();
        for (final String ruleName : rulesNames) {
            final String expression = new Call("rules", ruleName).getCodeDefinition().toString();
            final ValueInitialisation ruleValue = new ValueInitialisation(ruleName + RULE_SUFFIX, expression);
            ret.append(ruleValue.getCodeDefinition());
        }
        return ret.append("\n");
    }

    private ScalaBlock getMain() {
        final ScalaBlock ret = new ScalaBlock();

        ret.appendPreBlockCode(new StringBuilder("object RunningExample"));

        // helper function which converts iterable to collection
        ret.appendBlockCode(convert());
        ret.appendBlockCode(new StringBuilder("\n"));

        final ScalaBlock main = new ScalaBlock();
        final var signature = new MethodSignature("main", Arrays.asList(new ValueDeclaration("args", "Array[String]")),
                "Unit");
        main.appendPreBlockCode(signature);

        main.appendBlockCode(new StringBuilder(this.mainCode == null ? "//TODO: adapt to your usecase scenario\n"
                + "val scenario = new RunningExample(LocalTime.parse(\"13:00:00Z\", DateTimeFormatter.ISO_OFFSET_TIME))\n"
                + "val subjectA = new scenario.Subject(\"A\", \"Production_Hall_Section_1\", \"ASub\", \"Worker\", \"Shift 1\")\n"
                + "val subjectB = new scenario.Subject(\"B\", \"Shift 2\")\n"
                + "val resourceA = new scenario.Resource(\"machine\", \"INCIDENT_HAPPENED\", \"PUBLIC\", 5, 4)\n"
                + "scenario.components = List(subjectA, subjectB, resourceA)\n" + "scenario.rootEnsemble.init()\n"
                + "scenario.rootEnsemble.solve()\n"
                + "val testActionAllow = scenario.rootEnsemble.instance.testActionRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).contains(subjectA) && !convertToCol(x.allowedSubjects).contains(subjectB))\n"
                + "if(testActionAllow) {\n" + "println(\"allow\")\n" + "} else {\n" + "println(\"deny\")\n" + "}" : this.mainCode));

        ret.appendBlockCode(main);
        return ret;
    }

    private ScalaBlock convert() {
        final ScalaBlock ret = new ScalaBlock();
        final var signature = new MethodSignature("convertToCol",
                Arrays.asList(new ValueDeclaration("iterable", "Iterable[Component]")), "Collection[Component]");
        ret.appendPreBlockCode(signature);

        ret.appendBlockCode(new StringBuilder("val collection = new ArrayList[Component]\n" + "\n"
                + "val iter = iterable.iterator\n" + "while (iter.hasNext) {\n" + "collection.add(iter.next)\n" + "}\n"
                + "\n" + "return collection"));

        return ret;
    }
}
