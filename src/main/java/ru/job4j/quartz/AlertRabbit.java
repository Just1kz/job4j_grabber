package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class AlertRabbit {
    private final Map<String, String> values = new HashMap<String, String>();

    public static void main(String[] args) {
        AlertRabbit alertRabbit = new AlertRabbit();
        int x = alertRabbit.getSettingsFileProperties();
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(Rabbit.class).build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(x)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

    public int getSettingsFileProperties() {
        try (BufferedReader read = new BufferedReader(new FileReader(".\\src\\main\\resources\\rabbit.properties"))) {
            read.lines()
                    .filter(x -> x.length() != 0 && !x.startsWith("#"))
                    .map(line -> line.split("="))
                    .forEach(x -> values.put(x[0], x[1]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Integer.parseInt(values.get("rabbit.interval"));
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
        }
    }
}
