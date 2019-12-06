package com.diviso.graeshoppe.config;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface MessageBinderConfiguration {

	String SALE="sale";
	
	@Output(SALE)
	MessageChannel saleOut();
	
}
