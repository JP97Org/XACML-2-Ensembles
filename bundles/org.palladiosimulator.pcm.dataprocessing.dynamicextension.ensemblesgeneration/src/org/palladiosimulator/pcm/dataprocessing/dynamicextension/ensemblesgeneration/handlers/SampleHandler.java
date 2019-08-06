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
    ////////////////////////////////////////////////////////////////////////////////////////////

    private static final String PATH_POLICYSET = PATH_PREFIX + DIR_POLICYSETS + FILENAME_POLICYSET;
    private static final String PATH_SCALA_OUTPUT = PATH_PREFIX + DIR_SCALA_OUTPUT + FILENAME_SCALA_OUTPUT;

    public static final Logger LOGGER = PlatformUI.getWorkbench().getService(Logger.class);

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        LOGGER.info("Using the eclipse logger");

        // loading xacml policy set
        final PolicyLoader loader = new PolicyLoader(PATH_POLICYSET);
        final PolicySetType policySet = loader.loadPolicySet();
        LOGGER.info("loading " + policySet.getDescription());

        // generating code
        final PolicySetHandler handler = new PolicySetHandler(policySet);
        final String code = handler.getCode().toString();

        // writing code
        final String path = PATH_SCALA_OUTPUT;
        String error = null;
        try {
            final var writer = new PrintWriter(new File(path), Charset.forName("UTF-8"));
            writer.write(code);
            writer.close();
        } catch (IOException e) {
            error = e.getMessage();
            LOGGER.error(error);
        }

        // inform user
        final String result = error == null ? "ensembles sucessfully written to " + path : error;
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        MessageDialog.openInformation(window.getShell(), "ensemblesgeneration" + (error == null ? "" : " ERROR"),
                result);

        return null;
    }
}
