package org.aerogear.kryptowire;

import hudson.FilePath;
import org.json.JSONObject;

import java.io.IOException;

public interface KryptowireService {
    enum AnalysisStatus {
        COMPLETE,
        PROCESSING,
        NOT_SUBMITTED
    }

    JSONObject submit(String platform, FilePath filePath) throws IOException, InterruptedException;
    JSONObject getResult(String uuid) throws IOException, InterruptedException;
    AnalysisStatus getStatus(String hash) throws  IOException, InterruptedException;
    boolean isCompleted(String hash) throws IOException, InterruptedException;
}
