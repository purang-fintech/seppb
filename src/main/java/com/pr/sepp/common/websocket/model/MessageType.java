package com.pr.sepp.common.websocket.model;

public enum MessageType {
    WARNING("warningFetch"),
    MESSAGE("messageFetch");
    private String beanName;

    MessageType(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }

    public static final String MESSAGE_TYPE = "messageType";
}
