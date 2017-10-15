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

public class EventBus{

    @DataBoundConstructor
    public EventBus() {
    }


    public static void dispatch(Event<?> event){
      try(ACLContext ctx = ACL.as(ACL.SYSTEM)){ //Uses the security context of SYSTEM user in order to look at all of the jobs that Jenkins is running. Don't try this at home, kids.
        for(Job i: Jenkins.getInstance().getAllItems(Job.class)){
            EventListener listener = (EventListener)i.getProperty(EventListener.class);
            listener.notify(event);
        }
      }
    }

}
