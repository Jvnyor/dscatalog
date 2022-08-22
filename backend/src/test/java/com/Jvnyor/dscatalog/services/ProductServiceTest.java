package com.Jvnyor.dscatalog.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.Jvnyor.dscatalog.dto.ProductDTO;
import com.Jvnyor.dscatalog.entities.Product;
import com.Jvnyor.dscatalog.factory.Factory;
import com.Jvnyor.dscatalog.repositories.ProductRepository;
import com.Jvnyor.dscatalog.services.exceptions.DatabaseException;
import com.Jvnyor.dscatalog.services.exceptions.ResourceNotFoundException;

@ExtendWith(SpringExtension.class)
class ProductServiceTest {

	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private Page<Product> page;
	private Product product;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		dependentId = 4;
		product = Factory.createProduct();
		page = new PageImpl<Product>(List.of(product));
		
		when(repository.findAll(any(Pageable.class))).thenReturn(page);
		
		when(repository.save(any(Product.class))).thenReturn(product);
		
		when(repository.findById(existingId)).thenReturn(Optional.of(product));
		
		when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
		
		doNothing().when(repository).deleteById(existingId);
		
		doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		
		doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
	}

	@Test
	void findAllPagedShouldReturnPage() {
		Pageable pageable = PageRequest.of(0, 10);
		
		Page<ProductDTO> result = service.findAllPaged(pageable);
		
		assertNotNull(result);
		verify(repository, Mockito.times(1)).findAll(pageable);
	}
	
//	void findByIdShouldReturn
	
	@Test
	void deleteShouldDoNothingWhenIdExists() {
		
		assertDoesNotThrow(() -> service.delete(existingId));;
		
		verify(repository, Mockito.times(1)).deleteById(existingId);
	}
	
	@Test
	void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		
		assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));;
		
		verify(repository, Mockito.times(1)).deleteById(nonExistingId);
	}
	
	@Test
	void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		
		assertThrows(DatabaseException.class, () -> service.delete(dependentId));;
		
		verify(repository, Mockito.times(1)).deleteById(dependentId);
	}

}
