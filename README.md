# FormReturn
Optical Mark Recognition Made Simple

## Releases
Releases prior to version 1.7.5 were released under a closed-source license and can be downloaded from [https://releases.formreturn.com/](https://releases.formreturn.com/). If you purchased a license and require a license key to unlock older versions, download the license generator from [https://github.com/rquast/formreturn-license](https://github.com/rquast/formreturn-license). From 1.7.5 onward, open source contributions can be made by creating a pull request. Contribution guidelines will be drafted shortly.

## Tutorials
Tutorials for using FormReturn can be found at [http://content.formreturn.com/](http://content.formreturn.com/)

## Building

FormReturn currently requires OpenJDK 8 to compile and run.

### Configuration for Linux
Install Maven and NSIS:
```
apt install nsis
```

### Configuration for Mac

To switch to Java 1.8, execute the following command in a terminal:
```
export JAVA_HOME=`/usr/libexec/java_home -v 1.8`
```

### Maven repository

Some FormReturn dependencies are no longer hosted by the maven central repository, and are configured to be obtained from [http://maven.formreturn.com/](http://maven.formreturn.com/)
