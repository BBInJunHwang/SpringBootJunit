package com.spring.junitTest.web.dto.response.book;

import com.spring.junitTest.domain.Book;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class BookResponseDto {

	private Long id;
	private String title;
	private String author;
	
	@Builder
	public BookResponseDto(Long id, String title, String author) {
		this.id = id;
		this.title = title;
		this.author = author;
	}
	
	
	
	
	
 		//이렇게 호출하면 BookResponseDto 객체를 만들어서 (new BookResponseDto 생성 해줘야함) 리턴해야함..
//	public  BookResponseDto toDto(Book bookPS) {
//		this.id = bookPS.getId();
//		this.title = bookPS.getTitle();
//		this.author = bookPS.getAuthor();
//		return this;	// 자기자신 리턴
//	}
	
	// static 으로 만들어서 외부에서 ResponseDto.toDto 형식으로 호출 가능하게 만든다.
//	public static BookResponseDto toDto(Book bookPS) {
//		BookResponseDto dto = new BookResponseDto();
//		dto.id = bookPS.getId();
//		dto.title = bookPS.getTitle();
//		dto.author = bookPS.getAuthor();
//		return dto;	
//	}
//	

	
	
	
}
