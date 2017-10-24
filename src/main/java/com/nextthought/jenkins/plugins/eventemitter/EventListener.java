package com.nextthought.jenkins.plugins.eventemitter;
import hudson.Launcher;
import hudson.Extension;
import hudson.util.FormValidation;
import hudson.model.AbstractProject;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;
import hudson.model.JobProperty;
import hudson.model.Job;
import hudson.model.JobPropertyDescriptor;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ArrayList;
import jenkins.model.Jenkins;

public abstract class EventListener<E extends Event>{

    public EventListener(){

    }
    public EventListener(Job j){
      EventEmitter emitter = (EventEmitter)j.getProperty(EventEmitter.class);
      emitter.addListener(this);

    }

    public EventListener(String jobName){
      Job job = (Job)Jenkins.getInstance().getItemByFullName(jobName);
      EventEmitter emitter = (EventEmitter)job.getProperty(EventEmitter.class);
      emitter.addListener(this);
    }


    public abstract void notify(E event);

}
