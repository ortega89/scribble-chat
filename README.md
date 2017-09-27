# Overview

## What is Scribble Chat?
Scribble Chat is a simple graphical chat with a common canvas.

## How does it work?
Scribble Chat is a client-server application based on socket connections.
The project includes both client and server capabilities, so any user can run a server and let other users connect to it.

## How do I run the chat?
When you run the Server, it logs the local IPv4 addresses and a public one so you can see them in your console.

The Client requires an IP address of the server.
Use one of local addresses to connect your client to a machine within your local area network (LAN).
For Internet connection, use the public IP address.

## How does the client work?
If everything is fine, the client will request your name and then show the Swing GUI.
The interface is pretty simple: a canvas, a palette, a Clear button, and a list of users.


# Troubleshooting

## My client cannot reach the remote server via the Internet.
Maybe it's because the port on the server machine is not open.
You might want to tweak your router settings and open the port (34567).
To find out the port number, see the Constants class.

## Your code is full of errors, you've got missing libraries and invalid structures.
Please make sure you have the latest version of Java.

In order to resolve missing dependencies, import your local project as a Gradle project.
If you have Eclipse, consider getting a Gradle plugin from Eclipse Marketplace.
If you have IntelliJ IDEA, the Gradle plugin must already be there.

### Why Gradle?
Because GitHub. I would not like to store third party libraries here if they can be retrieved automatically by Gradle.

And Maven is clumsy.

## I found a reproducible bug, and I'd like to report it.
Feel free to send me an email with a list of steps to reproduce the bug.
You may also attach a short screen capture video which reveals the bug.
