/**
 * app.js
 *
 * This is the main controller for MateCo Support web app.
 */

const app = new Vue({
  el: '#mateco-support-app',

  data: {
    currentClient: null,
    clients: [],
    messages: [],
    errorMessage: null
  },

  mounted: function () {
    this.setupPunNub();
  },

  methods: {

    /**
     * Sets up the PubNub client and subscribes to the support channel.
     */
    setupPunNub: function() {
      var instance = this;

      this.pubnub = PUBNUB.init({
        subscribe_key: 'sub-c-ef77311c-57d7-11e8-b48d-6233128f1163',
        publish_key: 'pub-c-de29294d-b8f8-449b-94c6-84940ed06a97',
        uuid: 'CustomerSupport',
        ssl: true,
        error: function (error) {
          instance.errorMessage = JSON.stringify(error);
        }
      });

      this.pubnub.channel_group_list_channels({
        channel_group: "customer-support",
        callback : function(response, error){
          if (error) {
            instance.errorMessage = JSON.stringify(error);
          }
          if (response && response.channels) {
            instance.clients = response.channels;
          }
        },
        error : function (error) {
          console.log(JSON.stringify(error));
        }
      });

      this.pubnub.addListener({
        status: function(statusEvent) {
          console.log('PubNub status:', statusEvent.category);
        },
        message: function(message) {
          instance.handleNewMessage(message);
        },
        presence: function(presenceEvent) {
          // handle presence
        }
      })
    },

    /**
     * Sends a new message to clients.
     * @param text The text message..
     */
    sendMessage: function (text) {
      if (!this.hasClientSelected()) {
        return;
      }
      // TODO
    },

    /**
     * Sets the new page.
     * * @param event The click event.
     */
    setClient: function (client) {
      if (!client || this.currentClient === client) {
        return;
      }
      this.currentClient = client;
      this.messages = [];
    },

    /**
     * Checks if the given client is the current one.
     * @param client The client to check.
     * @returns {boolean} Is this the current client?
     */
    isClient: function (client) {
      return this.currentClient === client;
    },

    /**
     * Checks if there's a client selected.
     * @returns {boolean} Is there a client selected?
     */
    hasClientSelected: function (client) {
      return !!this.currentClient;
    }
  }
});