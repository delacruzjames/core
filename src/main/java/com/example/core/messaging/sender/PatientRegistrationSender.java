package com.example.core.messaging.sender;

import com.example.core.messaging.config.JmsConfig;
import com.example.core.messaging.model.PatientRegistrationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.messaging.enabled", havingValue = "true")
public class PatientRegistrationSender {

	private static final Logger log = LoggerFactory.getLogger(PatientRegistrationSender.class);

	private final JmsTemplate jmsTemplate;
	private final String patientRegistrationQueue;

	public PatientRegistrationSender(
			JmsTemplate jmsTemplate,
			@Value("${app.messaging.queue:" + JmsConfig.PATIENT_REGISTRATION_QUEUE + "}") String patientRegistrationQueue) {
		this.jmsTemplate = jmsTemplate;
		this.patientRegistrationQueue = patientRegistrationQueue;
	}

	public void send(PatientRegistrationMessage message) {
		jmsTemplate.convertAndSend(patientRegistrationQueue, message);
		log.info("Sent patient registration message for patientId={}", message.getPatientId());
	}

}
