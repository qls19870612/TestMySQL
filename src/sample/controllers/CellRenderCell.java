package sample.controllers;


import javafx.event.Event;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import sample.Main;
import sample.events.HistoryEvent;
import sample.vo.MysqlLoginInfo;

/**
 * @author sims
 * @date 2018/2/5 18:31
 **/
public class CellRenderCell extends ListCell<MysqlLoginInfo> {
    public Label label;
    public AnchorPane panel;
    private MysqlLoginInfo _item;


    @Override
    protected void updateItem(MysqlLoginInfo item, boolean empty) {
        super.updateItem(item, empty);
        _item = item;
        if(item != null)
        {

            String showTxt = item.toString();
            int MaxLen = 20;
            if(showTxt.length() >MaxLen)
            {
                showTxt = showTxt.substring(0,MaxLen) +"...";
            }
            label.setText(showTxt);
            setGraphic(panel);
        }
        else
        {
            setGraphic(null);
        }

    }

    public void settingBtnClick(MouseEvent mouseEvent) {
        HistoryEvent historyEvent = new HistoryEvent(_item,this, HistoryEvent.DELETE_HISTORY_EVENT);
        historyEvent.data = _item;
        Main.primaryStage.fireEvent(historyEvent);

    }
}
