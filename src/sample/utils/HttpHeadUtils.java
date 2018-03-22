package sample.utils;

import org.apache.http.client.methods.HttpGet;

/**
 * @author sims
 * @date 2018/3/5 17:48
 **/
public class HttpHeadUtils {

    public static void addHeads(HttpGet httpGet, String headStr ) {
        String[] headArr = headStr.split("\n");
        for(int i=0;i<headArr.length;i++)
        {
            String head = headArr[i];
            String[] headInfo = head.split(": ");
            if(headInfo[0].startsWith("Host") || headInfo[0].startsWith("GET"))continue;
            httpGet.addHeader(headInfo[0], headInfo[1]);
        }
    }
}
