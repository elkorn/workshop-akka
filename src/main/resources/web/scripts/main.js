(function(App){
    var ws = new App.WebSocket({
        uri: 'ws://localhost:8080/status',
        onOpen: console.log.bind(console),
        onClose: console.log.bind(console),
        onMessage: console.log.bind(console),
        onError: console.log.bind(console)
    });
})(McBurger);