package com.example.core.messaging.service;

import com.example.core.messaging.model.PatientRegistrationMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class PatientClinicalsStore {

	private final List<PatientRegistrationMessage> receivedMessages = new CopyOnWriteArrayList<>();
	private volatile CountDownLatch messageLatch = new CountDownLatch(0);

	public void record(PatientRegistrationMessage message) {
		receivedMessages.add(message);
		messageLatch.countDown();
	}

	public List<PatientRegistrationMessage> getReceivedMessages() {
		return Collections.unmodifiableList(new ArrayList<>(receivedMessages));
	}

	public void clear() {
		receivedMessages.clear();
		messageLatch = new CountDownLatch(0);
	}

	public void expectMessages(int count) {
		messageLatch = new CountDownLatch(count);
	}

	public boolean awaitMessages(long timeout, TimeUnit unit) throws InterruptedException {
		return messageLatch.await(timeout, unit);
	}

}
