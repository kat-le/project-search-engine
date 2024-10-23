package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Traverses a path, going through its directory and subdirectories.
 *
 * @author Katherine Le
 *
 */
public class DirectoryTraverser {
	/**
	 * Takes in a path and traverses its directory and all subdirectories, finding all text
	 * files and adds them to an arraylist. If it is a path to a text file, path is added to an arraylist
	 *
	 * @param directory the path that is to be transversed or added to arraylist if it is a file path
	 * @param filesList the arraylist to store the list of text files found in the path
	 * @throws IOException if an I/O error occurs
	 */
	public static void traverseDirectory(Path directory, Collection<Path> filesList) throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
			for (Path start : listing) {
				if (Files.isDirectory(start)) {
					traverseDirectory(start, filesList);
				} else if (isTextfile(start)){
					filesList.add(start);
				}
			}
		}
	}

	/**
	 * Method for returning the text files found in the path
	 *
	 * @param path the path to traverse
	 * @return an arraylist of files
	 * @throws IOException if an IO error occurs
	 */
	public static ArrayList<Path> getTextFiles(Path path) throws IOException {
		ArrayList<Path> files = new ArrayList<>();
		if (Files.isDirectory(path)) {
			traverseDirectory(path, files);
		} else {
			files.add(path);
		}
		return files;
	}

	/**
	 * Checks if the path is a directory or a text file
	 *
	 * @param path the path to check
	 * @return true if the path is a text file
	 * @throws IOException if an IO error occurs
	 */
	public static boolean isTextfile(Path path) throws IOException {
		String lower = path.getFileName().toString().toLowerCase();
		return Files.isRegularFile(path) && (lower.endsWith(".txt") || lower.endsWith(".text"));
	}
}
