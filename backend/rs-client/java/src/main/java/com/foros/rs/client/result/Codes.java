package com.foros.rs.client.result;

import java.util.HashMap;
import java.util.Map;

public class Codes {

    public static class HttpCode {
        private int code;
        private String message;
        private String description;

        HttpCode(int code, String message, String description) {
            this.code = code;
            this.message = message;
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return code + ": " + message; // + "\n\t- " + description;
        }
    }

    private static final Map<Integer, HttpCode> codes = initialize();

    private static Map<Integer, HttpCode> initialize() {
        HashMap<Integer, HttpCode> result = new HashMap<Integer, HttpCode>();
        result.put(100, new HttpCode(100, "Continue", "Continue with partial request. (New in HTTP 1.1)"));
        result.put(101, new HttpCode(101, "Switching Protocols", "Server will comply with Upgrade header and change to different protocol. (New in HTTP 1.1)"));
        result.put(200, new HttpCode(200, "OK", "Everything's fine; document follows for GET and POST requests. This is the default for servlets; if you don't use setStatus, you'll get this."));
        result.put(201, new HttpCode(201, "Created", "Server created a document; the Location header indicates its URL."));
        result.put(202, new HttpCode(202, "Accepted", "Request is being acted upon, but processing is not completed."));
        result.put(204, new HttpCode(204, "No Content", "No new document; browser should continue to display previous document. This is a useful if the user periodically reloads a page and you can determine that the previous page is already up to date. However, this does not work for pages that are automatically reloaded via the Refresh response header or the equivalent <META HTTP-EQUIV=\"Refresh\" ...> header, since returning this status code stops future reloading. JavaScript-based automatic reloading could still work in such a case, though."));
        result.put(205, new HttpCode(205, "Reset Content", "No new document, but browser should reset document view. Used to force browser to clear CGI form fields. (New in HTTP 1.1)"));
        result.put(206, new HttpCode(206, "Partial Content", "Client sent a partial request with a Range header, and server has fulfilled it. (New in HTTP 1.1)"));
        result.put(300, new HttpCode(300, "Multiple Choices", "Document requested can be found several places; they'll be listed in the returned document. If server has a preferred choice, it should be listed in the Location response header."));
        result.put(301, new HttpCode(301, "Moved Permanently", "Requested document is elsewhere, and the URL for it is given in the Location response header. Browsers should automatically follow the link to the new URL."));
        result.put(302, new HttpCode(302, "Found", "Similar to 301, except that the new URL should be interpreted as a temporary replacement, not a permanent one. Note: the message was \"Moved Temporarily\" in HTTP 1.0, and the constant in HttpServletResponse is SC_MOVED_TEMPORARILY, not SC_FOUND.Very useful header, since browsers automatically follow the link to the new URL. This status code is so useful that there is a special method for it, sendRedirect. Using response.sendRedirect(url) has a couple of advantages over doing response.setStatus(response.SC_MOVED_TEMPORARILY) and response.setHeader(\"Location\", url). First, it is easier. Second, with sendRedirect, the servlet automatically builds a page containing the link (to show to older browsers that don't automatically follow redirects). Finally, sendRedirect can handle relative URLs, automatically translating them to absolute ones."));
        result.put(303, new HttpCode(303, "See Other", "Like 301/302, except that if the original request was POST, the redirected document (given in the Location header) should be retrieved via GET. (New in HTTP 1.1)"));
        result.put(304, new HttpCode(304, "Not Modified", "Client has a cached document and performed a conditional request (usually by supplying an If-Modified-Since header indicating that it only wants documents newer than a specified date). Server wants to tell client that the old, cached document should still be used."));
        result.put(305, new HttpCode(305, "Use Proxy", "Requested document should be retrieved via proxy listed in Location header. (New in HTTP 1.1)"));
        result.put(307, new HttpCode(307, "Temporary Redirect", "This is identical to 302 (\"Found\" or \"Temporarily Moved\"). It was added to HTTP 1.1 since many browsers erroneously followed the redirection on a 302 response even if the original message was a POST, even though it really ought to have followed the redirection of a POST request only on a 303 response. This response is intended to be unambigously clear: follow redirected GET and POST requests in the case of 303 responses, only follow the redirection for GET requests in the case of 307 responses. Note: for some reason there is no constant in HttpServletResponse corresponding to this status code. (New in HTTP 1.1)"));
        result.put(400, new HttpCode(400, "Bad Request", "Bad syntax in the request."));
        result.put(401, new HttpCode(401, "Unauthorized", "Client tried to access password-protected page without proper authorization. Response should include a WWW-Authenticate header that the browser would use to pop up a username/password dialog box, which then comes back via the Authorization header."));
        result.put(403, new HttpCode(403, "Forbidden", "Resource is not available, regardless of authorization. Often the result of bad file or directory permissions on the server."));
        result.put(404, new HttpCode(404, "Not Found", "No resource could be found at that address. This is the standard \"no such page\" response. This is such a common and useful response that there is a special method for it in HttpServletResponse: sendError(message). The advantage of sendError over setStatus is that, with sendError, the server automatically generates an error page showing the error message."));
        result.put(405, new HttpCode(405, "Method Not Allowed", "The request method (GET, POST, HEAD, DELETE, PUT, TRACE, etc.) was not allowed for this particular resource. (New in HTTP 1.1)"));
        result.put(406, new HttpCode(406, "Not Acceptable", "Resource indicated generates a MIME type incompatible with that specified by the client via its Accept header. (New in HTTP 1.1)"));
        result.put(407, new HttpCode(407, "Proxy Authentication Required", "Similar to 401, but proxy server must return a Proxy-Authenticate header. (New in HTTP 1.1)"));
        result.put(408, new HttpCode(408, "Request Timeout", "The client took too long to send the request. (New in HTTP 1.1)"));
        result.put(409, new HttpCode(409, "Conflict", "Usually associated with PUT requests; used for situations such as trying to upload an incorrect version of a file. (New in HTTP 1.1)"));
        result.put(410, new HttpCode(410, "Gone", "Document is gone; no forwarding address known. Differs from 404 in that the document is is known to be permanently gone in this case, not just unavailable for unknown reasons as with 404. (New in HTTP 1.1)"));
        result.put(411, new HttpCode(411, "Length Required", "Server cannot process request unless client sends a Content-Length header. (New in HTTP 1.1)"));
        result.put(412, new HttpCode(412, "Precondition Failed", "Some precondition specified in the request headers was false. (New in HTTP 1.1)"));
        result.put(413, new HttpCode(413, "Request Entity Too Large", "The requested document is bigger than the server wants to handle now. If the server thinks it can handle it later, it should include a Retry-After header. (New in HTTP 1.1)"));
        result.put(414, new HttpCode(414, "Request URI Too Long", "The URI is too long. (New in HTTP 1.1)"));
        result.put(415, new HttpCode(415, "Unsupported Media Type", "Request is in an unknown format. (New in HTTP 1.1)"));
        result.put(416, new HttpCode(416, "Requested Range Not Satisfiable", "Client included an unsatisfiable Range header in request. (New in HTTP 1.1)"));
        result.put(417, new HttpCode(417, "Expectation Failed", "Value in the Expect request header could not be met. (New in HTTP 1.1)"));
        result.put(500, new HttpCode(500, "Internal Server Error", "Generic \"server is confused\" message. It is often the result of CGI programs or (heaven forbid!) servlets that crash or return improperly formatted headers."));
        result.put(501, new HttpCode(501, "Not Implemented", "Server doesn't support functionality to fulfill request. Used, for example, when client issues command like PUT that server doesn't support."));
        result.put(502, new HttpCode(502, "Bad Gateway", "Used by servers that act as proxies or gateways; indicates that initial server got a bad response from the remote server."));
        result.put(503, new HttpCode(503, "Service Unavailable", "Server cannot respond due to maintenance or overloading. For example, a servlet might return this header if some thread or database connection pool is currently full. Server can supply a Retry-After header."));
        result.put(504, new HttpCode(504, "Gateway Timeout", "Used by servers that act as proxies or gateways; indicates that initial server didn't get a response from the remote server in time. (New in HTTP 1.1)"));
        return result;
    }


    private Codes() {
    }

    public static HttpCode get(int code) {
        return codes.get(code);
    }
}
