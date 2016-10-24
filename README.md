# VertxSimpleWebChat

## What do I need?

* A text editor or an IDE (Intellij will be used, Atom or sublime will work to)
* Java 8 (Oracle JDK will be used, OpenJDK should work to)
* A web browser
* Node and Npm for static files resolution
* A command line (should also work on windows)

> In case you're using Sublime or Atom, would be nice to have the gradle and groovy plugin installed

------

#<a name="index"></a> Index

* [Setting all the environment and Hello World](#link-1)
* [Working with npm and static files](#link-2)
* [Having fun with the event bus](#link-3)



------

### <a name="link-1"></a> Setting all the environment and Hello World 
###### <sub>[Index](#index)</sub>
------


* Download or clone the [repository](https://github.com/jresendiz27/VertxSimpleWebChat.git).

```bash
$ git clone https://github.com/jresendiz27/VertxSimpleWebChat.git
```

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
            "--redeploy=src/**/*.*",
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

------
### <a name="link-2"></a> Working with npm and static files 
###### <sub>[Index](#index)</sub>
------

* Create a _package.json_ file via *npm init*, execute this inside _webroot_ directory.

```bash
$ npm init
```

* Follow the instructions, the file should look like this one.

``` json
{
  "name": "vertx-chat",
  "version": "1.0.0",
  "description": "",
  "main": "index.js",
  "scripts": {
    "test": "echo \"Error: no test specified\" && exit 1"
  },
  "author": "jresendiz",
  "license": "ISC"
}


```
* Install Vert.x dependency on the project, use the next command.

```bash
$ npm install --save vertx3-eventbus-client
```

> This will install all the required libraries for the project

* Modify _index.html_, add bootstrap, jquery and vert.x libraries. Add the next code inside the ```<head></head>``` tag.

```html
<script src="//cdn.jsdelivr.net/sockjs/1/sockjs.min.js"></script>
<script src="https://code.jquery.com/jquery-1.11.2.min.js"></script>
<link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet" crossorigin="anonymous">
<script src="node_modules/vertx3-eventbus-client/vertx-eventbus.js"></script>

```
* Also add the next content inside the ```<body>``` tag.

```html
<nav class="navbar navbar-default">
        <div class="container-fluid">            
            <div class="navbar-header">
                <a class="navbar-brand" href="#">
                    <img alt="Brand" src="http://vertx.io/vertx2/logo-white-big.png" width="55px" height="20px">
                </a>
            </div>
            <div class="collapse navbar-collapse">
                <form class="navbar-form navbar-nav" role="search">
                    <div class="form-group">
                        <input type="text" id="username" class="form-control" placeholder="Username">
                        <input type="text" id="content" class="form-control" placeholder="What do you wanna say?">
                    </div>
                    <button id="send" class="btn btn-default">Send</button>
                </form>
                <ul class="nav navbar-nav navbar-right">
                    Current Online
                    <p id="currentOnline">0</p>
                </ul>
            </div>
        </div>
    </nav>
    <div class="container">
        <div class="row">
            <div class="col-md-2 col-md-offset-5">
                <strong>Current live...</strong>
            </div>
            <div id="wall" class="col-md-6 col-md-offset-3">
                something
            </div>
        </div>
    </div>
```

* All the changes now should appear at [http://localhost:8080](http://localhost:8080) 

------
### <a name="link-3"></a> Having fun with the event bus 
###### <sub>[Index](#index)</sub>
------
* sample
------


