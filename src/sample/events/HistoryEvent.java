package sample.events;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * @author sims
 * @date 2018/2/8 14:11
 **/
public class HistoryEvent extends Event{
    public static final EventType<HistoryEvent> DELETE_HISTORY_EVENT =
            new EventType<HistoryEvent>("DELETE_HISTORY_EVENT");

    public Object data;
    public HistoryEvent(Object source, EventTarget target, EventType<? extends Event> eventType) {
        super(source, target, eventType);
    }
}
