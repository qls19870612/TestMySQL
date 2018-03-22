package sample.controllers;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author sims
 * @date 2018/3/5 17:54
 **/
public class HttpClientUtils {
    public static void excute(HttpClient client,HttpGet httpGet) {
        HttpResponse response = null;
        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();

            System.out.println("response.getStatusLine().getStatusCode():" + response.getStatusLine().getStatusCode());
            InputStream input = entity.getContent();
            String content = EntityUtils.toString(entity);
            System.out.println("content:" + content);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
