package sample.controllers;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

/**
 * @author sims
 * @date 2018/3/5 17:33
 **/
public class OpenUrlController {
    public static void openUrlInfo(HttpClient client, String urlInfo)
    {
        String url = "GET http://106.14.251.153:18081/nexus/ HTTP/1.1\n" +
                "Host: 106.14.251.153:18081\n" +
                "Connection: keep-alive\n" +
                "Cache-Control: max-age=0\n" +
                "Upgrade-Insecure-Requests: 1\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.168 Safari/537.36 OPR/51.0.2830.40\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8\n" +
                "Accept-Encoding: gzip, deflate\n" +
                "Accept-Language: zh-CN,zh;q=0.9\n" +
                "Cookie: NXSESSIONID=16518562-74e5-4ffc-b652-f371b89f589a\n" +
                "If-Modified-Since: Mon, 05 Mar 2018 09:31:34 GMT\n";
        HttpClientUtils.excute(client, new HttpGet(urlInfo));

    }
}
