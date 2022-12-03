package com.spring.junitTest.util;

import org.springframework.stereotype.Component;

// 가짜 객체, 아직 Mail이 완성 안되었을떄
@Component
public class MailSenderStub implements MailSender{

	@Override
	public boolean send() {
		return true;
	}
}
