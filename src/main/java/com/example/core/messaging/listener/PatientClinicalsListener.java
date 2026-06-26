package com.example.core.messaging.listener;

import com.example.core.messaging.model.PatientRegistrationMessage;
import com.example.core.messaging.service.PatientClinicalsStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.messaging.enabled", havingValue = "true")
public class PatientClinicalsListener {

	private static final Logger log = LoggerFactory.getLogger(PatientClinicalsListener.class);

	private final PatientClinicalsStore patientClinicalsStore;

	public PatientClinicalsListener(PatientClinicalsStore patientClinicalsStore) {
		this.patientClinicalsStore = patientClinicalsStore;
	}

	@JmsListener(destination = "${app.messaging.queue:patient.registration.queue}")
	public void receive(PatientRegistrationMessage message) {
		log.info("Received patient registration for patientName={}", message.getPatientName());
		patientClinicalsStore.record(message);
	}

}
