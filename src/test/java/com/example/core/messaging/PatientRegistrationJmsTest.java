package com.example.core.messaging;

import com.example.core.messaging.model.PatientRegistrationMessage;
import com.example.core.messaging.sender.PatientRegistrationSender;
import com.example.core.messaging.service.PatientClinicalsStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@TestPropertySource(properties = {
		"spring.jms.enabled=true",
		"app.messaging.enabled=true"
})
class PatientRegistrationJmsTest {

	private static final String TEST_QUEUE = "patient.registration.queue.test";

	@DynamicPropertySource
	static void messagingProperties(DynamicPropertyRegistry registry) {
		registry.add("app.messaging.queue", () -> TEST_QUEUE);
	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private PatientRegistrationSender patientRegistrationSender;

	@Autowired
	private PatientClinicalsStore patientClinicalsStore;

	@Autowired
	private JmsListenerEndpointRegistry jmsListenerEndpointRegistry;

	@BeforeEach
	void setUp() throws InterruptedException {
		patientClinicalsStore.clear();
		waitForJmsListeners();
	}

	@Test
	void senderDeliversMessageToListener() throws InterruptedException {
		patientClinicalsStore.expectMessages(1);
		PatientRegistrationMessage message = new PatientRegistrationMessage(
				"P-1001", "Jane Doe", "NEW_PATIENT");

		patientRegistrationSender.send(message);

		assertThat(patientClinicalsStore.awaitMessages(15, TimeUnit.SECONDS)).isTrue();
		assertThat(patientClinicalsStore.getReceivedMessages())
				.hasSize(1)
				.first()
				.extracting(
						PatientRegistrationMessage::getPatientId,
						PatientRegistrationMessage::getPatientName,
						PatientRegistrationMessage::getRegistrationType)
				.containsExactly("P-1001", "Jane Doe", "NEW_PATIENT");
	}

	@Test
	void registerPatientEndpointSendsMessageToClinicalsService() throws Exception {
		patientClinicalsStore.expectMessages(1);
		PatientRegistrationMessage message = new PatientRegistrationMessage(
				"P-2001", "John Smith", "FOLLOW_UP");

		mockMvc.perform(post("/api/patient-registrations")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(message)))
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("$.patientId").value("P-2001"))
				.andExpect(jsonPath("$.patientName").value("John Smith"));

		assertThat(patientClinicalsStore.awaitMessages(15, TimeUnit.SECONDS)).isTrue();
		assertThat(patientClinicalsStore.getReceivedMessages())
				.hasSize(1)
				.first()
				.extracting(PatientRegistrationMessage::getPatientName)
				.isEqualTo("John Smith");
	}

	private void waitForJmsListeners() throws InterruptedException {
		for (int attempt = 0; attempt < 100; attempt++) {
			if (jmsListenerEndpointRegistry.isRunning()) {
				return;
			}
			Thread.sleep(100);
		}
	}

}
