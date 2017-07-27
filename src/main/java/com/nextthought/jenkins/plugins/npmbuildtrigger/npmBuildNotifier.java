package com.nextthought.jenkins.plugins.npmBuildTrigger;

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
import hudson.security.*;
import static org.jenkinsci.plugins.github.util.JobInfoHelpers.triggerFrom;

import javax.annotation.Nonnull;
import java.io.IOException;

public class npmBuildNotifier extends Notifier implements SimpleBuildStep {
    
    @DataBoundConstructor
    public npmBuildNotifier(){
    }
    
    @Override
    public void perform(@Nonnull Run<?, ?> run,
                        @Nonnull FilePath workspace,
                        @Nonnull Launcher launcher,
                        @Nonnull TaskListener listener) throws InterruptedException, IOException {
        try(ACLContext ctx = ACL.as(ACL.SYSTEM)){ //Uses the security context of SYSTEM user in order to look at all of the jobs that Jenkins is running. Don't try this at home, kids.
            for(Item i: Jenkins.getInstance().getAllItems(Item.class)){
                if(triggerFrom(i, npmBuildTrigger.class) != null){
                    npmBuildTrigger trig = triggerFrom(i, npmBuildTrigger.class);
                    trig.checkDependencies(run.getParent().getDisplayName(), run);
                }
            }
        }
        
    }
    
    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
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
