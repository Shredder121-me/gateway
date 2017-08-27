# gateway

The software that feeds events to [amybot](https://amy.chat/) services. 

## How it works

The gateway is designed to work for basically any kind of `Networker` that is defined in [noelia](https://github.com/queer/noelia) or otherwise defined across the system, as long as all services agree on the type of `Networker` being used. For example, you can't create a `WebsocketNetworker` and expect it to work with an `HttpNetworker`.

Messages sent inside the system are meant to be proxied through the gateway; this is descriped in "gateway messages" below. "Real" gateway messages - as opposed to proxied messages - are described in the "ops" section.

## Ops

(This section is totally inspired by how Discord's API works)

Messages that originate from the gateway have a code, or "op," that uniquely identifies what kind of message it is. For instance, a "reboot service" op would be different from a "identify" op.

The gateway supports the following ops:

| opcoode | opcode name   | opcode description |
| ------- | ------------- | ------------------ |
| 0       | IDENTIFY      | The opcode for when a service connects to the gateway. The gateway should respond with a message that tells whether identification succeeded. |
| 1       | SHUTDOWN      | The opcode that tells a service to shut down. Should only be sent by the gateway. |
| 2       | REBOOT        | The opcode that tells a service to reboot. Should only be sent by the gateway. |
| 10      | SHARD_CONNECT | A Discord-specific opcode that is sent when a bot shard connects to the gateway. Used to determine how the bot should shard. |
| 11      | SHARD_ACCEPT  | A Discord-specific opcode that tells a shard that it's accepted, and what shard ID / how many shards it's to use when connecting to Discord's API. |
| 12      | SHARD_DEATH   | A Discord-specific opcode that a shard sends to the gateway to let it know that the shard died, and can be returned to the pool for future reconnects. |

Opcode messages should look like the following:

```javascript
{
    "s": "source",
    "t": "gateway:opcode",
    "d": {
        "opcode": "opcode value goes here",
        // Any other data goes here
    }
}
```
but are otherwise plain noelia messages. 

## Gateway proxy

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
  "t": "gateway-proxy:source:something:whatever:etc", // This is parsed by check() predicates
  "d": {
    // This can be literally anything you want, as long as you write the
    // predicates and handlers for it.  
  }
}
```

Basically, the topic is just extended a little bit to make it apparent that the message is proxied. Messages that originate from the gateway have the topics that one would expect; only messages sent to the gateway from some other service have this "annotation." Such messages are "namespaced" as `gateway-proxy` instead of just `gateway` so that they don't get confused with actual gateway messages. 

## Service types

The gateway is designed to feed events to services including, but not limited to, the following:

* amybot - the distributed Discord "gateway" that handles sharding the bot and connecting to Discord
* TBC...