# Welcome to the Project Dynamic CDI

[![Join the chat at https://gitter.im/RapidPM/dynamic-cdi](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/RapidPM/dynamic-cdi?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Here we will start with an more dynamic version of the default cdi implementations.
This project is based on the project ***ProxyBuilder***.

Please check out the HomePage for detailed informations. [www.dynamic-dependency-injection.org/](http://www.dynamic-dependency-injection.org/)


This project could be used alone, but is developed for the project rapidpm-microservice. To get the full stack please check out the following projects.

for SNAPSHOTS
+ https://github.com/RapidPM/rapidpm-dependencies (develop branch : clean install)
+ https://github.com/RapidPM/proxybuilder (develop branch : clean install)
+ https://github.com/RapidPM/dynamic-cdi (develop branch : clean install)
+ https://github.com/RapidPM/rapidpm-microservice (develop branch : clean install)
+ https://github.com/RapidPM/rapidpm-microservice-examples (develop branch : clean install)

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

## How DDI will resolve the corresponding Implementation?

if you write ``@Inject Interface``

1 ClassResolver means: 1 ClassResolver responsible for the given Interface. You can have n ClassResolver inside your classpath.
But for every Interface only one that is responsible for it.

* Interface , no Impl. -> Exception
* Interface , no Impl., 1 Producer for the Interface  -> Producer for the Interface will be used

* Interface , 1 Impl. -> will use the Impl.
* Interface , 1 Impl., 1 Producer for Impl. -> Producer for the Impl will be used
* Interface , 1 Impl., n Producer for Impl. -> Exception
* Interface , 1 Impl., 1 Producer for Interface -> Producer for the Interface will be used
* Interface , 1 Impl., n Producer for Interface -> Exception

* Interface , 1 Impl., 1 Producer for Interface , 1 Producer for Impl. -> Exception

* Interface , n Impl. -> Exception
* Interface , n Impl., 1 responsible ClassResolver -> result of the ClassResolver will be used
* Interface , n Impl., 1 Producer for Interface -> Producer for the Interface will be used
* Interface , n Impl., 1-n Producer for Impl. -> Exception

* Interface , n Impl., 1 responsible ClassResolver, 1 Producer for Interface -> Producer for the Interface will be used
* Interface , n Impl., 1 responsible ClassResolver, 1-n Producer for Impl. -> will use the resolved Class or corresponding Producer if available
* Interface , n Impl., n responsible ClassResolver -> Exception

* Interface, 1 Impl., n Producer for Impl, 1 ProducerResolver for Impl -> selected Producer from ProducerResolver
* Interface, n Impl., 1 responsible ClassResolver, 0-n Producer for every Impl, 1 ProducerResolver for every Impl -> selected Producer from ProducerResolver, for selected Impl from ClassResolver




if you write ``@Inject Impl.``

* Impl. -> will use the Impl.
* Impl., 1 Producer -> will use the Producer for the Impl.

You can combine all with a ``@Proxy(virtual=true)`` and it will use the same solution, but only as VirtualProxy.
