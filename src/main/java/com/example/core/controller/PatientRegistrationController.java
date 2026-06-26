package com.example.core.controller;

import com.example.core.messaging.model.PatientRegistrationMessage;
import com.example.core.messaging.sender.PatientRegistrationSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/patient-registrations")
@ConditionalOnProperty(name = "app.messaging.enabled", havingValue = "true")
public class PatientRegistrationController {

	private final PatientRegistrationSender patientRegistrationSender;

	public PatientRegistrationController(PatientRegistrationSender patientRegistrationSender) {
		this.patientRegistrationSender = patientRegistrationSender;
	}

	@PostMapping
	public ResponseEntity<PatientRegistrationMessage> register(@RequestBody PatientRegistrationMessage message) {
		if (message.getPatientId() == null || message.getPatientId().isBlank()) {
			message.setPatientId(UUID.randomUUID().toString());
		}
		patientRegistrationSender.send(message);
		return ResponseEntity.accepted().body(message);
	}

}
