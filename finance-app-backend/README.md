# Finance App Backend API


## Base URL

```
http://localhost:8080
```
### database connectivity:
if using mysql barebone installation then no changes need to be made but


while using using mysql instance in a docker container then go to `spring.datasource.url` in `/src/main/resources/application.properties` file and replace the `localhost` with `127.0.0.1` 

---

## Authentication

The API uses JWT-based authentication.

All protected endpoints require:

```
Authorization: Bearer <token>
```

---

## Endpoints

### Auth

#### Signup

**note** : signin endpoint can only make `VIEWER` role users

```
POST /api/v1/auth/signin
```

Request:

```json
{
  "username": "string",
  "password": "string"
}
```

---

#### Login

**note** : for testing purpose the application creates users in the database that can be used for testing endpoints since login only makes `VIEWER` role users

```
credentials of default users
role: admin
username: admin
password: pass

role: analyst
username: analyst
password: pass

role: viewer
username: viewer
password: pass
```

```
POST /api/v1/auth/login
```

Request:


```json
{
  "username": "string",
  "password": "string"
}
```

Response:

```json
{
  "username": "username",
  "token":"token"
}

```
---

### Records

#### Create Record

```
POST /api/v1/records
```

Request:

```json
{
  "amount": 1000,
  "transactionType": "INCOME",
  "category": "FOOD",
  "date": "2026-04-04T10:00:00",
  "description": "Lunch"
}
```

---

#### Get Records (with filtering)

```
GET /api/v1/records
```
*note* : dont include filtering parameters in order to not filter

Query Parameters:

* pageNumber (default: 1)
* pageSize (default: 10)
* sortBy (default: date)
* sortSeq (ascending | descending)
* category {INVESTMENT,FOOD, RENT, TAX, FUEL}
* minAmount
* maxAmount
* type {INCOME, EXPENSE}

Response:
```json
{
    "pageNumber": 1,
    "pageSize": 10,
    "totalElements": 2,
    "totalPages": 1,
    "content": [
        {
            "amount": 2,
            "category": "FUEL",
            "date": "2022-02-02T00:00:00.000Z",
            "description": "description",
            "id": 3,
            "transactionType": "INCOME"
        },
        {
            "amount": 22,
            "category": "FOOD",
            "date": "2022-02-02T00:00:00.000Z",
            "description": "description",
            "id": 4,
            "transactionType": "EXPENSE"
        },
    ],
    "last": true
}
```

---

#### Get Record by ID

```
GET /api/v1/records/{id}
```
Response:

```json
{
  "amount": 1000,
  "transactionType": "INCOME",
  "category": "FOOD",
  "date": "2026-04-04T10:00:00",
  "description": "Lunch"
}
```

---

#### Update Record

```
PUT /api/v1/records/{id}
```

Request:

```json
{
  "amount": 1000,
  "transactionType": "INCOME",
  "category": "FOOD",
  "date": "2026-04-04T10:00:00",
  "description": "Lunch"
}
```
Response: return the old Record object

---

#### Delete Record

```
DELETE /api/v1/records/{id}
```
Response: return the deleted Record object

---

#### Search Records

```
GET /api/v1/records/search?query=keyword
```
Response: list of query output records

---

### Dashboard

#### Get Dashboard Data

```
GET /api/v1/dashboard
```

Response includes:

* totalIncome -> sum of all income
* totalExpense -> sum of all expenses
* netBalance -> +income - expense
* categoryWiseNetTotals -> category wise net totals 
* monthlyTrend -> monthly income, expense and net totals
* recentActivities -> last week records

```json
{
    "categoryWiseTotals": {
        "INVESTMENT": 1073741824,
        "FUEL": 2,
        "FOOD": 644
    },
    "monthlyTrend": {
        "2022-02": {
            "income": 668,
            "expense": 22,
            "netBalance": 646
        },
        "2026-04": {
            "income": 1073741824,
            "expense": 0,
            "netBalance": 1073741824
        }
    },
    "netBalance": 1073742470,
    "recentActivities": [
        {
            "amount": 1073741824,
            "category": "INVESTMENT",
            "date": "2026-04-04T12:24:32.180Z",
            "description": "string",
            "id": 2,
            "transactionType": "INCOME"
        }
    ],
    "totalExpense": 22,
    "totalIncome": 1073742492
}
```

---

## Data Models
there are a lot of data models used in application some are for error respones, some are for data flow across the application
### RecordDto

```json
{
  "id": 1,
  "amount": 1000,
  "transactionType": "INCOME | EXPENSE",
  "category": "INVESTMENT | FOOD | RENT | TAX | FUEL",
  "date": "ISO date-time", // yyyy-mm-dd
  "description": "string"
}
```

## Swagger

```
http://localhost:8080/swagger-ui/index.html
```
---

## Project Features
* Global Exception handling and structured Error Response
* Functionality : all requested api's implemented to its basic use to more complex usage
* clean and readable code
* scalable application
* documentation using swagger and `README.md` file
* DTO objects for across application entity sharing
* Logical implementation of dashbaord api
* Application secrets generated during runtime so need for .env file
  

## Optional Features implemented
* Authentication using tokens or sessions
* Pagination for record listing
* Search support
* API documentation
  

## TradeOffs 

### generating JWT signing secret key during application startup
```java
  @Bean
	public Key key() throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
		keyGenerator.init(256);
		return keyGenerator.generateKey();
	}
```

* here the secrets required to sign the jwt token is generated automatically during application startup
* this results in the session tokens generated in previous runtime of the application invalid
* so the users have to login again
* but these autogenerated secrets are much secure and they don't have to be stored in `.env` file they have lower vulnerablity surface area

### using spring boot
* using spring boot for this task introduced a little bit of complexity since spring boot application needs a lot of configuration and a lot of classes/files
* but i believe the robustness, readibility and scalabitliy of my work will be best reflected via spring boot
* spring boot makes it very easy for a application to expand its functionality once the basic structure is put together, 
* for example in this project, introductin few more resources is only a matter of little effort
## Tech Stack

* Spring Boot
* Spring Security (JWT)
* Spring Data JPA
* MySQL
* SpringDoc OpenAPI

  
