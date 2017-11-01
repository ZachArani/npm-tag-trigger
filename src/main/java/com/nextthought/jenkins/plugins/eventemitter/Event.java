package com.nextthought.jenkins.plugins.eventemitter;

import hudson.model.Job;

public abstract class Event<T, J>{

    protected T payload = null;
    protected J origin = null;

    public Event(T payload, J origin) {
      this.payload = payload;
      this.origin = origin;
    }

    public abstract T getContent();

    public abstract T getOrigin();
}
