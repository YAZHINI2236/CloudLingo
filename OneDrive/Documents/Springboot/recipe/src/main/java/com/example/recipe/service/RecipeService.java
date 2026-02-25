package com.example.recipe.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.recipe.dto.RecipeDTO;
import com.example.recipe.dto.RecipePageResponse;
import com.example.recipe.model.Recipe;
import com.example.recipe.repository.RecipeRepository;

import jakarta.persistence.criteria.Expression;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
public class RecipeService {
	
	private final RecipeRepository recipeRepository;
	
	public RecipeService(RecipeRepository recipeRepository) {
		this.recipeRepository = recipeRepository;
	}
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	private static final int batchSize = 500;
	
	
		public void saveRecipes(MultipartFile file) throws Exception 
		{
			Map<String,RecipeDTO> data = objectMapper.readValue(file.getInputStream(), new TypeReference<Map<String,RecipeDTO>>(){});
			List<Recipe> batch = new ArrayList<>();
			for(RecipeDTO dto : data.values()) {
				Recipe recipe = new Recipe();
				recipe.setTitle(dto.getTitle());
				recipe.setCuisine(dto.getCuisine());
				recipe.setRating(dto.getRating());
				recipe.setPrepTime(dto.getPrepTime());
				recipe.setCookTime(dto.getCookTime());
				recipe.setTotalTime(dto.getTotalTime());
				recipe.setDescription(dto.getDescription());
				recipe.setServes(dto.getServes());
				
				recipe.setIngredients(objectMapper.writeValueAsString(dto.getIngredients()));
				recipe.setInstructions(objectMapper.writeValueAsString(dto.getInstructions()));
				recipe.setNutrients(objectMapper.writeValueAsString(dto.getNutrients()));
				
				batch.add(recipe);
				
				if(batch.size() == batchSize) {
					recipeRepository.saveAll(batch);
					batch.clear();
				}
			}
			if(!batch.isEmpty()) {
				recipeRepository.saveAll(batch);
			}
		}
		
		public RecipePageResponse getAllRecipes(int page,int limit)throws Exception 
		{
			Page<Recipe> recipes = recipeRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC,"rating")));
			for (Recipe recipe : recipes) {
				recipe.setIngredients(
						objectMapper.readValue(recipe.getIngredients(),List.class).toString()
						);
				recipe.setInstructions(
						objectMapper.readValue(recipe.getInstructions(),List.class).toString()
						);
				recipe.setNutrients(
						objectMapper.readValue(recipe.getNutrients(),Map.class).toString()
						);
			}
			RecipePageResponse response = new RecipePageResponse();
			response.setPage(page);
			response.setLimit(limit);
			response.setTotal(recipes.getTotalElements());
			response.setData(recipes.getContent());
			return response;	
		}
		
		public List<Recipe> searchRecipes(String calories, String title, String cuisine, String totalTime,String rating) throws Exception {
			Specification<Recipe> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

			if (calories != null) {
				spec = spec.and((root, query, criteriaBuilder) -> {
					Expression<String> nutrients = root.get("nutrients");
					return criteriaBuilder.like(nutrients, "%\"calories\":\"" + calories + "\"%");
				});
			}

			if (title != null) {
				spec = spec.and(
						(root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("title"), "%" + title + "%"));
			}

			if (cuisine != null) {
				spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("cuisine"),
						"%" + cuisine + "%"));
			}

			if (totalTime != null) {
				spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("totalTime"),
						"%" + totalTime + "%"));
			}

			if (rating != null) {
				try {
					Double ratingValue = Double.parseDouble(rating);
					spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder
							.equal(root.get("rating"), ratingValue));
				} catch (NumberFormatException e) {
					// If not a valid number, do a string comparison
					spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder
							.like(criteriaBuilder.function("CAST", String.class, root.get("rating")), "%" + rating + "%"));
				}
			}

			List<Recipe> recipes = recipeRepository.findAll(spec);

			for (Recipe recipe : recipes) {
				// Parse ingredients and set as string representation
				List<?> ingredients = objectMapper.readValue(recipe.getIngredients(), List.class);
				if (ingredients != null) {
					recipe.setIngredients(ingredients.toString());
				}
				
				// Parse instructions and set as string representation
				List<?> instructions = objectMapper.readValue(recipe.getInstructions(), List.class);
				if (instructions != null) {
					recipe.setInstructions(instructions.toString());
				}
				
				// Parse nutrients and set as string representation
				Map<?, ?> nutrients = objectMapper.readValue(recipe.getNutrients(), Map.class);
				if (nutrients != null) {
					recipe.setNutrients(nutrients.toString());
				}
			}

			return recipes;
		}
		
}
