package edu.usfca.cs272;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Data structure class for storing stem counts and an inverted index
 *
 * @author Katherine Le
 *
 */
public class InvertedIndex {
	/**
	 * Initializes the tree map used to store stem counts
	 */
	private final TreeMap<String, Integer> counts;

	/**
	 * Initializes the tree map used to store an inverted index
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;


	/**
	 * Constructor that initializes the counts, inverted index, and search results maps
	 */
	public InvertedIndex() {
		this.counts = new TreeMap<>();
		this.index = new TreeMap<>();
	}

	/**
	 * Writes the data structure for counts to a JSON file by calling the writeObject method
	 *
	 * @param path the path to write to
	 * @throws IOException if an IO error occurs
	 */
	public void countsToJson(Path path) throws IOException {
		JsonWriter.writeObject(counts, path);
	}

	/**
	 * Writed the inverted index as a pretty Json object
	 *
	 * @param path the path to write the inverted index to
	 * @throws IOException if an IO error occurs
	 */
	public void indexToJson(Path path) throws IOException {
		JsonWriter.writeInvertedIndex(index, path);
	}

	/**
	 * Returns the data structure of counts
	 *
	 * @return the map of counts
	 */
	public Map<String, Integer> getCounts() {
		return Collections.unmodifiableMap(counts);
	}

	/**
	 * Adds stem and its locations to the index map. Adds to the counts of each location.
	 *
	 * @param word the word to add
	 * @param location the location of the stem to add
	 * @param position the position in the file where the stem was found
	 */
	public void add(String word, String location, int position) {
		index.putIfAbsent(word, new TreeMap<>());
		index.get(word).putIfAbsent(location, new TreeSet<>());
		index.get(word).get(location).add(position);

		if (position > counts.getOrDefault(location, 0)) {
			counts.put(location, position);
		}
	}

	/**
	 * Method for adding a list of stems and its locations to the inverted index.
	 *
	 * @param words the list of words to add
	 * @param location the file the stem is located in
	 * @param position the position in the file the stem is found in
	 */
	public void addAll(List<String> words, String location, int position) {
		for(String word : words) {
			add(word, location, position++);
		}
	}

	/**
	 * Method for adding a list of stems and its locations to the inverted index
	 *
	 * @param words the list of words to add
	 * @param location the file the word is located in
	 */
	public void addAll(List<String> words, String location) {
		addAll(words, location, 1);
	}

	/**
	 * Adds all of the local inverted index into the inverted index.
	 *
	 * @param local the inverted index to add
	 */
	public void addAll(InvertedIndex local) {
		for (var wordEntry : local.index.entrySet()) {
			String localWord = wordEntry.getKey();
			var localLocations = wordEntry.getValue();
			var thisLocations = this.index.get(localWord);

			if (thisLocations == null) {
				this.index.put(localWord, localLocations);
			}
			else {
				for (var locationEntry : localLocations.entrySet()) {
					String localLocation = locationEntry.getKey();
					var localPositions = locationEntry.getValue();

					if (!thisLocations.containsKey(localLocation)) {
						thisLocations.put(localLocation, localPositions);
					}
					else {
						thisLocations.get(localLocation).addAll(localPositions);
					}
				}
			}
		}
		for (var countEntry : local.counts.entrySet()) {
			String localLocation = countEntry.getKey();
			var localCount = countEntry.getValue();
			var thisCount = this.counts.get(localLocation);

			if (thisCount == null) {
				this.counts.put(localLocation, localCount);
			}
			else {
				if (localCount > thisCount) {
					this.counts.put(localLocation, localCount);
				}
			}
		}
	}

	/**
	 * Checks if the index map already has a stem in it
	 *
	 * @param word the word to check if it exists
	 * @return true is the map contains the stem and false if it doesn't exist
	 */
	public boolean hasWord(String word) {
		return index.containsKey(word);
	}

	/**
	 * Looks up a location in the inverted index given a word. Returns false if no word exists
	 *
	 * @param word the word to check if it is in a location
	 * @param location the location of the word to check
	 * @return true if the index has the location for that word
	 */
	public boolean hasLocation(String word, String location) {
		return hasWord(word) && index.get(word).containsKey(location);
	}

	/**
	 * Looks up a location in the counts map.
	 *
	 * @param location the location to check for
	 * @return true if the location is found in counts
	 */
	public boolean hasLocation(String location) {
		return counts.containsKey(location);
	}

	/**
	 * Looks up a position in the index given a word and location. Returns false if no location or word exists.
	 *
	 * @param word the word to find its position for
	 * @param location the location the word is in
	 * @param position the position to check for
	 * @return true if the position of the word is found in the file
	 */
	public boolean hasPosition(String word, String location, int position) {
		return hasLocation(word, location) && index.get(word).get(location).contains(position);
	}

	/**
	 * Returns the number of words in the inverted index.
	 *
	 * @return the number of words in the index
	 */
	public int numWords() {
		return index.size();
	}

	/**
	 * Returns the number of locations that a word is found in. Returns 0 is word does not exist.
	 *
	 * @param word the word to get the number of locations for
	 * @return a number for the amount of locations in the index
	 */
	public int numLocations(String word) {
		return hasWord(word) ? index.get(word).size() : 0;
	}

	/**
	 * Returns the number of locations in counts
	 *
	 * @return a number for the total amount of locations
	 */
	public int numLocations() {
		return counts.size();
	}

	/**
	 * Returns the number of positions that a word is found in the file. Returns 0 if cannot get word or location
	 *
	 * @param word the word to check the amount of positions for
	 * @param location the location to check the positions in
	 * @return a number for the total amount of positions in the location
	 */
	public int numPositions(String word, String location) {
		return hasLocation(word, location) ? index.get(word).get(location).size() : 0;
	}

	/**
	 * Returns the positions of the stems in the file
	 *
	 * @param word the word to get its position from
	 * @param location the file where the stem is located
	 * @return an unmodifiable collection of the positions of the word
	 */
	public Set<Integer> viewPositions(String word, String location) {
		return hasLocation(word, location) ? Collections.unmodifiableSet(index.get(word).get(location)) : Collections.emptySet();
	}

	/**
	 * Returns the locations of the specified stem from the index map
	 *
	 * @param word the word to get its location from
	 * @return the unmodifiable set of locations for a word
	 */
	public Collection<String> viewLocations(String word) {
		return hasWord(word) ? Collections.unmodifiableCollection(index.get(word).keySet()) : Collections.emptyList();
	}

	/**
	 * Returns the collection of words in the inverted index.
	 *
	 * @return an unmodifiable collection words in the index
	 */
	public Collection<String> viewWords() {
		return Collections.unmodifiableCollection(index.keySet());
	}

	/**
	 * Returns the string representation of the index map
	 *
	 * @return the index map as a string
	 */
	@Override
	public String toString() {
		return index.toString();
	}

	/**
	 * Returns the string representation of the counts map
	 *
	 * @return the counts map as a string
	 */
	public String countsToString() {
		return counts.toString();
	}


	/**
	 * Searches through the index for each word in the query and sorts the results by its metadata
	 *
	 * @param query the set of words to search for
	 * @return the ordered list of results from the search
	 */
	public ArrayList<ResultsMetadata> exactSearch(Set<String> query) {
		ArrayList<ResultsMetadata> results = new ArrayList<>();
		Map<String, ResultsMetadata> lookup = new HashMap<>();

		for (String word : query) {
			if (hasWord(word)) {
				createResults(word, results, lookup);
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * Performs a partial search on the inverted index given a query line. Searches through inverted index and sorts the results
	 * based on its metadata
	 *
	 * @param query the query containing the words to do a partial search for
	 * @return an arraylist of ResultsMetadata that came from the partial search
	 */
	public ArrayList<ResultsMetadata> partialSearch(Set<String> query) {
		ArrayList<ResultsMetadata> results = new ArrayList<>();
		Map<String, ResultsMetadata> lookup = new HashMap<>();

		for (String word : query) {
			for (var iterator : index.tailMap(word).entrySet()) {
				String key = iterator.getKey();

				if (!key.startsWith(word)) {
					break;
				}
				createResults(key, results, lookup);
			}
		}
		Collections.sort(results);
		return results;
	}
	/**
	 * Creates a new ResultsMetadata or updates an already existing ResultsMetadata's matches and scores by searching through a
	 * lookup map.
	 *
	 * @param key the word in the inverted index
	 * @param results the array to populate with results from the search
	 * @param lookup the map to use to check if a location already exists for a ResultsMetadata
	 */
	private void createResults(String key, ArrayList<ResultsMetadata> results, Map<String, ResultsMetadata> lookup ) {
		for (String location : index.get(key).keySet()) {
			var result = lookup.get(location);

			if (result == null) {
				result = new ResultsMetadata(location);
				results.add(result);
				lookup.put(location, result);
			}
			result.update(key);
		}
	}

	/**
	 * Class for sorting the metadata of a search result.
	 *
	 * @author Katherine Le
	 *
	 */
	public class ResultsMetadata implements Comparable<ResultsMetadata>{

		/** The file that contains the word from the query*/
		private final String location;

		/**The number of matches found in the file*/
		private int matches;

		/**The score from the search*/
		private double score;

		/**
		 * Initializes a ResultMetadata object
		 *
		 * @param location the file the word is found in
		 */
		public ResultsMetadata(String location) {
			this.location = location;
			this.matches = 0;
			this.score = 0;
		}

		/**
		 * Method for adding to the total amount of matches
		 *
		 * @param key the file location
		 */
		private void update(String key) {
			this.matches += index.get(key).get(location).size();
			this.score = (double) this.matches / counts.get(this.location);
		}

		/**
		 * Gets this search result's score
		 *
		 * @return the score
		 */
		public double getScore() {
			return this.score;
		}

		/**
		 * Gets this search result's location.
		 *
		 * @return the file location
		 */
		public String getLocation() {
			return this.location;
		}

		/**
		 * Checks if the search result contains a specified location
		 *
		 * @param location the location to check
		 * @return true if the location for this search result matches the specified location
		 */
		public boolean hasLocation(String location) {
			return this.location.equals(location);
		}

		/**
		 * Returns the number of matches for this search result.
		 *
		 * @return the total amount of matches found for this search.
		 */
		public int getMatches() {
			return this.matches;
		}

		@Override
		public String toString() {
			return "where: " + this.location + " matches: " + this.matches + " score:" + this.score;
		}

		/**
		 * Writes an array of ResultsMetadata in pretty JSON format.
		 *
		 * @param writer the writer to use
		 * @param results the array of ResultsMetadata to write in JSON format
		 * @throws IOException if an I/O error occurs
		 */
		public static void toJson(Writer writer, Collection<? extends ResultsMetadata> results) throws IOException {
			ArrayList<TreeMap<String, Object>> resultsList = new ArrayList<>();
			for (var result : results) {
				resultsList.add(formatResults(result));
			}
			JsonWriter.writeArrayObjects(resultsList, writer, 1);
		}

		/**
		 * Formats one ResultMeta data to be written as a JSON object.
		 *
		 * @param result the results to format
		 * @return an object to be written as a JSON object
		 */
		public static TreeMap<String, Object> formatResults(ResultsMetadata result) {
			TreeMap<String, Object> resultsFormatted = new TreeMap<>();
			StringBuilder location = new StringBuilder(result.getLocation()).insert(0, "\"");
			resultsFormatted.put("count", result.getMatches());
			resultsFormatted.put("score", String.format("%.8f",result.getScore()));
			resultsFormatted.put("where", location.insert(location.length(), "\""));
			return resultsFormatted;
		}

		@Override
		public int compareTo(ResultsMetadata o) {
			int compared = Double.compare(o.score,  this.score);

			if (compared == 0) {
				compared = Integer.compare(o.matches, this.matches);
				if (compared == 0) {
					compared = this.location.compareToIgnoreCase(o.location);
				}
			}
			return compared;
		}
	}
}
