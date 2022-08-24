package com.Jvnyor.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.Jvnyor.dscatalog.dto.ProductDTO;
import com.Jvnyor.dscatalog.factory.Factory;
import com.Jvnyor.dscatalog.services.ProductService;
import com.Jvnyor.dscatalog.services.exceptions.DatabaseException;
import com.Jvnyor.dscatalog.services.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductResource.class)
class ProductResourceTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ProductService service;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;
	private ProductDTO productDTO;
	private Page<ProductDTO> page;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 2L;
		dependentId = 4L;
		productDTO = Factory.createProductDTO();
		page = new PageImpl<ProductDTO>(List.of(productDTO));
		
		when(service.findAllPaged(any(Pageable.class))).thenReturn(page);
		
		when(service.insert(any(ProductDTO.class))).thenReturn(productDTO);
		
		when(service.findById(eq(existingId))).thenReturn(productDTO);
		
		when(service.findById(eq(nonExistingId))).thenThrow(ResourceNotFoundException.class);
		
		when(service.update(eq(existingId), any(ProductDTO.class))).thenReturn(productDTO);
		
		when(service.update(eq(nonExistingId), any(ProductDTO.class))).thenThrow(ResourceNotFoundException.class);
		
		doNothing().when(service).delete(existingId);
		
		doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
		
		doThrow(DatabaseException.class).when(service).delete(dependentId);
	}

	@Test
	void insertShouldReturnProductDTO() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = 
				mockMvc.perform(post("/products")
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").value(productDTO.getId()));
		result.andExpect(jsonPath("$.name").value(productDTO.getName()));
		result.andExpect(jsonPath("$.description").exists());
		result.andExpect(jsonPath("$.price").exists());
		result.andExpect(jsonPath("$.imgUrl").exists());
		result.andExpect(jsonPath("$.date").exists());
		result.andExpect(jsonPath("$.categories.[0].id").exists());
		result.andExpect(jsonPath("$.categories.[0].name").exists());
	}
	
	@Test
	void updateShouldReturnProductDTOWhenIdExists() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = 
				mockMvc.perform(put("/products/{id}", existingId)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").value(productDTO.getId()));
		result.andExpect(jsonPath("$.name").value(productDTO.getName()));
		result.andExpect(jsonPath("$.description").exists());
		result.andExpect(jsonPath("$.price").exists());
		result.andExpect(jsonPath("$.imgUrl").exists());
		result.andExpect(jsonPath("$.date").exists());
		result.andExpect(jsonPath("$.categories.[0].id").exists());
		result.andExpect(jsonPath("$.categories.[0].name").exists());
	}
	
	@Test
	void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = 
				mockMvc.perform(put("/products/{id}", nonExistingId)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
		result.andExpect(jsonPath("$.timestamp").exists());
		result.andExpect(jsonPath("$.status").value(404));
		result.andExpect(jsonPath("$.error").value("Resource not found"));
	}
	
	@Test
	void findAllShouldReturnPage() throws Exception {
		ResultActions result = 
				mockMvc.perform(get("/products")
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
	}
	
	@Test
	void findByIdShouldReturnProductWhenIdExists() throws Exception {
		ResultActions result = 
				mockMvc.perform(get("/products/{id}", existingId)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").value(productDTO.getId()));
		result.andExpect(jsonPath("$.name").value(productDTO.getName()));
		result.andExpect(jsonPath("$.description").exists());
		result.andExpect(jsonPath("$.price").exists());
		result.andExpect(jsonPath("$.imgUrl").exists());
		result.andExpect(jsonPath("$.date").exists());
		result.andExpect(jsonPath("$.categories.[0].id").exists());
		result.andExpect(jsonPath("$.categories.[0].name").exists());
	}
	
	@Test
	void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		ResultActions result = 
				mockMvc.perform(get("/products/{id}", nonExistingId)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
		result.andExpect(jsonPath("$.timestamp").exists());
		result.andExpect(jsonPath("$.status").value(404));
		result.andExpect(jsonPath("$.error").value("Resource not found"));
	}
	
	@Test
	void deleteShouldReturnNothingWhenIdExists() throws Exception {
		ResultActions result = 
				mockMvc.perform(delete("/products/{id}", existingId)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNoContent());
	}
	
	@Test
	void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		ResultActions result = 
				mockMvc.perform(delete("/products/{id}", nonExistingId)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	void deleteShouldReturnBadRequestWhenIdIsDependent() throws Exception {
		ResultActions result = 
				mockMvc.perform(delete("/products/{id}", dependentId)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isBadRequest());
	}

}
