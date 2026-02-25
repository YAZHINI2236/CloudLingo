package com.example.recipe.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.recipe.dto.RecipePageResponse;
import com.example.recipe.model.Recipe;
import com.example.recipe.service.RecipeService;


@RestController
public class RecipeController {
	private final RecipeService recipeService;

	public RecipeController(RecipeService recipeService) {
		this.recipeService = recipeService;
	}
	
	
	@PostMapping("/upload")
	public ResponseEntity<String> uploadRecipes(@RequestParam("file") MultipartFile file) {
		try {
			recipeService.saveRecipes(file);
			return ResponseEntity.ok("Recipes uploaded successfully");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload recipes: " + e.getMessage());
		}
	}

	@GetMapping("/api/recipes")
	public ResponseEntity<RecipePageResponse> getAllRecipesByPaginationAndSorting(
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int limit) {
		try {
			return ResponseEntity.ok(recipeService.getAllRecipes(page, limit));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping("/api/recipes/search")
	public ResponseEntity<List<Recipe>> searchRecipes(@RequestParam(required = false) String calories,
			                                           @RequestParam(required = false) String title, 
			                                           @RequestParam(required = false) String cuisine,
			                                           @RequestParam(required = false, name = "total_time") String totalTime,
			                                           @RequestParam(required = false) String rating) {
		try {
			return ResponseEntity.ok(recipeService.searchRecipes(calories, title, cuisine, totalTime, rating));
		} catch (Exception e) {
			System.err.println("Error during recipe search: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
}
