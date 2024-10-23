package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;

import edu.usfca.cs272.InvertedIndex.ResultsMetadata;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Class for multithreaded searching of the inverted index.
 *
 * @author Katherine Le
 *
 */
public class MultiThreadQueryParser implements QueryParserInterface{

	/**The data structure for the results from a search*/
	private final TreeMap<String, ArrayList<ResultsMetadata>> results;

	/**The search function to use*/
	private final Function<TreeSet<String>, ArrayList<ResultsMetadata>> search;

	/**The work queue to use*/
	private final WorkQueue queue;

	/**
	 * Constructor to initialize a multithread query parser
	 *
	 * @param index the thread safe index to search from
	 * @param partial the boolean for the type of search
	 * @param queue the work queue to use
	 */
	public MultiThreadQueryParser(ThreadSafeIndex index, boolean partial, WorkQueue queue) {
		this.results = new TreeMap<>();
		this.search = partial ? index::partialSearch : index::exactSearch;
		this.queue = queue;
	}

	@Override
	public void parseQuery(Path file) throws IOException {
		QueryParserInterface.super.parseQuery(file);
		queue.finish();
	}

	@Override
	public void parseQuery(String line) {
		queue.execute(new Task(line));
	}

	@Override
	public void resultsToJson(Path path) throws IOException {
		synchronized (results) {
			JsonWriter.writeSearch(results, path);
		}
	}

	@Override
	public boolean hasQuery(String line) {
		SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		String joined = String.join(" ", FileProcessor.uniqueStems(line, stemmer));
		synchronized (results) {
			return results.containsKey(joined);
		}
	}

	@Override
	public Set<String> viewQueries() {
		synchronized (results) {
			return Collections.unmodifiableSet(results.keySet());
		}
	}

	@Override
	public List<ResultsMetadata> viewResults(String line) {
		SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		String joined = String.join(" ", FileProcessor.uniqueStems(line, stemmer));
		synchronized (results) {
			return hasQuery(line) ? Collections.unmodifiableList(results.get(joined)) : Collections.emptyList();
		}
	}

	@Override
	public String toString() {
		synchronized (results) {
			return results.toString();
		}
	}

	/**
	 * Class that assigns work to a thread.
	 */
	private class Task implements Runnable {

		/**The line to turn into a query string*/
		private final String line;

		/**
		 * Construstor for initializing a new task.
		 *
		 * @param line the line as a query string
		 */
		public Task(String line) {
			this.line = line;
		}

		@Override
		public void run() {
			SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
			TreeSet<String> query = FileProcessor.uniqueStems(line, stemmer);
			String joined = String.join(" ", query);

			synchronized (results) {
				if (query.size() == 0 || results.containsKey(joined)) {
					return;
				}
				results.put(joined, null);
			}

			ArrayList<ResultsMetadata> resultsList = search.apply(query);
			synchronized (results) {
				results.put(joined, resultsList);
			}
		}
	}
}
