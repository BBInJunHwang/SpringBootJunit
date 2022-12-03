package com.spring.junitTest.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.spring.junitTest.service.BookServiceAddMail;
import com.spring.junitTest.web.dto.request.book.BookSaveRequestDto;
import com.spring.junitTest.web.dto.response.book.BookResponseDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class BookApiController {

	private final BookServiceAddMail bookServiceAddMail;	// final field가 컴포지션(결합, has 관계) -> 해당 컨트롤러가 new 될때  반드시 초기화가 되어야한다.
															// final 이면 반드시 초기화 되어야함 -> @RequiredArgsConstructor 사용 
	
	/**
	 * 스프링 기본 parsing 전략
	 * key=value&key=value
	 * 
	 * json 타입
	 * {"key" : "value"} 받기 위해선 @RequestBody 필요 
	 * */
	
	//1. 책등록
	@PostMapping("/api/v1/book")
	public ResponseEntity<?> saveBook(@RequestBody BookSaveRequestDto bookSaveRequestDto) {
		BookResponseDto bookResponseDto = bookServiceAddMail.책등록하기(bookSaveRequestDto);
		return new ResponseEntity<>(bookResponseDto, HttpStatus.CREATED); // 201 insert
	}
	
	//2. 책리스트
	public ResponseEntity<?> getBookList() {
		return null;
		
	}
	
	//3. 책 한건보기
	public ResponseEntity<?> getBookOne() {
		return null;
		
	}
	
	//4. 책 삭제하기
	public ResponseEntity<?> deleteBook() {
		return null;
		
	}
	
	//5. 책 수정하기
	public ResponseEntity<?> updateBook() {
		return null;
		
	}
}