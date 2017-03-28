# Requirements
1. Maven
1. Java 8.0

# Adding an approach

## Build mvsimpl Project
1. Clone the repository
1. Navigate to the mvsimpl folder
1. Execute: `mvn install`

## Build the approach archetype
1. Clone the repository
1. Navigate to the /approaches/approach-archetype folder
1. Execute: `mvn install`

## Create a new approach
```
$ mvn -B archetype:generate \
-DarchetypeGroupId=edu.ucdenver \
-DarchetypeArtifactId=approach-archetype \
-DarchetypeVersion=1.0 \
-DgroupId=<your.group.id> \
-DartifactId=<your-artifact-id>
```
Rename the Approach class to the name of your approach. Update the class name on line 31 in the `/pom.xml` file and on line 1 in the `/src/main/resources/META-INF/services/edu.ucdenver.IApproachInterface` file.

## Using approach
1. In your base approach folder, execute `mvn package`
1. Copy the **uber-\*\*.jar** file to `/mvsui/bin` folder
1. Make a **${name-of-approach}.csv** file in `/mvsui/config` with any configuration you need
1. Execute the mvsui application

# Configuration Files
All configuration files are **.csv** files. The form is as follows:
```
${property-name},${property-type},${property-value}
...
```
property-name:  
The name that the Configuration class will use as the key value.

property-type:  
The datatype of the property. Currently, they can be `int`, `boolean`, or `string`.

property-value:  
The actual value to store. The value must match the type. There is no checking right now so the app will crash otherwise.

Example:  
`NumberOfKittens,int,10000000`  
This will be accessable inside the IApproachInterface implementation as `this.config.getInt("NumberOfKittens")` and will return `10000000`

# Running the Test Bed

## Build mvsimpl Project
1. Clone the repository
1. Navigate to the mvsimpl folder
1. Execute: `mvn install -DskipTests`

## Building Approaches
1. For each approach, in the base approach folder, execute `mvn package`
1. For each approach, copy the **uber-\*\*.jar** file to `/mvsui/bin` folder
1. For each approach, copy the **${name-of-approach}.properties** file in `/mvsui/config` with any configuration you need

## Build the mvsui Project
1. Navigate to the mvsui folder
1. Execute: `mvn install -DskipTests`

## Run the Test Bed
1. Execute: `java -jar target/mvsui-${version}-jar-with-dependencies.jar`
