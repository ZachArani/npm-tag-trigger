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
import org.kohsuke.stapler.DataBoundConstructor;
import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.io.File;
import hudson.FilePath;
import com.nextthought.jenkins.plugins.npmBuildTrigger.*;

public class EventEmitter<E extends Event> extends JobProperty<AbstractProject<?,?>>{
    ArrayList<EventListener> listeners = new ArrayList<EventListener>();
    boolean listening = false;
//    Logger logger = LogManager.getLogManager().getLogger("hudson.WebAppMain");

    /*@DataBoundConstructor
    public EventEmitter(){

    }*/

    @DataBoundConstructor
    public EventEmitter(boolean listening) {
      this.listening = listening;
    }

    @Override
    public JobProperty<AbstractProject<?,?>> reconfigure(StaplerRequest req, JSONObject form){
      //logger.info("Attempting to add EventListener");
      //if(!containsListener(npmBuildEventListener.class)){
        return addListener(new npmBuildEventListener(owner));
      //}
      //return null;

    }
    public boolean getListening(){
      return listening;
    }

    public void setListening(boolean listening){
      this.listening = listening;
    }

    public void send(E event){
      EventBus.dispatch(event);
    }

    public void notify(E event){
      //logger.info("notifying");
      for(EventListener listener : listeners){
        listener.notify(event);
      }
    }

    private boolean containsListener(Class listenerClass){
      for(EventListener listener : listeners)
        if(listener.getClass() == listenerClass)
          return true;
      return false;
    }

    public EventEmitter<E> addListener(EventListener listener){
      listeners.add(listener);
      return this;
    }

    public String toString(){
      return getClass().getName() + "@" + Integer.toHexString(hashCode()) + ":" + listeners.toString();
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
            req.bindJSON(this, formData); //Sends Jelly vars back to relevant constructors
            save();
            return true;
        }

    }
}
