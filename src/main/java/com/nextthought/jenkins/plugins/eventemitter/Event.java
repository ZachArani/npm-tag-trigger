package com.nextthought.jenkins.plugins.eventemitter;

public abstract class Event<T>{

    private T payload = null;

    public Event(T payload) {
      this.payload = payload;
    }

    public abstract T getContent();

}
