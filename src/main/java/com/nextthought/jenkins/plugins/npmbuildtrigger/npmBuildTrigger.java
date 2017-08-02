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
import org.jenkinsci.plugins.github.extension.GHEventsSubscriber;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import hudson.model.TaskListener;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.lang.Runtime;
import java.lang.Process;
import java.lang.ProcessBuilder;
import java.io.BufferedReader;
import java.io.InputStreamReader;





@Extension
public class npmBuildTrigger extends Trigger<BuildableItem>  {
    private static final Logger LOGGER = LoggerFactory.getLogger(GHEventsSubscriber.class);
    String message = "";
    Run<?,?> upstreamBuild;
    boolean buildCalled;
    @DataBoundConstructor
    public npmBuildTrigger(){

    }
    
    @Override
    public void run(){
        //if(!job.isBuilding())
            job.scheduleBuild(new UpstreamCause(upstreamBuild));
    }
    
    public void checkDependencies(String triggerer, Run<?,?> upstream){
        upstreamBuild = upstream;
        try{
            Runtime rt = Runtime.getRuntime();
            String[] commands = {"npm", "view", job.getDisplayName().replace(".", "-"), "devDependencies", "--json"};
            Process proc = rt.exec(commands);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String message = "";
            String s = null;
            while((s = stdInput.readLine()) !=null){
                message+=s;
            }
            JSONObject deps = new JSONObject(message);
            Iterator<String> keys = deps.keys();
            while(keys.hasNext()){
                String npmPackage = keys.next();
                //LOGGER.info(npmPackage);
                if(npmPackage.equals(triggerer.replace(".", "-"))){
                    LOGGER.info("PACKAGE FOUND");
                    run();
                    buildCalled = true;
                }
            }
            if(!buildCalled){
                String[] newCommands = {"npm", "view", job.getDisplayName().replace(".", "-"), "dependencies", "--json"};
                proc = rt.exec(newCommands);
                //stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                message = "";
                s = null;
                while((s = stdInput.readLine()) !=null)
                    message+=s;
                deps = new JSONObject(message);
                keys = deps.keys();
                while(keys.hasNext()){
                    String npmPackage = keys.next();
                    //LOGGER.info(npmPackage);
                    if(npmPackage.equals(triggerer.replace(".", "-"))){
                        LOGGER.info("PACKAGE FOUND");
                        run();
                        buildCalled = true;
                    }
                }
            }
        }
        catch(IOException e){}
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
