package com.nextthought.jenkins.plugins.npmBuildTrigger;

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

@Extension
public class npmBuildTrigger extends Trigger<Job>  {
    //The REAL trigger. Although just about all of the action happens in the Event class. Always gotta keep programmers on their toes.
    String message = "";
    BuildableItem buildable = (BuildableItem) job;
    Run<?,?> upstreamBuild;
    boolean buildCalled;
    @DataBoundConstructor
    public npmBuildTrigger(){

    }
    
    @Override
    public void run(){
        if(!job.isBuilding() && job.getQueueItem() == null)
            buildable.scheduleBuild(new UpstreamCause(upstreamBuild));
    }
    
    public void checkDependencies(String triggerer, Run<?,?> upstream){
        upstreamBuild = upstream;
        try{
            StringWriter test = new StringWriter();
            StreamTaskListener s = new StreamTaskListener(test);
            Launcher testLaunch = Jenkins.getInstance().createLauncher(s);
            testLaunch.launch().cmds("npm view" + job.getDisplayName().replace(".", "-") + "devDependencies --json").stdout(s).start();
            JSONObject dependencies = new JSONObject(test.toString());
            test.flush();
            Iterator<String> keys = dependencies.keys();
            while(keys.hasNext()){
                String npmPackage = keys.next();
                if(npmPackage.equals(triggerer)){
                    run();
                    buildCalled = true;
                }
            }
            if(!buildCalled){
                testLaunch.launch().cmds("npm view" + job.getDisplayName().replace(".", "-") + "dependencies --json").stdout(s).start();
                dependencies = new JSONObject(test.toString());
                test.flush();
                keys = dependencies.keys();
                while(keys.hasNext()){
                    String npmPackage = keys.next();
                    if(npmPackage.equals(triggerer)){
                        run();
                        buildCalled = true;
                    }
                }
            }
        }
        catch(IOException e){}
    }
    
    
    
    public Job getJob(){
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
