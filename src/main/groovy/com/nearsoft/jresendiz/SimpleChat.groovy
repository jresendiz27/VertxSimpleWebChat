package com.nearsoft.jresendiz;

import io.vertx.ext.web.handler.sockjs.BridgeEventType
import io.vertx.groovy.core.eventbus.EventBus;
import io.vertx.groovy.ext.web.handler.sockjs.SockJSHandler
import java.util.concurrent.atomic.AtomicInteger
import io.vertx.groovy.ext.web.Router
import io.vertx.groovy.ext.web.handler.StaticHandler
import io.vertx.groovy.core.http.HttpServer
import io.vertx.lang.groovy.GroovyVerticle

/*
 * This Groovy source file was auto generated by running 'gradle buildInit --type groovy-library'
 * by 'jresendiz' at '10/23/16 8:48 PM' with Gradle 2.14.1
 *
 * @author jresendiz, @date 10/23/16 8:48 PM
 */

class SimpleChat extends GroovyVerticle {
    HttpServer server;
    Router router;
    EventBus eventBus;
    AtomicInteger messageCounter = new AtomicInteger();
    AtomicInteger online = new AtomicInteger();

    public void start() {
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
    }

    public void stop() {
        // This method is called whenever a verticle is closed or dies
        System.err.println("Verticle has been closed!")
    }

}