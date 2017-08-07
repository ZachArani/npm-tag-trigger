package com.nextthought.jenkins.plugins.npmBuildTrigger;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.BuildableItem;
import hudson.model.Run;
import hudson.FilePath;
import hudson.model.Item;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.model.TaskListener;
import hudson.Launcher;
import jenkins.model.Jenkins;
import org.json.*;
import hudson.tasks.Notifier;
import jenkins.tasks.SimpleBuildStep;
import org.kohsuke.stapler.DataBoundConstructor;
import hudson.model.Job;
import hudson.model.Cause.RemoteCause;
import hudson.tasks.Publisher;
import hudson.tasks.BuildStepMonitor;
import hudson.model.AbstractProject;
import java.io.File;
import java.lang.Runtime;
import java.lang.Process;
import java.lang.ProcessBuilder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import hudson.security.*;
import static org.jenkinsci.plugins.github.util.JobInfoHelpers.triggerFrom;
import java.util.Iterator;


import javax.annotation.Nonnull;
import java.io.IOException;

public class npmBuildNotifier extends Notifier implements SimpleBuildStep {
    
    Run<?,?> build;
    TaskListener buildListener;
    String message = "";

    @DataBoundConstructor
    public npmBuildNotifier(){
    }
    
    @Override
    public void perform(@Nonnull Run<?, ?> run,
                        @Nonnull FilePath workspace,
                        @Nonnull Launcher launcher,
                        @Nonnull TaskListener listener) throws InterruptedException, IOException {
        buildListener = listener;
        build = run;
        EnvVars env = run.getEnvironment(listener);
        listener.getLogger().println("Starting notification");
        try(ACLContext ctx = ACL.as(ACL.SYSTEM)){ //Uses the security context of SYSTEM user in order to look at all of the jobs that Jenkins is running. Don't try this at home, kids.
            for(Item i: Jenkins.getInstance().getAllItems(Item.class)){
                if(triggerFrom(i, npmBuildTrigger.class) != null){
                    listener.getLogger().println("JOB FOUND " + i.getDisplayName());
                    npmBuildTrigger trig = triggerFrom(i, npmBuildTrigger.class);
                    if(checkDependencies(getPackageName(new File(workspace.toString())), getPackageName(i.getRootDir())))
                        trig.run();
                }
            }
        }
        
    }
    
    public String getPackageName(File workspace){
        String message = "";
        try{
            Runtime rt = Runtime.getRuntime();
            String[] commands = {"npm", "view", "", "name"};
            Process proc = rt.exec(commands, null, workspace);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String s = null;
            while((s = stdInput.readLine()) !=null){
                message+=s;
            }

        }
        catch(IOException e){}
        finally{ return message; }
    }
    
    public boolean checkDependencies(String triggerer, String triggeree){
        try{
            Runtime rt = Runtime.getRuntime();
            String[] commands = {"npm", "view", triggeree.replace(".", "-"), "devDependencies", "--json"};
            Process proc = rt.exec(commands);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String message = "";
            String s = null;
            while((s = stdInput.readLine()) !=null){
                message+=s;
            }
            buildListener.getLogger().println(message);
            /*JSONObject deps = new JSONObject("{}");
            Iterator<String> keys = deps.keys();
            while(keys.hasNext()){
                String npmPackage = keys.next();
                if(npmPackage.equals(triggerer.replace(".", "-"))){
                    return true;
                }
            }
            String[] newCommands = {"npm", "view", triggeree.replace(".", "-"), "dependencies", "--json"};
            proc = rt.exec(newCommands);
            //stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            message = "";
            s = null;
            while((s = stdInput.readLine()) !=null)
                message+=s;
            buildListener.getLogger().println(message);
            deps = new JSONObject("{}");
            keys = deps.keys();
            while(keys.hasNext()){
                String npmPackage = keys.next();
                if(npmPackage.equals(triggerer.replace(".", "-"))){
                    return true;
                }
            }
        */
        }
        catch(IOException e){}
        finally{return false;}
    }

    
    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean needsToRunAfterFinalized(){
        return true;
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher>{
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }
        public String getDisplayName(){
            return "Notify dependent packages on build";
        }
        
       
        
    }
}
