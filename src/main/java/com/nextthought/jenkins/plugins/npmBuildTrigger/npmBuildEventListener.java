package com.nextthought.jenkins.plugins.npmBuildTrigger;
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
import java.util.Set;
import jenkins.model.ParameterizedJobMixIn;
import com.nextthought.jenkins.plugins.eventemitter.*;
import org.json.*;

public class npmBuildEventListener extends EventListener<npmBuildEvent>{
    Job currentJob = (Job)owner;

    @Override
    public void notify(npmBuildEvent event){
      Run<?,?> upstreamBuild = event.getContent();
      if(hasPackage(upstreamBuild.getDisplayName(), getPackageName(new File(getWorkspace(currentJob.getDisplayName()))), currentJob.getDisplayName()))
        ParameterizedJobMixIn.getTrigger(currentJob, npmBuildTrigger.class).run(event.getContent());
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

    public String getWorkspace(String jobPackage){
          return "/Users/Shared/Jenkins/Home/workspace/" + jobPackage;
    }

    public boolean hasPackage(String searchPackage, String jobPackage, String jobName){
      boolean notFound = false;
      try{
          Runtime rt;
          String[] commands;
          Process proc;
          BufferedReader stdInput;
          String message;
          String s;
          org.json.JSONObject deps;
          Iterator<String> keys;
          rt = Runtime.getRuntime();
          String[] devCommands = {"npm", "view", jobPackage.replace(".", "-"), "devDependencies", "--json"};
          proc = rt.exec(devCommands);
          stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
          message = "";
          while((s = stdInput.readLine()) !=null){
              message+=s;
          }
          deps = new org.json.JSONObject(message);
          keys = deps.keys();
          while(keys.hasNext()){
              String npmPackage = keys.next();
              if(npmPackage.equals(searchPackage.replace(".", "-"))){
                return true;
              }
              if(npmPackage.equals("error")){
                notFound = true;
              }
          }
          if(notFound){
            String[] devNotFoundCommands = {"npm", "view", jobName.replace(".", "-"), "devDependencies", "--json"};
            proc = rt.exec(devNotFoundCommands);
            stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            message = "";
            while((s = stdInput.readLine()) !=null){
                message+=s;
              }
              deps = new org.json.JSONObject(message);
              keys = deps.keys();
              while(keys.hasNext()){
                  String npmPackage = keys.next();
                  if(npmPackage.equals(searchPackage.replace(".", "-"))){
                      return true;
                  }
              }
          }
          notFound = false;
          String[] depCommands = {"npm", "view", jobPackage.replace(".", "-"), "dependencies", "--json"};
          proc = rt.exec(depCommands);
          stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
          message = "";
          s = null;
          while((s = stdInput.readLine()) !=null)
              message+=s;
          deps = new org.json.JSONObject(message);
          keys = deps.keys();
          while(keys.hasNext()){
              String npmPackage = keys.next();
              if(npmPackage.equals(searchPackage.replace(".", "-"))){
                return true;
              }
              if(npmPackage.equals("error")){
                notFound = true;
              }
          }
          if(notFound){
            String[] depNotFoundCommands = {"npm", "view", jobName.replace(".", "-"), "dependencies", "--json"};
            proc = rt.exec(depNotFoundCommands);
            stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            message = "";
            while((s = stdInput.readLine()) !=null){
                message+=s;
              }
              deps = new org.json.JSONObject(message);
              keys = deps.keys();
              while(keys.hasNext()){
                  String npmPackage = keys.next();
                  if(npmPackage.equals(searchPackage.replace(".", "-"))){
                    return true;
                  }
              }
          }
          return false;
      }
      catch(IOException e){return false;}
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
          return "Npm Build Listener";
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
