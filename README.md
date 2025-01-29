

# SpringBootTweetArchive
A web application based on Spring Boot and MySQL that allows users to save tweets in collections, they can choose whether to make it public or private.

## Features

 - **Organization by collections**: Users can store tweets in collections.
 - **Relational Database**: Uses MySQL to manage the data of  tweets and collections.
 - **Automatic deletion of inactive collections**: Collections that have not been visited or interacted with for a period of time to be determined are automatically deleted.
  
## Database Connection
This project uses MySQL as the database management system. To configure the database connection, follow the steps below:

Configuration in **application.properties**
Make sure that the src/main/resources/application.properties file contains the correct database configuration:

# MySQL database configuration

    spring.datasource.url=jdbc:mysql://localhost:3306/DATABASENAME?serverTimezone=GMT&useLegacyDatetimeCode=false
	spring.datasource.username=YOUR_NAME
	spring.datasource.password=YOUR_PASSWORD

## Database Tables

 1. **Collection**
 This table stores the information for each collection of tweets.

|          Name      |Data type|Description                         |
|----------------|-------------------------------|-----------------------------|
|Id|`INT`            |Unique identifier of the collection            |
|Name|`varchar(25)`            |Collection name            |
|created_at|`TIMESTAMP`|Date of collection creation|
|`last_visited_at`|`TIMESTAMP`|Date of last collection visit|
|`public`|`tinyint(1)`|can be seen by other users|
|`user_id`|`int`|user foreign key|
2. **Tweet**
 This table stores the information for each tweet.

|          Name      |Data type|Description                         |
|----------------|-------------------------------|-----------------------------|
|Id|`INT`            |Unique identifier of the tweet|
|tweet|`TEXT`            |tweet link            |
|created_at|`INT`|Date of tweet creation|
|`collection_id`|`TIMESTAMP`|ID of the collection to which the tweet belongs.|
3. **User**
 This table stores the information for each user.

|          Name      |Data type|Description                         |
|----------------|-------------------------------|-----------------------------|
|Id|`INT`            |Unique identifier of the user|
|username|`varchar(25)`            |user name|
|passwpord|`varchar(100)`    |user password
## Project Paths
### Endpoints Documentation

### 1. `GET /`
- **Description**: Displays the main page showing all public collections.
- **Response**: A list of all public collections.
  
---

### 2. `GET /my-collections`
- **Description**: Displays a list of the user's own collections.
- **Response**: A list of collections created by the authenticated user.
  
---

### 3. `GET /create-collection`
- **Description**: Displays the form to create a new collection.
- **Response**: A form for entering the details of the new collection.
  
---

### 4. `POST /create-collection`
- **Description**: Creates a new collection.
- **Parameters**:
  - `collectionName` (String): The name of the new collection.
  - `isPublic` (boolean, default: false): Determines whether the collection is public or private.
- **Redirects**: Redirects to the collection's page: `redirect:/collection/{idCollection}`
  
---

### 5. `GET /collection/{collectionId}`
- **Description**: Displays the contents of the collection.
- **Conditions**:
  - If the collection is public or the user is the creator, the content is shown.
  - If neither condition is met, a 404 error is returned.
- **Response**: Collection details, including its content.
  
---

### 6. `POST /collection/{collectionId}/add-tweet`
- **Description**: Adds a tweet to a specific collection.
- **Parameters**:
  - `collectionId` (Long): The ID of the collection.
  - `tweetLink` (String): The link to the tweet to be added.
- **Response**: Confirmation that the tweet has been added.
  
---

### 7. `DELETE /collection/{collectionId}/remove-tweet`
- **Description**: Removes a tweet from a specific collection.
- **Parameters**:
  - `collectionId` (Long): The ID of the collection.
  - `tweetId` (String): The ID of the tweet to be removed.
- **Response**: Confirmation that the tweet has been removed.
  
---

### 8. `DELETE /collection/{collectionId}/remove-collection`
- **Description**: Deletes a collection.
- **Parameters**:
  - `collectionId` (Long): The ID of the collection to be deleted.
- **Response**: Confirmation that the collection has been removed.
  
---

### 9. `PUT /collection/{collectionId}/change-visibility`
- **Description**: Changes the visibility of a collection (public or private).
- **Parameters**:
  - `collectionId` (Long): The ID of the collection.
- **Response**: Confirmation that the collection visibility has been updated.
