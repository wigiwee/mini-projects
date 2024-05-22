# HTTP Server in Pure Vanilla Java 🚀

Welcome to my HTTP server repository, built using pure vanilla Java without any frameworks. 🎉 This project demonstrates core Java concepts like parsing HTTP requests, handling concurrent connections, parsing headers, responding to requests, sending and receiving files, and implementing gzip compression. 🌟

## Features ✨

- Parsing HTTP 1.1 requests 🔍
- Handling concurrent connections with multithreading 🔄
- Parsing headers using CRLF 📄
- Responding with body content 📬
- Sending and receiving files 📁
- Implementing gzip compression 🗜️

## How It Works 🛠️

### Parsing HTTP 1.1 Requests 🔍

HTTP 1.1 requests are parsed using basic if-else constructs. ⚙️

### Handling Concurrent Connections 🔄

Concurrent connections are handled using multithreading. Each connection is processed in a separate thread. 🧵

### Parsing Headers 📄

Headers are parsed by reading lines until a blank line (CRLF) is encountered. 📜

### Responding with Body Content 📬

The server responds with HTTP status, headers, and body content. 📥

### Sending and Receiving Files 📁

Files are handled by reading from and writing to streams. 📤📥

### Implementing Gzip Compression 🗜️

Gzip compression is implemented using `GZIPOutputStream`. 🗜️

### Conclusion 🎯

This project demonstrates an HTTP server built with pure vanilla Java. It covers request parsing, multithreading, header parsing, response handling, file transfers, and gzip compression. Enjoy coding! 💻🚀😊

