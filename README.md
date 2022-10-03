[doc/README.md](doc/README.md)

### Build

```
./gradlew build

```

### Run 

```
./gradlew bootRun

```

1. Application starts on port 8080.
2. [http://localhost:8080/](http://localhost:8080/) should display "Hello"
3. [http://localhost:8080/tasks](http://localhost:8080/tasks) answers with task list. 
3. POST [http://localhost:8080/tasks](http://localhost:8080/tasks) registers new tasks. Body example: <code>{"input":"1234567890","pattern":"678"}</code>
   Application answers with same data structure extended with additional fields - including ID.
4. http://localhost:8080/tasks/{ID} answers with details of one task.  

Example endpoint collection for Postman: [postman_collection.json](doc/postman_collection.json)           

Notes: 
- Running application creates TDB2 directory in the root project with triplestore database (actual location triplestore is created in is the working directory).