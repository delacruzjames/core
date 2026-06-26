package com.example.core.batch.processor;

import com.example.core.batch.model.StudentCsv;
import com.example.core.model.Student;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class StudentItemProcessor implements ItemProcessor<StudentCsv, Student> {

	@Override
	public Student process(StudentCsv studentCsv) {
		if (studentCsv.getName() == null || studentCsv.getName().isBlank()) {
			return null;
		}
		if (studentCsv.getEmail() == null || studentCsv.getEmail().isBlank()) {
			return null;
		}

		String name = studentCsv.getName().trim();
		String email = studentCsv.getEmail().trim().toLowerCase();
		return new Student(name, email);
	}

}
