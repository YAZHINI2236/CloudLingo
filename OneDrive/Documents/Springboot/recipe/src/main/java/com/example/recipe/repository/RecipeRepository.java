package com.example.recipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.recipe.model.Recipe;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>,JpaSpecificationExecutor<Recipe> {

}
