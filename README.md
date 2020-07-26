# FormReturn
Optical Mark Recognition Made Simple

## Download
Download the latest release from: [https://github.com/rquast/formreturn/releases](https://github.com/rquast/formreturn/releases)

### Releases
Releases prior to version 1.7.5 were released under a closed-source license and can be downloaded from [https://releases.formreturn.com/](https://releases.formreturn.com/). If you purchased a license and require a license key to unlock older versions, download the license generator from [https://github.com/rquast/formreturn-license](https://github.com/rquast/formreturn-license). From 1.7.5 onward, open source contributions can be made by creating a pull request. Contribution guidelines will be drafted shortly.

### Code Signing Issues
Open source releases are not codesigned. 

If you're using a Mac, you will need to download, extract and right-click the application file and select "open" to run. This will bypass gatekeeper. 

If you're using Windows, you will get a message saying that the software is from an unknown publisher.

### Tutorials
Tutorials for using FormReturn can be found at [http://content.formreturn.com/](http://content.formreturn.com/)

---

## Building & Developing

FormReturn currently requires OpenJDK 8 and Maven to compile and run.

### Requirements

NSIS is required for buiding and packaging the windows binaries and installer. Install NSIS (ubuntu example below):
```
apt install nsis
```

### Setting Java 1.8 on Mac

To switch to Java 1.8, execute the following command in a terminal:
```
export JAVA_HOME=`/usr/libexec/java_home -v 1.8`
```

### Maven repository

Some FormReturn dependencies are no longer hosted by the maven central repository, and are configured to be obtained from [http://maven.formreturn.com/](http://maven.formreturn.com/)

### Building & Packaging
To build, from the root of the project, run:
```
mvn clean install
```
This will build the formreturn library that is then installed in your local maven repository.

To create a package distribution, from the "installer" directory, run:
```
mvn clean package
```
In the "installer/target" directory, this will create a Mac "app", a windows exe installer and a Linux jar installer.

