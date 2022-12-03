package com.spring.junitTest.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.spring.junitTest.web.dto.response.book.BookResponseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity // 테이블 생성
@Getter // setter 사용 금지
@NoArgsConstructor
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 1씩 증가
	private long id;
	
	@Column(length = 50, nullable = false)
	private String title;
	
	@Column(length = 20, nullable = false)
	private String author;
	
	@Builder
	public Book(long id, String title, String author) {
		this.id = id;
		this.title = title;
		this.author = author;
	}
	
	public void update(String title, String author) {
		this.title = title;
		this.author = author;
	}
	
	public BookResponseDto toDto() {
		return BookResponseDto.builder().id(id).title(title).author(author).build();
	}
	

	@Override
	public String toString() {
		return "Book [id=" + id + ", title=" + title + ", author=" + author + "]";
	}
}
