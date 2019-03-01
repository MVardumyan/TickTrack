# Postman Collections

## Backend

### Category Operations

```
{
	"info": {
		"_postman_id": "4225c3d5-278b-47f6-bdd1-fc32db84f6bc",
		"name": "TickTrack_Categories",
		"description": "Testing backend endpoints for Category operations",
		"schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
	},
	"item": [
		{
			"name": "Add Category",
			"request": {
				"method": "POST",
				"header": [
					{
						"key": "Content-Type",
						"name": "Content-Type",
						"type": "text",
						"value": "application/x-www-form-urlencoded"
					}
				],
				"body": {
					"mode": "raw",
					"raw": ""
				},
				"url": {
					"raw": "localhost:9001/backend/v1/categories/add?name=Category1",
					"host": [
						"localhost"
					],
					"port": "9001",
					"path": [
						"backend",
						"v1",
						"categories",
						"add"
					],
					"query": [
						{
							"key": "name",
							"value": "Category1"
						}
					]
				}
			},
			"response": []
		},
		{
			"name": "Get All",
			"request": {
				"method": "GET",
				"header": [],
				"body": {
					"mode": "raw",
					"raw": ""
				},
				"url": {
					"raw": "http://localhost:9001/backend/v1/categories/getAll",
					"protocol": "http",
					"host": [
						"localhost"
					],
					"port": "9001",
					"path": [
						"backend",
						"v1",
						"categories",
						"getAll"
					]
				}
			},
			"response": []
		},
		{
			"name": "Get All Active",
			"request": {
				"method": "GET",
				"header": [],
				"body": {
					"mode": "raw",
					"raw": ""
				},
				"url": {
					"raw": "http://localhost:9001/backend/v1/categories/getAllActive",
					"protocol": "http",
					"host": [
						"localhost"
					],
					"port": "9001",
					"path": [
						"backend",
						"v1",
						"categories",
						"getAllActive"
					]
				}
			},
			"response": []
		},
		{
			"name": "Deactivate Category",
			"request": {
				"method": "GET",
				"header": [],
				"body": {
					"mode": "raw",
					"raw": ""
				},
				"url": {
					"raw": "localhost:9001/backend/v1/categories/deactivate?name=testCategoryForDeactivation",
					"host": [
						"localhost"
					],
					"port": "9001",
					"path": [
						"backend",
						"v1",
						"categories",
						"deactivate"
					],
					"query": [
						{
							"key": "name",
							"value": "testCategoryForDeactivation"
						}
					]
				}
			},
			"response": []
		},
		{
			"name": "Change Category Name",
			"protocolProfileBehavior": {
				"disableBodyPruning": true
			},
			"request": {
				"method": "GET",
				"header": [],
				"body": {
					"mode": "raw",
					"raw": "{\r\n  \"categoryOperation\": {\r\n    \"categoryOpUpdateRequest\": {\r\n      \"oldName\": \"Category1\",\r\n      \"newName\": \"testCategoryForDeactivation\"\r\n    }\r\n  }\r\n}"
				},
				"url": {
					"raw": "localhost:9001/backend/v1/categories/changeName",
					"host": [
						"localhost"
					],
					"port": "9001",
					"path": [
						"backend",
						"v1",
						"categories",
						"changeName"
					]
				}
			},
			"response": []
		}
	]
}
```

### Search Operations

```
{
	"info": {
		"_postman_id": "77726be9-4e54-478b-b894-1a445e0e1e54",
		"name": "TickTrack_Search",
		"schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
	},
	"item": [
		{
			"name": "Search by Priority",
			"protocolProfileBehavior": {
				"disableBodyPruning": true
			},
			"request": {
				"method": "GET",
				"header": [],
				"body": {
					"mode": "raw",
					"raw": "{\r\n  \"searchOperation\": {\r\n    \"searchOpRequest\": {\r\n      \"priority\": [\"Low\", \"High\"]\r\n    }\r\n  }\r\n}"
				},
				"url": {
					"raw": "localhost:9001/backend/v1/search",
					"host": [
						"localhost"
					],
					"port": "9001",
					"path": [
						"backend",
						"v1",
						"search"
					]
				}
			},
			"response": []
		}
	]
}
```
