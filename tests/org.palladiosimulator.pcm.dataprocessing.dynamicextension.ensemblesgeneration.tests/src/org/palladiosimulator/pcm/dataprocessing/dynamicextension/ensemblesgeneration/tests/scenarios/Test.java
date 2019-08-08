package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.tests.scenarios;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.PolicySetHandler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.MainLoader.PolicyLoader;

import scenarios.RunningExample;

public class Test {
    // SETTINGS ////////////////////////////////////////////////////////////////////////////////
    private static final String PATH_PREFIX = "/home/jojo/Schreibtisch/KIT/Bachelorarbeit/";
    private static final String DIR_POLICYSETS = "out/";
    private static final String DIR_SCALA_OUTPUT = "models/ensembleTester/src/main/scala/scenarios/"; 

    private static final String FILENAME_POLICYSET = "UC-Test.xml"; // "UC-Shift.xml"; //
    private static final String FILENAME_SCALA_OUTPUT = "out.scala";
    ////////////////////////////////////////////////////////////////////////////////////////////

    private static final String PATH_POLICYSET = PATH_PREFIX + DIR_POLICYSETS + FILENAME_POLICYSET;
    private static final String PATH_SCALA_OUTPUT = PATH_PREFIX + DIR_SCALA_OUTPUT + FILENAME_SCALA_OUTPUT;
    
    // CODES ///////////////////////////////////////////////////////////////////////////////////
    private static final String CODE_TEST_ALLOW = "//TODO: adapt to your usecase scenario\n"
            + "val scenario = new RunningExample(LocalTime.parse(\"13:00:00Z\", DateTimeFormatter.ISO_OFFSET_TIME))\n"
            + "val subjectA = new scenario.Subject(\"A\", \"Production_Hall_Section_1\", \"ASub\", \"Worker\", \"Shift 1\")\n"
            + "val subjectB = new scenario.Subject(\"B\", \"Shift 2\")\n"
            + "val resourceA = new scenario.Resource(\"machine\", \"INCIDENT_HAPPENED\", \"PUBLIC\", 5, 4)\n"
            + "scenario.components = List(subjectA, subjectB, resourceA)\n" + "scenario.rootEnsemble.init()\n"
            + "scenario.rootEnsemble.solve()\n"
            + "val testActionAllow = scenario.rootEnsemble.instance.testActionRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).contains(subjectA) && !convertToCol(x.allowedSubjects).contains(subjectB))\n"
            + "if(testActionAllow) {\n" + "println(\"allow\")\n" + "} else {\n" + "println(\"deny\")\n" + "}";
    
    private static final String CODE_TEST_DENY = "//TODO: adapt to your usecase scenario\n"
            + "val scenario = new RunningExample(LocalTime.parse(\"13:00:00Z\", DateTimeFormatter.ISO_OFFSET_TIME))\n"
            + "val subjectA = new scenario.Subject(\"A\", \"Production_Hall_Section_1\", \"ASub\", \"Blob\", \"Shift 1\")\n"
            + "val subjectB = new scenario.Subject(\"B\", \"Shift 2\")\n"
            + "val resourceA = new scenario.Resource(\"machine\", \"INCIDENT_HAPPENED\", \"PUBLIC\", 5, 4)\n"
            + "scenario.components = List(subjectA, subjectB, resourceA)\n" + "scenario.rootEnsemble.init()\n"
            + "scenario.rootEnsemble.solve()\n"
            + "val testActionAllow = scenario.rootEnsemble.instance.testActionRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).contains(subjectA) && !convertToCol(x.allowedSubjects).contains(subjectB))\n"
            + "if(testActionAllow) {\n" + "println(\"allow\")\n" + "} else {\n" + "println(\"deny\")\n" + "}";
    
    ////////////////////////////////////////////////////////////////////////////////////////////
    
	public static void main(String[] args) {
	    //TODO: adapt to test case, run once, refresh ScalaEnsembleTester, run again and check if result is as expected
	    final String codeMain = CODE_TEST_ALLOW; 
	    
	    final PolicyLoader loader = new PolicyLoader(PATH_POLICYSET);
	    final PolicySetHandler handler = new PolicySetHandler(loader.loadPolicySet(), codeMain);
	    final String code = handler.getCode().getCodeDefinition().toString();
	    
	    // writing code
        final String path = PATH_SCALA_OUTPUT;
        String error = null;
        try {
            final var writer = new PrintWriter(new File(path), Charset.forName("UTF-8"));
            writer.write(code);
            writer.close();
        } catch (IOException e) {
            error = e.getMessage();
            System.err.println(error);
        }
        
        // starting scala ensemble system
	    RunningExample.main(args);
	}
}
