package com.spring.junitTest.service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.junitTest.domain.Book;
import com.spring.junitTest.domain.BookRepository;
import com.spring.junitTest.util.MailSender;
import com.spring.junitTest.web.dto.request.book.BookSaveRequestDto;
import com.spring.junitTest.web.dto.response.book.BookResponseDto;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor		// final 필드가 있을때 무조건 주입해준다.
@Service
public class BookServiceAddMail {
	
	private final BookRepository bookRepository;		
	
	/**
	 * mail send 예시
	 * */
	private final MailSender mailSender;
	
	// 1. 등록
	@Transactional(rollbackFor = RuntimeException.class)
	public BookResponseDto 책등록하기(BookSaveRequestDto dto) {
		Book bookPS = bookRepository.save(dto.toEntity());		
		
		if(bookPS != null) {
			if(!mailSender.send()) {
				throw new RuntimeException("메일이 전송되지 않았습니다.");
			}
		}
											  
		
		return bookPS.toDto();	// 영속객체 -> 영속끊는 Dto로 변경해 리턴
															  
	}	
	
	// 2. 리스트 조회
	public List<BookResponseDto> 책목록보기(){			
		return bookRepository.findAll()
				.stream() 
				//.map((bookPS) -> new BookResponseDto().toDto(bookPS))
				//.map((bookPS) -> bookPS.toDto())
				.map(Book::toDto) // 메서드 참조 방식 ::	List stream 내 하나를 꺼내서 Book type으로 toDto 실행한다.
				.collect(Collectors.toList());	
		
		//  메서드 참조방식 :: 을 쓰게되면 아래와 같이 하나의 dto 리턴 -> 힙이 하나, 동일한 dto를 사용하기 때문에 new 가 list 사이즈만큼된게 아니라 new는 1번만 되고
		//  toDto 메서드만 list 사이즈만큼 실행되는거다. -> 결국 한번 전달 받고 toDto만 N번 실행되는거다. -> 처음 전달받은 값 N번 실행되어서 하나값만 저장된다.
//		BookResponseDto dto = new BookResponseDto();
//		.map(dto::toDto)

		
		
//				.stream() 
//				.map(new BookResponseDto()::toDto) 
//				.collect(Collectors.toList());	
	}
	
	
	// 3. 하나 조회
	public BookResponseDto 책한건보기(Long id) {
		
		Optional<Book> bookOP = bookRepository.findById(id);
		
		if(bookOP.isPresent()) {
			Book BookPS = bookOP.get();
			return BookPS.toDto();
		}else {
			throw new RuntimeException("해당 아이디를 찾을 수 없습니다.");
		}
	}
	
	// 4. 삭제
	
	@Transactional(rollbackFor = RuntimeException.class)
	public void 책삭제하기(Long id) {
		bookRepository.deleteById(id);	
	}
	
	// 5. 수정
	@Transactional(rollbackFor = RuntimeException.class)
	public BookResponseDto 책수정하기(Long id, BookSaveRequestDto dto) {
		Optional<Book> bookOP = bookRepository.findById(id);
		if(bookOP.isPresent()) {
			Book bookPS = bookOP.get();
			bookPS.update(dto.getTitle(), dto.getAuthor());
			
			return bookPS.toDto();
		}else {
			throw new RuntimeException("해당 아이디를 찾을 수 없습니다.");	
		}
	}
	
//	@Transactional(rollbackFor = RuntimeException.class)
//	public void 책수정하기(Long id, BookSaveRequestDto dto) {
//		Optional<Book> bookOP = bookRepository.findById(id);
//		if(bookOP.isPresent()) {
//			Book bookPS = bookOP.get();
//			bookPS.update(dto.getTitle(), dto.getAuthor());
//		}else {
//			throw new RuntimeException("해당 아이디를 찾을 수 없습니다.");	
//		}
//	}
}