package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import edu.usfca.cs272.InvertedIndex.ResultsMetadata;

/**
 * An interface for a query parser.
 *
 * @author Katherine Le
 *
 */
public interface QueryParserInterface {

	/**
	 * Method for parsing a text file line by line and performs a search on the inverted index. The results from
	 * the search are added into a data structure.
	 *
	 * @param file the file to get each query from
	 * @throws IOException if an I/O error occurs
	 */
	public default void parseQuery(Path file) throws IOException {
		try (BufferedReader br = Files.newBufferedReader(file)){
			String line;
			while ((line = br.readLine()) != null){
				parseQuery(line);
			}
		}
	}

	/**
	 * Parses one line in the text file and applies the type of search to use on the query. The results from the search
	 * are added into the data structure for search results
	 *
	 * @param line the line to produce a query from
	 */
	public void parseQuery(String line);

	/**
	 * Writes the searchResults data structure to the path in JSON format.
	 *
	 * @param path the path to write to
	 * @throws IOException if an I/O error occurs
	 */
	public void resultsToJson(Path path) throws IOException;

	/**
	 * Checks if a query exists in the search results.
	 *
	 * @param line the word or words to search for
	 * @return true if the line is in the search results and false if there is no query that matches
	 */
	public boolean hasQuery(String line);

	/**
	 * Returns the number of queries generated from the file
	 *
	 * @return the total number of queries
	 */
	public default int numQueries() {
		return viewQueries().size();
	}

	/**
	 * Returns the number of results found given a query line
	 *
	 * @param query the query line to return the number of results found
	 * @return the total amount of results that was found or 0 if the query does not exist
	 */
	public default int numResults(String query) {
		return viewResults(query).size();
	}

	/**
	 * Returns an unmodifiable set of queries that were generated from the file
	 *
	 * @return an unmodifiable set of queries
	 */
	public Set<String> viewQueries();

	/**
	 * Returns an unmodifiable list of ResultsMetadata given a line.
	 *
	 * @param line the line to get the list of ResultsMetadata from
	 * @return an unmodifiable list of ResultsMetadata or an empty list if the line does not exist in results
	 */
	public List<ResultsMetadata> viewResults(String line);

}
