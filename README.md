

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
|Id|`INT`            |'Unique identifier of the collection            |
|Name|`varchar(25)`            |Collection name            |
|created_at|`TIMESTAMP`|Date of collection creation|
|`last_visited_at`|`TIMESTAMP`|Date of last collection visit|
|`public`|`tinyint(1)`|can be seen by other users|
|`user_id`|`int`|user foreign key|
2. **Tweet**
 This table stores the information for each collection of tweets.

|          Name      |Data type|Description                         |
|----------------|-------------------------------|-----------------------------|
|Id|`INT`            |'Unique identifier of the tweet|
|tweet|`TEXT`            |tweet link            |
|created_at|`INT`|Date of tweet creation|
|`collection_id`|`TIMESTAMP`|ID of the collection to which the tweet belongs.|
3. **User**
 This table stores the information for each collection of tweets.

|          Name      |Data type|Description                         |
|----------------|-------------------------------|-----------------------------|
|Id|`INT`            |'Unique identifier of the user|
|username|`varchar(25)`            |user name|
|passwpord|`varchar(100)`    |user password
## Project Paths
### Routes available to interact with tweets and collections:
#### **1. List Collections**
- Path**: `/`
- **Method**: `GET`.
- **Description**: Displays a list of all collections of tweets.
- **Response**: An HTML view that lists all existing collections.
#### **2. View Collection (with tweets)**

- Path**: `/collection/{id}`.
- Method**: `GET`.
- **Description**: Displays tweets from a specific collection.
- **Parameters**: `id` - ID of the collection of tweets.
- **Response**: An HTML view that displays the tweets associated with a collection.
#### **3. Add a Tweet to a Collection**

- Path**: `/collection/{id}/add-tweet`.
- Method**: `POST`.
- **Description**: Allows adding a tweet to a specific collection.
- **Parameters**: `id` - ID of the collection to which the tweet will be added.
- **Form body**: Link of the tweet to be added.
