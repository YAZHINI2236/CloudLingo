package com.example.recipe.dto;

import java.util.List;

import com.example.recipe.model.Recipe;

public class RecipePageResponse {
	private int page;
	private int limit;
	private long total;
	private List<Recipe> data;
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public List<Recipe> getData() {
		return data;
	}
	public void setData(List<Recipe> data) {
		this.data = data;
	}
}
