# Overview

This is a Spring boot application that providers rest web service api. The web service api works on a user data source
from a rest api endpoint. the user data comes with residential location as lattitude and longitude.
 
This application provides a single service to find out all londoners (users who live within a circle with radius of 50 
miles, and circle centre is the  london centre).

The service easily becomes generic when being parameterised, e.g. make central point and distance as parameters. 

The purpose of this application is to provide a sample project that is neat, with clean structure and code, which can
work as a starting point to develop a full-fledged micro-service project by adding swagger document support, updating 
the existing service, and adding some other services (likely to have some other services although in a micro-service 
architecture). 

## The Solution

The solution to the problem of "finding users within 50 miles distance" is by searching on the elastic search engine 
with geo-distance query. The service itself consumes a rest web service that provides all users that live far from or 
near to London. When the application is starting, if the user data has not been indexed, it would index the user data 
that include a geo-point type field. Considering this service is likely working together with other services that rely 
on the elastic search engine, it is actually the right solution in the real world.

Another solution is doing an algorithm exercise, which iterator through all the users and use the latitude and longitude
of each user for the calculating of the distance to the centre of london, and filter out those users that match the 50
miles criteria. It is simpler because it need less coding and dependencies. However,in most cases, it is not consistent 
to other services in the real world, and more importantly it is not scale well. This simple solution has been used in
the test case for the verification of the solution that uses elastic search engine.

# Run the Service

The application requires JDK 8+. And the application need to connect to an instance of elastic search server, and the
easier way to run the elastic search server is via docker, so it is recommended to have docker locally. Otherwise,
you need to find a way to launch a the elastic search server locally. The version of elastic search server I ran to 
develop the application is version 7.6.2.

below is the docker command I used to launch an elastic search server locally:
```aidl
docker run -d --name es762 -p 9200:9200 -e "discovery.type=single-node" elasticsearch:7.6.2
```
Please make sure to find out the container's ip address and server port, and update the 'spring.data.elasticsearch.uri' 
value in './src/main/resources/application.properties'.

Below is the file content in the source code:
```aidl
spring.data.elasticsearch.uri=192.168.99.100:9200
spring.data.elasticsearch.dataSource.uri=https://bpdts-test-app.herokuapp.com/users
```
After launching the elastic search server, replace '192.168.99.100:9200' with whatever ip and port your local environment.

By this point, we are able to run the spring boot application. We provide two ways below.

## Run the App in IntelliJ

open the project as a maven project. ( select pom.xml as project file )
1. right click the class source file 'ShapeIntersetApplication.java'.
2. Choose to click "run 'ShapeIntersetA....main()'"

## Run the app with command line:

You need to have maven installed in your local environment.

1. open a terminal
2. run "cd 'path_to_project_directory'"
3. run "mvn spring-boot:run"

## View the Result

Open a browser, in the location bar, enter 'http://localhost:8080/api/v1/londoners'

**Three** users who live within 50 miles of central London are returned like below:

http://localhost:8080/api/v1/londoners

[
 {
   "id":554,
   "first_name":"Phyllys",
   "last_name":"Hebbs",
   "email":"phebbsfd@umn.edu",
   "ip_address":"100.89.186.13",
   "latitude":51.5489435,
   "longitude":0.3860497
 },
 {
   "id":266,
   "first_name":"Ancell",
   "last_name":"Garnsworthy",
   "email":"agarnsworthy7d@seattletimes.com",
   "ip_address":"67.4.69.137",
   "latitude":51.6553959,
   "longitude":0.0572553
  },
  {
   "id":322,
   "first_name":"Hugo",
   "last_name":"Lynd",
   "email":"hlynd8x@merriam-webster.com",
   "ip_address":"109.0.153.166",
   "latitude":51.6710832,
   "longitude":0.8078532
 }
 ]

