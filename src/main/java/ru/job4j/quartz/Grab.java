package ru.job4j.quartz;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import ru.job4j.db.Store;
import ru.job4j.html.Parse;

public interface Grab {
    void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException;
}
