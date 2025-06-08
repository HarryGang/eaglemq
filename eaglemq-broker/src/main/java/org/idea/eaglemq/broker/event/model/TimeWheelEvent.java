package org.idea.eaglemq.broker.event.model;

import org.idea.eaglemq.broker.timewheel.TimeWheelSlotModel;
import org.idea.eaglemq.common.event.model.Event;

import java.util.List;

/**
 * @Author idea
 * @Date: Created at 2024/8/4
 * @Description 时间轮到期事件
 */
public class TimeWheelEvent extends Event {

    private List<TimeWheelSlotModel> timeWheelSlotModelList;

    public List<TimeWheelSlotModel> getTimeWheelSlotModelList() {
        return timeWheelSlotModelList;
    }

    public void setTimeWheelSlotModelList(List<TimeWheelSlotModel> timeWheelSlotModelList) {
        this.timeWheelSlotModelList = timeWheelSlotModelList;
    }
}
