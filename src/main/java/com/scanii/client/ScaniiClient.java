package com.scanii.client;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created by IntelliJ IDEA.
 * User: rafael
 * Date: Dec 28, 2010
 * Time: 4:42:58 PM
 * Copyright 2010 Uva Software, LLC
 */
public class ScaniiClient {

    private String apiEndpointSync = "https://scanii.com/api/scan/";
    private String apiEndpointAsync = "https://scanii.com/api/scan/async/";
    private String apiEndpointStatus = "https://scanii.com/api/scan/status/";
    private HttpClient client;

    /**
     * use this if you would like to tweak the http client settings
     * @return
     */
    public HttpClient getClient() {
        return client;
    }

    public String getApiEndpointStatus() {
        return apiEndpointStatus;
    }

    public void setApiEndpointStatus(String apiEndpointStatus) {
        this.apiEndpointStatus = apiEndpointStatus;
    }

    public ScaniiClient(String key, String secret) {
        client = new HttpClient();

        // basic auth configuration:
        client.getState().setCredentials(new AuthScope(null, 443), new UsernamePasswordCredentials(key, secret));
    }

    public String getApiEndpointAsync() {
        return apiEndpointAsync;
    }

    public void setApiEndpointAsync(String apiEndpointAsync) {
        this.apiEndpointAsync = apiEndpointAsync;
    }

    public String getApiEndpointSync() {
        return apiEndpointSync;
    }

    public void setApiEndpointSync(String apiEndpointSync) {
        this.apiEndpointSync = apiEndpointSync;
    }

    private String call(PostMethod post) throws IOException {
        post.setDoAuthentication(true);

         try {
            int result = this.client.executeMethod(post);
            if (result == 200) {
                return post.getResponseBodyAsString();
            }

            throw new IOException(String.format("API error: http code:%d body:%s", result, post.getResponseBodyAsString()));

        } finally {
            post.releaseConnection();
        }
    }

    /**
     * Utility method for scanning byte[]
     * @param content content to be scanned
     * @return
     * @throws IOException
     */
    public String scan(byte[] content) throws IOException {
        PostMethod post = new PostMethod(apiEndpointSync);
        post.setRequestEntity(new ByteArrayRequestEntity(content));
        return call(post);

    }

    /**
     * Easiest entry point for scanning files since contents are not completely buffered
     * @param in input stream
     * @return API response
     * @throws IOException
     */
    public String scan(InputStream in) throws IOException {
        PostMethod post = new PostMethod(apiEndpointSync);
        post.setRequestEntity(new InputStreamRequestEntity(in));
        return call(post);
    }

    /**
     * looks up the status of a request by id
     * @param requestId the id of the request to be looked up as string, null is returned if the lookup returns a 404 (invalid id)
     * @return API response
     * @throws IOException
     */
    public String status(String requestId) throws IOException {
        GetMethod get = new GetMethod(apiEndpointStatus + requestId + "/");      
        get.setDoAuthentication(true);
        try {
            int result = this.client.executeMethod(get);            
            if (result == 200) {                
                return get.getResponseBodyAsString();
            }
            if (result == 404) {
                throw new IOException(String.format("API error: invalid api request id:%s", requestId));
            }
            throw new IOException(String.format("API error: http code:%d body:%s", result, get.getResponseBodyAsString()));
            
        } finally {
            get.releaseConnection();
        }
        
    }

    /**
     * Requests content to be scanned asynchronously, please note that the normal behavior is to use this in conjunction with the status api call
     * @param in input stream
     * @return API response
     * @throws IOException
     */
    public String scanAsync(InputStream in) throws IOException {
        PostMethod post = new PostMethod(apiEndpointAsync);
        post.setRequestEntity(new InputStreamRequestEntity(in));
        return call(post);
    }
}
