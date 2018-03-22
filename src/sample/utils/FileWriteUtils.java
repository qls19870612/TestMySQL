package sample.utils;

import javafx.scene.input.DataFormat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author sims
 * @date 2018/2/8 17:14
 **/
public class FileWriteUtils {
    public static void writeDBString(String cacheBuffString) {
        try{
            SimpleDateFormat smft=new SimpleDateFormat("YYYY年MM月dd-HH-mm-ss");
            String nowString=smft.format(new Date().getTime());
            RandomAccessFile randomAccessFile = new RandomAccessFile(nowString + ".txt","rw");
            randomAccessFile.write(cacheBuffString.getBytes("utf-8"));
            randomAccessFile.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
