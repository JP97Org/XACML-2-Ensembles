package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.tests.evaluation;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.MainLoader.PolicyLoader;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.PolicySetHandler;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaBlock;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;

/**
 * Class for evaluating scalability of the XACML-2-Ensembles transformation.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class ScalabilityEvaluation {
    // SETTINGS ////////////////////////////////////////////////////////////////////////////////
    private static final String PATH_PREFIX = "/home/jojo/Schreibtisch/KIT/Bachelorarbeit/";
    private static final String DIR_POLICYSETS = "out/";
    private static final String DIR_SCALA_OUTPUT = "out/scalaEval/"; 
    
    // printing needs at least 0.4GB free disk space
    private static final boolean IS_PRINTING = false;
    ////////////////////////////////////////////////////////////////////////////////////////////
    
    private static final String PATH_IN_DIR = PATH_PREFIX + DIR_POLICYSETS;
    private static final String PATH_OUT_DIR = PATH_PREFIX + DIR_SCALA_OUTPUT;
    
    private static final int ONE = 1;
    private static final int TEN = 10;
    private static final int HUNDRED = 100;
    private static final int THOUSAND = 1000;
    private static final int TEN_THOUSAND = 10000;
    private static final int HUNDRED_THOUSAND = 100000; 
    //private static final int FIVE_HUNDRED_THOUSAND = 500000; //is not tested due to exceeding of memory

    private static final String[] BASE_MODELS = { "UC-Scale", "UC-Test" };

    private static final int[] COPY_NUMS = { ONE, TEN, HUNDRED, THOUSAND, TEN_THOUSAND, HUNDRED_THOUSAND};

    // TODO run with at least -Xmx8g
    public static void main(String[] args) {
        try {
            System.out.println("OR\n---------------------------------------------------------------\n");
            test(true);
            System.out.println("AND\n---------------------------------------------------------------\n");
            test(false);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static final void test(boolean isOr) throws IOException {
        for (final String uc : BASE_MODELS) {
            System.out.println(uc + "\n");
            for (final int copyNum : COPY_NUMS) {
                System.out.println("context " + (isOr ? "||" : "&&") + " num. = " + copyNum);
                final long before = System.currentTimeMillis();
                final var policySet = loadPolicySet(uc, isOr, copyNum);
                final var code = new PolicySetHandler(policySet, null).getCode();
                final long after = System.currentTimeMillis();
                System.out.println("time consumed = " + (after - before) + "ms\n");
                print(uc, code, isOr, copyNum);
            }
            System.out.println("---------------------------------------------------------------\n");
            if (!isOr) {
                // TODO change to continue and other condition in if, if other base-models are
                // checked
                // break in AND test because tests resulting from UC-Test and UC-Scale are
                // equivalent
                break;
            }
        }
    }
    
    private static final PolicySetType loadPolicySet(final String name, final boolean isOr, final int copyNum) 
            throws IOException {
        final String filename = PATH_IN_DIR + name + (isOr ? "OR" : "AND") + copyNum + ".xml";
        return new PolicyLoader(filename).loadPolicySet();
    }

    private static void print(final String name, final ScalaBlock toPrint, final boolean isOr, final int copyNum) 
            throws IOException {
        if (IS_PRINTING) {
            final String path = PATH_OUT_DIR + name + (isOr ? "OR" : "AND") + copyNum + ".scala";
            System.out.println("printing to " + path + " ...");
            final var writer = new PrintWriter(new File(path), Charset.forName("UTF-8"));
            writer.write(toPrint.getCodeDefinition().toString());
            writer.close();
            System.out.println("printing done.");
        }
    }
}
