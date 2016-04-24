# selenium-samples

## Requirements: 
- A machine with Windows 7 or above
- JDK 8 or above
- Maven v3.3 or above
- Firefox v45 or above

## Setup Java:
- Download Java SE Development Kit 8u91 (Select a Windows x64 distribution) from here: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
- Install JDK from the downloaded file. This would install Java in C:\Program Files\Java\jdkxxxxxx (look up your actual installation path)
- Set JAVA_HOME environment variable with value: C:\Program Files\Java\jdkxxxxxx
- Check if it works:
```sh
javac -version
java -version
```

## Setup Maven:
- Download the archive from here: http://mirror.fibergrid.in/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.zip
- Extract the contents (will create a directory 'apache-maven-3.3.9-bin')
- Create M2_HOME and MAVEN_HOME environment variables with values as directory path where you extracted Maven archive ([parent-directory]\apache-maven-3.3.9-bin)
- Append the %M2_HOME\bin; to the 'Path' environment variable
- Check if it works by typing the following command in your command prompt:
```sh
mvn -version
```
- For detailed instructions, check here: http://www.mkyong.com/maven/how-to-install-maven-in-windows/

## Build:
- If you're building it for the first time on a particular, Maven downloads and resolves all the dependencies for the project. Subsequent builds will be faster as Maven would have already downloaded the necessary dependencies.
- You would need to modify the configuration file to run different scenarios. To do so, you must modify the config file and MAKE SURE you build it before running the app.
- The config file would be located in: [project-directory]/src/main/resources/config.properties
- Go to the [project-directory] in the command prompt and run the following command:
```sh
mvn clean compile assembly:single
```
- This will generate a JAR file under [project-directory]/target directory.
- Now, there are 2 parts of the application that you can run:
  -- Open Facebook accounts of multiple users (configured in the config file) in different browser windows, capture their profile photo, save it to a directory and close the window.
  -- Open different tabs in a single browser window, enter a URL in the URL bar and capture the resultant page as an image to a directory and close the tabs and finally the window.
  
For running the app in mode 1, run the following command:
```sh
java -cp target\selenium-samples-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.amithkoujalgi.selenium.entry.Application
```

For running the app in mode 2, run the following command:
```sh
java -cp target\selenium-samples-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.amithkoujalgi.selenium.entry.MultiTabbedApplication
```