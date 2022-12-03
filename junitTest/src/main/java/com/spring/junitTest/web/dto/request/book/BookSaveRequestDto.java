package com.spring.junitTest.web.dto.request.book;

import com.spring.junitTest.domain.Book;

import lombok.Getter;
import lombok.Setter;

/**
 * 절대로 Entity로 통신하면 안된다.
 * DB 처리 시 dto 로 변환해서 처리해야한다.
 * 
 * Controller 에서 Request 받은 후 RequestDto로 변환해 Service로 값을 넘긴다.
 * Service는 RequestDto를 받은 후 Entity로 변환해 Repository로 넘긴다.
 * 
 * */

@Getter
@Setter // Controller 에서 setter 가 호출되면서 Dto에 값이 채워짐
public class BookSaveRequestDto {

	private String title;
	private String author;
	
	
	public Book toEntity() {
		return Book.builder()
				.title(title)
				.author(author)
				.build();
	}
	
}
