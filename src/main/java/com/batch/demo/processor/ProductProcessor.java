package com.batch.demo.processor;

import org.springframework.batch.item.ItemProcessor;

import com.batch.demo.entity.Product;

public class ProductProcessor implements ItemProcessor<Product, Product> {

	@Override
	public Product process(Product item) throws Exception {
		double cost = item.getProdCost();
		item.setProdDisc(cost * 12 / 100.0);
		item.setProdGst(cost * 22 / 100.0);
		return item;
	}

}
