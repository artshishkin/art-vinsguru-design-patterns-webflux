[![CircleCI](https://circleci.com/gh/artshishkin/art-vinsguru-design-patterns-webflux.svg?style=svg)](https://circleci.com/gh/artshishkin/art-vinsguru-design-patterns-webflux)
[![codecov](https://codecov.io/gh/artshishkin/art-vinsguru-design-patterns-webflux/branch/main/graph/badge.svg?token=U5YRYVEM7N)](https://codecov.io/gh/artshishkin/art-vinsguru-design-patterns-webflux)
![Java CI with Maven](https://github.com/artshishkin/art-vinsguru-design-patterns-webflux/workflows/Java%20CI%20with%20Maven/badge.svg)
[![GitHub issues](https://img.shields.io/github/issues/artshishkin/art-vinsguru-design-patterns-webflux)](https://github.com/artshishkin/art-vinsguru-design-patterns-webflux/issues)
![Spring Boot version][springver]
![Project licence][licence]
# art-vinsguru-design-patterns-webflux

Tutorial - Design Patterns With Spring WebFlux - from Vinoth Selvaraj (Udemy)

#### Section 2: Gateway Aggregator Pattern

##### 11.2 Docker image for external services

1. Build image
    - `docker build -t artarkatesoft/vinsguru-external-services .`
2. Push image
    - `docker push artarkatesoft/vinsguru-external-services`

[springver]: https://img.shields.io/badge/dynamic/xml?label=Spring%20Boot&query=%2F%2A%5Blocal-name%28%29%3D%27project%27%5D%2F%2A%5Blocal-name%28%29%3D%27parent%27%5D%2F%2A%5Blocal-name%28%29%3D%27version%27%5D&url=https%3A%2F%2Fraw.githubusercontent.com%2Fartshishkin%2Fart-vinsguru-design-patterns-webflux%2Fmaster%2Fwebflux-patterns%2Fpom.xml&logo=Spring&labelColor=white&color=grey
[licence]: https://img.shields.io/github/license/artshishkin/art-vinsguru-design-patterns-webflux.svg

####  Installing

-  Dependency
```xml
<dependency>
  <groupId>net.shyshkin.study</groupId>
  <artifactId>webflux-patterns</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```
- Repository
```xml
<repositories>
  <repository>
    <id>art_shishkin-snapshot</id>
    <url>https://packagecloud.io/art_shishkin/snapshot/maven2</url>
    <releases>
      <enabled>true</enabled>
    </releases>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>
```
