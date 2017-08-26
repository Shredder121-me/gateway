# gateway

The software that feeds events to [amybot](https://amy.chat/) services. 

## How it works

The gateway is designed to work for basically any kind of `Networker` that is defined in [noelia](https://github.com/queer/noelia) or otherwise defined across the system, as long as all services agree on the type of `Networker` being used. For example, you can't create a `WebsocketNetworker` and expect it to work with an `HttpNetworker`. 

## Service types

The gateway is designed to feed events to services including, but not limited to, the following:

* amybot - the distributed Discord "gateway" that handles sharding the bot and connecting to Discord
* TBC...