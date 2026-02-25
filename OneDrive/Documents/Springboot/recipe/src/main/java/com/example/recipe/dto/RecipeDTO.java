package com.example.recipe.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecipeDTO {
	private String title;
	private String cuisine;
	private Double rating;

	@JsonProperty("prep_time")
	private Integer prepTime;
	
	@JsonProperty("cook_time")
	private Integer cookTime;
	
	@JsonProperty("total_time")
	private Integer totalTime;
	
	private String description;
	private List<String> ingredients;
	private List<String> instructions;
	private Map<String,String> nutrients;
	private String serves;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCuisine() {
		return cuisine;
	}
	public void setCuisine(String cuisine) {
		this.cuisine = cuisine;
	}
	public Double getRating() {
		return rating;
	}
	public void setRating(Double rating) {
		this.rating = rating;
	}
	public Integer getPrepTime() {
		return prepTime;
	}
	public void setPrepTime(Integer prepTime) {
		this.prepTime = prepTime;
	}
	public Integer getCookTime() {
		return cookTime;
	}
	public void setCookTime(Integer cookTime) {
		this.cookTime = cookTime;
	}
	public Integer getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(Integer totalTime) {
		this.totalTime = totalTime;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<String> getIngredients() {
		return ingredients;
	}
	public void setIngredients(List<String> ingredients) {
		this.ingredients = ingredients;
	}
	public List<String> getInstructions() {
		return instructions;
	}
	public void setInstructions(List<String> instructions) {
		this.instructions = instructions;
	}
	public Map<String, String> getNutrients() {
		return nutrients;
	}
	public void setNutrients(Map<String, String> nutrients) {
		this.nutrients = nutrients;
	}
	public String getServes() {
		return serves;
	}
	public void setServes(String serves) {
		this.serves = serves;
	}
	
}
