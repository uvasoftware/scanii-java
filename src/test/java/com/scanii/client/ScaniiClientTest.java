package com.scanii.client;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;


/**
 * Created by IntelliJ IDEA.
 * User: rafael
 * Date: Dec 30, 2010
 * Time: 8:38:18 PM
 * Copyright 2010 BrowserMob, LLC
 */
public class ScaniiClientTest {
    private static String EICAR = "X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*";
    private ScaniiClient client;

    public ScaniiClientTest() {
        
        String key = System.getProperty("SCANII_CRED").split(":")[0];
        String secret = System.getProperty("SCANII_CRED").split(":")[1];

        this.client = new ScaniiClient(key, secret);
    }


    @Test
    public void testScan() throws Exception {
        System.out.println(client.scan(EICAR.getBytes()));

    }

    public void testBadCreds() throws Exception {
        ScaniiClient sc = new ScaniiClient("a","b");
        System.out.println(sc.scan(EICAR.getBytes()));

    }

    @Test
    public void testScanStream() throws IOException {
        System.out.println(client.scan(new ByteArrayInputStream(EICAR.getBytes())));
    }

    @Test
    public void testScanAsync() throws IOException, InterruptedException {

        // queueing it
        String resp = client.scanAsync(new ByteArrayInputStream(EICAR.getBytes()));       

        HashMap<String,String> result = new ObjectMapper().readValue(resp, HashMap.class);
        String requestId = result.get("request_id");
        System.out.println("request_id:" + requestId);

        // giving it some time to catch up
        Thread.sleep(1000);
        
        // looking it up
        resp = client.status(requestId);
        System.out.println(resp);
        result = new ObjectMapper().readValue( resp, HashMap.class);

        Assert.assertEquals("infected", result.get("status"));
        
    }
}
