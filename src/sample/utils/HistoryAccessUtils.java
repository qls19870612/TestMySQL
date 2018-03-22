package sample.utils;

import sample.vo.MysqlLoginInfo;

import java.io.*;
import java.util.ArrayList;

/**
 * @author sims
 * @date 2018/2/5 12:05
 **/
public class HistoryAccessUtils {
    private static final int MAX_SHOW_COUNT = 10;
    static private String fileName = "history.byte";
    static  public ArrayList<MysqlLoginInfo> wirteInfo(MysqlLoginInfo info)
    {
        ArrayList<MysqlLoginInfo> history = addOneToHistoryToList(info);
        writeHistoryList(history);
        return history;
    }

    private static ArrayList<MysqlLoginInfo> addOneToHistoryToList(MysqlLoginInfo info) {
        ArrayList<MysqlLoginInfo> history = getHistoryList();
        int removeMoreCount = MAX_SHOW_COUNT - 1;
        int len = history.size();
        if(len > removeMoreCount)
        {
            history = (ArrayList<MysqlLoginInfo>)history.subList(history.size() - removeMoreCount, history.size());
        }
        MysqlLoginInfo sameInfo = null;
        for(int i = 0; i< len; i++)
        {

            MysqlLoginInfo loginInfo = history.get(i);
            if(info.equals(loginInfo))
            {
                sameInfo = loginInfo;
                history.add(0,history.remove(i));
                break;
            }
        }
        if(sameInfo == null)
        {
            history.add(0,info);
        }
        return history;
    }

    private static void writeHistoryList(ArrayList<MysqlLoginInfo> history) {
        ObjectOutputStream objectOutputStream = null;
        FileOutputStream fileOutputStream = null;

        try
        {
            File file = new File(fileName);
            fileOutputStream = new FileOutputStream(file);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.reset();
            int count = 0;
            for(MysqlLoginInfo mysqlLoginInfo : history)
            {
                objectOutputStream.writeObject(mysqlLoginInfo);
            }
            objectOutputStream.flush();

        }catch (Exception e){
            System.out.println("Error.message:" +e.getMessage());
        }finally{
            try{
                if(fileOutputStream != null)
                    fileOutputStream.close();
                if(objectOutputStream != null)
                    objectOutputStream.close();
            }
            catch (Exception e)
            {

            }
        }
    }

    static public ArrayList<MysqlLoginInfo> getHistoryList()
        {
            ArrayList<MysqlLoginInfo> arrayList = new ArrayList<>();
            File file = new File(fileName);
            if(file.exists() == false)return arrayList;
            try
            {
                FileInputStream fileInputStream = new FileInputStream(file);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

                while (true)
                {
                    arrayList.add(0,(MysqlLoginInfo) objectInputStream.readObject());
                }
            }
            catch (EOFException eofe){

            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
            System.out.println("arrayList.size():" + arrayList.size());
            return arrayList;
        }

    public static ArrayList<MysqlLoginInfo> removeInfo(MysqlLoginInfo info) {
        ArrayList<MysqlLoginInfo> history = removeOneFromHistoryToList(info);
        writeHistoryList(history);
        return history;
    }

    private static ArrayList<MysqlLoginInfo> removeOneFromHistoryToList(MysqlLoginInfo info) {
        ArrayList<MysqlLoginInfo> history = getHistoryList();
        int len = history.size();
        for(int i = history.size() - 1;i>=0;i--)
        {
            MysqlLoginInfo hiInfo = history.get(i);
            if(hiInfo.equals(info))
            {
                history.remove(i);
            }
        }
        return history;
    }
}
