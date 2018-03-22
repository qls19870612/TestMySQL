package sample.controllers;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import sample.utils.HttpHeadUtils;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * @author sims
 * @date 2018/3/5 15:29
 **/
public class LoginController {
    private static String headString = "Connection: keep-alive\n" +
            "accept: application/json,application/vnd.siesta-error-v1+json,application/vnd.siesta-validation-errors-v1+json\n" +
            "X-Requested-With: XMLHttpRequest\n" +
            "X-Nexus-UI: true\n" +
//            "Authorization: Basic ZGVwbG95OlZhbHVlODg4\n" +
            "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.168 Safari/537.36 OPR/51.0.2830.40\n" +
            "Referer: http://106.14.251.153:18081/nexus/\n" +
            "Accept-Encoding: gzip, deflate\n" +
            "Accept-Language: zh-CN,zh;q=0.9";
    public static void login(HttpClient client, String url, String username, String pwd)
    {
//        testBase64();

        client = new DefaultHttpClient();
        HttpGet httpGet = null;
        try{
            httpGet = new HttpGet(url);
            HttpHeadUtils.addHeads(httpGet,headString);
            String autoEncode = Base64.getEncoder().encodeToString((username + ":" + pwd).getBytes("UTF-8"));
            System.out.println("autoEncode:" + autoEncode);
            httpGet.addHeader("Authorization","Base " + autoEncode);
            HttpClientUtils.excute(client,httpGet);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void testBase64() {
        /**
         * No Proxy-Authorization Header is present.

         Authorization Header is present: Basic ZGVwbG95OlZhbHVlODg4
         Decoded Username:Password= deploy:Value888

         */
        String pwd = "deploy:Value888";
        String rawStr = "ZGVwbG95OlZhbHVlODg4";
  
        String encode = null;
        String decode = null;
        String decode1 = null;
        try {
            encode = Base64.getEncoder().encodeToString(pwd.getBytes("UTF-8")).toString();
            decode = Base64.getDecoder().decode(encode).toString();
            decode1 = new String(Base64.getDecoder().decode(encode),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(rawStr);
        System.out.println(encode);
        System.out.println(decode);
        System.out.println(decode1);


    }

}
