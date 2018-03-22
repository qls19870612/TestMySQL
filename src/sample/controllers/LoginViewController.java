package sample.controllers;

import com.mysql.jdbc.StringUtils;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import sample.http.Downloader;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sims
 * @date 2018/2/24 10:41
 **/
public class LoginViewController {
    public TextField userNameTF;
    public TextField passwordTF;
    public WebView pageView;
    private WebEngine engine;
    private String test="242http://106.14.251.153:18081/nexus/content/repositories/snapshots/com/ftvalue/queryClient/1.0.2-SNAPSHOT/queryClient-1.0.2-20180227.055942-2.jar\">knowledge";
    private Pattern pattern = Pattern.compile("((http)|(https))+[^\\s\"<|]+\\.jar",Pattern.MULTILINE);
    private Pattern pattern2 = Pattern.compile("((http)|(https))+([^\\s|\"|<])+",Pattern.MULTILINE);
    private boolean isLogined = false;
    private HashMap<String,String> urlHashmap = new HashMap<>();
    private boolean printTxt;
    private String[] excptArr = null;

    public void onLoginBtnClick(MouseEvent mouseEvent) {
        if(!StringUtils.isNullOrEmpty(passwordTF.getText()))
        {
            printTxt = true;
        }
        isLogined = true;
        Matcher matcher = pattern.matcher(test);
        while (matcher.find())
        {
            String url = matcher.group();
            System.out.println("mather:" + url);
        }

    }



    private void load2(String url, String path) throws IOException {
        if(!isValid(url))
        {
            System.out.println("load2return:" + url);
            return;
        }
        else
        {

            System.out.println("load2:" + url);
        }
        urlHashmap.put(url,"1");
        String savePath = System.getProperty("user.dir");
        savePath = savePath + "\\" + path + "\\";
        savePath = savePath.replaceAll("\\\\","\\\\\\\\");
        System.out.println(url);
        Downloader.downloadFile(url,savePath,1024);
    }

    private boolean isValid(String path) {

        if(!isLogined)
        {
            return false;
        }
        if(path.indexOf("18081") < 0)
        {
            return false;
        }
        if(urlHashmap.containsKey(path))return false;
        return true;
    }

    private void load(String path) throws IOException {
        if(!isValid(path))
        {
            System.out.println("loadreturn:" + path);
            return;
        }
        else
        {
            System.out.println("load:" + path);
        }
        urlHashmap.put(path,"1");
        engine.load(path);
    }

    public void init() {
        if(true)
        {
            HttpClient client = new DefaultHttpClient();
            LoginController.login(client,"http://106.14.251.153:18081/nexus/service/local/authentication/login?_dc=1520241841914","deploy","Value888");
            OpenUrlController.openUrlInfo(client,"http://106.14.251.153:18081/nexus/#view-repositories;public~browsestorage");

            return;
        }
        String excpt = ".png,.ico,.xml,.jpg,.md5,.pom,.css";
       excptArr = excpt.split(",");

        engine = pageView.getEngine();
        engine.setOnStatusChanged(new EventHandler<WebEvent<String>>() {
            @Override
            public void handle(WebEvent<String> event) {
                System.out.println("stat:" + event.toString() + "->" + event.getData());

                Document document = engine.getDocument();
                if(document != null)
                {

                    String textContext = (String) engine.executeScript("document.documentElement.outerHTML");

                    if(!StringUtils.isNullOrEmpty(textContext))
                    {
                        textContext = testDownLoad(textContext);
                        testOpenUrl(textContext);

                    }

//                    System.out.println(textContext);
                }
            }
        });
//        engine.getLoadWorker().stateProperty().addListener(
//                new ChangeListener<State>() {
//                    public void changed(ObservableValue ov, State oldState, State newState) {
//                        if (newState == State.SUCCEEDED) {
//                            String textContext = (String) engine.executeScript("document.documentElement.outerHTML");
////                            System.out.println("textContextaddListener:" + textContext);
//                    if(!StringUtils.isNullOrEmpty(textContext))
//                    {
//                        textContext = testDownLoad(textContext);
//                        testOpenUrl(textContext);
//
//                    }
//
//                        }
//                    }
//                });

        engine.load("http://106.14.251.153:18081/nexus/content/repositories/snapshots/");
    }

    private void testOpenUrl(String textContext)  {
        Matcher matcher = pattern2.matcher(textContext);
        while (matcher.find()) {
            String url = matcher.group();
            url = handUrl(url);
            if(!isNeedOpen(url))continue;
            try{

                load(url);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private boolean isNeedOpen(String url) {
        for(int i= 0;i<excptArr.length;i++)
        {
            if(url.indexOf(excptArr[i])>-1)
            {
                return false;
            }
        }
        return true;
    }

    private String handUrl(String url) {
        return url.split(" ")[0];
    }

    private String testDownLoad(String textContext) {
        Matcher matcher = pattern.matcher(textContext);
//        StringBuffer buffer = new StringBuffer("");
        String retStr = textContext.concat("");
        while (matcher.find())
        {
            String url = matcher.group();
            retStr = retStr.replaceFirst(url,"");
            url = handUrl(url);

            try {
                int firstPathIndex = url.indexOf("com");
                String subPath="unknow";
                if(firstPathIndex != -1)
                {
                    int lastXiexian = url.lastIndexOf("/");
                    if(lastXiexian >=0)
                    {
                        subPath = url.substring(firstPathIndex,lastXiexian-1);
                    }
                }
                load2(url, subPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return retStr;
    }
}
