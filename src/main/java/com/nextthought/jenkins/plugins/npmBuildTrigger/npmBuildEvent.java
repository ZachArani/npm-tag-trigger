package com.nextthought.jenkins.plugins.npmBuildTrigger;
import hudson.model.Run;
import com.nextthought.jenkins.plugins.eventemitter.*;
import com.nextthought.jenkins.plugins.eventemitter.Event;
import hudson.model.FreeStyleProject;
import hudson.model.FreeStyleBuild;
import java.util.ArrayList;
public class npmBuildEvent extends Event<FreeStyleBuild, FreeStyleProject>{

    public npmBuildEvent(FreeStyleBuild runner, FreeStyleProject origin){
      super(runner, origin);
      if(!EventBus.containsEmitter(new npmBuildEventEmitter()))
        EventBus.addEmitter(new npmBuildEventEmitter());
    }

    public FreeStyleBuild getContent(){
      return payload;
    }

    public FreeStyleProject getOrigin(){
      return origin;
    }

}
