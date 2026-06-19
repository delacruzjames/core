package com.example.core.repository;

import com.example.core.model.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StudentRepositoryTest {

	@Autowired
	private StudentRepository studentRepository;

	@Test
	void saveAndFindStudent() {
		Student student = new Student("Alice", "alice@example.com");
		Student saved = studentRepository.save(student);

		assertThat(saved.getId()).isNotNull();
		assertThat(studentRepository.findById(saved.getId()))
				.isPresent()
				.get()
				.extracting(Student::getName, Student::getEmail)
				.containsExactly("Alice", "alice@example.com");
	}

}
