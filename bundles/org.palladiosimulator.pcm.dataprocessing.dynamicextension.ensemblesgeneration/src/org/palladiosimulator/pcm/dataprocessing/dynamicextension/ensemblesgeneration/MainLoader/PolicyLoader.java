package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.MainLoader;

import java.nio.file.Path;

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
     */
    public PolicySetType loadPolicySet() {
        //TODO: EXC if file not exists or other scanner error
        return (PolicySetType) (new XACMLPolicyScanner(Path.of(this.pathPolicy), null).scan());
    }

}
