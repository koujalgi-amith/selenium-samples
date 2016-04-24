# selenium-samples

## Requirements: 
- JDK 8 or above
- Maven v3.3 or above
- Firefox v45 or above

## Setup Java:
- Download Java SE Development Kit 8u91 (Select a Windows x64 distribution) from here: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
- Install JDK from downloaded file
- Set JAVA_HOME

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

Build:
- If you're building it for the first time on a particular, Maven downloads and resolves all the dependencies for the project. Subsequent builds will be faster as Maven would have already downloaded the necessary dependencies.