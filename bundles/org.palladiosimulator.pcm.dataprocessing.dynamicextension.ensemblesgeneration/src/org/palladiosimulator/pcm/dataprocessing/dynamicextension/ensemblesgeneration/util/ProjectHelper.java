package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

/**
 * Helper Class for the project.
 * 
 * @author majuwa
 * @author Jonathan Schenkenberger
 * @version 1.01
 */
public class ProjectHelper {
    private static final String NAME_SBT = "build.sbt";
    /*
     * Scala for ensembles
     */
    private static final String SCALA_VERSION = "2.12.4";
    private static final String SCALA_NAME = "scala-reflect";
    private static final String SCALA_NAME_QUALIFIER = "org.scala-lang";
    /*
     * Solver for ensembles
     */
    private static final String SOLVER_VERSION = "4.0.0";
    private static final String SOLVER_NAME = "choco-solver";
    private static final String SOLVER_NAME_QUALIFIER = "org.choco-solver";
    /*
     * Map for ensembles
     */
    private static final String SCALA_MAP_VERSION = "1.0.0";
    private static final String SCALA_MAP_NAME = "scala-prioritymap";
    private static final String SCALA_MAP_QUALIFIER = "de.ummels";
    /*
     * Math for ensembles
     */
    private static final String APACHE_MATH_VERSION = "3.6.1";
    private static final String APACHE_MATH_NAME = "commons-math3";
    private static final String APACHE_MATH_QUALIFIER = "org.apache.commons";

    private static final String SEPARATOR = " % ";

    /**
     * Method to write build.sbt.
     * 
     * @param path - the path
     * @param projectName - the project name
     * @return whether writing was successful
     */
    public static boolean buildProjectStructure(String path, String projectName) {
        try (var writer = new PrintWriter(new File(path + File.pathSeparator + NAME_SBT), Charset.forName("UTF-8"))) {
            /*
             * write preamble
             */
            writer.println("name := " + wrapQuotes(projectName));
            writer.println("version := " + wrapQuotes("1.0"));
            writer.println("scalaVersion := " + wrapQuotes(SCALA_VERSION));
            writer.println("libraryDependencies ++= Seq(");
            /*
             * Write dependencies
             */
            writeComma(writer, wrapQuotes(SCALA_NAME_QUALIFIER) + SEPARATOR + wrapQuotes(SCALA_NAME) + SEPARATOR
                    + wrapQuotes(SCALA_VERSION));
            writeComma(writer, wrapQuotes(SOLVER_NAME_QUALIFIER) + SEPARATOR + wrapQuotes(SOLVER_NAME) + SEPARATOR
                    + wrapQuotes(SOLVER_VERSION));
            writeComma(writer, wrapQuotes(SCALA_MAP_QUALIFIER) + SEPARATOR + wrapQuotes(SCALA_MAP_NAME) + SEPARATOR
                    + wrapQuotes(SCALA_MAP_VERSION));
            writeComma(writer, wrapQuotes(APACHE_MATH_QUALIFIER) + SEPARATOR + wrapQuotes(APACHE_MATH_NAME) + SEPARATOR
                    + wrapQuotes(APACHE_MATH_VERSION));

            writer.println("}");

        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Wraps the given string with quotes.
     * 
     * @param s - the given string
     * @return the string wrapped with quotes
     */
    private static String wrapQuotes(String s) {
        return "\"" + s + "\"";
    }

    /**
     * Writes the given string and a comma.
     * 
     * @param writer - the writer
     * @param s - the given string
     * @throws IOException - if an IO exception occurs
     */
    private static void writeComma(PrintWriter writer, String s) throws IOException {
        writer.println(s + ",");
    }

}
