package org.jenkinsci.plugins.github.util;


import com.google.common.base.Function;
import com.google.common.base.Predicate;
import hudson.model.AbstractProject;
import hudson.model.BuildableItem;
import hudson.model.Item;
import hudson.model.Job;
import hudson.triggers.Trigger;
import jenkins.model.ParameterizedJobMixIn;

import javax.annotation.CheckForNull;
import java.util.Collection;

/**
 * Utility class which holds converters or predicates (matchers) to filter or convert job lists
 *
 * @author lanwen (Merkushev Kirill)
 * @since 1.12.0
 */
public final class JobInfoHelpers {
    
    private JobInfoHelpers() {
        throw new IllegalAccessError("Do not instantiate it");
    }
    
    /**
     * @param clazz trigger class to check in job
     *
     * @return predicate with true on apply if job contains trigger of given class
     */
    public static <ITEM extends Item> Predicate<ITEM> withTrigger(final Class<? extends Trigger> clazz) {
        return new Predicate<ITEM>() {
            public boolean apply(Item item) {
                return triggerFrom(item, clazz) != null;
            }
        };
    }
    
    /**
     * Can be useful to ignore disabled jobs on reregistering hooks
     *
     * @return predicate with true on apply if item is buildable
     */
    public static <ITEM extends Item> Predicate<ITEM> isBuildable() {
        return new Predicate<ITEM>() {
            public boolean apply(ITEM item) {
                return item instanceof Job ? ((Job<?, ?>) item).isBuildable() : item instanceof BuildableItem;
            }
        };
    }
    
    

    /**
     * @param job    job to search trigger in
     * @param tClass trigger with class which we want to receive from job
     * @param <T>    type of trigger
     *
     * @return Trigger instance with required class or null
     * TODO use standard method in 1.621+
     * @deprecated use {@link #triggerFrom(Item, Class)}
     */
    @Deprecated
    @CheckForNull
    public static <T extends Trigger> T triggerFrom(Job<?, ?> job, Class<T> tClass) {
        return triggerFrom((Item) job, tClass);
    }
    
    /**
     * @param item    job to search trigger in
     * @param tClass trigger with class which we want to receive from job
     * @param <T>    type of trigger
     *
     * @return Trigger instance with required class or null
     * @since 1.25.0
     * TODO use standard method in 1.621+
     */
    @CheckForNull
    public static <T extends Trigger> T triggerFrom(Item item, Class<T> tClass) {
        if (item instanceof ParameterizedJobMixIn.ParameterizedJob) {
            ParameterizedJobMixIn.ParameterizedJob pJob = (ParameterizedJobMixIn.ParameterizedJob) item;
            
            for (Trigger candidate : pJob.getTriggers().values()) {
                if (tClass.isInstance(candidate)) {
                    return tClass.cast(candidate);
                }
            }
        }
        return null;
    }
    
    /**
     * Converts any child class of {@link Job} (such as {@link AbstractProject}
     * to {@link ParameterizedJobMixIn} to use it for workflow
     *
     * @param job to wrap
     * @param <T> any child type of Job
     *
     * @return ParameterizedJobMixIn
     * TODO use standard method in 1.621+
     */
    public static <T extends Job> ParameterizedJobMixIn asParameterizedJobMixIn(final T job) {
        return new ParameterizedJobMixIn() {
            @Override
            protected Job asJob() {
                return job;
            }
        };
    }
}
