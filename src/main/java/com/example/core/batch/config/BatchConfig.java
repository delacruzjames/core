package com.example.core.batch.config;

import com.example.core.batch.listener.ImportJobListener;
import com.example.core.batch.model.StudentCsv;
import com.example.core.batch.processor.StudentItemProcessor;
import com.example.core.model.Student;
import com.example.core.repository.StudentRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

	private static final int CHUNK_SIZE = 3;

	@Bean
	@StepScope
	public FlatFileItemReader<StudentCsv> studentReader(
			@Value("#{jobParameters['fileName']}") String fileName) {
		return new FlatFileItemReaderBuilder<StudentCsv>()
				.name("studentReader")
				.resource(new ClassPathResource(fileName))
				.delimited()
				.names("name", "email")
				.targetType(StudentCsv.class)
				.linesToSkip(1)
				.build();
	}

	@Bean
	public RepositoryItemWriter<Student> studentWriter(StudentRepository studentRepository) {
		RepositoryItemWriter<Student> writer = new RepositoryItemWriter<>();
		writer.setRepository(studentRepository);
		writer.setMethodName("save");
		return writer;
	}

	@Bean
	public Step importStudentStep(
			JobRepository jobRepository,
			PlatformTransactionManager transactionManager,
			FlatFileItemReader<StudentCsv> studentReader,
			StudentItemProcessor studentItemProcessor,
			RepositoryItemWriter<Student> studentWriter) {
		return new StepBuilder("importStudentStep", jobRepository)
				.<StudentCsv, Student>chunk(CHUNK_SIZE, transactionManager)
				.reader(studentReader)
				.processor(studentItemProcessor)
				.writer(studentWriter)
				.build();
	}

	@Bean
	public Job importStudentJob(
			JobRepository jobRepository,
			Step importStudentStep,
			ImportJobListener importJobListener) {
		return new JobBuilder("importStudentJob", jobRepository)
				.listener(importJobListener)
				.start(importStudentStep)
				.build();
	}

}
