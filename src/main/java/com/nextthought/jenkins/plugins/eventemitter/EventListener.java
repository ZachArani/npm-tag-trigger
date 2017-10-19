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

public class EventListener<E extends Event> extends JobProperty<Job<?,?>>{

    public EventListener() {
    }

    public void send(E event){
      EventBus.dispatch(event);
    }

    public void notify(E event){}

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
            return true;
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
