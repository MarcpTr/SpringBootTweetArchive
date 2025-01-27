
# SpringBootTweetArchive
A Spring Boot and MySQL-based web app that allows users to share tweets in groups, with automatic deletion of inactive groups after one year.


## Features

 - **Organization by collections**: Users can store tweets in collections.
 - **Relational Database**: Uses MySQL to manage the data of  tweets and groups.
 - **Automatic Removal of Inactive Collections**: Collections that have had no visits or interaction for a year are automatically deleted.
  
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
2. **Tweet**
 This table stores the information for each collection of tweets.

|          Name      |Data type|Description                         |
|----------------|-------------------------------|-----------------------------|
|Id|`INT`            |'Unique identifier of the tweet|
|tweet|`TEXT`            |tweet link            |
|created_at|`INT`|Date of tweet creation|
|`collection_id`|`TIMESTAMP`|ID of the collection to which the tweet belongs.|
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
- **Parameters**: `id` - ID of the group of tweets.
- **Response**: An HTML view that displays the tweets associated with a collection.
- #### **3. Add a Tweet to a Group**

- Path**: `/collection/{id}/add-tweet`.
- Method**: `POST`.
- **Description**: Allows adding a tweet to a specific collection.
- **Parameters**: `id` - ID of the collection to which the tweet will be added.
- **Form body**: Link of the tweet to be added.
