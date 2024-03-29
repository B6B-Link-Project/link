package com.link.back.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

	@Value("${spring.mail.host}")
	private String mailHost;

	@Value("${spring.mail.port}")
	private int mailPort;

	@Value("${spring.mail.username}")
	private String mailUsername;

	@Value("${spring.mail.password}")
	private String mailPassword;

	@Value("${spring.mail.properties.mail.smtp.auth}")
	private String mailSmtpAuth;

	// @Value("${spring.mail.properties.mail.smtp.timeout}")
	// private String mailTimeout;

	@Value("${spring.mail.properties.mail.smtp.starttls.enable}")
	private String mailStartTlsEnable;

	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

		javaMailSender.setHost(mailHost);
		javaMailSender.setPort(mailPort);
		javaMailSender.setUsername(mailUsername);
		javaMailSender.setPassword(mailPassword);
		javaMailSender.setJavaMailProperties(getMailProperties());

		return javaMailSender;
	}

	private Properties getMailProperties() {
		Properties properties = new Properties();

		// properties.setProperty("mail.transport.protocol", "smtp");
		properties.setProperty("mail.smtp.auth", mailSmtpAuth);
		properties.setProperty("mail.smtp.starttls.enable", mailStartTlsEnable);
		// properties.setProperty("mail.debug", "true");
		properties.setProperty("mail.smtp.ssl.trust","smtp.gmail.com");
		// properties.setProperty("mail.smtp.ssl.enable","true");

		return properties;
	}
}
