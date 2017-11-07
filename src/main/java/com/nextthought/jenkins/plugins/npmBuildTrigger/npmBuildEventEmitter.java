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
import hudson.model.Cause;
import java.util.Set;
import jenkins.model.ParameterizedJobMixIn;
import com.nextthought.jenkins.plugins.eventemitter.*;
import org.json.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import hudson.model.FreeStyleProject;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import jenkins.model.Jenkins;

public class npmBuildEventEmitter extends EventEmitter{


    @Override
    public void perform(Event event, FreeStyleProject targetJob){
      npmBuildEvent eventProper = (npmBuildEvent)event;
      Run<?,?> upstreamBuild = eventProper.getContent();
      String searchP = upstreamBuild.getParent().getDisplayName();
      String jobP = getPackageName(new File(getWorkspace(targetJob.getDisplayName())));
      String jobN = targetJob.getDisplayName();
      if(!isPR(upstreamBuild) && hasPackage(searchP, jobP, jobN))
        ParameterizedJobMixIn.getTrigger((Job)(Jenkins.getInstance().getItemByFullName(targetJob.getFullName())), npmBuildTrigger.class).run(upstreamBuild);
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

    @Override
    public ArrayList<FreeStyleProject> getReceivers(){
      ArrayList<FreeStyleProject> projects = new ArrayList<FreeStyleProject>();
      for(FreeStyleProject proj : Jenkins.getInstance().getAllItems(FreeStyleProject.class)){
        if(proj.getWorkspace() != null && new File(proj.getWorkspace().toString() + "/package.json").exists()){
          projects.add(proj);
        }
      }
      return projects;
    }


    private boolean isPR(Run<?,?> build){
      for(Cause c : build.getCauses()){
        if(c.getShortDescription().contains("GitHub PR"))
          return true;
      }
      return false;
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
          //logger.info("During the package search, the message (on 1st run) is " + message);
          if(message.contains("{") && message.contains("}")){
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
          }
          else
            notFound = true;
          if(notFound){
            String[] devNotFoundCommands = {"npm", "view", jobName.replace(".", "-"), "devDependencies", "--json"};
            proc = rt.exec(devNotFoundCommands);
            stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            message = "";
            while((s = stdInput.readLine()) !=null){
                message+=s;
              }
            //logger.info("During the package search, the message (on 2nd run) is " + message);
            if(message.contains("{") && message.contains("}")){
              deps = new org.json.JSONObject(message);
              keys = deps.keys();
              while(keys.hasNext()){
                  String npmPackage = keys.next();
                  if(npmPackage.equals(searchPackage.replace(".", "-"))){
                      return true;
                  }
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
          //logger.info("During the package search, the message (on 3rd run) is " + message);
          if(message.contains("{") && message.contains("}")){
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
          }
          else
            notFound = true;
          if(notFound){
            String[] depNotFoundCommands = {"npm", "view", jobName.replace(".", "-"), "dependencies", "--json"};
            proc = rt.exec(depNotFoundCommands);
            stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            message = "";
            while((s = stdInput.readLine()) !=null){
                message+=s;
            }
            if(message.contains("{") && message.contains("}")){
              deps = new org.json.JSONObject(message);
              keys = deps.keys();
              while(keys.hasNext()){
                  String npmPackage = keys.next();
                  if(npmPackage.equals(searchPackage.replace(".", "-"))){
                    return true;
                  }
              }
            }
          }
          return false;
      }
      catch(IOException e){return false;}
    }


}