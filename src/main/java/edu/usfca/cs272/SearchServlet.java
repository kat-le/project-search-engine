package edu.usfca.cs272;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;

import com.google.gson.Gson; // For converting Java objects to JSON

public class SearchServlet extends HttpServlet {
    private final QueryParserInterface parser;
    private final WorkQueue queue;
    private final Gson gson;

    public SearchServlet(QueryParserInterface parser, WorkQueue queue) {
        this.parser = parser;
        this.queue = queue;
        this.gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String queryLine = request.getParameter("q");
        List<SearchResult> resultsList = new ArrayList<>();

        if (queryLine != null && !queryLine.isBlank()) {
            parser.parseQuery(queryLine);
            if (queue != null) {
                queue.finish();
            }

            var results = parser.viewResults(queryLine);
            for (InvertedIndex.ResultsMetadata result : results) {
                resultsList.add(new SearchResult(
                        result.getLocation(),
                        result.getScore(),
                        result.getMatches()
                ));
            }
        }

        String json = gson.toJson(resultsList);
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    // Inner class for JSON serialization
    private static class SearchResult {
        private final String location;
        private final double score;
        private final int matches;

        public SearchResult(String location, double score, int matches) {
            this.location = location;
            this.score = score;
            this.matches = matches;
        }
    }
}
