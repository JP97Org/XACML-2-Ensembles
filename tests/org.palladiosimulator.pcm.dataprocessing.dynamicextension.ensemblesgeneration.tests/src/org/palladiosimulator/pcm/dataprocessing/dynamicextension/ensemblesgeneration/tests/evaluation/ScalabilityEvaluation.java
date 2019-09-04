package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.tests.evaluation;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    // base path is the org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.tests folder inside the tests folder in the XACML-2-Ensembles git
    private static final String RELATIVE_PATH_PREFIX = Paths.get("../../../../").toAbsolutePath().toString() + "/";;
    private static final String DIR_POLICYSETS = "out/";
    private static final String DIR_SCALA_OUTPUT = "out/scalaEval/"; 
    
    // printing needs at least 0.4GB free disk space
    private static final boolean IS_PRINTING = false;
    ////////////////////////////////////////////////////////////////////////////////////////////
    
    private static final String PATH_IN_DIR = RELATIVE_PATH_PREFIX + DIR_POLICYSETS;
    private static final String PATH_OUT_DIR = RELATIVE_PATH_PREFIX + DIR_SCALA_OUTPUT;
    
    private static final int ONE = 1;
    private static final int TEN = 10;
    private static final int HUNDRED = 100;
    private static final int THOUSAND = 1000;
    private static final int TEN_THOUSAND = 10000;
    private static final int HUNDRED_THOUSAND = 100000; 
    //private static final int FIVE_HUNDRED_THOUSAND = 500000; //is not tested due to exceeding of memory

    private static final String[] BASE_MODELS = { "UC-Scale", "UC-Test" };

    private static final int[] COPY_NUMS = { ONE, TEN, HUNDRED, THOUSAND, TEN_THOUSAND, HUNDRED_THOUSAND};

    private static final Map<Pair<String, Integer>, List<Long>> RESULTS_OR = new HashMap<>();
    private static final Map<Pair<String, Integer>, List<Long>> RESULTS_AND = new HashMap<>();
    private static final int NUM_WARM_UP = 1;
    private static final int NUM_REPETITIONS = 10;
    
    static {
        for (final String uc : BASE_MODELS) {
            for (final int copyNum : COPY_NUMS) {
                final var pair = new Pair<>(uc, copyNum);
                RESULTS_OR.put(pair, new ArrayList<>());
                if (!uc.equals("UC-Test")) {
                    RESULTS_AND.put(pair, new ArrayList<>());
                }
            }
        }
    }
    
    // TODO run with at least -Xmx8g
    public static void main(String[] args) {
        for (int i = 0; i < NUM_WARM_UP + NUM_REPETITIONS; i++) {
            final int index = i - NUM_WARM_UP;
            final boolean isWarmUp = index < 0;
            try {
                System.out.println("index= " + index + "\nOR\n---------------------------------------------------------------\n");
                test(true, isWarmUp);
                System.out.println("AND\n---------------------------------------------------------------\n");
                test(false, isWarmUp);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println("\n\n");
        for (final String uc : BASE_MODELS) {
            for (final int copyNum : COPY_NUMS) {
                final var pair = new Pair<>(uc, copyNum);
                final var orList = RESULTS_OR.get(pair);
                System.out.println(pair + " OR: average= " + orList.stream().mapToLong(x -> x).average() + " | " + orList);
                if (!uc.equals("UC-Test")) {
                    final var andList = RESULTS_AND.get(pair);
                    System.out.println(pair + " AND: average= " + andList.stream().mapToLong(x -> x).average() + " | " + andList);
                }
            }
        }
    }

    private static final void test(boolean isOr, final boolean isWarmUp) throws IOException {
        for (final String uc : BASE_MODELS) {
            System.out.println(uc + "\n");
            for (final int copyNum : COPY_NUMS) {
                System.out.println("context " + (isOr ? "||" : "&&") + " num. = " + copyNum);
                final long before = System.currentTimeMillis();
                final var policySet = loadPolicySet(uc, isOr, copyNum);
                final var code = new PolicySetHandler(policySet, null).getCode();
                final long after = System.currentTimeMillis();
                final long time = after - before;
                System.out.println("time consumed = " + time + "ms\n");
                print(uc, code, isOr, copyNum);
                if (!isWarmUp) {
                    if (isOr) {
                        RESULTS_OR.get(new Pair<>(uc, copyNum)).add(time);
                    } else {
                        RESULTS_AND.get(new Pair<>(uc, copyNum)).add(time);
                    }
                }
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
            for (final var block : toPrint) {
                writer.write(block.toString());
            }
            writer.close();
            System.out.println("printing done.");
        }
    }
}
