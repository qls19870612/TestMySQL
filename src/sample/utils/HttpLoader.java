package sample.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.InputStream;

/**
 * @author sims
 * @date 2018/3/2 9:23
 **/
public class HttpLoader {
    public void load(String path)
    {
                HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = null;
        try{
            httpGet = new HttpGet(path);

            HttpResponse response = client.execute(httpGet);

            HttpEntity entity = response.getEntity();

            System.out.println("response.getStatusLine().getStatusCode():" + response.getStatusLine().getStatusCode());
            InputStream input = entity.getContent();
            String content = EntityUtils.toString(entity);
            System.out.println("content:" + content);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
