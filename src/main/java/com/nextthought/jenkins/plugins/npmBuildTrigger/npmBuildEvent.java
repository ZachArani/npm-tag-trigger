package com.nextthought.jenkins.plugins.npmBuildTrigger;
import hudson.model.Run;
import com.nextthought.jenkins.plugins.eventemitter.*;
import com.nextthought.jenkins.plugins.eventemitter.Event;
import hudson.model.FreeStyleProject;
import hudson.model.FreeStyleBuild;
import java.util.ArrayList;
public class NpmBuildEvent extends Event<NpmBuild, FreeStyleProject>{

    public NpmBuildEvent(NpmBuild build, FreeStyleProject origin){
      super(build, origin);
      if(!EventBus.containsEmitter(new NpmBuildEventEmitter()))
        EventBus.addEmitter(new NpmBuildEventEmitter());
    }

    public NpmBuild getContent(){
      return payload;
    }

    public FreeStyleProject getOrigin(){
      return origin;
    }
}
