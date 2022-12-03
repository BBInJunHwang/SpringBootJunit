package com.spring.junitTest.service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.junitTest.domain.Book;
import com.spring.junitTest.domain.BookRepository;
import com.spring.junitTest.web.dto.request.book.BookSaveRequestDto;
import com.spring.junitTest.web.dto.response.book.BookResponseDto;

import lombok.RequiredArgsConstructor;

/**
 * 스프링 순서											(Transaction Start)
 * client -> filter -> Dispatcher Servlet -> Controller -> Service -> Repository -> Persistent Context -> DB
 * Req   (json data)								 (reqDto)	   (Entity)		(is Data exist?)		(save)
 * Res   (json data)								 (resDto)	   (Entity)		(Persistent Object)		(save)
 * 													(Transaction End) -> db write x
 * 									(Session close)	-> select o		
 * 
 * 서비스 단에서 트랜잭션은 종료 (DB save,update 등 불가능)
 * 컨트롤러 단에서 세션 종료 (select 가능)
 * 만약 컨트롤러에서 서비스단에서 영속객체를 받게되면.. 컨트롤러에서 연관관계 있는 데이터를 조회하게되면 ex) book-user 연관관계가 있어서 .getUser 등 하게 되면
 * Lazy-loading 이 일어나게 되면서 다시 DB에서 user 데이터를 select 하게된다.
 * 
 * 컨트롤러가 client로 응답하게될때 MessageConverter 발동하게 되는데 이때 자동으로 getter 메소드를 다 호출 후 json으로 변경해서 응답한다.
 * (컨트롤러 로직에서 영속객체 사용을 하지 않더라도 리턴 시 자동으로 발동) 
 * 이때 FK 연관관계 데이터를 전부 찾게된다
 * ex) book 데이터만 달라했는데 연관된 user 데이터를 전부 찾아서 리턴한다. (비효율적?)
 * 
 * 결론은 bookPS 처럼 영속객체를 리턴하게 되면 컨트롤러에서 lazy-loading이 일어날수 있기 때문에
 * 서비스 단에서 PS객체 -> DTO 변환하면 발생을 방지할 수 있다.
 * 
 * 
 * */



@RequiredArgsConstructor		// final 필드가 있을때 무조건 주입해준다.
@Service
public class BookService {
	
	private final BookRepository bookRepository;		// final 넣게 되면 객체 생성직전에 반드시 값이 들어와야함
	
	// 1. 등록
	@Transactional(rollbackFor = RuntimeException.class)
	public BookResponseDto 책등록하기(BookSaveRequestDto dto) {
		Book bookPS = bookRepository.save(dto.toEntity());		// controller 에서 dto로 받아서 repository에 Entity형태로 넘김
		
		/**
		 * bookPS 는 영속화된 객체로서 컨트롤러까지 return 하게 되면 컨트롤러 단에서 lazy loading 발생 시
		 * (open-in-view : true - 컨트롤러단까지 세션을 유지하는 형식(지연 로딩 가능) -> 수많은 변수가 발생될 수도 있음)
		 * 영속화된 객체는 서비스단에서 빠져나가지 못하게 막아야함
		 * 그렇기 때문에 Book 구조가 아닌 BookResponseDto 구조로 return 하면서 영속화를 끊어야함 (컨트롤러는 영속화 객체가 존재하면 안됨?)
		 * */
		//Book bookPS = bookRepository.save(dto.toEntity());  
		// return bookPS;									  
		
		return bookPS.toDto();	// 영속객체 -> 영속끊는 Dto로 변경해 리턴
															  
	}	
	
	// 2. 리스트 조회
	public List<BookResponseDto> 책목록보기(){			
		return bookRepository.findAll()
				.stream() // List를 stream 하면 type없이 들어오게 되고 기존 객체를 안건드리고 복제됨
				.map(Book::toDto) // .map을 하면 하나씩 순회하면서 '다른타입' 으로 변경해서(그대로도 가능)리턴해준다. toDto가 static 이면 new 객체 없이 바로 사용가능
				.collect(Collectors.toList());	// List로 변환해준다.
		
//		return bookRepository.findAll()
//		.stream() 
//		//.map((bookPS) -> new BookResponseDto.toDto(bookPS))	// map 방식 동일하게 바꿀수있지만 
//		//.map(new BookResponseDto()::toDto) 					// 자바1.8에서 나온 메서드 참조 문법임 :: , 생성자 참조 및 인스턴스, 스태틱 메서드 참조가능
//		.collect(Collectors.toList());	
		
		
		// stream 강의
		// ex) 리스트를 A A A B B 이런 stream으로 변경 후 Filter 통해서 A만 걸러내고 map 을 이용해 A타입 stream으로 다시변경 후 
		// 	   collect로 수집(변환.. 최종목적지로 전달?) 
	}
	
	
	// 3. 하나 조회
	public BookResponseDto 책한건보기(Long id) {
		
		Optional<Book> bookOP = bookRepository.findById(id);
		
		if(bookOP.isPresent()) {
			Book bookPS = bookOP.get();
			return bookPS.toDto();
		}else {
			throw new RuntimeException("해당 아이디를 찾을 수 없습니다.");
		}
	}
	
	// 4. 삭제
	
	@Transactional(rollbackFor = RuntimeException.class)
	public void 책삭제하기(Long id) {
		bookRepository.deleteById(id);	// delete from book where id = ??? 호출로 만약 없다고 해도 DB 에러는 안나지만
										// ??? 가 없다고 해서 에러가 발생하지는 않지만
		 								//	만약 id 값이 null 인경우 IllegalArgumentException 날린다.
	}
	
	// 5. 수정
	@Transactional(rollbackFor = RuntimeException.class)
	public void 책수정하기(Long id, BookSaveRequestDto dto) {
		Optional<Book> bookOP = bookRepository.findById(id);
		if(bookOP.isPresent()) {
			Book bookPS = bookOP.get();
			bookPS.update(dto.getTitle(), dto.getAuthor());	// 영속객체 값을 변경한다. 메소드 종료 후 dirty check으로 자동으로 DB flush 되면서 update commit 된다.
		}else {
			throw new RuntimeException("해당 아이디를 찾을 수 없습니다.");	// 트랜잭션 걸린 메서드는 컨트롤러로 리턴할 필요가 없다. throw 감지 처리 로직 필요
		}
	}

}