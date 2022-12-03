package com.spring.junitTest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.spring.junitTest.domain.BookRepository;
import com.spring.junitTest.util.MailSenderStub;
import com.spring.junitTest.web.dto.request.book.BookSaveRequestDto;
import com.spring.junitTest.web.dto.response.book.BookResponseDto;

// @Service만 띄우는 테스트 어노테이션은 없다
@DataJpaTest
public class BookServiceTest {

	@Autowired
	BookRepository bookRepository;
	
	// 문제점 -> Service 만 테스트하고 싶은데 Repository 레이어가 함께 테스트 된다.
	// 해결하기 위해서 Mockito를 사용한다 -> BookServiceMockTest.java 참고
	@Test
	public void 책등록하기_테스트() {
		//given
		BookSaveRequestDto bookSaveRequestDto = new BookSaveRequestDto();
		bookSaveRequestDto.setTitle("서비스 제목");
		bookSaveRequestDto.setAuthor("서비스 저자");
		
		// stub -> MailAdapter 가짜 객체
		MailSenderStub mailSenderStub = new MailSenderStub();
		
		// when 
		BookServiceAddMail bookServiceAddMail = new BookServiceAddMail(bookRepository, mailSenderStub);
		BookResponseDto bookResponseDto = bookServiceAddMail.책등록하기(bookSaveRequestDto);
		
		// then
		assertEquals(bookSaveRequestDto.getTitle(), bookResponseDto.getTitle());
		assertEquals(bookSaveRequestDto.getAuthor(), bookResponseDto.getAuthor());
	}
}