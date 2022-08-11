package com.Jvnyor.dscatalog.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Jvnyor.dscatalog.dto.CategoryDTO;
import com.Jvnyor.dscatalog.repositories.CategoryRepository;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;
	
	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll() {
		return repository.findAll()
				.stream()
				.map(c -> new CategoryDTO(c))
				.collect(Collectors.toList());
	}
}