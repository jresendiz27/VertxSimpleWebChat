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
                <button id="clear" class="btn btn-danger">Clear</button>
            </form>
            <ul class="nav navbar-nav navbar-right">
                Number of messages:
                <p id="numberOfMessages">0</p>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                Current online:
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

        </div>
    </div>
</div>
```

* All the changes now should appear at [http://localhost:8080](http://localhost:8080) 

------
### <a name="link-3"></a> Having fun with the event bus 
###### <sub>[Index](#index)</sub>
------

* We need to import ```vertx-eventbus-js``` into our project, consider paste the next line at the end of the body tag.

```html
<script src="node_modules/vertx3-eventbus-client/vertx-eventbus.js"></script>
``` 

* Vert.x uses handlers and events, we need to create the basic handlers for our chat application, this can be on a new javascript file, or inside a ```<script>``` tag at the end of the body. This script will create a new socket connection, and all the required handlers. 

```html
<script>
        var eventBus = new EventBus("http://localhost:8080/chat");
        eventBus.onopen = function () {
            eventBus.registerHandler("numberOfMessages", function (err, message) {
                $("#numberOfMessages").html(message.body.counter);
            });
            eventBus.registerHandler("newMessage", function (err, message) {
                var content = "<div class='row'>" +
                        "<div class=\"btn-group btn-group-xs\" role=\"group\" aria-label=\"...\" style=\"width:100% !important;\">" +
                        "<button type=\"button\" class=\"btn btn-default\" style=\"width:20% !important;\">" + message.body.time + "</button>" +
                        "<button type=\"button\" class=\"btn btn-default\" style=\"width:20% !important;\">" + message.body.username + "</button>" +
                        "<button type=\"button\" class=\"btn btn-default\" style=\"width:60% !important;\">" + message.body.content + "</button>" +
                        "</div>" +
                        "</div>";
                $("#wall").append(content);
            });
            eventBus.registerHandler("online", function (err, message) {
               $("#currentOnline").html(message.body.online);
            });
        };        
        $("#clear").on("click", function (event) {
            event.preventDefault();
            $("#wall").html("");
        });        
        $("#send").on("keyup", function (event) {
            if (event.keyCode == 13 || event.which == 13) {
                send()
            }
        });
        $("#send").on("click", function (event) {
            event.preventDefault();
            send();
        });
        function send() {
            var currentMessage = {};
            var now = new Date();
            currentMessage.username = $("#username").val() || "anonymous";
            currentMessage.content = $("#content").val() || " -- -- -- -- -- --";
            currentMessage.time = now.getUTCDate() + "/" + (now.getUTCMonth() + 1) + "/" + now.getFullYear() + " " + now.getHours() + ":" + now.getMinutes();
            eventBus.send("sendMessage", currentMessage);
            $('#content').val("");
        }
    </script>
```

* Now, on the back-end, we have to configure the server and create a *bridge* on the event bus. First, we need to update the imports. Just below the package, paste the new classes to be imported.


```java
import io.vertx.ext.web.handler.sockjs.BridgeEventType
import io.vertx.groovy.core.eventbus.EventBus;
import io.vertx.groovy.ext.web.handler.sockjs.SockJSHandler
import java.util.concurrent.atomic.AtomicInteger

```
 
 * We have now to have fun with the event bus, also, we need to know how many are connected and the number of messages. Consider the next code, this should be pasted just bellow the ``` Router router; ``` declaration.
  
``` java
    EventBus eventBus;
    AtomicInteger messageCounter = new AtomicInteger();
    AtomicInteger online = new AtomicInteger();
```
 
* The body of the ```start()``` method must be updated. First of all we have to access the eventbus and create the restrictions of it. This must be the new body.
 
```groovy
 // This method is called whenever you create a verticle
 server = vertx.createHttpServer().websocketHandler()
 eventBus = vertx.eventBus();
 // This options define the allowed addresses, in and out
 def options = [
         inboundPermitteds : [
                 [address: "sendMessage"],
         ],
         outboundPermitteds: [
                 [address: "newMessage"],
                 [address: "numberOfMessages"],
                 [address: "online"],
         ],
         heartbeatInterval : 2000
 ]
 SockJSHandler sockJSHandler = SockJSHandler.create(vertx).bridge(options, { eventHandler ->
     // We use the handler to know when a websocket was created or removed
     if(eventHandler.type() == BridgeEventType.SOCKET_CREATED) {
         println "Socket Created!!!"
         online.incrementAndGet();
     }
     if(eventHandler.type() == BridgeEventType.SOCKET_CLOSED) {
         println "Socket Closed :( !!!"
         online.decrementAndGet();
     }
     // Keep moving to the next function 
     eventHandler.complete(true);
 });

 router = Router.router(vertx)
 // Define a channel named chat, this is where the EventBus will live
 router.route("/chat/*").handler(sockJSHandler) 
 router.route().handler(StaticHandler.create())

 server.requestHandler(router.&accept).listen(8080)
 // Register handlers to event bus
 eventBus.consumer("sendMessage").handler({ message ->
     eventBus.publish("newMessage", message.body());
     messageCounter.getAndIncrement()
     eventBus.publish("numberOfMessages", [counter: messageCounter.intValue()]);
 });
 // We need to know how many messages has been sent and the current users online
 vertx.setPeriodic(1000, { handler ->
     eventBus.publish("numberOfMessages", [counter: messageCounter.intValue()]);
     eventBus.send("online", [online: online.intValue()])
 });
 ```

* Now we can take a look to [http://localhost:8080](http://localhost:8080/) and see what's happening

> If you can't see the page, try killing the ```./gradlew run``` and run the application again.

----------