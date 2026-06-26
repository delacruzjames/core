package com.example.core.batch;

import com.example.core.model.Student;
import com.example.core.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBatchTest
@SpringBootTest
@ActiveProfiles("dev")
class StudentImportJobTest {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	@Qualifier("importStudentJob")
	private Job importStudentJob;

	@Autowired
	private StudentRepository studentRepository;

	@BeforeEach
	void setUp() {
		jobLauncherTestUtils.setJob(importStudentJob);
		studentRepository.deleteAll();
	}

	@Test
	void importsStudentsFromCsv() throws Exception {
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("fileName", "students.csv")
				.addLong("run.id", System.currentTimeMillis())
				.toJobParameters();

		JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

		assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
		assertThat(studentRepository.count()).isEqualTo(5);
		assertThat(studentRepository.findAll())
				.extracting(Student::getName, Student::getEmail)
				.contains(
						tuple("Alice Johnson", "alice@example.com"),
						tuple("Bob Smith", "bob@example.com"),
						tuple("Eve Martinez", "eve@example.com"));
	}

}
