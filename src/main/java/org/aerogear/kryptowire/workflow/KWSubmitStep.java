package org.aerogear.kryptowire.workflow;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.TaskListener;
import jenkins.model.GlobalConfiguration;
import org.aerogear.kryptowire.GlobalConfigurationImpl;
import org.aerogear.kryptowire.KryptowireService;
import org.aerogear.kryptowire.KryptowireServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class KWSubmitStep extends AbstractStepImpl {

    private String platform;
    private String filePath;

    public String getPlatform() {
        return platform;
    }

    @DataBoundSetter
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getFilePath() {
        return filePath;
    }

    @DataBoundSetter
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @DataBoundConstructor
    public KWSubmitStep(@Nonnull String platform, @Nonnull String filePath) {
        this.platform = platform;
        this.filePath = filePath;
    }

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(KWSubmitExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "kwSubmit";
        }

        @Override
        public String getDisplayName() {
            return "Submit to Kryptowire";
        }

    }

    public static class KWSubmitExecution extends AbstractSynchronousNonBlockingStepExecution<Void> {

        private static final long serialVersionUID = 1L;

        @Inject
        transient KWSubmitStep step;

        @StepContextParameter
        transient TaskListener listener;

        @Override
        protected Void run() throws Exception {

            GlobalConfigurationImpl pluginConfig = GlobalConfiguration.all().get(GlobalConfigurationImpl.class);

            String kwEndpoint = pluginConfig.getKwEndpoint();
            String kwApiKey = pluginConfig.getKwApiKey();

            if(StringUtils.isEmpty(kwEndpoint) || StringUtils.isEmpty(kwApiKey)) {
                throw new RuntimeException("Kryptowire plugin configuration is not set!");
            }

            FilePath fp = getContext().get(FilePath.class).child(step.filePath);

            listener.getLogger().println(" --- Kryptowire submit Start ---");
            listener.getLogger().println("kwSubmit: " + step.platform + " : " + step.filePath);
            KryptowireService kws = new KryptowireServiceImpl(kwEndpoint,  kwApiKey);
            JSONObject resp = kws.submit(step.platform, fp);
            listener.getLogger().println("kw msg: " + resp.get("msg"));
            listener.getLogger().println("kw uuid: " + resp.get("uuid"));
            listener.getLogger().println("kw platform: " + resp.get("platform"));
            listener.getLogger().println("kw package: " + resp.get("package"));
            listener.getLogger().println("kw version: " + resp.get("version"));
            listener.getLogger().println("kw hash: " + resp.get("hash"));

            listener.getLogger().println(" --- Kryptowire submit Done ---");

            return null;
        }

    }
}
