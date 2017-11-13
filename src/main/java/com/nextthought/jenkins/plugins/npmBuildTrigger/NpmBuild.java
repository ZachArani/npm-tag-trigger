package com.nextthought.jenkins.plugins.npmBuildTrigger;
import hudson.model.Run;
import com.nextthought.jenkins.plugins.eventemitter.*;
import com.nextthought.jenkins.plugins.eventemitter.Event;
import hudson.model.FreeStyleProject;
import hudson.model.FreeStyleBuild;
import java.util.ArrayList;
public class NpmBuild {

    String packageName;
    FreeStyleBuild build;

    public NpmBuild(String packageName, FreeStyleBuild build){
      this.packageName = packageName;
      this.build = build;
    }

    public String getPackageName(){
      return packageName;
    }

    public FreeStyleBuild getBuild(){
      return build;
    }

}
