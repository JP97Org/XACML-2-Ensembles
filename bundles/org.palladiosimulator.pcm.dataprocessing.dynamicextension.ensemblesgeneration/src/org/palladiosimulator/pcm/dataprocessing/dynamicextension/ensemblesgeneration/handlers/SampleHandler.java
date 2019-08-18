package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.handlers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.MainLoader.PolicyLoader;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.PolicySetHandler;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;

/**
 * The SampleHandler is the main class for the plugin from which the generation is started.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class SampleHandler extends AbstractHandler {
    // SETTINGS ////////////////////////////////////////////////////////////////////////////////
    private static final String PATH_PREFIX = "/home/jojo/Schreibtisch/KIT/Bachelorarbeit/";
    private static final String DIR_POLICYSETS = "out/";
    private static final String DIR_SCALA_OUTPUT = "models/ensembleTester/src/main/scala/scenarios/"; 

    private static final String FILENAME_POLICYSET = "UC-Test.xml"; // "UC-Shift.xml"; //
    private static final String FILENAME_SCALA_OUTPUT = "out.scala";
    
    private static final boolean IS_ECLIPSE_LOGGING = false;
    ////////////////////////////////////////////////////////////////////////////////////////////

    private static final String PATH_POLICYSET = PATH_PREFIX + DIR_POLICYSETS + FILENAME_POLICYSET;
    private static final String PATH_SCALA_OUTPUT = PATH_PREFIX + DIR_SCALA_OUTPUT + FILENAME_SCALA_OUTPUT;

    public static final Logger LOGGER = IS_ECLIPSE_LOGGING ? PlatformUI.getWorkbench().getService(Logger.class)
            : new Logger() {
        @Override
        public boolean isErrorEnabled() {
            return false;
        }

        @Override
        public void error(Throwable t, String message) {
            error(message);
        }
        
        @Override
        public void error(String message) {
            System.err.println("ERROR | " + message);
        }

        @Override
        public boolean isWarnEnabled() {
            return false;
        }

        @Override
        public void warn(Throwable t, String message) {
            warn(message);
        }
        
        @Override
        public void warn(String message) {
            System.err.println("WARN | " + message);
        }

        @Override
        public boolean isInfoEnabled() {
            return false;
        }

        @Override
        public void info(Throwable t, String message) {
           info(message);
        }
        
        @Override
        public void info(String message) {
            System.err.println("INFO | " + message);
        }

        @Override
        public boolean isTraceEnabled() {
            return false;
        }

        @Override
        public void trace(Throwable t, String message) {
            trace(message);
        }

        @Override
        public boolean isDebugEnabled() {
            return false;
        }

        @Override
        public void debug(Throwable t) {
            debug(t.getMessage());
        }

        @Override
        public void debug(Throwable t, String message) {
            debug(message);
        }
};

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        LOGGER.info(IS_ECLIPSE_LOGGING ? "Using the eclipse logger" : "Using an eclipse logger mock-up");

        // loading xacml policy set
        final PolicyLoader loader = new PolicyLoader(PATH_POLICYSET);
        PolicySetType policySet = null;
        String error = null;
        try {
            policySet = loader.loadPolicySet();
        } catch (IOException e1) {
            e1.printStackTrace();
            error = e1.getMessage();
        }
        String path = null;
        if (error == null) {
            LOGGER.info("loaded " + policySet.getDescription());

            // generating code
            final PolicySetHandler handler = new PolicySetHandler(policySet, null);
            final String code = handler.getCode().toString();

            // writing code
            path = PATH_SCALA_OUTPUT;
       
            try {
                final var writer = new PrintWriter(new File(path), Charset.forName("UTF-8"));
                writer.write(code);
                writer.close();
            } catch (IOException e) {
                error = e.getMessage();
                LOGGER.error(error);
            }
        }

        // inform user
        final String result = error == null ? "ensembles sucessfully written to " + path : error;
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        MessageDialog.openInformation(window.getShell(), "ensemblesgeneration" + (error == null ? "" : " ERROR"),
                result);

        return null;
    }
}
