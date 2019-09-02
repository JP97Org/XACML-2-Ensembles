package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.handlers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.MainLoader.PolicyLoader;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.PolicySetHandler;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;

/**
 * The MainHandler is the main class for the plugin from which the generation is started.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class MainHandler extends AbstractHandler {
    private static final boolean IS_ECLIPSE_RUNNING = Platform.isRunning();

    public static final Logger LOGGER = IS_ECLIPSE_RUNNING ? PlatformUI.getWorkbench().getService(Logger.class)
            : new MockLogger();

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        LOGGER.info(IS_ECLIPSE_RUNNING ? "Using the eclipse logger" : "Using an eclipse logger mock-up");

        // loading xacml policy set
        final PolicyLoader loader = new PolicyLoader(PreferencesHandler.getPolicySetPath());
        PolicySetType policySet = null;
        String error = null;
        try {
            policySet = loader.loadPolicySet();
        } catch (IOException e1) {
            e1.printStackTrace();
            error = e1.getMessage();
        }
        final String path = PreferencesHandler.getOutputPath();
        if (error == null) {
            LOGGER.info("loaded " + policySet.getDescription());

            // generating code
            final PolicySetHandler handler = new PolicySetHandler(policySet, null);
            final String code = handler.getCode().toString();

            // writing code
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
