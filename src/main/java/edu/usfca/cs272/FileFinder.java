package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A utility class for finding all text files in a directory using lambda
 * expressions and streams.
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2023
 */
public class FileFinder {
	/**
	 * A lambda expression that returns true if the path is a file that ends in a
	 * .txt or .text extension (case-insensitive). Useful for
	 * {@link Files#walk(Path, FileVisitOption...)}.
	 *
	 * @see Files#isRegularFile(Path, java.nio.file.LinkOption...)
	 * @see Path#getFileName()
	 *
	 * @see String#toLowerCase()
	 * @see String#compareToIgnoreCase(String)
	 * @see String#endsWith(String)
	 *
	 * @see Files#walk(Path, FileVisitOption...)
	 */
	// TODO Assign IS_TEXT below to a lambda expression
	public static final Predicate<Path> IS_TEXT = null;

	/**
	 * Returns a stream of all paths within the starting path that match the
	 * provided filter. Follows any symbolic links encountered.
	 *
	 * @param start the initial path to start with
	 * @param keep function that determines whether to keep a path
	 * @return a stream of paths
	 * @throws IOException if an IO error occurs
	 *
	 * @see FileVisitOption#FOLLOW_LINKS
	 * @see Files#walk(Path, FileVisitOption...)
	 */
	public static Stream<Path> find(Path start, Predicate<Path> keep) throws IOException {
		// TODO Implement find(...) using streams
		throw new UnsupportedOperationException("Not yet implemented.");
	};

	/**
	 * Returns a stream of text files, following any symbolic links encountered.
	 *
	 * @param start the initial path to start with
	 * @return a stream of text files
	 * @throws IOException if an IO error occurs
	 *
	 * @see #find(Path, Predicate)
	 * @see #IS_TEXT
	 */
	public static Stream<Path> findText(Path start) throws IOException {
		// TODO Implement find(Path) reusing as much as possible
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	/**
	 * Returns a list of text files using streams.
	 *
	 * @param start the initial path to search
	 * @return list of text files
	 * @throws IOException if an IO error occurs
	 *
	 * @see #findText(Path)
	 * @see Stream#toList()
	 */
	public static List<Path> listText(Path start) throws IOException {
		// TODO Implement list(Path) reusing as much as possible
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	/**
	 * Returns a list of text files using streams if the provided path is a valid
	 * directory, otherwise returns a list containing only the default path.
	 *
	 * @param start the starting path
	 * @param defaultPath the default to include if the starting path is not a valid
	 *   directory
	 * @return a list of paths
	 * @throws IOException if an IO error occurs
	 *
	 * @see #listText(Path)
	 * @see List#of()
	 * @see Files#isDirectory(Path, java.nio.file.LinkOption...)
	 */
	public static List<Path> listText(Path start, Path defaultPath) throws IOException {
		// TODO Implement list(Path, Path) reusing as much as possible
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	/**
	 * Demonstrates this class.
	 *
	 * @param args unused
	 * @throws IOException if unable to list path
	 */
	public static void main(String[] args) throws IOException {
		Path simple = Path.of("src", "test", "resources", "simple");
		Path hello = simple.resolve("hello.txt"); // text file
		Path dir = simple.resolve("dir.txt"); // directory
		Path nowhere = simple.resolve("nowhere.txt"); // does not exist

		// demonstrates isDirectory and isRegularFile
		System.out.println("Files.isDirectory");
		System.out.println(simple + ": " + Files.isDirectory(simple));
		System.out.println(hello + ": " + Files.isDirectory(hello));
		System.out.println(dir + ": " + Files.isDirectory(dir));
		System.out.println(nowhere + ": " + Files.isDirectory(nowhere));

		System.out.println();
		System.out.println("Files.isRegularFile");
		System.out.println(simple + ": " + Files.isRegularFile(simple));
		System.out.println(hello + ": " + Files.isRegularFile(hello));
		System.out.println(dir + ": " + Files.isRegularFile(dir));
		System.out.println(nowhere + ": " + Files.isRegularFile(nowhere));

		// Demonstrates IS_TEXT predicate
		System.out.println();
		System.out.println("IS_TEXT");

		try {
			System.out.println(hello + ": " + IS_TEXT.test(hello));
			System.out.println(dir + ": " + IS_TEXT.test(dir));
		}
		catch (Exception e) {
			e.printStackTrace(System.out);
		}

		// Demonstrates listText
		System.out.println();
		System.out.println("listText");

		try {
			System.out.println(listText(simple));
		}
		catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
}
