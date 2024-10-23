package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  A thread-safe version of the inverted index using a read/write lock.
 *
 * @author Katherine Le
 *
 */
public class ThreadSafeIndex extends InvertedIndex {

	/** The lock used to protect concurrent access to the underlying inverted index. */
	private final MultiReaderLock lock;

	/**
	 * Initializes a thread-safe inverted index.
	 */
	public ThreadSafeIndex() {
		super();
		lock = new MultiReaderLock();
	}

	/**
	 * Returns the identity hashcode of the lock object.
	 *
	 * @return the identity hashcode of the lock object
	 */
	public int lockCode() {
		return System.identityHashCode(lock);
	}

	@Override
	public void add(String word, String location, int position) {
		lock.writeLock().lock();

		try {
			 super.add(word, location, position);
		}
		finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void addAll(List<String> words, String location, int position) {
		lock.writeLock().lock();

		try {
			 super.addAll(words, location, position);
		}
		finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void addAll(List<String> words, String location) {
		lock.writeLock().lock();

		try {
			 super.addAll(words, location);
		}
		finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void countsToJson(Path path) throws IOException {
		lock.readLock().lock();

		try {
			 super.countsToJson(path);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void indexToJson(Path path) throws IOException {
		lock.readLock().lock();

		try {
			 super.indexToJson(path);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasLocation(String location) {
		lock.readLock().lock();

		try {
			return super.hasLocation(location);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasWord(String word) {
		lock.readLock().lock();

		try {
			return super.hasWord(word);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasLocation(String word, String location) {
		lock.readLock().lock();

		try {
			return super.hasLocation(word, location);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean hasPosition(String word, String location, int position) {
		lock.readLock().lock();

		try {
			return super.hasPosition(word, location, position);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int numWords() {
		lock.readLock().lock();

		try {
			return super.numWords();
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int numLocations() {
		lock.readLock().lock();

		try {
			return super.numLocations();
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int numLocations(String word) {
		lock.readLock().lock();

		try {
			return super.numLocations(word);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int numPositions(String word, String location) {
		lock.readLock().lock();

		try {
			return super.numPositions(word, location);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<Integer> viewPositions(String word, String location) {
		lock.readLock().lock();

		try {
			return super.viewPositions(word, location);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Collection<String> viewLocations(String word) {
		lock.readLock().lock();

		try {
			return super.viewLocations(word);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Collection<String> viewWords() {
		lock.readLock().lock();

		try {
			return super.viewWords();
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Map<String, Integer> getCounts() {
		lock.readLock().lock();

		try {
			return super.getCounts();
		}
		finally {
			lock.readLock().unlock();
		}
	}


	@Override
	public String toString() {
		lock.readLock().lock();

		try {
			return super.toString();
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public String countsToString() {
		lock.readLock().lock();

		try {
			return super.countsToString();
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public ArrayList<ResultsMetadata> exactSearch(Set<String> query) {
		lock.readLock().lock();

		try {
			return super.exactSearch(query);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public ArrayList<ResultsMetadata> partialSearch(Set<String> query) {
		lock.readLock().lock();

		try {
			return super.partialSearch(query);
		}
		finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void addAll(InvertedIndex local) {
		lock.writeLock().lock();

		try {
			 super.addAll(local);
		}
		finally {
			lock.writeLock().unlock();
		}
	}
}
