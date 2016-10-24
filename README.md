# VertxSimpleWebChat

## What do I need?

* A text editor or an IDE (Intellij will be used, Atom or sublime will work to)
* Java 8 (Oracle JDK will be used, OpenJDK should work to)
* A web browser
* Node and Npm for static files resolution
* A command line (should also work on windows)

> In case you're using Sublime or Atom, would be nice to have the gradle and groovy plugin installed

------

# Instructions

## Step 0:

* Download or clone the [repository](https://github.com/jresendiz27/VertxSimpleWebChat.git).

```bash
$ git clone https://github.com/jresendiz27/VertxSimpleWebChat.git
```
## Step 1: Adding dependencies and setting all the environment

* Make sure you have *JDK 8* installed.

```bash
$ java -version
```

* Open the project using your text editor or IDE.

* Add Vert.x dependencies into *build.gradle* file at the root of the project, this needs to be inside the *dependencies* block.

```gradle
compile "io.vertx:vertx-core:3.3.3"
compile "io.vertx:vertx-lang-groovy:3.3.3"
compile 'io.vertx:vertx-web:3.3.3'
```

* Add a new task to _build.gradle_ at the end of the file, this will help us to run the application

```gradle
// Vert.x core launcher, needed to execute the application
mainClassName = "io.vertx.core.Launcher"
// Main verticle to be deployed
def mainVerticle = "groovy:com.nearsoft.jresendiz.SimpleChat"

run {
    args = [
            'run', mainVerticle,
            "--redeploy=src/**/*.groovy",
            "--launcher-class=$mainClassName",
            "--on-redeploy=./gradlew classes"
    ]
}

```


* Create the _webroot_ directory inside src/main/resources/

```bash
$ mkdir src/main/resources/webroot
```

* Create _index.html_ file inside the _webroot_ directory. Consider the next example:

```html
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <title>Hello World</title>
    </head>
    <body>
        <h1>Hello World</h1>        
    </body>
</html>
```

* Modify _SimpleChat.groovy_ file inside the _src/main/groovy/com/nearsoft/jresendiz/_ folder. Consider the next content.
```groovy
package com.nearsoft.jresendiz

import io.vertx.groovy.ext.web.Router
import io.vertx.groovy.ext.web.handler.StaticHandler
import io.vertx.groovy.core.http.HttpServer
import io.vertx.lang.groovy.GroovyVerticle

class SimpleChat extends GroovyVerticle {
  HttpServer server;
  Router router;

  public void start() {
      // This method is called whenever you create a verticle
      server = vertx.createHttpServer()

      router = Router.router(vertx)
      router.route().handler(StaticHandler.create())

      server.requestHandler(router.&accept).listen(8080)
  }

  public void stop() {
      // This method is called whenever a verticle is closed or dies
      System.err.println("Verticle has been closed!")
  }
}
```

* Run the example using the next command on your terminal.
``` bash
$ ./gradlew run
```

* Go to [http://localhost:8080/](http://localhost:8080/)

## 
