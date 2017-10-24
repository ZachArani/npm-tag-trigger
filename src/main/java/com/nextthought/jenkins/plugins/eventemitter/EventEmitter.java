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

public class EventEmitter<E extends Event> extends JobProperty<AbstractProject<?,?>>{
    ArrayList<EventListener> listeners = new ArrayList<EventListener>();

    public EventEmitter() {
    }

    public void send(E event){
      EventBus.dispatch(event);
    }

    public void notify(E event){
      for(EventListener listener : listeners){
        listener.notify(event);
      }
    }

    public void addListener(EventListener listener){
      listeners.add(listener);
    }

    public String toString(){
      return listeners.toString();
    }


    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends JobPropertyDescriptor {

        public DescriptorImpl() {
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends Job> jobType) {
            return AbstractProject.class.isAssignableFrom(jobType);
        }

        @Override
        public String getDisplayName(){
          return "Event Emitter";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setUseFrench)
            save();
            return super.configure(req,formData);
        }

    }
}
