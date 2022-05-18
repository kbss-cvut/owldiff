
## How to use user interface

 **Install dependencies**
    Navigate into gui directory and install dependencies
 

    cd owldiff-gui/
    npm install


**Run locally**

    npm run dev

The web application is now running at http://localhost:8000!

**Build static files**

    npm run build

   
## How to run the API
 **Install dependencies**
 Navigate into API directory and install dependencies
 
     cd owldiff-api/
    mvn install

**Run locally**

    mvn clean spring-boot:run -f pom.xml -Plocal

The web application is now running at http://localhost:9000!

**Build package for deployment**

    mvn package

   
