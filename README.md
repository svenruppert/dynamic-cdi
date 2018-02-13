# Welcome to the Project Dynamic CDI

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.rapidpm.dynamic-cdi/rapidpm-dynamic-cdi/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.rapidpm.dynamic-cdi/rapidpm-dynamic-cdi)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/1b039c89fb9f4baa91f5d7b906bf13f6)](https://www.codacy.com/app/sven-ruppert/dynamic-cdi?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Dynamic-Dependency-Injection/dynamic-cdi&amp;utm_campaign=Badge_Grade)
[![](http://drone.rapidpm.org:8000/api/badges/RapidPM/dynamic-cdi/status.svg?branch=develop)](http://drone.rapidpm.org:8000/api/badges/RapidPM/dynamic-cdi/status.svg?branch=develop)


Here we will start with an more dynamic version of the default cdi implementations.

Please check out the HomePage for detailed informations. [www.dynamic-dependency-injection.org/](http://www.dynamic-dependency-injection.org/)

for SNAPSHOTS
+ https://github.com/RapidPM/rapidpm-dependencies (develop branch : clean install)
+ https://github.com/RapidPM/dynamic-cdi (develop branch : clean install)

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
