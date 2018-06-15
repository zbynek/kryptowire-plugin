package org.aerogear.kryptowire;

import hudson.FilePath;
import org.json.JSONObject;

import java.io.IOException;

public interface KryptowireService {

    JSONObject submit(String platform, FilePath filePath) throws IOException, InterruptedException;

}
