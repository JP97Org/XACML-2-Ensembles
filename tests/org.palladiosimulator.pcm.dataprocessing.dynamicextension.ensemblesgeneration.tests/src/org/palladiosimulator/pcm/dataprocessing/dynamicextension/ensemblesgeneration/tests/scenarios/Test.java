package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.tests.scenarios;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.PolicySetHandler;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaBlock;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.MainLoader.PolicyLoader;

import scenarios.RunningExample;

/**
 * Scenario testing structure for the XACML-2-Ensembles transformation. Tests a complete transformation.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class Test {
    // SETTINGS ////////////////////////////////////////////////////////////////////////////////
    private static final String PATH_PREFIX = "/home/jojo/Schreibtisch/KIT/Bachelorarbeit/";
    private static final String DIR_POLICYSETS = "out/";
    private static final String DIR_SCALA_OUTPUT = "models/ensembleTester/src/main/scala/scenarios/"; 

    //TODO: adapt to test case
    private static final String FILENAME_POLICYSET = "UC-Test.xml"; //"UC-Running.xml"; //  "UC-Empty.xml"; // "UC3.xml"; // "UC-Combined.xml"; // "UC-Shift.xml"; // 
    
    private static final String FILENAME_SCALA_OUTPUT = "out.scala";
    
    private static String getCode() {
        //TODO: adapt to test case, run once, refresh ScalaEnsembleTester, run again and check if result is as expected
        return CODE_TEST_ALLOW; 
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    private static final String PATH_POLICYSET = PATH_PREFIX + DIR_POLICYSETS + FILENAME_POLICYSET;
    private static final String PATH_SCALA_OUTPUT = PATH_PREFIX + DIR_SCALA_OUTPUT + FILENAME_SCALA_OUTPUT;
    
    // CODES ///////////////////////////////////////////////////////////////////////////////////
    private static final String CODE_RUNNING_ALLOW = "//TODO: adapt to your usecase scenario\n" + 
            "    val scenario = new RunningExample(LocalTime.parse(\"13:00:00Z\", DateTimeFormatter.ISO_OFFSET_TIME))\n" + 
            "    val subjectA = new scenario.Subject(\"W\", \"A\", \"Worker\")\n" + 
            "    val subjectA2 = new scenario.Subject(\"W2\", \"A\", \"Worker\")\n" + 
            "    val subjectB = new scenario.Subject(\"R\", \"B\", \"Repairperson\")\n" + 
            "    val accessSubject = subjectA\n" + 
            "    val resourceA = new scenario.Resource(\"machineOk\", accessSubject, \"OK\")\n" + 
            "    val resourceB = new scenario.Resource(\"machineDef\", accessSubject, \"DEFECTIVE\")\n" + 
            "    scenario.components = List(subjectA, subjectB, subjectA2, resourceA, resourceB)\n" + 
            "    scenario.rootEnsemble.init()\n" + 
            "    val solved = scenario.rootEnsemble.solve()\n" + 
            "    scenario.rootEnsemble.instance.readLogRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));\n" + 
            "    scenario.rootEnsemble.instance.readLogRule.selectedMembers.foreach(x => println(convertToCol(x.allowedResources)));\n" + 
            "    val testActionAllow = solved && scenario.rootEnsemble.instance.readLogRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).contains(subjectA) && convertToCol(x.allowedResources).contains(resourceA) && !convertToCol(x.allowedSubjects).contains(subjectB))\n" + 
            "    if (testActionAllow) {\n" + 
            "      println(\"allow\")\n" + 
            "    } else {\n" + 
            "      println(\"deny\")\n" + 
            "    }";
    
    private static final String CODE_RUNNING_DENY = "//TODO: adapt to your usecase scenario\n" + 
            "    val scenario = new RunningExample(LocalTime.parse(\"13:00:00Z\", DateTimeFormatter.ISO_OFFSET_TIME))\n" + 
            "    val subjectA = new scenario.Subject(\"W\", \"A\", \"Worker\")\n" + 
            "    val subjectA2 = new scenario.Subject(\"W2\", \"A\", \"Worker\")\n" + 
            "    val subjectB = new scenario.Subject(\"R\", \"B\", \"Repairperson\")\n" + 
            "    val accessSubject = subjectB\n" + 
            "    val resourceA = new scenario.Resource(\"machineOk\", accessSubject, \"OK\")\n" + 
            "    val resourceB = new scenario.Resource(\"machineDef\", accessSubject, \"DEFECTIVE\")\n" + 
            "    scenario.components = List(subjectA, subjectB, subjectA2, resourceA, resourceB)\n" + 
            "    scenario.rootEnsemble.init()\n" + 
            "    val solved = scenario.rootEnsemble.solve()\n" + 
            "    scenario.rootEnsemble.instance.readLogRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));\n" + 
            "    scenario.rootEnsemble.instance.readLogRule.selectedMembers.foreach(x => println(convertToCol(x.allowedResources)));\n" + 
            "    val testActionAllow = solved && scenario.rootEnsemble.instance.readLogRule.selectedMembers.exists(x => convertToCol(x.allowedResources).contains(resourceA))\n" + 
            "    if (testActionAllow) {\n" + 
            "      println(\"allow\")\n" + 
            "    } else {\n" + 
            "      println(\"deny\")\n" + 
            "    }";
    
    private static final String CODE_TEST_ALLOW = "//TODO: adapt to your usecase scenario\n"
            + "val scenario = new RunningExample(LocalTime.parse(\"13:00:00Z\", DateTimeFormatter.ISO_OFFSET_TIME))\n"
            + "val subjectA = new scenario.Subject(\"A\", \"Production_Hall_Section_1\", \"ASub\", \"Worker\", \"Shift 1\")\n"
            + "val subjectB = new scenario.Subject(\"B\", \"Shift 2\")\n"
            + "val resourceA = new scenario.Resource(\"machine\", subjectA, \"INCIDENT_HAPPENED\", \"PUBLIC\", 5, 4)\n"
            + "scenario.components = List(subjectA, subjectB, resourceA)\n" + "scenario.rootEnsemble.init()\n"
            + "val solved = scenario.rootEnsemble.solve()\n"
            + "val testActionAllow = solved && scenario.rootEnsemble.instance.testActionRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).contains(subjectA) && convertToCol(x.allowedResources).contains(resourceA) && !convertToCol(x.allowedSubjects).contains(subjectB))\n"
            + "if(testActionAllow) {\n" + "println(\"allow\")\n" + "} else {\n" + "println(\"deny\")\n" + "}";
    
    private static final String CODE_TEST_ALLOW_SHIFT1 = "//TODO: adapt to your usecase scenario\n" + 
            "val scenario = new RunningExample(LocalTime.parse(\"13:00:00Z\", DateTimeFormatter.ISO_OFFSET_TIME))\n" + 
            "val subjectA = new scenario.Subject(\"A\", \"Production_Hall_Section_1\", \"ASub\", \"Worker\", \"Shift 1\")\n" + 
            "val subjectB = new scenario.Subject(\"B\")\n" + 
            "val resourceA = new scenario.Resource(\"machine\", subjectA, \"INCIDENT_HAPPENED\", \"PUBLIC\", 5, 4)\n" + 
            "scenario.components = List(subjectA, subjectB, resourceA)\n" + 
            "scenario.rootEnsemble.init()\n" + 
            "val solved = scenario.rootEnsemble.solve()\n" + 
            "val testActionAllow = solved && scenario.rootEnsemble.instance.multipleShiftsActionRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).contains(subjectA) && convertToCol(x.allowedResources).contains(resourceA) && !convertToCol(x.allowedSubjects).contains(subjectB))\n" + 
            "if(testActionAllow) {\n" + 
            "println(\"allow\")\n" + 
            "} else {\n" + 
            "println(\"deny\")\n" + 
            "}";
    
    private static final String CODE_TEST_ALLOW_SHIFT2 = "//TODO: adapt to your usecase scenario\n" + 
            "val scenario = new RunningExample(LocalTime.parse(\"14:00:00Z\", DateTimeFormatter.ISO_OFFSET_TIME))\n" + 
            "val subjectA = new scenario.Subject(\"A\", \"Production_Hall_Section_1\", \"ASub\", \"Worker\", \"Shift 2\")\n" + 
            "val subjectB = new scenario.Subject(\"B\")\n" + 
            "val resourceA = new scenario.Resource(\"machine\", subjectA, \"INCIDENT_HAPPENED\", \"PUBLIC\", 5, 4)\n" + 
            "scenario.components = List(subjectA, subjectB, resourceA)\n" + 
            "scenario.rootEnsemble.init()\n" + 
            "val solved = scenario.rootEnsemble.solve()\n" + 
            "val testActionAllow = solved && scenario.rootEnsemble.instance.multipleShiftsActionRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).contains(subjectA) && convertToCol(x.allowedResources).contains(resourceA) && !convertToCol(x.allowedSubjects).contains(subjectB))\n" + 
            "if(testActionAllow) {\n" + 
            "println(\"allow\")\n" + 
            "} else {\n" + 
            "println(\"deny\")\n" + 
            "}";
    
    private static final String CODE_TEST_DENY_SHIFT2 = "//TODO: adapt to your usecase scenario\n" + 
            "val scenario = new RunningExample(LocalTime.parse(\"13:00:00Z\", DateTimeFormatter.ISO_OFFSET_TIME))\n" + 
            "val subjectA = new scenario.Subject(\"A\", \"Production_Hall_Section_1\", \"ASub\", \"Worker\", \"Shift 2\")\n" + 
            "val subjectB = new scenario.Subject(\"B\")\n" + 
            "val resourceA = new scenario.Resource(\"machine\", subjectA, \"INCIDENT_HAPPENED\", \"PUBLIC\", 5, 4)\n" + 
            "scenario.components = List(subjectA, subjectB, resourceA)\n" + 
            "scenario.rootEnsemble.init()\n" + 
            "val solved = scenario.rootEnsemble.solve()\n" + 
            "val testActionAllow = solved && scenario.rootEnsemble.instance.multipleShiftsActionRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).contains(subjectA) && convertToCol(x.allowedResources).contains(resourceA) && !convertToCol(x.allowedSubjects).contains(subjectB))\n" + 
            "if(testActionAllow) {\n" + 
            "println(\"allow\")\n" + 
            "} else {\n" + 
            "println(\"deny\")\n" + 
            "}";
    
    private static final String CODE_TEST_ALLOW_SHIFT_OVERLAP = "//TODO: adapt to your usecase scenario\n" + 
            "val scenario = new RunningExample(LocalTime.parse(\"14:00:00Z\", DateTimeFormatter.ISO_OFFSET_TIME))\n" + 
            "val subjectA = new scenario.Subject(\"A\", \"Production_Hall_Section_1\", \"ASub\", \"Worker\", \"Shift 1\")\n" + 
            "val subjectB = new scenario.Subject(\"B\", \"Production_Hall_Section_1\", \"ASub\", \"Worker\", \"Shift 2\")\n" + 
            "val resourceA = new scenario.Resource(\"machine\", subjectA, \"INCIDENT_HAPPENED\", \"PUBLIC\", 5, 4)\n" + 
            "scenario.components = List(subjectA, subjectB, resourceA)\n" + 
            "scenario.rootEnsemble.init()\n" + 
            "val solved = scenario.rootEnsemble.solve()\n" + 
            "val testActionAllow = solved && scenario.rootEnsemble.instance.multipleShiftsActionRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).contains(subjectA) && convertToCol(x.allowedResources).contains(resourceA) && convertToCol(x.allowedSubjects).contains(subjectB))\n" + 
            "if(testActionAllow) {\n" + 
            "println(\"allow\")\n" + 
            "} else {\n" + 
            "println(\"deny\")\n" + 
            "}";
    
    private static final String CODE_TEST_DENY = "//TODO: adapt to your usecase scenario\n"
            + "val scenario = new RunningExample(LocalTime.parse(\"13:00:00Z\", DateTimeFormatter.ISO_OFFSET_TIME))\n"
            + "val subjectA = new scenario.Subject(\"A\", \"Production_Hall_Section_1\", \"ASub\", \"Blob\", \"Shift 1\")\n"
            + "val subjectB = new scenario.Subject(\"B\", \"Shift 2\")\n"
            + "val resourceA = new scenario.Resource(\"machine\", subjectA, \"INCIDENT_HAPPENED\", \"PUBLIC\", 5, 4)\n"
            + "scenario.components = List(subjectA, subjectB, resourceA)\n" + "scenario.rootEnsemble.init()\n"
            + "val solved = scenario.rootEnsemble.solve()\n"
            + "val testActionAllow = solved && scenario.rootEnsemble.instance.testActionRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).contains(subjectA) && !convertToCol(x.allowedSubjects).contains(subjectB))\n"
            + "if(testActionAllow) {\n" + "println(\"allow\")\n" + "} else {\n" + "println(\"deny\")\n" + "}";
    
    private static final String CODE_SHIFT_ALLOW = "//TODO: adapt to your usecase scenario\n" + 
            "val scenario = new RunningExample(LocalTime.parse(\"14:00:00Z\", DateTimeFormatter.ISO_OFFSET_TIME))\n" + 
            "val subjectA = new scenario.Subject(\"A\", \"Production_Hall\", \"A\", \"Worker\", \"Early-Production\")\n" + 
            "val subjectB = new scenario.Subject(\"B\")\n" + 
            "val resourceA = new scenario.Resource(\"data\", subjectA);\n" + 
            "scenario.components = List(subjectA, subjectB, resourceA)\n" + 
            "scenario.rootEnsemble.init()\n" + 
            "val solved = scenario.rootEnsemble.solve()\n" + 
            "val testActionAllow = solved && scenario.rootEnsemble.instance.showPlanRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).contains(subjectA) && !convertToCol(x.allowedSubjects).contains(subjectB))\n" + 
            "if(testActionAllow) {\n" + 
            "println(\"allow\")\n" + 
            "} else {\n" + 
            "println(\"deny\")\n" + 
            "}";
    
    private static final String CODE_SHIFT_DENY = "//TODO: adapt to your usecase scenario\n" + 
            "val scenario = new RunningExample(LocalTime.parse(\"05:59:59Z\", DateTimeFormatter.ISO_OFFSET_TIME))\n" + 
            "val subjectA = new scenario.Subject(\"A\", \"Production_Hall\", \"A\", \"Worker\", \"Early-Production\")\n" + 
            "val subjectB = new scenario.Subject(\"B\")\n" + 
            "val resourceA = new scenario.Resource(\"data\", subjectA);\n" + 
            "scenario.components = List(subjectA, subjectB, resourceA)\n" + 
            "scenario.rootEnsemble.init()\n" + 
            "val solved = scenario.rootEnsemble.solve()\n" + 
            "val testActionAllow = solved && scenario.rootEnsemble.instance.showPlanRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).contains(subjectA) && !convertToCol(x.allowedSubjects).contains(subjectB))\n" + 
            "if(testActionAllow) {\n" + 
            "println(\"allow\")\n" + 
            "} else {\n" + 
            "println(\"deny\")\n" + 
            "}";
    
    private static final String CODE_COMBINED_ALLOW = "//TODO: adapt to your usecase scenario\n" + 
            "val scenario = new RunningExample(LocalTime.parse(\"13:00:00Z\", DateTimeFormatter.ISO_OFFSET_TIME))\n" + 
            "val subjectA = new scenario.Subject(\"A\", \"Production B\", \"A\", \"QAInspector\")\n" + 
            "val subjectB = new scenario.Subject(\"B\")\n" + 
            "val resourceA = new scenario.Resource(\"machine\", subjectA, \"INCIDENT_HAPPENED\");\n" + 
            "scenario.components = List(subjectA, subjectB, resourceA)\n" + 
            "scenario.rootEnsemble.init()\n" + 
            "val solved = scenario.rootEnsemble.solve()\n" + 
            "val testActionAllow = solved && scenario.rootEnsemble.instance.checkPhysicalAccessRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).contains(subjectA) && convertToCol(x.allowedResources).contains(resourceA)  && !convertToCol(x.allowedSubjects).contains(subjectB))\n" + 
            "if(testActionAllow) {\n" + 
            "println(\"allow\")\n" + 
            "} else {\n" + 
            "println(\"deny\")\n" + 
            "}";
    
    private static final String CODE_COMBINED_DENY = "//TODO: adapt to your usecase scenario\n" + 
            "val scenario = new RunningExample(LocalTime.parse(\"13:00:00Z\", DateTimeFormatter.ISO_OFFSET_TIME))\n" + 
            "val subjectA = new scenario.Subject(\"A\", \"Production B\", \"B\", \"QAInspector\")\n" + 
            "val subjectB = new scenario.Subject(\"B\")\n" + 
            "val resourceA = new scenario.Resource(\"machine\", subjectA, \"INCIDENT_HAPPENED\");\n" + 
            "scenario.components = List(subjectA, subjectB, resourceA)\n" + 
            "scenario.rootEnsemble.init()\n" + 
            "val solved = scenario.rootEnsemble.solve()\n" + 
            "val testActionAllow = solved && scenario.rootEnsemble.instance.checkPhysicalAccessRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).contains(subjectA) && convertToCol(x.allowedResources).contains(resourceA)  && !convertToCol(x.allowedSubjects).contains(subjectB))\n" + 
            "if(testActionAllow) {\n" + 
            "println(\"allow\")\n" + 
            "} else {\n" + 
            "println(\"deny\")\n" + 
            "}";
    
    private static final String CODE_3_ALLOW = "//TODO: adapt to your usecase scenario\n" + 
            "val scenario = new RunningExample(LocalTime.parse(\"13:00:00Z\", DateTimeFormatter.ISO_OFFSET_TIME))\n" + 
            "val subjectA = new scenario.Subject(\"A\")\n" + 
            "val subjectB = new scenario.Subject(\"B\")\n" + 
            "val resourceA = new scenario.Resource(\"machine\", subjectA, \"INCIDENT_HAPPENED\");\n" + 
            "scenario.components = List(subjectA, subjectB, resourceA)\n" + 
            "scenario.rootEnsemble.init()\n" + 
            "val solved = scenario.rootEnsemble.solve()\n" + 
            "val testActionAllow = solved && scenario.rootEnsemble.instance.sendRawDataP1Rule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).contains(subjectA) && convertToCol(x.allowedSubjects).contains(subjectB))\n" + 
            "if(testActionAllow) {\n" + 
            "println(\"allow\")\n" + 
            "} else {\n" + 
            "println(\"deny\")\n" + 
            "}";
    
    private static final String CODE_3_DENY = "//TODO: adapt to your usecase scenario\n" + 
            "val scenario = new RunningExample(LocalTime.parse(\"13:00:00Z\", DateTimeFormatter.ISO_OFFSET_TIME))\n" + 
            "val subjectA = new scenario.Subject(\"A\", \"Board\", \"Analyst\")\n" + 
            "val subjectB = new scenario.Subject(\"B\")\n" + 
            "val resourceA = new scenario.Resource(\"machine\", subjectA, \"OK\");\n" + 
            "scenario.components = List(subjectA, subjectB, resourceA)\n" + 
            "scenario.rootEnsemble.init()\n" + 
            "val solved = scenario.rootEnsemble.solve()\n" + 
            "val testActionAllow = solved && scenario.rootEnsemble.instance.aNameRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).contains(subjectA) && convertToCol(x.allowedResources).contains(resourceA))\n" + 
            "if(testActionAllow) {\n" + 
            "println(\"allow\")\n" + 
            "} else {\n" + 
            "println(\"deny\")\n" + 
            "}";
    
    private static final String CODE_EMPTY_ALLOW = "//TODO: adapt to your usecase scenario\n" + 
            "val scenario = new RunningExample(LocalTime.parse(\"14:00:00Z\", DateTimeFormatter.ISO_OFFSET_TIME))\n" + 
            "val subjectA = new scenario.Subject(\"A\")\n" + 
            "val subjectB = new scenario.Subject(\"B\")\n" + 
            "val resourceA = new scenario.Resource(\"data\", subjectA);\n" + 
            "scenario.components = List(subjectA, subjectB, resourceA)\n" + 
            "scenario.rootEnsemble.init()\n" + 
            "val solved = scenario.rootEnsemble.solve()\n" + 
            "val testActionAllow = solved\n" + 
            "if(testActionAllow) {\n" + 
            "println(\"allow\")\n" + 
            "} else {\n" + 
            "println(\"deny\")\n" + 
            "}";
    
    ////////////////////////////////////////////////////////////////////////////////////////////
    
	public static void main(String[] args) {
	    //TODO: see above in settings
	    runTest(PATH_POLICYSET, getCode());
	}
	
	public static void runTest(final String pathPolicySet, final String codeMain) {
        final PolicyLoader loader = new PolicyLoader(pathPolicySet);
        PolicySetHandler handler = null;
        try {
            handler = new PolicySetHandler(loader.loadPolicySet(), codeMain);
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }
        final ScalaBlock scalaBlock = handler.getCode();
        
        // writing code
        final String path = PATH_SCALA_OUTPUT;
        String error = null;
        try {
            final var writer = new PrintWriter(new File(path), Charset.forName("UTF-8"));
            for (final StringBuilder code : scalaBlock) {
                writer.write(code.toString());
            }
            writer.close();
        } catch (IOException e) {
            error = e.getMessage();
            System.err.println(error);
        }
        
        // starting scala ensemble system
        RunningExample.main(new String[]{});
	}
}
