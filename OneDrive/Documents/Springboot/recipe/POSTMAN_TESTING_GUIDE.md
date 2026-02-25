# Postman Testing Guide for Recipe API

## Fixed Issues
✅ Fixed 500 internal server error in search endpoint
✅ Corrected Specification initialization (was ambiguous with null)
✅ Fixed rating field comparison (Double type issue)
✅ Added proper error handling and logging

## API Endpoints

### 1. Search Recipes
**Endpoint:** `GET /api/recipes/search`

**Description:** Search recipes by optional parameters (calories, title, cuisine, total_time, rating)

**Query Parameters:**
- `calories` (optional): Filter by calories value in nutrients JSON
- `title` (optional): Filter by recipe title (partial match)
- `cuisine` (optional): Filter by cuisine type (partial match)
- `total_time` (optional): Filter by total time (partial match)
- `rating` (optional): Filter by rating (exact match if valid Double, partial if string)

**Example URLs:**

```
GET http://localhost:8080/api/recipes/search?title=pasta
GET http://localhost:8080/api/recipes/search?cuisine=Italian
GET http://localhost:8080/api/recipes/search?rating=4.5
GET http://localhost:8080/api/recipes/search?title=pasta&cuisine=Italian&rating=4.5
GET http://localhost:8080/api/recipes/search?total_time=30
GET http://localhost:8080/api/recipes/search?calories=200
```

**Request Method:** GET
**Expected Response:** 200 OK with array of Recipe objects

**Response Example:**
```json
[
  {
    "id": 1,
    "title": "Pasta Carbonara",
    "cuisine": "Italian",
    "rating": 4.5,
    "prepTime": 10,
    "cookTime": 20,
    "totalTime": 30,
    "description": "...",
    "serves": "4",
    "ingredients": "[\"pasta\", \"eggs\", \"bacon\"]",
    "instructions": "[\"Boil pasta\", \"Mix eggs with bacon\"]",
    "nutrients": "{\"calories\": \"400\", \"protein\": \"15g\"}"
  }
]
```

### 2. Get All Recipes (Paginated)
**Endpoint:** `GET /api/recipes`

**Query Parameters:**
- `page` (optional, default=1): Page number
- `limit` (optional, default=10): Records per page

**Example:**
```
GET http://localhost:8080/api/recipes?page=1&limit=10
```

### 3. Upload Recipes
**Endpoint:** `POST /upload`

**Request Type:** Form-Data
**Parameter:** `file` (multipart file containing JSON data)

**Example:**
```
POST http://localhost:8080/upload
Content-Type: multipart/form-data
Body: file: <select your JSON file>
```

## Troubleshooting

If you still get a 500 error:
1. Check the console output for detailed error messages (now enabled with System.err.println)
2. Verify all request parameters are properly formatted
3. Ensure the database is populated with recipe data
4. Check that the MySQL connection is working

## Key Code Changes Made

1. **Fixed Specification initialization** - Changed from `Specification.where(null)` to `(root, query, criteriaBuilder) -> criteriaBuilder.conjunction()`
2. **Added smart rating search** - Now handles both Double (exact match) and String (partial match) comparisons
3. **Added error logging** - Console will now show detailed error messages
4. **Proper type casting** - Used `criteriaBuilder.function("CAST", String.class, ...)` for proper database CAST operations
