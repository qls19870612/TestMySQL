package sample.controllers;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import sample.Main;
import sample.dao.JDBCMySql;
import sample.utils.FileWriteUtils;
import sample.utils.HistoryAccessUtils;
import sample.events.HistoryEvent;
import sample.vo.MysqlLoginInfo;

import java.io.FilePermission;
import java.lang.reflect.Field;
import java.lang.reflect.ReflectPermission;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author sims
 * @date 2018/2/2 18:35
 **/
public class PanelController {

    public TextField ip;
    public TextField username;
    public TextField pwd;
    public ListView historyList;
    public Hyperlink linkTxt;
    private JDBCMySql jdbc;
    public void init()
    {

        historyList.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                FXMLLoader loader= new FXMLLoader(getClass().getResource("../fxml/items/CellRender.fxml"));
                try
                {
                    loader.load();
                    return (ListCell)loader.getController();

                }
                catch (Exception e)
                {

                }

                return null;
            }
        });
        ArrayList<MysqlLoginInfo> arrayList = HistoryAccessUtils.getHistoryList();
        updateHistoryListView(arrayList);
        if(arrayList.size() > 0)
        {
            setInfoByHistory(arrayList.get(0));
        }
        addGlobalEvent();

    }
    private void addGlobalEvent() {
        Main.primaryStage.addEventHandler(HistoryEvent.DELETE_HISTORY_EVENT, new EventHandler<HistoryEvent>() {
            @Override
            public void handle(HistoryEvent event) {
                MysqlLoginInfo mysqlLoginInfo = (MysqlLoginInfo) event.data;
                ArrayList<MysqlLoginInfo> arrayList = HistoryAccessUtils.removeInfo(mysqlLoginInfo);
                updateHistoryListView(arrayList);
            }
        });
    }
    private void updateHistoryListView(ArrayList<MysqlLoginInfo> arrayList) {
        historyList.getItems().setAll(arrayList);
    }

    public void onClickBtn(MouseEvent mouseEvent) {
        if(jdbc == null)
        {
            jdbc = new JDBCMySql();
        }
        try
        {

            boolean isSuccess = jdbc.operateMySql(ip.getText(), username.getText(), pwd.getText());
            FileWriteUtils.writeDBString(jdbc.getCacheBuffString());
            MysqlLoginInfo info = createHistoryInfo();
            ArrayList<MysqlLoginInfo> arrayList =   HistoryAccessUtils.wirteInfo(info);
            updateHistoryListView(arrayList);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }




    public void onClickList(MouseEvent mouseEvent) {
        ListView listView = (ListView)mouseEvent.getSource();

        if(listView != null)
        {
           ObservableList observableList = listView.getSelectionModel().getSelectedItems();
            Object object = listView.getSelectionModel().getSelectedItem();
            MysqlLoginInfo mysqlLoginInfo = (MysqlLoginInfo)listView.getSelectionModel().getSelectedItem();
            if(mysqlLoginInfo != null)
            {
                setInfoByHistory(mysqlLoginInfo);
            }
        }
    }
    private MysqlLoginInfo createHistoryInfo() {
        Field[] fields = PanelController.class.getFields();

        MysqlLoginInfo ret = new MysqlLoginInfo();
        for(Field field:fields)
        {
            try
            {
                Object object = field.get(this);
                if(object instanceof TextField)
                {
                    TextField textField = (TextField)object;
                    Field loginInfoField = MysqlLoginInfo.class.getField(field.getName());
                    if(loginInfoField != null)
                    {
                        loginInfoField.set(ret,textField.getText());
                    }
                }

            }
            catch ( Exception e)
            {
                System.out.println("--------------------" +  e.toString());
            }
        }
        return ret;
    }
    private void setInfoByHistory(MysqlLoginInfo mysqlLoginInfo) {
        Field[] fields = MysqlLoginInfo.class.getFields();


        for(Field field:fields)
        {
            try
            {
                Field panelFiled = PanelController.class.getField(field.getName());
                if(panelFiled == null)continue;
                Object object = panelFiled.get(this);
                if(object instanceof TextField)
                {
                    TextField textField = (TextField)object;
                    textField.setText((String)field.get(mysqlLoginInfo));
                }

            }
            catch ( Exception e)
            {
                System.out.println("+++++++++++++++" +  e.toString());
            }
        }
    }


    public void onClickHyperLink(MouseEvent mouseEvent) {
        new Timer("onClick....").schedule(new TimerTask() {
            @Override
            public void run() {
                linkTxt.setCursor(linkTxt.getCursor()==Cursor.CLOSED_HAND?Cursor.HAND:Cursor.CLOSED_HAND);
                System.out.println(  "interval:" + new Date().toString());
            }
        },1200,1200);
    }
}
