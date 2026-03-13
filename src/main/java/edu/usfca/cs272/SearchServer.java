package edu.usfca.cs272;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class SearchServer {
    public static void start(QueryParserInterface parser, int port, WorkQueue queue)
            throws Exception {

        Server server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setResourceBase("src/main/resources/web-app");

        // Serve static files (HTML, JS)
        context.addServlet(DefaultServlet.class, "/");

        // Add SearchServlet at /search
        context.addServlet(new ServletHolder(new SearchServlet(parser, queue)), "/search");

        server.setHandler(context);
        server.start();
        server.join();
    }
}
