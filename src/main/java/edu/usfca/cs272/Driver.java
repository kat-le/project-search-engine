package edu.usfca.cs272;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Path;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author Katherine Le
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2023
 */
public class Driver {

	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {

		CommandLineParser parser = new CommandLineParser(args);
		InvertedIndex data;
		QueryParserInterface query;
		WorkQueue queue;
		ThreadSafeIndex safe;
		int crawls = 1;
		
		if (args.length >= 1) {
			if (parser.hasFlag("-threads") || parser.hasFlag("-crawl") || parser.hasFlag("-html")) {
				int threads = parser.getInteger("-threads", 5);
				queue = threads < 1 ? new WorkQueue() : new WorkQueue(threads);
				safe = new ThreadSafeIndex();
				query = new MultiThreadQueryParser(safe, parser.hasFlag("-partial"), queue);
				data = safe;
				crawls = parser.getInteger("-crawl", 1);
			}
			else {
				data = new InvertedIndex();
				query = new QueryParser(data, parser.hasFlag("-partial"));
				queue = null;
				safe = null;
			}

			if (parser.hasFlag("-html")) {
				WebCrawler crawler = new WebCrawler(crawls, queue);
				String seedURL = parser.getString("-html");

				try {
					crawler.processHTML(safe, seedURL);
				}
				catch (MalformedURLException | URISyntaxException e) {
					System.out.println("Unable to crawl from the url: " + seedURL);
				}
			}

			if (parser.hasFlag("-text")) {
				Path textPath = parser.getPath("-text");
				try {
					if (queue != null && safe != null) {
						MultiThreadBuilder.buildInvertedIndex(safe, textPath, queue);
					}
					else {
						InvertedIndexBuilder.buildInvertedIndex(data, textPath);
					}
				}
				catch  (IOException | NullPointerException e) {
					System.out.println("Unable to traverse from the path: " + textPath);
				}
			}

			if (parser.hasFlag("-query")) {
				Path queryPath = parser.getPath("-query");
				try {
					query.parseQuery(queryPath);
				}
				catch (IOException | NullPointerException e) {
					System.out.println("Unable to traverse from the path: " + queryPath);
				}
			}

			if (queue != null) {
				queue.shutdown();
			}

			if (parser.hasFlag("-counts")) {
				Path countsPath = parser.getPath("-counts", Path.of("counts.json"));
				try {
					data.countsToJson(countsPath);
				}
				catch (IOException e) {
					System.out.println("Unable to write word counts to the path: " + countsPath);
				}
			}

			if (parser.hasFlag("-index")) {
				Path indexPath = parser.getPath("-index", Path.of("index.json"));
				try {
					data.indexToJson(indexPath);
				}
				catch (IOException e) {
					System.out.println("Unable to write inverted index to the path: " + indexPath);
				}
			}

			if (parser.hasFlag("-results")) {
				Path resultsPath = parser.getPath("-results", Path.of("results.json"));
				try {
					query.resultsToJson(resultsPath);
				}
				catch (IOException e) {
					System.out.println("Unable to write inverted index to the path: " + resultsPath);
				}
			}
		}
	}
}
