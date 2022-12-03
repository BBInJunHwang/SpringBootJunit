package com.spring.junitTest.domain;

import org.springframework.data.jpa.repository.JpaRepository;

// jpaRepository extends 하면 @Component 생략가능

public interface BookRepository extends JpaRepository<Book, Long> {

}
