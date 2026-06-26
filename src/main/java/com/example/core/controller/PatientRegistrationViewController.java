package com.example.core.controller;

import com.example.core.messaging.model.PatientRegistrationMessage;
import com.example.core.messaging.sender.PatientRegistrationSender;
import com.example.core.messaging.service.PatientClinicalsStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.UUID;

@Controller
@RequestMapping("/patient-registrations")
@ConditionalOnProperty(name = "app.messaging.enabled", havingValue = "true")
public class PatientRegistrationViewController {

	private final PatientRegistrationSender patientRegistrationSender;
	private final PatientClinicalsStore patientClinicalsStore;

	public PatientRegistrationViewController(
			PatientRegistrationSender patientRegistrationSender,
			PatientClinicalsStore patientClinicalsStore) {
		this.patientRegistrationSender = patientRegistrationSender;
		this.patientClinicalsStore = patientClinicalsStore;
	}

	@GetMapping
	public String showRegistrationPage(Model model) {
		if (!model.containsAttribute("registration")) {
			model.addAttribute("registration", new PatientRegistrationMessage());
		}
		model.addAttribute("pageTitle", "Patient Registration");
		model.addAttribute("receivedMessages", new ArrayList<>(patientClinicalsStore.getReceivedMessages()));
		return "patient-registrations/index";
	}

	@PostMapping
	public String registerPatient(
			@ModelAttribute("registration") PatientRegistrationMessage registration,
			RedirectAttributes redirectAttributes) {
		if (registration.getPatientId() == null || registration.getPatientId().isBlank()) {
			registration.setPatientId("P-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
		}
		patientRegistrationSender.send(registration);
		redirectAttributes.addFlashAttribute("successMessage",
				"Registration sent to ActiveMQ for patient " + registration.getPatientName() + ".");
		return "redirect:/patient-registrations";
	}

}
