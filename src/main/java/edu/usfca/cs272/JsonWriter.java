package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import edu.usfca.cs272.InvertedIndex.ResultsMetadata;

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented using spaces.
 *
 * @author Katherine Le
 *
 */
public class JsonWriter {

	/**
	 * Indents the writer by the specified number of times. Does nothing if the
	 * indentation level is 0 or less.
	 *
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndent(Writer writer, int indent) throws IOException {
		while (indent-- > 0) {
			writer.write("  ");
		}
	}

	/**
	 * Indents and then writes the String element.
	 *
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndent(String element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent);
		writer.write(element);
	}


	/**
	 * Indents and then writes the text element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeQuote(String element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent);
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Method for writing entry values of an object in Json format
	 *
	 * @param entry the entry to write
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException is an I/O error occurs
	 */
	public static void writeObjectEntry(Entry<String, ? extends Object> entry, Writer writer, int indent) throws IOException {
		writer.write("\n");
		writeIndent(writer, indent);
		writeQuote(entry.getKey(), writer, 1);
		writer.write(": ");
		writer.write(entry.getValue().toString());
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at the
	 *   initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 */
	public static void writeObject(Map<String, ? extends Object> elements, Writer writer, int indent) throws IOException {
		var iterator = elements.entrySet().iterator();

		writer.write("{");
		if (iterator.hasNext()) {
			writeObjectEntry(iterator.next(), writer, indent);
		}
		while (iterator.hasNext()) {
			writer.write(",");
			writeObjectEntry(iterator.next(), writer, indent);
		}
		writer.write("\n");
		writeIndent("}", writer, indent);
	}
	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeObject(Map, Writer, int)
	 */
	public static void writeObject(Map<String, ? extends Number> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 * @throws IOException if an I/O error occurs
	 *
	 * @see StringWriter
	 * @see #writeObject(Map, Writer, int)
	 */
	public static String writeObject(Map<String, ? extends Number> elements) throws IOException {
		StringWriter writer = new StringWriter();
		writeObject(elements, writer, 0);
		return writer.toString();
	}

	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at the
	 *   initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 */
	public static void writeArray(Collection<? extends Number> elements, Writer writer, int indent) throws IOException {
		var iterator = elements.iterator();
		writer.write("[");

		if (iterator.hasNext()) {
			writer.write("\n");
			writeIndent(writer, indent);
			writeIndent(iterator.next().toString(), writer, 1);
		}
		while (iterator.hasNext()) {
			writer.write(",");
			writer.write("\n");
			writeIndent(writer, indent);
			writeIndent(iterator.next().toString(), writer, 1);
		}
		writer.write("\n");
		writeIndent("]", writer, indent);
	}

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static void writeArray(Collection<? extends Number> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 * @throws IOException if an I/O error occurrs
	 *
	 * @see StringWriter
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static String writeArray(Collection<? extends Number> elements) throws IOException{
		StringWriter writer = new StringWriter();
		writeArray(elements, writer, 0);
		return writer.toString();
	}
	/**
	 * Writes the elements as a pretty JSON object with nested arrays. The generic
	 * notation used allows this method to be used for any type of map with any type
	 * of nested collection of number objects.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at the
	 *   initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 * @see #writeArray(Collection)
	 */
	public static void writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements, Writer writer,
			int indent) throws IOException {
		var iterator = elements.entrySet().iterator();
		writer.write("{");

		if (iterator.hasNext()) {
			writeObjectArrayEntry(iterator.next(), writer, indent);
		}
		while (iterator.hasNext()) {
			writer.write(",");
			writeObjectArrayEntry(iterator.next(), writer, indent);
		}
		writer.write("\n");
		writeIndent("}", writer,indent);
	}

	/**
	 * Method for writing the entry value of a nested object
	 *
	 * @param entry the entry to write
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an I/O error occurs
	 */
	public static void writeObjectArrayEntry(Entry<String, ? extends Collection<? extends Number>> entry, Writer writer, int indent) throws IOException {
		writer.write("\n");
		writeIndent(writer, indent);
		writeQuote(entry.getKey(), writer, 1);
		writer.write(": ");
		writeArray(entry.getValue(), writer, indent + 1);
	}

	/**
	 * Writes the elements as a pretty JSON object with nested arrays to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeObjectArrays(Map, Writer, int)
	 */
	public static void writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeObjectArrays(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object with nested arrays.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 * @throws IOException if an I/O error occurs
	 *
	 * @see StringWriter
	 * @see #writeObjectArrays(Map, Writer, int)
	 */
	public static String writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements) throws IOException{
		StringWriter writer = new StringWriter();
		writeObjectArrays(elements, writer, 0);
		return writer.toString();
	}

	/**
	 * Writes the elements as a pretty JSON array with nested objects. The generic
	 * notation used allows this method to be used for any type of collection with
	 * any type of nested map of String keys to number objects.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at the
	 *   initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 * @see #writeObject(Map)
	 */
	public static void writeArrayObjects(Collection<? extends Map<String, ? extends Object>> elements, Writer writer, int indent) throws IOException {
		var iterator = elements.iterator();
		writer.write("[");

		if (iterator.hasNext()) {
			writer.write("\n");
			writeIndent(writer, indent + 1);
			writeObject(iterator.next(), writer, indent + 1);
		}
		while (iterator.hasNext()) {
			writer.write(",");
			writer.write("\n");
			writeIndent(writer, indent + 1);
			writeObject(iterator.next(), writer, indent + 1);
		}
		writer.write("\n");
		writeIndent("]", writer, indent);
	}

	/**
	 * Writes the elements as a pretty JSON array with nested objects to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeArrayObjects(Collection)
	 */
	public static void writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeArrayObjects(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array with nested objects.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 * @throws IOException if an I/O error occurs
	 *
	 * @see StringWriter
	 * @see #writeArrayObjects(Collection)
	 */
	public static String writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements) throws IOException{
		StringWriter writer = new StringWriter();
		writeArrayObjects(elements, writer, 0);
		return writer.toString();
	}

	/**
	 * Writes the elements as an inverted index in pretty JSON format.
	 *
	 * @param elements the inverted index that is stored here
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an I/O error occurs
	 */
	public static void writeInvertedIndex(Map<String, ? extends Map<String, ? extends Collection<? extends Number>>> elements, Writer writer, int indent)
			throws IOException{
		var iterator = elements.entrySet().iterator();
		writer.write("{");

		if (iterator.hasNext()) {
			writeInvertedIndexEntry(iterator.next(), writer, indent);
		}
		while (iterator.hasNext()) {
			writer.write(",");
			writeInvertedIndexEntry(iterator.next(), writer, indent);
		}
		writer.write("\n");
		writer.write("}");
	}

	/**
	 * Method for writing the entry values of the inverted index.
	 *
	 * @param entry the entry to write
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException is an I/O error occurs
	 */
	public static void writeInvertedIndexEntry(Entry<String, ? extends Map<String, ? extends Collection<? extends Number>>> entry, Writer writer, int indent) throws IOException {
		writer.write("\n");
		writeQuote(entry.getKey(), writer, 1);
		writer.write(": ");
		writeObjectArrays(entry.getValue(), writer, indent + 1);
	}

	/**
	 * Writes the elements as an inverted index to the file path.
	 *
	 * @param elements the inverted index that is stored
	 * @param path the path to write the inverted index to
	 * @throws IOException if an IO error occurs
	 */
	public static void writeInvertedIndex(Map<String, ? extends Map<String, ? extends Collection<? extends Number>>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeInvertedIndex(elements, writer, 0);
		}
	}

	/**
	 * Returns the elemnts as a pretty JSON inverted index with nested arrays
	 *
	 * @param elements the elements to write
	 * @throws IOException if an I/O error occurs
	 * @return a {@link String} containing the elements in pretty JSON format
	 */
	public static String writeInvertedIndex(Map<String, ? extends Map<String, ? extends Collection<? extends Number>>> elements) throws IOException{
		StringWriter writer = new StringWriter();
		writeInvertedIndex(elements, writer, 0);
		return writer.toString();
	}

	/**
	 * Writes the results from a search as a pretty JSON.
	 *
	 * @param elements the data structure containing results from the search
	 * @param writer the writer to use
	 * @throws IOException if an I/O error occurs
	 */
	public static void writeSearch(Map<String, ? extends Collection<? extends ResultsMetadata>> elements, Writer writer) throws IOException {
		var iterator = elements.entrySet().iterator();
		writer.write("{");

		if (iterator.hasNext()) {
			writeSearchEntry(iterator.next(), writer);
		}
		while (iterator.hasNext()) {
			writer.write(",");
			writeSearchEntry(iterator.next(), writer);
		}
		writer.write("\n");
		writer.write("}");
	}

	/**
	 * Writes one entry of the search results data structure.
	 *
	 * @param entry the entry to write
	 * @param writer the write to use
	 * @throws IOException if an I/O error occurs
	 */
	public static void writeSearchEntry(Entry<String, ? extends Collection<? extends ResultsMetadata>> entry, Writer writer) throws IOException {
		writer.write("\n");
		writeQuote(entry.getKey(), writer, 1);
		writer.write(": ");
		ResultsMetadata.toJson(writer, entry.getValue());
	}

	/**
	 * Returns the elements as a pretty JSON with a nested array of objects
	 * @param elements the elements to write
	 * @return a string representation of the elements in a pretty JSON
	 * @throws IOException if an I/O error occurs
	 */
	public static String writeSearch(Map<String, ? extends Collection<? extends ResultsMetadata>>  elements) throws IOException{
		StringWriter writer = new StringWriter();
		writeSearch(elements, writer);
		return writer.toString();
	}

	/**
	 * Method for writing the search results to the path.
	 *
	 * @param elements the search result to write to a file
	 * @param path the path to write to
	 * @throws IOException if an I/O error occurs
	 */
	public static void writeSearch(Map<String, ? extends Collection<? extends ResultsMetadata>> elements, Path path) throws IOException	 {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeSearch(elements, writer);
		}
	}
}
