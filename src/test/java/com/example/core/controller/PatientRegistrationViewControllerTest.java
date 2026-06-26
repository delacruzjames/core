package com.example.core.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@TestPropertySource(properties = {
		"spring.jms.enabled=true",
		"app.messaging.enabled=true"
})
class PatientRegistrationViewControllerTest {

	private static final String TEST_QUEUE = "patient.registration.queue.view-test";

	@DynamicPropertySource
	static void messagingProperties(DynamicPropertyRegistry registry) {
		registry.add("app.messaging.queue", () -> TEST_QUEUE);
	}

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() throws InterruptedException {
		Thread.sleep(200);
	}

	@Test
	void showsRegistrationForm() throws Exception {
		mockMvc.perform(get("/patient-registrations"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Register a patient")))
				.andExpect(content().string(containsString("Received by Patient Clinicals")));
	}

	@Test
	void submitsRegistrationAndShowsReceivedMessage() throws Exception {
		mockMvc.perform(post("/patient-registrations")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("patientId", "P-WEB-001")
						.param("patientName", "Maria Garcia")
						.param("registrationType", "NEW_PATIENT"))
				.andExpect(status().is3xxRedirection())
				.andExpect(flash().attribute("successMessage", containsString("Maria Garcia")));

		mockMvc.perform(get("/patient-registrations"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Maria Garcia")))
				.andExpect(content().string(containsString("P-WEB-001")));
	}

}
