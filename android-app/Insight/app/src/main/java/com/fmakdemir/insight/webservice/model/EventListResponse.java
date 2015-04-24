package com.fmakdemir.insight.webservice.model;

import java.util.List;

public class EventListResponse extends BaseResponse {
    public List<Event> events;

    static public class Event {
        public String id;
        public String date;
        public String deviceid;
        public String type;
        public String filename;
        public String encryption;
        public Integer priority;
    }
}
