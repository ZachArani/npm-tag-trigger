package com.nextthought.jenkins.plugins.npmBuildTrigger;
import hudson.model.Run;
import com.nextthought.jenkins.plugins.eventemitter.Event;
import hudson.model.AbstractProject;
import java.io.ArrayList;
public class npmBuildEvent extends Event<Run<?,?>, AbstractProject<?,?>>{

    public npmBuildEvent(Run<?, ?> runner, AbstractProject<?,?> origin){
      super(runner, origin);
      if(!EventBus.containsEmitter(this.class))
        EventBus.addEmitter(npmBuildEventEmitter);
    }

    public Run<?,?> getContent(){
      return payload;
    }

    public AbstractProject<?,?> getOrigin(){
      return origin;
    }

}
