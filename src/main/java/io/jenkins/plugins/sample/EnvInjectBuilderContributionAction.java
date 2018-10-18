package io.jenkins.plugins.sample;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.EnvironmentContributingAction;

import javax.annotation.CheckForNull;
import java.util.Map;

/**
 * Created by phy on 2018/10/17.
 */
public class EnvInjectBuilderContributionAction implements EnvironmentContributingAction {

    public static final String ENVINJECT_BUILDER_ACTION_NAME = "EnvInjectBuilderAction";
    @CheckForNull
    private transient Map<String, String> variables;

    public EnvInjectBuilderContributionAction(Map<String, String> variables) {
        this.variables = variables;
    }

    @Override
    public void buildEnvVars(AbstractBuild<?, ?> abstractBuild, EnvVars envVars) {

        if (envVars == null) {
            return;
        }

        if (variables == null) {
            return;
        }

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null && value != null) {
                envVars.put(key, value);
            }
        }
    }

    @CheckForNull
    @Override
    public String getIconFileName() {
        return null;
    }

    @CheckForNull
    @Override
    public String getDisplayName() {
        return ENVINJECT_BUILDER_ACTION_NAME;
    }

    @CheckForNull
    @Override
    public String getUrlName() {
        return null;
    }


}
