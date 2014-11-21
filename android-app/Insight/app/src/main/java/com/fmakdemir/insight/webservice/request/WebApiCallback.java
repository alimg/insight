package com.fmakdemir.insight.webservice.request;

public interface WebApiCallback<T> {
        public void onSuccess(T data);
        public void onError(String cause);
    }