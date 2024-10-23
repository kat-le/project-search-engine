package edu.usfca.cs272;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Class for crawling a web page to build an inverted index.
 *
 * @author Katherine Le
 *
 */
public class WebCrawler {

	private final HashSet<URL> fetched;

	private final int maxCrawls;

	private final WorkQueue queue;

	/**
	 * Constructor to initialize a new web crawler
	 *
	 * @param index the index to build
	 */
	public WebCrawler(int maxCrawls, WorkQueue queue) {
		this.fetched = new HashSet<>();
		this.maxCrawls = maxCrawls;
		this.queue = queue;
	}

	/**
	 * Downloads HTML from seed URL if content is HTML and has status code 200. Follows up to 3 redirects.
	 * Processes the HTML by removing any comments,block elements, tags, and HTML 4 entities.
	 * The resulting text is cleaned, stemmed, parsed and added into the inverted index.
	 *
	 * @param seed the URL to start the crawl from
	 * @throws URISyntaxException if unable to craft new URI
	 * @throws MalformedURLException if unable to craft new URL
	 */
	public void processHTML(ThreadSafeIndex index, String seed) throws MalformedURLException, URISyntaxException {
		URL url = new URL(seed);
		queue.execute(new Task(url, index, fetched));
		fetched.add(url);
		queue.finish();
	}

	private class Task implements Runnable {

		private URL url;

		private final ThreadSafeIndex index;

		private HashSet<URL> fetched;

		private final ArrayList<URL> urls = new ArrayList<>();

		public Task(URL url, ThreadSafeIndex index, HashSet<URL> fetched) {
			this.url = url;
			this.index = index;
			this.fetched = fetched;
		}

		@Override
		public void run() {
			InvertedIndex local = new InvertedIndex();
			SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

			String html = HtmlFetcher.fetch(url, 3);

			if (html != null) {
				html = HtmlCleaner.stripBlockElements(html);
				LinkFinder.findUrls(url, html, urls);
				html = HtmlCleaner.stripTags(html);
				html = HtmlCleaner.stripEntities(html);
				ArrayList<String> stems = FileProcessor.listStems(html, stemmer);
				local.addAll(stems, url.toString());
				index.addAll(local);

				for (URL foundURL : urls) {
					synchronized (fetched) {
						if (fetched.contains(foundURL) || fetched.size() >= maxCrawls) {
							continue;
						}
					}
					queue.execute(new Task(foundURL, index, fetched));
					synchronized (fetched) {
						fetched.add(foundURL);
					}
				}
			}
		}
	}
}
