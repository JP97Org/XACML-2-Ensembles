package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.tests.evaluation;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.PolicySetHandler;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaBlock;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Paths;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.MainLoader.PolicyLoader;

import scenarios.RunningExample;

/**
 * Scenario testing structure for the XACML-2-Ensembles transformation. Tests a complete transformation.
 * This class is used for accuracy evaluation of the XACML-2-Ensembles transformation.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class AccuracyEvaluation {
    //TODO: accuracy evaluation for each usecase
    
    // SETTINGS ////////////////////////////////////////////////////////////////////////////////
    // base path is the org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.tests folder inside the tests folder in the XACML-2-Ensembles git
    private static final String RELATIVE_PATH_IN = Paths.get("../../../../out/").toAbsolutePath().toString() + "/";
    private static final String RELATIVE_PATH_OUT = Paths.get("../../../ensembleTester/src/main/scala/scenarios/").toAbsolutePath().toString() + "/"; 

    //TODO: adapt to test case
    private static final String FILENAME_POLICYSET = "UC3.xml"; // "UC2.xml"; // "UC1.xml"; // "UC0.xml"; //  "UC4.xml"; // "UC5.xml"; // "UC-Combined.xml"; // "UC-Running.xml"; //
    
    private static final String FILENAME_SCALA_OUTPUT = "out.scala";
    
    private static String getCode() {
        //TODO: adapt to test case, run once, refresh ScalaEnsembleTester, run again and check if result is as expected
        return EVAL_UC3_DENY; 
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    private static final String PATH_POLICYSET_INPUT = RELATIVE_PATH_IN + FILENAME_POLICYSET;
    private static final String PATH_SCALA_OUTPUT = RELATIVE_PATH_OUT + FILENAME_SCALA_OUTPUT;
    
    // CODES ///////////////////////////////////////////////////////////////////////////////////
    private static final String EVAL_UC0_ALLOW = "    val scenario = new RunningExample(LocalTime.parse(\"16:00:00Z\", DateTimeFormatter.ISO_OFFSET_TIME))\n" + 
            "    val subjectA = new scenario.Subject(\"R\", \"A\", \"Service Technician\", \"Maintenance Time\")\n" + 
            "    val subjectB = new scenario.Subject(\"W\", \"B\", \"Worker\", \"Maintenance Time\")\n" + 
            "    val accessSubject = subjectA\n" + 
            "    val resourceA = new scenario.Resource(\"machineService\", accessSubject, \"SERVICE_MODE\")\n" + 
            "    val resourceB = new scenario.Resource(\"machineOk\", accessSubject, \"OK\")\n" + 
            "    scenario.components = List(subjectA, subjectB, resourceA, resourceB)\n" + 
            "    scenario.rootEnsemble.init()\n" + 
            "    val solved = scenario.rootEnsemble.solve()\n" + 
            "    scenario.rootEnsemble.instance.telemaintainRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));\n" + 
            "    scenario.rootEnsemble.instance.telemaintainRule.selectedMembers.foreach(x => println(convertToCol(x.allowedResources)));\n" + 
            "    val ok = solved && scenario.rootEnsemble.instance.telemaintainRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).contains(subjectA) && convertToCol(x.allowedResources).contains(resourceA) && !convertToCol(x.allowedResources).contains(resourceB) && !convertToCol(x.allowedSubjects).contains(subjectB))\n" + 
            "    if (ok) {\n" + 
            "      println(\"OK\")\n" + 
            "    } else {\n" + 
            "      println(\"NOT OK\")\n" + 
            "    }"; // OK
    
    private static final String EVAL_UC0_DENY = "    val scenario = new RunningExample(LocalTime.parse(\"14:59:59Z\", DateTimeFormatter.ISO_OFFSET_TIME))\n" + 
            "    val subjectA = new scenario.Subject(\"R\", \"A\", \"Service Technician\", \"Maintenance Time\")\n" + 
            "    val subjectB = new scenario.Subject(\"W\", \"B\", \"Worker\", \"Maintenance Time\")\n" + 
            "    val accessSubject = subjectA\n" + 
            "    val resourceA = new scenario.Resource(\"machineService\", accessSubject, \"SERVICE_MODE\")\n" + 
            "    val resourceB = new scenario.Resource(\"machineOk\", accessSubject, \"OK\")\n" + 
            "    scenario.components = List(subjectA, subjectB, resourceA, resourceB)\n" + 
            "    scenario.rootEnsemble.init()\n" + 
            "    val solved = scenario.rootEnsemble.solve()\n" + 
            "    scenario.rootEnsemble.instance.telemaintainRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));\n" + 
            "    scenario.rootEnsemble.instance.telemaintainRule.selectedMembers.foreach(x => println(convertToCol(x.allowedResources)));\n" + 
            "    val ok = solved && scenario.rootEnsemble.instance.telemaintainRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).isEmpty() && convertToCol(x.allowedResources).isEmpty())\n" + 
            "    if (ok) {\n" + 
            "      println(\"OK\")\n" + 
            "    } else {\n" + 
            "      println(\"NOT OK\")\n" + 
            "    }"; // OK
    
    private static final String EVAL_UC1_ALLOW = "    val scenario = new RunningExample(LocalTime.parse(\"13:00:00Z\", DateTimeFormatter.ISO_OFFSET_TIME))\n" + 
            "    val subjectA = new scenario.Subject(\"A\", \"Production_Hall\", \"A\", \"Worker\", \"Early-Production\")\n" + 
            "    val subjectB = new scenario.Subject(\"B\", \"Production_Hall\", \"A\", \"Foreman\", \"Early-Production\")\n" + 
            "    scenario.components = List(subjectA, subjectB)\n" + 
            "    scenario.rootEnsemble.init()\n" + 
            "    val solved = scenario.rootEnsemble.solve()\n" + 
            "    scenario.rootEnsemble.instance.sendReportRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));\n" + 
            "    scenario.rootEnsemble.instance.receiveDataRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));\n" + 
            "    scenario.rootEnsemble.instance.showScheduleRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));\n" + 
            "    val ok = solved && scenario.rootEnsemble.instance.sendReportRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).contains(subjectA) && !convertToCol(x.allowedSubjects).contains(subjectB)) &&\n" + 
            "                       scenario.rootEnsemble.instance.receiveDataRule.selectedMembers.exists(x => !convertToCol(x.allowedSubjects).contains(subjectA) && convertToCol(x.allowedSubjects).contains(subjectB)) && \n" + 
            "                       scenario.rootEnsemble.instance.showScheduleRule.selectedMembers.exists(x => !convertToCol(x.allowedSubjects).contains(subjectA) && convertToCol(x.allowedSubjects).contains(subjectB));\n" + 
            "    if(ok) {\n" + 
            "      println(\"OK\")\n" + 
            "    } else {\n" + 
            "      println(\"NOT OK\")\n" + 
            "    }"; //OK
    
    private static final String EVAL_UC1_DENY = "    val scenario = new RunningExample(LocalTime.parse(\"13:00:00Z\", DateTimeFormatter.ISO_OFFSET_TIME))\n" + 
            "    val subjectA = new scenario.Subject(\"A\", \"Production_Hall B\", \"B\", \"Worker\", \"Early-Production\")\n" + 
            "    val subjectB = new scenario.Subject(\"B\", \"Production_Hall C\", \"A\", \"Worker\", \"Early-Production\")\n" + 
            "    scenario.components = List(subjectA, subjectB)\n" + 
            "    scenario.rootEnsemble.init()\n" + 
            "    val solved = scenario.rootEnsemble.solve()\n" + 
            "    scenario.rootEnsemble.instance.sendReportRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));\n" + 
            "    scenario.rootEnsemble.instance.receiveDataRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));\n" + 
            "    scenario.rootEnsemble.instance.showScheduleRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));\n" + 
            "    val ok = solved && scenario.rootEnsemble.instance.sendReportRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).isEmpty()) &&\n" + 
            "                       scenario.rootEnsemble.instance.receiveDataRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).isEmpty()) && \n" + 
            "                       scenario.rootEnsemble.instance.showScheduleRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).isEmpty());\n" + 
            "    if(ok) {\n" + 
            "      println(\"OK\")\n" + 
            "    } else {\n" + 
            "      println(\"NOT OK\")\n" + 
            "    }"; // OK
    
    private static final String EVAL_UC2_ALLOW = "    val scenario = new RunningExample(LocalTime.parse(\"13:00:00Z\", DateTimeFormatter.ISO_OFFSET_TIME))\n" + 
            "    val subjectW = new scenario.Subject(\"W\", \"Factory B\", \"B\", \"Worker\")\n" + 
            "    val subjectF = new scenario.Subject(\"F\", \"Factory B\", \"B\", \"Foreman\")\n" + 
            "    val subjectS = new scenario.Subject(\"S\", null, \"A\", \"Supplier\")\n" + 
            "    val accessSubject = subjectW\n" + 
            "    val resourceF = new scenario.Resource(\"Faulty Item\", accessSubject, \"FAULTY\");\n" + 
            "    val resourceO = new scenario.Resource(\"OK Item\", accessSubject, \"OK\");\n" + 
            "    scenario.components = List(subjectW, subjectF, subjectS, resourceF)\n" + 
            "    scenario.rootEnsemble.init()\n" + 
            "    val solved = scenario.rootEnsemble.solve()\n" + 
            "    scenario.rootEnsemble.instance.informForemanRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));\n" + 
            "    scenario.rootEnsemble.instance.informForemanRule.selectedMembers.foreach(x => println(convertToCol(x.allowedResources)));\n" + 
            "    scenario.rootEnsemble.instance.receiveInformationForemanRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));\n" + 
            "    scenario.rootEnsemble.instance.receiveInformationForemanRule.selectedMembers.foreach(x => println(convertToCol(x.allowedResources)));\n" + 
            "    scenario.rootEnsemble.instance.informSupplierRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));\n" + 
            "    scenario.rootEnsemble.instance.informSupplierRule.selectedMembers.foreach(x => println(convertToCol(x.allowedResources)));\n" + 
            "    scenario.rootEnsemble.instance.receiveInformationSupplierRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));\n" + 
            "    scenario.rootEnsemble.instance.receiveInformationSupplierRule.selectedMembers.foreach(x => println(convertToCol(x.allowedResources)));\n" + 
            "    val ok = solved && scenario.rootEnsemble.instance.informForemanRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).contains(subjectW) && !convertToCol(x.allowedSubjects).contains(subjectF) && !convertToCol(x.allowedSubjects).contains(subjectS) && convertToCol(x.allowedResources).contains(resourceF) && !convertToCol(x.allowedResources).contains(resourceO)) &&\n" + 
            "                       scenario.rootEnsemble.instance.receiveInformationForemanRule.selectedMembers.exists(x => !convertToCol(x.allowedSubjects).contains(subjectW) && convertToCol(x.allowedSubjects).contains(subjectF) && !convertToCol(x.allowedSubjects).contains(subjectS)) && \n" + 
            "                       scenario.rootEnsemble.instance.informSupplierRule.selectedMembers.exists(x => !convertToCol(x.allowedSubjects).contains(subjectW) && convertToCol(x.allowedSubjects).contains(subjectF) && !convertToCol(x.allowedSubjects).contains(subjectS)) &&\n" + 
            "                       scenario.rootEnsemble.instance.receiveInformationSupplierRule.selectedMembers.exists(x => !convertToCol(x.allowedSubjects).contains(subjectW) && !convertToCol(x.allowedSubjects).contains(subjectF) && convertToCol(x.allowedSubjects).contains(subjectS));\n" + 
            "    if(ok) {\n" + 
            "      println(\"OK\")\n" + 
            "    } else {\n" + 
            "      println(\"NOT OK\")\n" + 
            "    }"; // OK
    
    private static final String EVAL_UC2_DENY = "    val scenario = new RunningExample(LocalTime.parse(\"13:00:00Z\", DateTimeFormatter.ISO_OFFSET_TIME))\n" + 
            "    val subjectW = new scenario.Subject(\"W\", \"Factory B\", \"C\", \"Foreman\")\n" + 
            "    val subjectF = new scenario.Subject(\"F\", \"Factory B\", \"C\", \"Foreman\")\n" + 
            "    val subjectS = new scenario.Subject(\"S\", null, \"D\", \"Supplier\")\n" + 
            "    val accessSubject = subjectW\n" + 
            "    val resourceF = new scenario.Resource(\"OK Item 1\", accessSubject, \"OK\");\n" + 
            "    val resourceO = new scenario.Resource(\"OK Item 2\", accessSubject, \"OK\");\n" + 
            "    scenario.components = List(subjectW, subjectF, subjectS, resourceF)\n" + 
            "    scenario.rootEnsemble.init()\n" + 
            "    val solved = scenario.rootEnsemble.solve()\n" + 
            "    scenario.rootEnsemble.instance.informForemanRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));\n" + 
            "    scenario.rootEnsemble.instance.informForemanRule.selectedMembers.foreach(x => println(convertToCol(x.allowedResources)));\n" + 
            "    scenario.rootEnsemble.instance.receiveInformationForemanRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));\n" + 
            "    scenario.rootEnsemble.instance.receiveInformationForemanRule.selectedMembers.foreach(x => println(convertToCol(x.allowedResources)));\n" + 
            "    scenario.rootEnsemble.instance.informSupplierRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));\n" + 
            "    scenario.rootEnsemble.instance.informSupplierRule.selectedMembers.foreach(x => println(convertToCol(x.allowedResources)));\n" + 
            "    scenario.rootEnsemble.instance.receiveInformationSupplierRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));\n" + 
            "    scenario.rootEnsemble.instance.receiveInformationSupplierRule.selectedMembers.foreach(x => println(convertToCol(x.allowedResources)));\n" + 
            "    val ok = solved && scenario.rootEnsemble.instance.informForemanRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).isEmpty() && convertToCol(x.allowedResources).isEmpty()) &&\n" + 
            "                       scenario.rootEnsemble.instance.receiveInformationForemanRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).isEmpty() && convertToCol(x.allowedResources).isEmpty()) &&\n" + 
            "                       scenario.rootEnsemble.instance.informSupplierRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).isEmpty() && convertToCol(x.allowedResources).isEmpty()) &&\n" + 
            "                       scenario.rootEnsemble.instance.receiveInformationSupplierRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).isEmpty() && convertToCol(x.allowedResources).isEmpty());\n" + 
            "    if(ok) {\n" + 
            "      println(\"OK\")\n" + 
            "    } else {\n" + 
            "      println(\"NOT OK\")\n" + 
            "    }"; // OK
    
    private static final String EVAL_UC3_ALLOW = "    val scenario = new RunningExample(LocalTime.parse(\"13:00:00Z\", DateTimeFormatter.ISO_OFFSET_TIME))\n" + 
            "    val subjectAC = new scenario.Subject(\"AC\", \"A\", \"Company\")\n" + 
            "    val subjectBC = new scenario.Subject(\"BC\", \"B\", \"Company\")\n" + 
            "    val subjectB = new scenario.Subject(\"B\", \"Board\", \"Analyst\")\n" + 
            "    val subjectX = new scenario.Subject(\"X\", \"Y\", \"Z\")\n" + 
            "    val accessSubject = subjectB\n" + 
            "    val resourceA = new scenario.Resource(\"A\", accessSubject, \"UNDERPERFORMANCE\");\n" + 
            "    val resourceB = new scenario.Resource(\"B\", accessSubject, \"UNDERPERFORMANCE\");\n" + 
            "    val resourceC = new scenario.Resource(\"C\", accessSubject, \"OK\");\n" + 
            "    val resourceD = new scenario.Resource(\"D\", accessSubject, \"OK\");\n" + 
            "    scenario.components = List(subjectAC, subjectBC, subjectB, subjectX, resourceA, resourceB, resourceC, resourceD)\n" + 
            "    scenario.rootEnsemble.init()\n" + 
            "    val solved = scenario.rootEnsemble.solve()\n" + 
            "    scenario.rootEnsemble.instance.yieldAnalystRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));\n" + 
            "    scenario.rootEnsemble.instance.yieldAnalystRule.selectedMembers.foreach(x => println(convertToCol(x.allowedResources)));\n" + 
            "    scenario.rootEnsemble.instance.boardSendRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));\n" + 
            "    scenario.rootEnsemble.instance.boardSendRule.selectedMembers.foreach(x => println(convertToCol(x.allowedResources)));\n" + 
            "    scenario.rootEnsemble.instance.companyReceiveRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));\n" + 
            "    scenario.rootEnsemble.instance.companyReceiveRule.selectedMembers.foreach(x => println(convertToCol(x.allowedResources)));\n" + 
            "    val ok = solved && scenario.rootEnsemble.instance.yieldAnalystRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).contains(subjectB) && !convertToCol(x.allowedSubjects).contains(subjectX) && convertToCol(x.allowedResources).contains(resourceA) && convertToCol(x.allowedResources).contains(resourceB) && !convertToCol(x.allowedResources).contains(resourceC) && !convertToCol(x.allowedResources).contains(resourceD)) &&\n" + 
            "                       scenario.rootEnsemble.instance.boardSendRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).contains(subjectB) && !convertToCol(x.allowedSubjects).contains(subjectX) && convertToCol(x.allowedResources).contains(resourceA) && convertToCol(x.allowedResources).contains(resourceB) && !convertToCol(x.allowedResources).contains(resourceC) && !convertToCol(x.allowedResources).contains(resourceD)) &&\n" + 
            "                       scenario.rootEnsemble.instance.companyReceiveRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).contains(subjectAC) && convertToCol(x.allowedSubjects).contains(subjectBC) && convertToCol(x.allowedResources).isEmpty());" + 
            "    if(ok) {\n" + 
            "      println(\"OK\")\n" + 
            "    } else {\n" + 
            "      println(\"NOT OK\")\n" + 
            "    }"; // OK
    
    private static final String EVAL_UC3_DENY = "    val scenario = new RunningExample(LocalTime.parse(\"13:00:00Z\", DateTimeFormatter.ISO_OFFSET_TIME))\n" + 
            "    val subjectAC = new scenario.Subject(\"AC\", \"C\", \"Company\")\n" + 
            "    val subjectBC = new scenario.Subject(\"BC\", \"D\", \"Company\")\n" + 
            "    val subjectB = new scenario.Subject(\"B\", \"Board\", \"Analyst\")\n" + 
            "    val subjectX = new scenario.Subject(\"X\", \"Y\", \"Z\")\n" + 
            "    val accessSubject = subjectB\n" + 
            "    val resourceA = new scenario.Resource(\"A\", accessSubject, \"UNDERPERFORMANCE\");\n" + 
            "    val resourceB = new scenario.Resource(\"B\", accessSubject, \"UNDERPERFORMANCE\");\n" + 
            "    val resourceC = new scenario.Resource(\"C\", accessSubject, \"OK\");\n" + 
            "    val resourceD = new scenario.Resource(\"D\", accessSubject, \"OK\");\n" + 
            "    scenario.components = List(subjectAC, subjectBC, subjectB, subjectX, resourceA, resourceB, resourceC, resourceD)\n" + 
            "    scenario.rootEnsemble.init()\n" + 
            "    val solved = scenario.rootEnsemble.solve()\n" + 
            "    scenario.rootEnsemble.instance.yieldAnalystRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));\n" + 
            "    scenario.rootEnsemble.instance.yieldAnalystRule.selectedMembers.foreach(x => println(convertToCol(x.allowedResources)));\n" + 
            "    scenario.rootEnsemble.instance.boardSendRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));\n" + 
            "    scenario.rootEnsemble.instance.boardSendRule.selectedMembers.foreach(x => println(convertToCol(x.allowedResources)));\n" + 
            "    scenario.rootEnsemble.instance.companyReceiveRule.selectedMembers.foreach(x => println(convertToCol(x.allowedSubjects)));\n" + 
            "    scenario.rootEnsemble.instance.companyReceiveRule.selectedMembers.foreach(x => println(convertToCol(x.allowedResources)));\n" + 
            "    val ok = solved && scenario.rootEnsemble.instance.yieldAnalystRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).contains(subjectB) && !convertToCol(x.allowedSubjects).contains(subjectX) && convertToCol(x.allowedResources).contains(resourceA) && convertToCol(x.allowedResources).contains(resourceB) && !convertToCol(x.allowedResources).contains(resourceC) && !convertToCol(x.allowedResources).contains(resourceD)) &&\n" + 
            "                       scenario.rootEnsemble.instance.boardSendRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).contains(subjectB) && !convertToCol(x.allowedSubjects).contains(subjectX) && convertToCol(x.allowedResources).contains(resourceA) && convertToCol(x.allowedResources).contains(resourceB) && !convertToCol(x.allowedResources).contains(resourceC) && !convertToCol(x.allowedResources).contains(resourceD)) &&\n" + 
            "                       scenario.rootEnsemble.instance.companyReceiveRule.selectedMembers.exists(x => convertToCol(x.allowedSubjects).isEmpty() && convertToCol(x.allowedResources).isEmpty());    if(ok) {\n" + 
            "      println(\"OK\")\n" + 
            "    } else {\n" + 
            "      println(\"NOT OK\")\n" + 
            "    }"; // OK
    
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
    
    ////////////////////////////////////////////////////////////////////////////////////////////
    
	public static void main(String[] args) {
	    //TODO: see above in settings
	    runTest(PATH_POLICYSET_INPUT, getCode());
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
