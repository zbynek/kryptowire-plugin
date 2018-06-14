package org.aerogear.kryptowire;

import hudson.model.*;
import jenkins.model.GlobalConfiguration;
import jenkins.model.RunAction2;
import org.json.JSONObject;
import java.io.IOException;


public class BinaryHistoryAction implements RunAction2 {
    private BinaryInfo info;
    private transient Run run;
    private BinaryStatus status;
    private transient GlobalConfigurationImpl cfg;


    public BinaryHistoryAction(BinaryInfo info) {
        this.info = info;
    }

    @Override
    public void onAttached(Run<?, ?> run) {
        this.run = run;
    }

    @Override
    public void onLoad(Run<?, ?> run) {
        this.run = run;
    }

    @Override
    public String getIconFileName() {
        return "/plugin/aerogear-kryptowire-plugin/menu-logo.png";
    }

    @Override
    public String getDisplayName() {
        return "Kryptowire Scan Results";
    }

    @Override
    public String getUrlName() {
        return "kryptowire";
    }

    public BinaryInfo getInfo() {
        return info;
    }

    public void setInfo(BinaryInfo info) {
        this.info = info;
    }

    public Run getRun() {
        return run;
    }

    public void setRun(Run run) {
        this.run = run;
    }


    public void setStatus(BinaryStatus status) {
        this.status = status;
    }

    public BinaryStatus getStatus() throws IOException, InterruptedException {
        if (this.status != null) {
            return this.status;
        }
        GlobalConfigurationImpl cfg = this.getCfg();
        KryptowireService kws = new KryptowireServiceImpl(cfg.getKwEndpoint(),  cfg.getKwApiKey());
        if (!kws.isCompleted(this.info.getHash())) {
            return BinaryStatus.notReady();
        }
        JSONObject out = kws.getResult(this.info.getUuid());
        if (out.isNull("threat_score")) {
            return BinaryStatus.notReady();
        }
        this.status = BinaryStatus.fromJSONObject(out);
        this.getRun().save();
        return this.status;
    }

    public String getExternalLink() {
        GlobalConfigurationImpl cfg = this.getCfg();
        return cfg.getKwEndpoint() + "/#/" + info.getPlatform() + "-report/" + info.getUuid();
    }

    public GlobalConfigurationImpl getCfg() {
         if (this.cfg == null) {
             this.cfg = GlobalConfiguration.all().get(GlobalConfigurationImpl.class);
         }
         return this.cfg;
    }
}
