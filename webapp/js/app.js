/**
 * app.js
 */

const PN_SUB_KEY = 'sub-c-ef77311c-57d7-11e8-b48d-6233128f1163';
const PN_PUB_KEY = 'pub-c-de29294d-b8f8-449b-94c6-84940ed06a97';
const PN_UUID    = 'CustomerSupport';

/**
 * This is the main controller for MateCo Support web app.
 */
const app = new Vue({
  el: '#mateco-support-app',

  data: {
    currentClient: null,
    clients: [],
    clientsPresence: {},
    messages: [],
    ownMessage: "",
    errorMessage: null
  },

  mounted: function () {
    this.setupPunNub();
  },

  methods: {

    /**
     * Sets up the PubNub client and subscribes to the support channel.
     */
    setupPunNub: function () {
      let instance = this;

      this.pubnub = new PubNub({
        subscribeKey: PN_SUB_KEY,
        publishKey: PN_PUB_KEY,
        uuid: PN_UUID,
        presenceTimeout: 20,
        error: function (error) {
          instance.errorMessage = JSON.stringify(error);
        }
      });

      this.pubnub.channelGroups.listChannels(
        { channelGroup: "customer-support" },
        function(status, response){
          if (status.error) {
            instance.errorMessage = JSON.stringify(status);
          }
          if (response && response.channels) {
            instance.clients = response.channels;
            instance.pollClientsPresence();
          }
        }
      );

      this.pubnub.addListener({
        status: function (statusEvent) {
          console.log('PubNub status:', statusEvent.category);
        },
        message: function (message) {
          instance.handleNewMessage(message.message);
        },
        presence: function (presenceEvent) { }
      });
    },

    /**
     * Handle a new incoming message.
     * @param message The message received.
     */
    handleNewMessage: function (message) {
      if (message && message.type) {
        this.messages.push(message);
        let chatArea = this.$refs['chatarea'];
        chatArea.scrollTop = chatArea.scrollHeight
      }
    },

    /**
     * Sends a new message to clients.
     * @param text The text message.
     */
    sendMessage: function () {
      if (!this.hasClientSelected() || !this.ownMessage) {
        return;
      }
      let message = {text: this.ownMessage, type: 'text', sender: 'CustomerService'};
      this.pubnub.publish(
        { message, channel: this.currentClient }, 
        function(status, response) {
          if (status.error) {
            console.log("publishing failed w/ status: ", status);
          }
        }
      );
      this.ownMessage = "";
    },

    /**
     * Sets the new current client.
     * @param client The client to set to.
     */
    setClient: function (client) {
      if (!client || this.currentClient === client) {
        return;
      }
      if (this.currentClient) {
        this.disconnectClient(this.currentClient);
      }
      this.messages = [];
      this.currentClient = client;
      this.connectClient(this.currentClient);
      this.$refs['chatinput'].focus();
    },

    /**
     * Connects from a clent.
     * @param client The client to connect.
     */
    connectClient: function (client) {
      if (!client) {
        return;
      }
      this.pubnub.subscribe({ 
        channels: [client] 
      });
    },

    /**
     * Disconnects from a clent.
     * @param client The client to disconnect.
     */
    disconnectClient: function (client) {
      if (!client) {
        return;
      }
      this.pubnub.unsubscribe({
        channels: [client]
      });
    },

    /**
     * Checks if the given client is the current one.
     * @param client The client to check.
     * @returns {boolean} Is this the current client?
     */
    isCurrentClient: function (client) {
      return this.currentClient === client;
    },

    /**
     * Checks if the given client is online.
     * @param client The client to check.
     * @returns {boolean} Is this client online?
     */
    isClientOnline: function (client) {
      return !!this.clientsPresence[client];
    },

    /**
     * Checks if there's a client selected.
     * @returns {boolean} Is there a client selected?
     */
    hasClientSelected: function (client) {
      return !!this.currentClient;
    },

    /**
     * Periodically checks occupancy on each client channel, to verify presence.
     */
    pollClientsPresence: function () {
      let instance = this;

      let channelHasClient = function(occupants) {
        // just filter 'me' out
        return Array.isArray(occupants)
          && occupants.filter( o => o.uuid != PN_UUID).length > 0;
      };

      let poll = function () {
        instance.pubnub.hereNow(
          { channels: this.clients, includeState: true },
          function (status, response) {
            if (status.error) {
              return;
            }
            let newPresenceState = {};
            for (let channel in response.channels) {
              newPresenceState[channel] = channelHasClient(response.channels[channel].occupants);
            }
            instance.clientsPresence = newPresenceState;
          }
        );
      };
      // infinitely poll
      setInterval(poll, 2000);
      // and once immediately
      poll();
    }
  }
});