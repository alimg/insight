package com.fmakdemir.insight.webservice.model;

import java.util.List;

public class EventListResponse extends BaseResponse {
    public List<Event> events;

    private class Event {
        public String id;
        public String date;
    }
}
