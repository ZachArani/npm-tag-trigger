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
import hudson.model.Cause;
import hudson.tasks.Publisher;
import hudson.model.FreeStyleProject;
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
import hudson.model.Result;
import javax.annotation.Nonnull;
import java.io.IOException;
import com.nextthought.jenkins.plugins.eventemitter.EventBus;
import hudson.model.FreeStyleBuild;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;

public class NpmBuildNotifier extends Notifier implements SimpleBuildStep {

    Run<?,?> build;
    TaskListener buildListener;
    String message = "";

    @DataBoundConstructor
    public NpmBuildNotifier(){
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run,
                        @Nonnull FilePath workspace,
                        @Nonnull Launcher launcher,
                        @Nonnull TaskListener listener) throws InterruptedException, IOException {
        buildListener = listener;
        build = run;
        EnvVars env = run.getEnvironment(listener);
        if(run.getResult() == Result.SUCCESS && !isPR(run)){
          listener.getLogger().println("Running NPM Build Trigger");
          JSONObject reader = new JSONObject(workspace.child("package.json").readToString());
          EventBus.dispatch(new NpmBuildEvent(new NpmBuild(reader.getString("name"), (FreeStyleBuild)run), (FreeStyleProject)run.getParent()));
        }
    }

    private boolean isPR(Run<?,?> build){
      for(Cause c : build.getCauses()){
        if(c.getShortDescription().contains("GitHub PR"))
          return true;
      }
      return false;
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
            return "Notify dependent packages";
        }



    }
}
