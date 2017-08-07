package com.nextthought.jenkins.plugins.npmBuildTrigger;
import hudson.Proc;
import hudson.Extension;
import hudson.model.BuildableItem;
import hudson.model.Run;
import hudson.model.TopLevelItem;
import hudson.FilePath;
import hudson.model.Item;
import hudson.model.Result;
import hudson.Launcher;
import jenkins.model.Jenkins;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import hudson.model.Job;
import hudson.model.Cause.UpstreamCause;
import hudson.model.WorkspaceBrowser;
import java.io.StringWriter;
import hudson.util.StreamTaskListener;
import hudson.Launcher;
import java.io.IOException;
import hudson.EnvVars;
import org.json.*;
import java.util.Iterator;
import hudson.model.BuildableItem;
import hudson.model.TaskListener;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.lang.Runtime;
import java.lang.Process;
import java.lang.ProcessBuilder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Extension
public class npmBuildTrigger extends Trigger<BuildableItem>  {
    private static final Logger LOGGER = LoggerFactory.getLogger(npmBuildTrigger.class);
    Run<?,?> upstreamBuild;
    @DataBoundConstructor
    public npmBuildTrigger(){

    }
    
    @Override
    public void run(){
        //if(!job.isBuilding())
            job.scheduleBuild(new UpstreamCause(upstreamBuild));
    }
    
    
    
    public BuildableItem getJob(){
            return job;
        }
    
    @Extension
    public static class DescriptorImpl extends TriggerDescriptor {
        public boolean isApplicable(Item item){
            return true;
        }
        
        public String getDisplayName(){
            return "Dependent package completes a build";
        }
        
       
        
    }
}
