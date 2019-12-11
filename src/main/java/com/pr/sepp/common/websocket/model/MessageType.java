package com.pr.sepp.common.websocket.model;

public enum MessageType {
    ALARM("alertFetch"),
    NOTICE("noticeFetch");
    private String beanName;

    MessageType(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }

    public static final String MESSAGE_TYPE = "messageType";
}
