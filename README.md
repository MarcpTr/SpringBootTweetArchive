
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

# Configuración de la base de datos MySQL

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

