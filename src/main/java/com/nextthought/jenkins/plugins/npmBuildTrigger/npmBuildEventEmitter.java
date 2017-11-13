package com.nextthought.jenkins.plugins.npmBuildTrigger;
import hudson.Launcher;
import hudson.Extension;
import hudson.util.FormValidation;
import hudson.model.AbstractProject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;
import hudson.model.JobProperty;
import hudson.model.Job;
import java.io.File;
import hudson.model.JobPropertyDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.lang.Runtime;
import java.lang.Process;
import java.lang.ProcessBuilder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import hudson.model.Run;
import hudson.model.Cause;
import java.util.Set;
import jenkins.model.ParameterizedJobMixIn;
import com.nextthought.jenkins.plugins.eventemitter.*;
import org.json.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import hudson.model.FreeStyleProject;
import hudson.model.FreeStyleBuild;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import jenkins.model.Jenkins;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import org.json.*;

public class NpmBuildEventEmitter extends EventEmitter{


    @Override
    public void perform(Event event, FreeStyleProject targetJob){
      NpmBuildEvent eventProper = (NpmBuildEvent)event;
      String searchPackage = eventProper.getContent().getPackageName();
      FreeStyleBuild upstreamBuild = eventProper.getContent().getBuild();
      JSONObject targetReader = null;
      try{
        targetReader = new JSONObject(targetJob.getWorkspace().child("package.json").readToString());
      }
      catch(IOException | InterruptedException e){}
      if(newHasPackage(searchPackage, targetReader)){
        if(ParameterizedJobMixIn.getTrigger(targetJob, NpmBuildTrigger.class)!=null)
          ParameterizedJobMixIn.getTrigger(targetJob, NpmBuildTrigger.class).run(upstreamBuild);
      }
    }

    @Override
    public ArrayList<FreeStyleProject> getReceivers(){
      ArrayList<FreeStyleProject> projects = new ArrayList<FreeStyleProject>();
      for(FreeStyleProject proj : Jenkins.getInstance().getAllItems(FreeStyleProject.class)){
        try{
        if(proj.getWorkspace() != null && proj.getWorkspace().child("package.json").exists()){
          projects.add(proj);
        }
      } catch(IOException | InterruptedException e){}
    }
      return projects;
    }



    public boolean newHasPackage(String searchP, JSONObject jobReader){
      if(jobReader.has("devDependencies") && jobReader.getJSONObject("devDependencies").has(searchP))
        return true;
      if(jobReader.has("dependencies") && jobReader.getJSONObject("dependencies").has(searchP))
        return true;
      return false;
    }

}
