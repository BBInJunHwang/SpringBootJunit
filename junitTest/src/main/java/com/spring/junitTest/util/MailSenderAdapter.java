package com.spring.junitTest.util;

// 추후에 Mail 클래스가 완성되면 코드를 완성하면됨
public class MailSenderAdapter implements MailSender{
	
//	private Mail mail;
//
//	public MailSenderAdapter(Mail mail) {
//		this.mail = mail;
//	}


	@Override
	public boolean send() {
		//return mail.sendMail();
		return true;
	}

}
