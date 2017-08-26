# gateway

The software that feeds events to [amybot](https://amy.chat/) services. 

## How it works

The gateway is designed to work for basically any kind of `Networker` that is defined in [noelia](https://github.com/queer/noelia) or otherwise defined across the system, as long as all services agree on the type of `Networker` being used. For example, you can't create a `WebsocketNetworker` and expect it to work with an `HttpNetworker`.

## Gateway messages

Messages sent to the gateway are effectively proxied between services; this is done so that, among other things, it is possible to get an accurate evaluation of how many messages pass through the entire system without having to instrument every component.

Proxied messages are exactly the same as normal messages, but the topics are different. For example, when a normal message may look like

```Javascript
{
  "s": "source", // To know where it came from, since it's the likely 
                 // place for sending responses
  "t": "something:whatever:etc", // This is parsed by check() predicates
  "d": {
    // This can be literally anything you want, as long as you write the
    // predicates and handlers for it.  
  }
}
```
the proxied equivalent looks like
```Javascript
{
  "s": "source", // To know where it came from, since it's the likely 
                 // place for sending responses
  "t": "gateway:source:something:whatever:etc", // This is parsed by check() predicates
  "d": {
    // This can be literally anything you want, as long as you write the
    // predicates and handlers for it.  
  }
}
```

Basically, the topic is just extended a little bit to make it apparent that the message is proxied. Messages that originate from the gateway have the topics that one would expect; only messages sent to the gateway from some other service have this "annotation."

## Service types

The gateway is designed to feed events to services including, but not limited to, the following:

* amybot - the distributed Discord "gateway" that handles sharding the bot and connecting to Discord
* TBC...