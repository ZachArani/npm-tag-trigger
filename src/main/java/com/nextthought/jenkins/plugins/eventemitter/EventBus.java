package com.nextthought.jenkins.plugins.eventemitter;
import hudson.Launcher;
import hudson.Extension;
import hudson.util.FormValidation;
import hudson.model.AbstractProject;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;
import hudson.model.JobProperty;
import hudson.model.Job;
import hudson.model.JobPropertyDescriptor;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import hudson.security.*;
import jenkins.model.Jenkins;
import hudson.model.Item;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.ArrayList;

public static class EventBus{
    private static ArrayList<EventEmitter> emitters = new ArrayList<EventEmitter>();
    public static void dispatch(Event<?, ?> event){
      for(EventEmitter emitter: emitters){
        emitter.notify(event);
      }
    }

    public static ArrayList<EventEmitter> getEmitters(){
      return emitters;
    }

    public static void addEmitter(EventEmitter emitter){
      emitters.add(emitter);
    }
    private static boolean containsEmitter(Class class){
      for(EventEmitter emitter : emitters)
        if(emitter.getClass() != class)
          return true;
      return false;
    }

}
