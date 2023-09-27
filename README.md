# Run Application Instruction
1. Execute [MarsRoverApplication.java](src%2Fmain%2Fjava%2Fcom%2Fjpmorgan%2Fzhiyan%2Fassignment%2FMarsRoverApplication.java)
2. If the port number differs, replace the port number in the curl commands

# Functional Requirement 
1. A Mars Rover should be able to move forward by 1 coordinate in the current direction
2. A Mars Rover should be able to move backward by 1 coordinate  in the current direction
3. A Mars Rover should be able to rotate 90 degree clockwise  in the current direction
4. A Mars Rover should be able to rotate 90 degree anti-clockwise in the current direction
5. Two Mars Rover should not be in the same xy-coordinate

# Software
1. IntelliJ IDEA 2023.1.4 (Community Edition)
   * Runtime version: 17.0.7+10-b829.16 amd64
2. Postman (Free)
3. Windows 10

# Additional
1. Project is generated via spring initializer

# To wipe Database
1. Remove [roverdb.mv.db](src%2Fmain%2Fresources%2Froverdb.mv.db), under [resources](src%2Fmain%2Fresources)

# Task
1. Documentation
2. Unit Test
3. Functional Test

# Current System Design
```
Client <- communicate via a set of apis -> backend service <- persists data -> database
```

# API Design
## To run create Rover (`/rover/create`)
### Request (with System Generated Rover Name)
```bash
curl --location --request POST 'http://localhost:8080/rover/create' \
--header 'Content-Type: application/json' \
--data-raw '{
    "Starting Position":{
        "X Coordinate" : 1,
        "Y Coordinate" : 2,
        "Cardinal Direction": "E"
    }
}
```
### Sample Response (with System Generated Rover Name)
```
{
    "New Rover": "R0",
    "Created Position": {
        "X-Axis": 1,
        "Y-Axis": 2,
        "Cardinal Direction": "East"
    }
}
```

### Request (with Customized Rover Name)
```bash
curl --location --request POST 'http://localhost:8080/rover/create' \
--header 'Content-Type: application/json' \
--data-raw '{
    "Rover Name to create": "TEST",
    "Starting Position":{
        "X-Axis" : 0,
        "Y-Axis" : 0,
        "Cardinal Direction": "N"
    }
}'
```
### Sample Response (with Customized Rover Name)
```
{
    "New Rover": "TEST",
    "Created Position": {
        "X-Axis": 0,
        "Y-Axis": 0,
        "Cardinal Direction": "North"
    }
}
```

## 2. To get Rover position (`rover/position?roverName=R1`)
### Request
```bash
curl --location --request GET 'http://localhost:8080/rover/position?roverName=R1' \
--header 'Content-Type: application/json'
```
### Sample Response
```
{
    "Rover Name": "R1",
    "Current Position": {
        "X-Axis": 1,
        "Y-Axis": 2,
        "Cardinal Direction": "East"
    }
}
```

## 3. To move Rover API (`rover/move`)
### Request (No Collision)
```bash
curl --location --request POST 'http://localhost:8080/rover/move' \
--header 'Content-Type: application/json' \
--data-raw '{
    "Rover Name": "R0",
    "Rover Commands": "f,f,r,f,f"
}'
```
### Sample Response (No Collision)
```
{
    "Rover Name": "R0",
    "Final Position": {
        "X-Axis": 5,
        "Y-Axis": 6,
        "Cardinal Direction": "East"
    },
    "Command(s) in Request": "[f, f, r, f, f]",
    "Command(s) executed": "ffrff"
}
```

### Request (With Collision - Collision is detected if (x,y) coordinate is occupied. Cardinal direction is not considered)
```bash
curl --location --request POST 'http://localhost:8080/rover/move' \
--header 'Content-Type: application/json' \
--data-raw '{
    "Rover Name": "TEST",
    "Rover Commands": "f,f,b,f,f"
}'
```
### Sample Response (With Collision)
```
{
    "Rover Name": "TEST",
    "Final Position": {
        "X-Axis": 1,
        "Y-Axis": 0,
        "Cardinal Direction": "East"
    },
    "Command(s) in Request": "[f, f, b, f, f]",
    "Command(s) executed": "ff",
    "Command(s) not executed after Collision": "[b, f, f]"
}
```

# Unit Test and Functional Test
![Alt text](./UnitAndFunctionalTest.png?raw=true "Test Result")

# Future Improvement
1. Integrate with Swagger for API Design
   *  Easier for the backend to parse request for further processing (If we have Enum param, we need to ensure that the request is converted to Enum during parsing, and it is not straightforward)
2. Add a Custom Exception for Mars Rover Domain
3. Add HATEOAS for response
   * From Frontend perspective, the user can easily navigate from a place to another
4. Add Pub-Sub to track live movement
   * Dashboard can subscribe to the service - allowing the user to visualize the Mars Rovers in real-time
5. Store Rover's last command
   * Allow rover to move back to the previous location
6. Using cache
   * To reduce DB load
   * To decrease data fetching latency

# Future System Design
```
Client <- communicate via a set of apis -> backend service <- check in cache -> cache
                                                           <- persists data -> database
```
* Whenever the rover is moved, or, whenever there is a coordinate update, the entry will be invalidated from the cache
