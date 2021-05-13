[Simple Binary Encoding](https://github.com/real-logic/simple-binary-encoding) plugin for [Gradle Build Tool](https://gradle.org/)
------------------------------------------
Runs [SBE Tool](https://github.com/real-logic/simple-binary-encoding/wiki/Sbe-Tool-Guide)
in a Gradle project.

Introduces a set of Gradle tasks which can be used to generate
[Simple Binary Encoding](https://github.com/real-logic/simple-binary-encoding)
codecs for Java and C++.

Compiled Java codecs are packed into a jar file.

C++ codecs are delivered in a tarball, as a header-only
[CMake](https://cmake.org/)-ready library.

Plugin site: https://plugins.gradle.org/plugin/com.github.vontikov.sbe-generator-plugin

Usage
-----

#### Plugins DSL

```Groovy

plugins {
  id "com.github.vontikov.sbe-generator-plugin" version "0.0.5"
}
```

#### Legacy plugin application


```Groovy

buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "com.github.vontikov:sbe-generator-plugin:0.0.5"
  }
}

apply plugin: "com.github.vontikov.sbe-generator-plugin"
```

#### Full configuration

```Groovy

//
// build.gradle
//
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "com.github.vontikov:sbe-generator-plugin:0.0.5"
  }
}

apply plugin: "com.github.vontikov.sbe-generator-plugin"

sbeGenerator {
  src {
    dir = 'src/main/resources/xml'
    includes = []
    excludes = []
  }

  javaCodecsDir = 'build/generated/src/main/java'
  javaClassesDir = 'build/generated/classes'

  cppCodecsDir = 'build/generated/src/main/cpp1'
  cppCmakeDir = 'build/generated/cmake-project'

  archivesDir = 'build/archives'

  javaOptions = [:]
  cppOptions = [:]

  // enable or disable validation (enabled by default)
  shouldValidate = true
}
```

Java or CPP options should be provided in Groovy map format.
For example:

```Groovy
javaOptions = ['sbe.java.generate.interfaces': 'true', 'sbe.generate.ir': 'true']
cppOptions = ['sbe.generate.ir': 'true']
```
The full list of options is [here](https://github.com/real-logic/simple-binary-encoding/wiki/Sbe-Tool-Guide)


#### SBE version
```
#
# gradle.properties
#
sbe_version=1.22.0
```

#### SBE Generator tasks

| Task                  | Description                                |
| --------------------- | ------------------------------------------ |
| sbeValidate           | Validates message declaration schemas      |
| sbeGenerateJavaCodecs | Generates Java SBE codecs                  |
| sbeCompileJavaCodecs  | Compiles Java SBE codecs                   |
| sbeJavaArchive        | Creates Java jar with SBE codecs           |
| sbeGenerateCppCodecs  | Generates C++ SBE codecs                   |
| sbeCppArchive         | Packs generated C++ codecs                 |
| sbeCppCmakeScripts    | Prepares C++ CMake scripts for SBE library |

Example
------------------------------------------

https://github.com/vontikov/sbe-example

License (See LICENSE file for full license)
------------------------------------------

Copyright 2018-2021 Vladimir Ontikov

Licensed under the MIT License

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
