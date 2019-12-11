package com.pr.sepp.notify.alert;

import org.springframework.context.ApplicationEvent;

import java.util.function.Consumer;

public class AlertMonitorEvent<T> extends ApplicationEvent {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private T t;

    public AlertMonitorEvent(T t) {
        super(t);
        this.t = t;
    }

    public T getSource() {
        return t;
    }

    public void push(Consumer<? super ApplicationEvent> action) {
        action.accept(this);
    }
}