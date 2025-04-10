# Dictionary Management Application

A Spring Boot application for managing dictionary entries from multiple JSON files.

## Features

- Display dictionary entries in a paginated table using jQuery DataTables
- Sort entries by "from", "to", and "freq" fields
- Filter entries based on "from", "to", and "freq" fields
- Select and delete multiple entries
- Edit individual entries
- Select which dictionary file to display/edit from a dropdown menu
- Automatic backup of dictionary files before saving changes

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

## Setup

1. Clone or download this repository
2. Place your dictionary JSON files in the `~/dictionaries` directory
   - The application expects files named like `dictionary_DETORU.json`, `dictionary_ENTODE.json`, etc.
   - Files should follow the format shown in the example below

## Running the Application

1. Open a terminal in the project root directory
2. Run the application using Maven:
   ```
   mvn spring-boot:run
   ```
3. Open a web browser and navigate to `http://localhost:8080`

## Dictionary File Format

The application expects dictionary files in the following JSON format:

```json
{
  "language": "DETORU",
  "dictionaryEntries": [
    {
      "from": "предупредили",
      "to": "gewarnt",
      "known": false,
      "freq": 0
    },
    {
      "from": "яркую",
      "to": "hell",
      "known": false,
      "freq": 0
    }
  ]
}
```

## Configuration

The application can be configured by editing the `application.properties` file:

- `server.port`: The port the application runs on (default: 8080)
- `dictionary.files.path`: The directory where dictionary files are stored (default: user home directory + /dictionaries)

## Building from Source

To build the application from source:

1. Make sure you have Java 11+ and Maven installed
2. Clone this repository
3. Run `mvn clean package` in the project root directory
4. The built JAR file will be in the `target` directory

## Usage

1. Select a dictionary file from the dropdown menu
2. Use the DataTable to view, sort, and filter entries
3. Click the edit button to modify an entry
4. Select entries using checkboxes and click "Delete Selected" to remove them
5. Click "Add Entry" to create a new entry
