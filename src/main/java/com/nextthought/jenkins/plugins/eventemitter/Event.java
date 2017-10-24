package com.nextthought.jenkins.plugins.eventemitter;

public abstract class Event<T>{

    protected T payload = null;

    public Event(T payload) {
      this.payload = payload;
    }

    public abstract T getContent();

}
