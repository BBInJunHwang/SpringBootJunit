package com.spring.junitTest.service;

// assertJ 사용
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.spring.junitTest.domain.Book;
import com.spring.junitTest.domain.BookRepository;
import com.spring.junitTest.util.MailSender;
import com.spring.junitTest.web.dto.request.book.BookSaveRequestDto;
import com.spring.junitTest.web.dto.response.book.BookResponseDto;



@ExtendWith(MockitoExtension.class)
public class BookServiceMockTest {

	//@Autowired 사용 불가 -> Service를 올리는 어노테이션이 BookServiceMockTest 위에 명시가 되지 않기 때문 
	@InjectMocks	// 해당 서비스가 메모리에 뜰때 @Mock로 만든 가짜 객체들을 주입한다.
	private BookServiceAddMail bookServiceAddMail;
	
	
	/**
	 * @Mock 이용해서 가짜환경을 띄운다.
	 * 인터페이스를 이용한 익명클래스(인터페이스를 new 하면 -> 인터페이스는 new 불가능해서 구현체 생성)
	 * */
	@Mock
	private BookRepository bookRepository;
	
	@Mock
	private MailSender mailSender;

	
	@Test
	public void 책등록하기_테스트() {
		//given
		BookSaveRequestDto bookSaveRequestDto = new BookSaveRequestDto();
		bookSaveRequestDto.setTitle("서비스 제목");
		bookSaveRequestDto.setAuthor("서비스 저자");

		// bookRepository 에서 DB에 데이터를 조회하지 않기때문에 찾은 bookPS는 null일거고 결국 NPE가 발생한다.
		// 가설 정의 (stub 정의) -> 행동정의가 필요 없으면 NPE 발생
		when(bookRepository.save(any())).thenReturn(bookSaveRequestDto.toEntity());	// save가 실행될때 any(아무거나 넣을거임) thenReturn값으로 가정한다.
		when(mailSender.send()).thenReturn(true);	// send가 실행할때 true라고 가정한다.
		
		
		// when 
		BookResponseDto bookResponseDto = bookServiceAddMail.책등록하기(bookSaveRequestDto);
		
		// then
//		assertEquals(bookSaveRequestDto.getTitle(), bookResponseDto.getTitle());
//		assertEquals(bookSaveRequestDto.getAuthor(), bookResponseDto.getAuthor());
		
		//https://assertj.github.io/doc/ assertJ 사용법
		// then assertJ lib 사용 
		assertThat(bookResponseDto.getTitle()).isEqualTo(bookSaveRequestDto.getTitle());
		assertThat(bookResponseDto.getAuthor()).isEqualTo(bookSaveRequestDto.getAuthor());
	}
	
	@Test
	public void 책목록보기_테스트() {
		// given
		List<Book> books = new ArrayList<>();
		books.add(new Book(1L,"제목1","저자1"));
		books.add(new Book(2L,"제목2","저자2"));
		books.add(new Book(3L,"제목3","저자3"));
		
		// stub
		when(bookRepository.findAll()).thenReturn(books);
		
		// when
		List<BookResponseDto> list = bookServiceAddMail.책목록보기();
		
		// then
		assertThat(list.get(0).getTitle()).isEqualTo("제목1");
		assertThat(list.get(0).getAuthor()).isEqualTo("저자1");
		assertThat(list.get(1).getTitle()).isEqualTo("제목2");
		assertThat(list.get(1).getAuthor()).isEqualTo("저자2");
		assertThat(list.get(2).getTitle()).isEqualTo("제목3");
		assertThat(list.get(2).getAuthor()).isEqualTo("저자3");
	}
	
	@Test
	public void 책한건보기_test() {
		// given
		Long id = 1L;
		Book book = new Book(1L,"제목1","저자1");
		
		// findById 는 optional을 반환하기 때문에 감싸줘야함
		Optional<Book> bookOP = Optional.of(book);
		
		// stub
		when(bookRepository.findById(id)).thenReturn(bookOP);
		
		
		// when
		BookResponseDto bookResponseDto = bookServiceAddMail.책한건보기(id);
		
		// then
		assertThat(bookResponseDto.getTitle()).isEqualTo(book.getTitle());
		assertThat(bookResponseDto.getAuthor()).isEqualTo(book.getAuthor());
	}
	
	@Test
	public void 책수정하기_test() {
		// given
		Long id = 1L;
		BookSaveRequestDto bookSaveRequestDto = new BookSaveRequestDto();
		bookSaveRequestDto.setTitle("제목1 수정");
		bookSaveRequestDto.setAuthor("저자1 수정");
		
		// stub
		Book book = new Book(1L,"제목1","저자1");
		Optional<Book> bookOP = Optional.of(book);
		when(bookRepository.findById(id)).thenReturn(bookOP);
		
		// when
		BookResponseDto bookResponseDto = bookServiceAddMail.책수정하기(id, bookSaveRequestDto);
		
		// then
		assertThat(bookResponseDto.getTitle()).isEqualTo(bookSaveRequestDto.getTitle());
		assertThat(bookResponseDto.getAuthor()).isEqualTo(bookSaveRequestDto.getAuthor());
	}
}