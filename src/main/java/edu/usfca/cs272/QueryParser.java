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
 * Class parsing a file into query lines and performing a search on the inverted index. Populates the data structure of search results used for
 * writing into JSON format to a file.
 *
 * @author Katherine Le
 *
 */
public class QueryParser implements QueryParserInterface{

	/**The search function that performs a search on the inverted index*/
	private final Function<TreeSet<String>, ArrayList<ResultsMetadata>> search;

	/**The stemmer to use to process a line in a file*/
	private final SnowballStemmer stemmer;

	/** Initializes the data structure used to store query search results*/
	private final TreeMap<String, ArrayList<ResultsMetadata>> results;

	/**
	 * Constructor to initialize the search function to use
	 *
	 * @param data the inverted index
	 * @param partial boolean for checking if there is a partial flag
	 */
	public QueryParser(InvertedIndex data, boolean partial) {
		this.search = partial ? data::partialSearch : data::exactSearch;
		this.stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		this.results = new TreeMap<>();
	}

	@Override
	public void parseQuery(String line) {
		TreeSet<String> query = FileProcessor.uniqueStems(line, stemmer);
		String joined = String.join(" ", query);
		if (query.size() > 0 && !results.containsKey(joined)) {
			ArrayList<ResultsMetadata> resultsList = search.apply(query);
			results.put(joined, resultsList);
		}
	}

	@Override
	public void resultsToJson(Path path) throws IOException {
		JsonWriter.writeSearch(results, path);
	}

	@Override
	public boolean hasQuery(String line) {
		String joined = String.join(" ", FileProcessor.uniqueStems(line, stemmer));
		return results.containsKey(joined);
	}

	@Override
	public Set<String> viewQueries() {
		return Collections.unmodifiableSet(results.keySet());
	}

	@Override
	public List<ResultsMetadata> viewResults(String line) {
		String joined = String.join(" ", FileProcessor.uniqueStems(line, stemmer));
		return hasQuery(line) ? Collections.unmodifiableList(results.get(joined)) : Collections.emptyList();
	}

	@Override
	public String toString() {
		return results.toString();
	}
}
