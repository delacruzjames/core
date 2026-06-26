package com.example.core.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class ImportJobListener implements JobExecutionListener {

	private static final Logger log = LoggerFactory.getLogger(ImportJobListener.class);

	@Override
	public void beforeJob(JobExecution jobExecution) {
		log.info("Starting student import job: {}", jobExecution.getJobInstance().getJobName());
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("Student import job completed successfully");
		}
		else {
			log.warn("Student import job finished with status: {}", jobExecution.getStatus());
		}
	}

}
