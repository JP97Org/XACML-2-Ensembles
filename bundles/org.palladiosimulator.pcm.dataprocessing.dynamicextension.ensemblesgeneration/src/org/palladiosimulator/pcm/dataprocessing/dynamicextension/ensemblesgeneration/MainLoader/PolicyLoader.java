package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.MainLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.handlers.SampleHandler;

import com.att.research.xacml.util.XACMLPolicyScanner;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;

/**
 * Loads the policy set.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class PolicyLoader {
    private String pathPolicy;

    /**
     * Creates a new policy loader.
     * 
     * @param pathPolicy - the path to the policy set
     */
    public PolicyLoader(final String pathPolicy) {
        this.pathPolicy = pathPolicy;
    }

    /**
     * Loads the policy set.
     * 
     * @return the policy set
     * @throws IOException - if the file does not exist or the policy could not be read
     */
    public PolicySetType loadPolicySet() throws IOException {
        if (!new File(this.pathPolicy).exists()) {
            final var error = "file at path \"" + this.pathPolicy + "\" does not exist";
            SampleHandler.LOGGER.error(error);
            throw new FileNotFoundException(error);
        }
        
        final var scannedObject = (new XACMLPolicyScanner(Path.of(this.pathPolicy), null).scan());
        if (scannedObject == null || !(scannedObject instanceof PolicySetType)) {
            final var error = "file at path \"" + this.pathPolicy 
                    + "\" could not be read because it is syntactically broken or defines no policy set";
            SampleHandler.LOGGER.error(error);
            throw new IOException(error);
        }
        
        return (PolicySetType) scannedObject;
    }

}
