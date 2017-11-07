package com.nextthought.jenkins.plugins.eventemitter;

import hudson.model.Job;

public abstract class Event<P, O>{

    protected P payload = null;
    protected O origin = null;

    public Event(P payload, O origin) {
      this.payload = payload;
      this.origin = origin;
    }

    public abstract P getContent();

    public abstract O getOrigin();
}
