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

public abstract class EventEmitter{

    public void notify(E event){
      for(EventListener listener : getReceivers()){
        perform(E, J);
      }
    }

    public abstract ArrayList<J extends Job> getReceivers();

    public abstract void perform(E event, ArrayList<J extends Job> job);

}
