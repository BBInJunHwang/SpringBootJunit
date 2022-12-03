package com.spring.junitTest.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;


/**
 * 
 * 테스트란?
 * 
 * 스프링 부트 실행 시 스프링 부트 전체 설정 및 전체 실행
 * 
 * 테스트 실행 시 스프링 부트가 아닌 테스트 환경이 실행됨
 * 이때 controller, service, repository 모두 띄우는걸 통합 테스트라고 한다.
 * 
 * 테스트 환경에서는 repository 처럼 필요한 환경만 메모리에 띄우는 단위테스트를 한다.
 * 
 * 
 * junit 안에 메서드가 여러개가 있을때 동시 실행 시 순서 보장이 안된다.
 * ex) 등록 > 리스트 > 1개조회 > 삭제 이런 순으로 나열되어져 있다고 해도 순서대로 동작 보장안됨
 * 순서 보장을 위해서는 Order() 어노테이션이 필요하다.
 * 
 * 테스트 메서드 하나 실행 후 종료되면 데이터가 초기화 된다.
 * @DataJpaTest 안에 @Transactional 가 @Test 메서드 하나 끝나고 rollback? 초기화 시켜준다.
 * 
 * @Transactional 실행 후 데이터 초기화 당시 pk (auto increment) 되고있는 값이 초기화가 안된다.
 * 그래서 책한권보기_test에서 1L 가져와라 할 실제로 등록,리스트 테스트에서 1,2..등 데이터가 들어가고 삭제되었지만 auto increment 증가로 인해 No value present 뜨는거다.
 * 
 * Junit Test
 * 메서드 실행   -> 메서드 종료  -> RollBack (Junit 아닌경우 default 는 commit 임, runtime Exception 시 rollback )
 * (트랜잭션 시작) (트랜잭션 종료)
 * 
 * 
 * */

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)	// @Order 아라비아 숫자 순서로 테스트 실행 활성화
@DataJpaTest // DB 관련된 component 만 메모리에 로드 된다, Controller,service 안 띄운다.
public class BookRepositoryTest {
	
	@Autowired	// 테스트 시에는 @Autowired 를 쓰는게 좋다더라.., 생성자 주입말고 
	private BookRepository bookRepository;
	
	/**
	 * 
	 * 순서
	 * 1. 등록
	 * 2. 리스트 조회
	 * 3. 하나 조회
	 * 4. 삭제
	 * 5. 수정
	 * 
	 * 테스트를 위해 준비해야하는 절차
	 * 1. given(데이터 준비) -> controller, service 메모리에 띄는게 아니기 때문에 직접 데이터(request)를 준비해야한다.
	 * 2. when (테스트 실행)
	 * 3. then (검증)
	 * 
	 * */
	
	//@BeforeAll //  테스트 시작전에 한번만 실행 
	@BeforeEach // 각 테스트 시작 전에 한번씩 시작함 -> 메서드 하나씩만 트랜잭션이 묶인다. (책등록, 책목록, 책한권... 개별로 묶임)
	public void 데이터준비() {
		System.out.println("Before Data Ready----------------------------------");
		String title = "junit5";
		String author = "ijhwang01";
		Book book = Book.builder().title(title).author(author).build();
		bookRepository.save(book);
	} // [데이터 준비 + 책등록]트랜잭션묶임  [데이터 준비 + 책목록보기]트랜잭션묶임  [데이터 준비 + 책한권보기]트랜잭션묶임
	
	
	@Test
	@Order(0)
	public void 책등록Test() {
		
		// 1. given (데이터 준비)
		String title = "junit";
		String author = "ijhwang";
		
		// Repository 에서는 이미 Service에서 Dto -> Entity로 변환되어서 값이 들어왔다 가정하고 Book 빌더한다.
		Book book = Book.builder().title(title).author(author).build();
		
		// 2. when (테스트 실행)
		Book bookPS = bookRepository.save(book);	// client에서 받은 book이 DB에 저장이 되고, PK id가 생성되며, save 함수가 DB 저장된 Book을 리턴한다.(DB에 저장된 값과 동기화 된 데이터)
		// ps -> persistence 영구적 저장 의미, book 은 client 통해서 받은값이지만, bookPS는 DB로 부터 영구적으로(영속화 된 데이터) 저장된 값을 의미
		
		// 3. then (검증) 실행 후 debug console 에서 녹색으로 뜨면 검증성공 assertEquals 성공 의미
		//				 실제 하나하나 값을 찍어봐도 되지만.. 비지니스 로직이 엄청 복잡 가정하에 전부 확인 할 수 없으니 assertEquals 통과 시 성공으로 가정하고 넘어간다.
		assertEquals(title, bookPS.getTitle());	// 기대값, 실제값 순서로 같은지 assertEquals 로 검증한다.
		assertEquals(author, bookPS.getAuthor());
		
	}// @Test 후 트랜잭션 실행 후 저장안하고 날려버림
	
	
	@Test	
	@Order(1)
	public void 책목록보기_test() {
		// given
		String title = "junit5";
		String author = "ijhwang01";
		// when
		List<Book> booksPS = bookRepository.findAll();
		
		// then
		assertEquals(3L, booksPS.get(0).getId());			// @BeforeEach 데이터 + 등록 데이터 해서 2개 들어간 후 rollback 해도 auto increment는 증가되기 때문에 3번 부터 insert됨
		assertEquals(title, booksPS.get(0).getTitle());
		assertEquals(author, booksPS.get(0).getAuthor());
	}
	
	
	
	
	// table drop 하는 이유
	// 1. 테스트 등록, 목록 시작~종료~트랜잭션 종료 (등록 하기 @BeforeEach 데이터 + 테스트 등록 데이터 해서 2개   +  목록 보기 @BeforeEach 데이터 해서 총 3개가 이미 들어갔음, 롤백 처리로 데이터가 남아있진 않지만 auto_increment값이 증가되버렸음)
	// 2. 테스트 한권 보기 	시작 (한권 보기 @BeforeEach 데이터 등록 시 4번 데이터로 들어감 -> auto increment는 시퀀스처럼 트랜잭션 롤백과 별개로 돌아오지 않음)
	@Sql("classpath:db/tableInit.sql") // classpath -> src/main/resources 영역, 해당 메서드 실행전에 sql 쿼리 실행, 동시 junit 실행시 pk 꼬임 방지
	@Test
	@Order(2)
	public void 책한권보기_test() {
		// given
		String title = "junit5";
		String author = "ijhwang01";
		
		// when
		//Book bookPS = bookRepository.findById(4L).get();
		Book bookPS = bookRepository.findById(1L).get();
		
		// then
		assertEquals(title, bookPS.getTitle());
		assertEquals(author, bookPS.getAuthor());
	}

	@Sql("classpath:db/tableInit.sql") 
	@Test
	@Order(3)
	public void 책삭제_test() {
		//given
		//Long id = 5L;	
		Long id = 1L;
		
		bookRepository.deleteById(id);
		
		//then
		assertFalse(bookRepository.findById(id).isPresent()); // false 이면 성공 -> isPresent는 Optional 안에 뭔가가 있으면 true 반환 없으면 false 반환 
	}
	
	
	
	// commit 이란 트랜잭션동안 메모리에 발생된 데이터를 HDD 로 옮김
	// rollback 이란 트랜잭션동안 메모리 데이터를 날림 
	
	// Junit update flow
	//  1. @BeforeEach (트랜잭션 시작) 1건 save 시작	  		-> 아직 commit 이 아니기 때문에 메모리에 존재(insert 데이터)  
	//  2. @Sql update 메소드 호출 직전에 table drop이 일어남 	-> 메모리가 아닌 HDD에 있는 테이블을 날리는거임(auto_increment 초기화), 메모리에는 데이터 그대로 유지, 테이블 drop과는 상관없음
	//  3. update 테스트 (트랜잭션 유지) 1건 update     		-> 트랜잭션이 유지되기 때문에 메모리에 존재하는 데이터를 update
	//  4. 메소드 종료										-> 트랜잭션 종료, Rollback -> 메모리 롤백
	@Sql("classpath:db/tableInit.sql") 
	@Test
	@Order(4)
	public void 책수정_test() {
		// given
		//Long id = 6L;
		Long id = 1L;
		String title = "junit5 수정";
		String author ="ijhwang01 수정";
		Book book = new Book(id,title,author);
		
		//when
		// 원래라면 dirty check를 해야하지만, before 데이터 insert 가 미리 되버리기 때문에 불가능 , save 에 1L 가 있기때문에 merge 수행
		Book bookPS = bookRepository.save(book);
		
//		bookRepository.findAll().stream()
//		.forEach(b -> {
//			System.out.println("-----------------------");
//			System.out.println(b.getId());
//			System.out.println(b.getTitle());
//			System.out.println(b.getAuthor());
//			System.out.println("-----------------------");
//			});

		//then
		assertEquals(id, bookPS.getId());
		assertEquals(title, bookPS.getTitle());
		assertEquals(author, bookPS.getAuthor());
	}
	
	
}