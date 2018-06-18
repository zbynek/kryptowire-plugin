package org.aerogear.kryptowire;

import hudson.FilePath;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import java.io.IOException;
import java.util.logging.Logger;

public class KryptowireServiceImpl implements KryptowireService {

    private static final Logger logger = Logger.getLogger(KryptowireServiceImpl.class.getName());

    private String apiKey;

    private String apiEndpoint;

    public KryptowireServiceImpl(String apiEndpoint, String apiKey) {
        if (this.apiEndpoint != null && !this.apiEndpoint.isEmpty() && !this.apiEndpoint.endsWith("/")) {
            this.apiEndpoint += "/";
        }
        this.apiEndpoint = apiEndpoint;
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    @Override
    public JSONObject submit(String platform, FilePath filePath) throws IOException, InterruptedException {
        String endPointUrl = getApiEndpoint() + "/submit";

        HttpPost post = new HttpPost(endPointUrl);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("app", filePath.read(), ContentType.APPLICATION_OCTET_STREAM, filePath.getName());
        builder.addTextBody("key", getApiKey());
        builder.addTextBody("platform", platform);

        HttpEntity entity = builder.build();
        post.setEntity(entity);

        HttpClient httpclient = HttpClients.createDefault();

        ResponseHandler<String> handler = new BasicResponseHandler();
        String responseBody = httpclient.execute(post, handler);
        return new JSONObject(responseBody);
    }

}
