You are required to build a CLI tool that starts a caching proxy server, it will forward requests to the actual server and cache the responses. If the same request is made again, it will return the cached response instead of forwarding the request to the server.
Requirements

User should be able to start the caching proxy server by running a command like following:
```
caching-proxy --port <number> --origin <url>
```
<li>
    --port is the port on which the caching proxy server will run.
</li>
<li>
    --origin is the URL of the server to which the requests will be forwarded.
</li>

For example, if the user runs the following command:

```
caching-proxy --port 3000 --origin http://dummyjson.com
```
The caching proxy server should start on port 3000 and forward requests to http://dummyjson.com.

Taking the above example, if the user makes a request to http://localhost:3000/products, the caching proxy server should forward the request to http://dummyjson.com/products, return the response along with headers and cache the response. Also, add the headers to the response that indicate whether the response is from the cache or the server.
```
# If the response is from the cache
X-Cache: HIT

# If the response is from the origin server
X-Cache: MISS
```

If the same request is made again, the caching proxy server should return the cached response instead of forwarding the request to the server.

You should also provide a way to clear the cache by running a command like following:

```
caching-proxy --clear-cache
```

After building the above project, you should have a good understanding of how caching works and how you can build a caching proxy server to cache responses from other servers.


source https://roadmap.sh/projects/caching-server