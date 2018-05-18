MateCo Customer Support chat
====

This app lets you connect and chat with the Customer Support "team" of MateCo (a terribly named fictitious company).
On the "other" side, the support can answer your messages using a simple web page.

*Note*: This app only serves the purposes of development playground, learning and showcasing for myself.


General architecture
----
The app is native Android (Java), while the Customer Support client is Single Page web App (Vue.js).
These apps communicate using PubNub third party service for message exchange and presence tracking.


The client app
----
On startup, the app will create (or re-use) a uuid, which is used to identify this 'pubnub client' uniquely (and hence messages can be exchanged 'privately').
Then, client will subscribe to its 'pubnub channel' for customer support (string using pattern 'chat-<client-uuid>'.
This connection allows this client to receive/send messages to/from the Customer Support page.
The Chat Activity is then started, bringing in the views that allow for user interaction.

The customer support page
----
Since this was not the focus of this iteration, the web page is very simple. The code is built into a single HTML file so it can be opened on a local browser.
This file will first load its dependencies (Vue.js and PubNub clients). Then, the Vue App starts. It lists all known client channels, then checks if any of those clients are online.
When a client link is tapped, it opens up a simple chat view where messages can be exchanged with that specific client.

Known Issues and TODO's
----
- Currently there are no tests.
- Only Text messages are currently supported.
- No special support for bigger screens (tablets).
- No message history is persisted.
- Customer Support page needs to be hosted somewhere.
