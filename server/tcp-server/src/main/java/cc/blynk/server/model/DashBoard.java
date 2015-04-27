package cc.blynk.server.model;

import cc.blynk.server.model.widgets.Widget;
import cc.blynk.server.model.widgets.others.Mail;
import cc.blynk.server.model.widgets.others.Timer;
import cc.blynk.server.model.widgets.outputs.Graph;

import java.util.*;

/**
 * User: ddumanskiy
 * Date: 21.11.13
 * Time: 13:04
 */
public class DashBoard {

    private int id;

    private String name;

    private Long timestamp;

    private Widget[] widgets;

    private String boardType;

    public List<Timer> getTimerWidgets() {
        if (widgets == null || widgets.length == 0) {
            return Collections.emptyList();
        }

        List<Timer> timerWidgets = new ArrayList<>();
        for (Widget widget : widgets) {
            if (widget instanceof Timer) {
                Timer timer = (Timer) widget;
                if ((timer.startTime != null && timer.startValue != null && !timer.startValue.equals("")) ||
                    (timer.stopTime != null && timer.stopValue != null && !timer.stopValue.equals(""))) {
                    timerWidgets.add(timer);
                }
            }
        }

        return timerWidgets;
    }

    //expecting only 1 email widget.
    public Mail getEmailWidget() {
        if (widgets == null || widgets.length == 0) {
            return null;
        }

        for (Widget widget : widgets) {
            if (widget instanceof Mail) {
                return (Mail) widget;
            }
        }

        return null;
    }

    public Set<Byte> getGraphWidgetPins() {
        if (widgets == null || widgets.length == 0) {
            return Collections.emptySet();
        }

        Set<Byte> graphPins = new HashSet<>();
        for (Widget widget : widgets) {
            if (widget instanceof Graph) {
                graphPins.add(widget.pin);
            }
        }

        return graphPins;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Widget[] getWidgets() {
        return widgets;
    }

    public void setWidgets(Widget[] widgets) {
        this.widgets = widgets;
    }

    public String getBoardType() {
        return boardType;
    }

    public void setBoardType(String boardType) {
        this.boardType = boardType;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DashBoard dashBoard = (DashBoard) o;

        if (id != dashBoard.id) return false;
        if (name != null ? !name.equals(dashBoard.name) : dashBoard.name != null) return false;
        if (!Arrays.equals(widgets, dashBoard.widgets)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (widgets != null ? Arrays.hashCode(widgets) : 0);
        return result;
    }
}
