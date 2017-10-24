package com.nextthought.jenkins.plugins.npmBuildTrigger;
import hudson.model.Run;
import com.nextthought.jenkins.plugins.eventemitter.Event;

public class npmBuildEvent extends Event<Run<?,?>>{

    public npmBuildEvent(Run<?, ?> runner){
      super(runner);
    }

    public Run<?,?> getContent(){
      return payload;
    }

}
