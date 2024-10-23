package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

/**
 * Class for processing a file and building the inverted index while storing counts in a data structure
 *
 * @author Katherine Le
 *
 */
public class InvertedIndexBuilder {
	/**
	 * Builds and stores an inverted index by looping through the array list of files.
	 *
	 * @param index the data structure that stores the inverted index
	 * @param directory the path to process and get counts and inverted index from
	 *
	 * @throws IOException if an I/O error occurs
	 */
	public static void buildInvertedIndex(InvertedIndex index, Path directory) throws IOException {
		ArrayList<Path> files = DirectoryTraverser.getTextFiles(directory);
		for (Path file : files) {
			processFile(file, index);
		}
	}

	/**
	 * Method that reads, parses, and stems file line by line and adds directrly into the inverted index. Also stores counts of stems.
	 *
	 * @param file the file to process
	 * @param index the data structure for the inverted index
	 *
	 * @throws IOException if an I/O error occurs
	 */
	public static void processFile(Path file, InvertedIndex index) throws IOException {
		int position = 1;
		String location = file.toString();
		try (BufferedReader reader =  Files.newBufferedReader(file, UTF_8)) {
			SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
			String line;
			while ((line = reader.readLine()) != null) {
				for (String word : FileProcessor.parse(line)) {
					index.add(stemmer.stem(word).toString(), location, position++);
				}
			}
		}
	}
}
