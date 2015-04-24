# Routing

Different routing strategies can be used, according to your application's needs.

A router can be created externally or as a self contained actor that manages the routees itself and loads routing logic and other settings from configuration.

This type of router actor comes in two distinct flavors:

Pool - The router creates routees as child actors and removes them from the router if they terminate.
Group - The routee actors are created externally to the router and the router sends messages to the specified path using actor selection, without watching for termination.

We will focus on using the configuration to configure routers.