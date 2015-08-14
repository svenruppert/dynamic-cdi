# Welcome to the Project Dynamic CDI

[![Join the chat at https://gitter.im/RapidPM/dynamic-cdi](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/RapidPM/dynamic-cdi?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Here we will start with an more dynamic version of the default cdi implementations.
This project is based on the project ***ProxyBuilder***.

build:
+ master:
[![Build Status](https://travis-ci.org/RapidPM/dynamic-cdi.svg?branch=master)](https://travis-ci.org/RapidPM/dynamic-cdi)
+ develop:
[![Build Status](https://travis-ci.org/RapidPM/dynamic-cdi.svg?branch=develop)](https://travis-ci.org/RapidPM/dynamic-cdi)

branch:
+ master:
[![Coverage Status - master](https://coveralls.io/repos/RapidPM/dynamic-cdi/badge.svg?branch=master)](https://coveralls.io/r/RapidPM/dynamic-cdi?branch=master)
[![Dependency Status](https://www.versioneye.com/user/projects/55a3ad19323939002100061e/badge.svg?style=flat)](https://www.versioneye.com/user/projects/55a3ad19323939002100061e)

+ develop:
[![Coverage Status - develop](https://coveralls.io/repos/RapidPM/dynamic-cdi/badge.svg?branch=develop)](https://coveralls.io/r/RapidPM/dynamic-cdi?branch=develop)
[![Dependency Status](https://www.versioneye.com/user/projects/55a3ad16323939001800069a/badge.svg?style=flat)](https://www.versioneye.com/user/projects/55a3ad16323939001800069a)

This project could be used alone, but is developed for the project rapidpm-microservice. To get the full stack please check out the following projects.

https://github.com/RapidPM/rapidpm-dependencies (master branch : clean install)
https://github.com/RapidPM/proxybuilder (developer branch : clean install)
https://github.com/RapidPM/dynamic-cdi (developer branch : clean install)
https://github.com/RapidPM/rapidpm-microservice (developer branch : clean install)
https://github.com/RapidPM/rapidpm-microservice-examples (master branch : clean install)

## SNAPSHOTS
If you are using maven you could add the following to your settings.xml to get the snapshots.

```
   <profile>
      <id>allow-snapshots</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <repositories>
        <repository>
          <id>snapshots-repo</id>
          <url>https://oss.sonatype.org/content/repositories/snapshots</url>
          <releases>
            <enabled>false</enabled>
          </releases>
          <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
          </snapshots>
        </repository>
      </repositories>
    </profile>
```
