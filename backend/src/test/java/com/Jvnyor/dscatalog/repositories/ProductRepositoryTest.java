package com.Jvnyor.dscatalog.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.Jvnyor.dscatalog.entities.Product;
import com.Jvnyor.dscatalog.factory.Factory;

@DataJpaTest
class ProductRepositoryTest {

	@Autowired
	private ProductRepository repository;
	
	private long existingId;
	private long nonExistingId;
	private long countTotalProducts;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1;
		nonExistingId = 1000L;
		countTotalProducts = 25L;
	}
	
	@Test
	public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
		Product product = Factory.createProduct();
		product.setId(null);
		
		product = repository.save(product);
		
		assertNotNull(product.getId());
		assertEquals(countTotalProducts + 1, product.getId());
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExist() {
		
		repository.deleteById(existingId);
		
		Optional<Product> result = repository.findById(existingId);
		
		assertThat(result.isPresent()).isFalse();
	}
	
	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWHenIdDoesNotExist() {
		
		assertThrows(EmptyResultDataAccessException.class, () -> repository.deleteById(nonExistingId));
	}
	
	@Test
	public void findByIdShouldReturnNotEmptyOptionalWhenIdExist() {
		
		Optional<Product> result = repository.findById(existingId);
		
		assertThat(result.isPresent()).isTrue();
	}
	
	@Test
	public void findByIdShouldReturnEmptyOptionalWhenIdNotExist() {
		
		Optional<Product> result = repository.findById(nonExistingId);
		
		assertThat(result.isPresent()).isFalse();
	}
}
