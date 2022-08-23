package com.Jvnyor.dscatalog.factory;

import java.time.Instant;

import com.Jvnyor.dscatalog.dto.ProductDTO;
import com.Jvnyor.dscatalog.entities.Category;
import com.Jvnyor.dscatalog.entities.Product;

public class Factory {

	public static Product createProduct() {
		Product product = new Product(1L, "Phone", "Good Phone", 800.0, "https://img.com/img.png", Instant.now());
		product.getCategories().add(createCategory());
		return product;
	}
	
	public static ProductDTO createProductDTO() {
		Product product = createProduct();
		return new ProductDTO(product, product.getCategories());
	}
	
	public static Category createCategory() {
		Category category = new Category(2L, "Eletronics");
		return category;
	}
}
