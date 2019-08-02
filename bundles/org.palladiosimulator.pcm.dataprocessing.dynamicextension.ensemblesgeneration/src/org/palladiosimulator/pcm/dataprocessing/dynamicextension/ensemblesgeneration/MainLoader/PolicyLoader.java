package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.MainLoader;

import java.nio.file.Path;

import com.att.research.xacml.util.XACMLPolicyScanner;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;

/**
 * Loads the policy.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class PolicyLoader {
    private String pathPolicy;

    public PolicyLoader(final String pathPolicy) {
        this.pathPolicy = pathPolicy;
    }

    public PolicySetType loadPolicySet() {
        return (PolicySetType) (new XACMLPolicyScanner(Path.of(this.pathPolicy), null).scan());
    }

}
