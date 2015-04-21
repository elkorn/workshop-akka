(function(exports) {
    function WS(options) {
        websocket = new WebSocket(options.uri);
        websocket.onopen = options.onOpen;
        websocket.onclose = options.onClose;
        websocket.onmessage = options.onMessage;
        websocket.onerror = options.onError;
    }

    exports.WebSocket = WS;
}(window.McBurger));
