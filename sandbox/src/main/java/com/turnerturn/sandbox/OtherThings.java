package com.turnerturn.sandbox;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

public class OtherThings {
}
//@EnableScheduling
//@Configuration
class SchedulerConfiguration {
    
	@Bean
	public ThreadPoolTaskScheduler scheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(10);
		scheduler.setThreadNamePrefix("task-scheduler-");
		scheduler.initialize();
		return scheduler;
	}
}
@Slf4j
@Component
@AllArgsConstructor
@NoArgsConstructor 
@ConfigurationProperties(prefix = "scheduler")
class TaskScheduler extends ThreadPoolTaskScheduler {

	@Getter
	@Setter
	private List<ScheduledTask> tasks;

	@Autowired
	private ThreadPoolTaskScheduler scheduler;

	protected void scheduleTaskWithCronExpression(String bashCommand, String cron) {
		log.info("scheduleTaskWithCronExpression({},{})", bashCommand, cron);
		if (bashCommand == null || bashCommand.isEmpty() || cron == null || cron.isEmpty()) {
			log.warn("bashCommand is null/empty or cron is null/empty. Cannot schedule task.");
			return;
		}
		Runnable task = () -> {
			try {
				new ShellCommandExecutor().execute(bashCommand);
			} catch (IOException | InterruptedException e) {
				log.error("Error executing bash command: {}", e.getMessage());
			}
		};

		scheduler.schedule(task, new CronTrigger(cron, ZoneId.systemDefault()));
	}

	@PostConstruct
	public void scheduleSchedulerTasks() {
		log.trace("scheduleSchedulerTasks()");

		log.info(
				"Scheduling scheduler tasks.  This service allows for configured to execute bash commands per a specific cron expression. See 'scheduler.tasks' in your application.yml file.");
		if (tasks == null || tasks.isEmpty()) {
			log.warn(
					"No tasks have been configured for the scheduler.  Please configure tasks in your application.yml file.");
			return;
		}
		tasks.forEach(configuredScheduledTask -> {
			scheduleTaskWithCronExpression(configuredScheduledTask.getCommand(), configuredScheduledTask.getCron());
		});

	}
}

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
class ScheduledTask {
	private String name;
	private String cron;
	private String command;
}


@Component
@RequiredArgsConstructor
@Slf4j
class ShellCommandExecutor {

	public void execute(String command) throws IOException, InterruptedException {
		log.trace("execute({})", command);
		ExecutorService executorService = Executors.newFixedThreadPool(1);
		boolean isWindows = System.getProperty("os.name")
				.toLowerCase().startsWith("windows");
		ProcessBuilder builder = new ProcessBuilder();
		if (isWindows) {
			builder.command("cmd.exe", "/c", command);
		} else {
			builder.command("sh", "-c", command);
		}
		builder.directory(new File(System.getProperty("user.home")));
		Process process = builder.start();
		StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
		Future<?> future = executorService.submit(streamGobbler);

		int exitCode = process.waitFor();
	}

	private static class StreamGobbler implements Runnable {
		private InputStream inputStream;
		private Consumer<String> consumer;

		public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
			this.inputStream = inputStream;
			this.consumer = consumer;
		}

		@Override
		public void run() {
			new BufferedReader(new InputStreamReader(inputStream)).lines()
					.forEach(consumer);
		}
	}

}
