package edu.usfca.cs272;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Class for the multithreaded building of the inverted index.
 *
 * @author Katherine Le
 *
 */
public class MultiThreadBuilder {

	/**
	 * Builds the inverted index using multithreading such that a worker threads processes a single file.
	 *
	 * @param index the thread safe index
	 * @param directory the path containing files
	 * @param queue the work queue to use
	 * @throws IOException if an I/O error occurs
	 */
	public static void buildInvertedIndex(ThreadSafeIndex index, Path directory, WorkQueue queue) throws IOException {
		ArrayList<Path> files = DirectoryTraverser.getTextFiles(directory);

		for (Path file : files) {
			queue.execute(new Task(file, index));
		}
		queue.finish();
	}

	/**
	 * Class that assigns work to a thread.
	 */
	private static class Task implements Runnable {

		/**The file to process*/
		private final Path file;

		/**The thread safe inverted index to build*/
		private final ThreadSafeIndex index;

		/**
		 * Constructor for initializing a new task.
		 *
		 * @param file the file to process
		 * @param index the thread safe inverted index
		 */
		public Task(Path file, ThreadSafeIndex index) {
			this.file = file;
			this.index = index;
		}

		@Override
		public void run() {
			InvertedIndex local = new InvertedIndex();

			try {
				InvertedIndexBuilder.processFile(file, local);
			}
			catch (IOException e) {
				throw new UncheckedIOException(e);
			}
			index.addAll(local);
		}
	}
}
