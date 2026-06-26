package com.example.core.messaging.model;

public class PatientRegistrationMessage {

	private String patientId;
	private String patientName;
	private String registrationType;

	public PatientRegistrationMessage() {
	}

	public PatientRegistrationMessage(String patientId, String patientName, String registrationType) {
		this.patientId = patientId;
		this.patientName = patientName;
		this.registrationType = registrationType;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getRegistrationType() {
		return registrationType;
	}

	public void setRegistrationType(String registrationType) {
		this.registrationType = registrationType;
	}

}
