package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.TagFilter;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

/**
 * Tests the {@link FileFinder} class.
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2023
 */
@TestMethodOrder(MethodName.class)
public class FileFinderTest {
	/**
	 * Tests that text extensions are detected properly.
	 *
	 * @see FileFinder#IS_TEXT
	 */
	@Nested
	@TestMethodOrder(OrderAnnotation.class)
	public class A_TextExtensionTests {
		/**
		 * Tests files that SHOULD be considered text files.
		 *
		 * @param file the file name
		 */
		@Order(1)
		@ParameterizedTest
		@ValueSource(strings = { "animals_copy.text", "capital_extension.TXT", "empty.txt",
				"position.teXt", "words.tExT", "digits.tXt" })
		public void testIsTextFile(String file) {
			Path path = root.resolve(file);
			Assertions.assertTrue(FileFinder.IS_TEXT.test(path), path::toString);
		}

		/**
		 * Tests files that SHOULD NOT be considered text files.
		 *
		 * @param file the file name
		 */
		@Order(2)
		@ParameterizedTest
		@ValueSource(strings = { "double_extension.txt.html", "no_extension", "wrong_extension.html",
				"dir.txt", "nowhere.txt", ".txt" })
		public void testIsNotTextFile(String file) {
			Path path = root.resolve(file);
			Assertions.assertFalse(FileFinder.IS_TEXT.test(path), path::toString);
		}
	}

	/**
	 * Tests the default find functionality.
	 *
	 * @see FileFinder#findText(Path)
	 */
	@Nested
	@TestMethodOrder(OrderAnnotation.class)
	public class B_FindTextTests {
		/**
		 * Tests the {@link FileFinder} method.
		 *
		 * @throws IOException if an I/O error occurs
		 */
		@Test
		@Order(1)
		public void testDirectoryOneFile() throws IOException {
			Path directory = root.resolve("dir.txt");
			Path expected = directory.resolve("findme.Txt");
			Path actual = FileFinder.findText(directory).findFirst().get();
			Assertions.assertEquals(expected, actual, directory.toString());
		}

		/**
		 * Tests the {@link FileFinder} method.
		 *
		 * @throws IOException if an I/O error occurs
		 */
		@Test
		@Order(2)
		public void testOneFile() throws IOException {
			Path directory = root.resolve("hello.txt");
			Path expected = directory;
			Path actual = FileFinder.findText(directory).findFirst().get();
			Assertions.assertEquals(expected, actual, directory.toString());
		}

		/**
		 * Tests the {@link FileFinder} method.
		 *
		 * @throws IOException if an I/O error occurs
		 */
		@Test
		@Order(3)
		public void testNestedDirectory() throws IOException {
			String actual = FileFinder.findText(root).distinct().sorted()
					.map(Path::toString).collect(newLines);

			Path[] paths = new Path[] {
					root.resolve("symbols.txt"),
					root.resolve("dir.txt").resolve("findme.Txt"),
					root.resolve("empty.txt"), root.resolve(".txt").resolve("hidden.txt"),
					root.resolve("position.teXt"), root.resolve("animals_copy.text"),
					root.resolve("digits.tXt"), root.resolve("capital_extension.TXT"),
					root.resolve("animals_double.text"),
					root.resolve("a")
							.resolve("b")
							.resolve("c")
							.resolve("d")
							.resolve("subdir.txt"),
					root.resolve("words.tExT"), root.resolve("animals.text"),
					root.resolve("hello.txt"), root.resolve("capitals.txt") };

			String expected = Stream.of(paths).distinct().sorted().map(Path::toString).collect(newLines);

			// String comparison for nicer debugging in JUnit (but less efficient)
			Assertions.assertEquals(expected, actual);
		}

		/**
		 * Tests the stream has the expected number of paths.
		 *
		 * @throws IOException if an I/O error occurs
		 */
		@Test
		@Order(4)
		public void testNestedDirectorySize() throws IOException {
			Assertions.assertEquals(14, FileFinder.findText(root).count());
		}

		/**
		 * Tests that IO exceptions are NOT caught in the methods.
		 */
		@Test
		@Order(5)
		public void testException() {
			Path nowhere = root.resolve("nowhere.txt");
			Executable test = () -> FileFinder.findText(nowhere);
			String debug = "Do not catch IO exceptions in your methods!";
			Assertions.assertThrows(IOException.class, test, debug);
		}
	}

	/**
	 * Tests the general find functionality.
	 *
	 * @see FileFinder#findText(Path)
	 */
	@Nested
	@TestMethodOrder(OrderAnnotation.class)
	public class C_FindGeneralTests {

		/**
		 * Tests the general {@link FileFinder#find(Path, Predicate)} method works
		 * as expected.
		 *
		 * @throws IOException if an IO error occurs.
		 */
		@Test
		@Order(1)
		public void testMarkdown() throws IOException {
			Stream<Path> stream = FileFinder.find(root, p -> p.toString().endsWith(".md"));
			Path first = stream.findFirst().get();
			Assertions.assertEquals(root.resolve("sentences.md"), first);
		}

		/**
		 * Tests the general {@link FileFinder#find(Path, Predicate)} method works
		 * as expected.
		 *
		 * @throws IOException if an IO error occurs.
		 */
		@Test
		@Order(2)
		public void testHtmlFiles() throws IOException {
			Predicate<Path> html = p -> p.toString().endsWith(".html");
			String actual = FileFinder.find(root, html).sorted().map(Path::toString).collect(newLines);

			Path[] paths = new Path[] {
					root.resolve("double_extension.txt.html"),
					root.resolve("wrong_extension.html") };

			String expected = Stream.of(paths).sorted().map(Path::toString).collect(newLines);

			// String comparison for nicer debugging in JUnit (but less efficient)
			Assertions.assertEquals(expected, actual);
		}

		/**
		 * Tests the general {@link FileFinder#find(Path, Predicate)} method works
		 * as expected.
		 *
		 * @throws IOException if an IO error occurs.
		 */
		@Test
		@Order(3)
		public void testSubdirectories() throws IOException {
			String actual = FileFinder.find(root, Files::isDirectory).sorted()
					.map(Path::toString).collect(newLines);

			Path[] paths = new Path[] {
					root, root.resolve(".txt"), root.resolve("a"),
					root.resolve("a").resolve("b"),
					root.resolve("a").resolve("b").resolve("c"),
					root.resolve("a").resolve("b").resolve("c").resolve("d"),
					root.resolve("dir.txt") };

			String expected = Stream.of(paths).sorted().map(Path::toString).collect(newLines);

			// String comparison for nicer debugging in JUnit (but less efficient)
			Assertions.assertEquals(expected, actual);
		}

		/**
		 * Tests that symbolic links (i.e. shortcuts) are working as expected. The
		 * test will be skipped on systems that do not support symbolic links.
		 *
		 * @throws IOException if an IO error occurs.
		 */
		@Test
		@Tag("approach")
		@Order(4)
		public void testSymbolicLinks() throws IOException {
			Path parent = root.getParent();
			Path symbolic = parent.resolve("symbolic");

			try {
				Files.deleteIfExists(symbolic);
				Files.createSymbolicLink(symbolic.toAbsolutePath(), root.toAbsolutePath());
			}
			catch (Exception e) {
				String warning = "Warning: Unable to test symbolic links on your system.";
				System.err.println(warning);

				// skip the test
				Assumptions.assumeTrue(false, warning);
			}

			Stream<Path> expected = FileFinder.findText(root);
			Stream<Path> actual = FileFinder.findText(symbolic);

			Assertions.assertEquals(expected.count(), actual.count());
		}

		/**
		 * Tests that IO exceptions are NOT caught in the methods.
		 */
		@Test
		@Order(5)
		public void testException() {
			Path nowhere = root.resolve("nowhere.txt");
			Executable test = () -> FileFinder.find(nowhere, p -> true);
			String debug = "Do not catch IO exceptions in your methods!";
			Assertions.assertThrows(IOException.class, test, debug);
		}
	}

	/**
	 * Tests the text list functionality.
	 *
	 * @see FileFinder#listText(Path)
	 */
	@Nested
	@TestMethodOrder(OrderAnnotation.class)
	public class D_TextListTests {
		/**
		 * Tests the {@link FileFinder} method.
		 *
		 * @throws IOException if an I/O error occurs
		 */
		@Test
		@Order(1)
		public void testDirectoryOneFile() throws IOException {
			Path directory = root.resolve("dir.txt");
			List<Path> expected = List.of(directory.resolve("findme.Txt"));
			List<Path> actual = FileFinder.listText(directory);
			Assertions.assertEquals(expected, actual, directory.toString());
		}

		/**
		 * Tests the {@link FileFinder} method.
		 *
		 * @throws IOException if an I/O error occurs
		 */
		@Test
		@Order(2)
		public void testOneFile() throws IOException {
			Path directory = root.resolve("hello.txt");
			List<Path> expected = List.of(directory);
			List<Path> actual = FileFinder.listText(directory);
			Assertions.assertEquals(expected, actual, directory.toString());
		}

		/**
		 * Tests the {@link FileFinder} method.
		 *
		 * @throws IOException if an I/O error occurs
		 */
		@Test
		@Order(3)
		public void testNestedList() throws IOException {
			List<Path> actualList = FileFinder.listText(root);
			List<Path> expectedList = List.of(root.resolve("symbols.txt"),
					root.resolve("dir.txt").resolve("findme.Txt"),
					root.resolve("empty.txt"), root.resolve(".txt").resolve("hidden.txt"),
					root.resolve("position.teXt"), root.resolve("animals_copy.text"),
					root.resolve("digits.tXt"), root.resolve("capital_extension.TXT"),
					root.resolve("animals_double.text"),
					root.resolve("a")
							.resolve("b")
							.resolve("c")
							.resolve("d")
							.resolve("subdir.txt"),
					root.resolve("words.tExT"), root.resolve("animals.text"),
					root.resolve("hello.txt"), root.resolve("capitals.txt"));

			String actual = actualList.stream().sorted().map(Path::toString).collect(newLines);
			String expected = expectedList.stream().sorted().map(Path::toString).collect(newLines);

			// Uses String comparison for nicer debugging in JUnit (but less efficient)
			Assertions.assertEquals(expected, actual);
		}

		/**
		 * Tests the list has the expected number of paths.
		 *
		 * @throws IOException if an I/O error occurs
		 */
		@Test
		@Order(4)
		public void testNestedListSize() throws IOException {
			var actual = FileFinder.listText(root);
			Assertions.assertEquals(14, actual.size(), actual::toString);
		}

		/**
		 * Tests that IO exceptions are NOT caught in the methods.
		 */
		@Test
		@Order(5)
		public void testException() {
			Path nowhere = root.resolve("nowhere.txt");
			Executable test = () -> FileFinder.listText(nowhere);
			String debug = "Do not catch IO exceptions in your methods!";
			Assertions.assertThrows(IOException.class, test, debug);
		}
	}

	/**
	 * Tests the default list functionality.
	 *
	 * @see FileFinder#listText(Path, Path)
	 */
	@Nested
	@TestMethodOrder(OrderAnnotation.class)
	public class E_DefaultListTests {
		/**
		 * Tests the {@link FileFinder} method.
		 *
		 * @throws IOException if an I/O error occurs
		 */
		@Test
		@Order(1)
		public void testDirectoryOneFile() throws IOException {
			Path directory = root.resolve("dir.txt");
			Path hello = root.resolve("hello.txt");
			List<Path> expected = List.of(directory.resolve("findme.Txt"));
			List<Path> actual = FileFinder.listText(directory, hello);
			Assertions.assertEquals(expected, actual);
		}

		/**
		 * Tests the {@link FileFinder} method.
		 *
		 * @throws IOException if an I/O error occurs
		 */
		@Test
		@Order(2)
		public void testOneFile() throws IOException {
			Path sentences = root.resolve("sentences.md");
			Path hello = root.resolve("hello.txt");
			List<Path> expected = List.of(hello);
			List<Path> actual = FileFinder.listText(sentences, hello);
			Assertions.assertEquals(expected, actual);
		}

		/**
		 * Tests the {@link FileFinder} method.
		 *
		 * @throws IOException if an I/O error occurs
		 */
		@Test
		@Order(5)
		public void testNowhere() throws IOException {
			Path nowhere = root.resolve("nowhere.txt");
			List<Path> actual = FileFinder.listText(nowhere, nowhere);
			List<Path> expected = List.of(nowhere);
			Assertions.assertEquals(expected, actual, nowhere.toString());
		}
	}

	/**
	 * Tests the approach.
	 */
	@Nested
	@Tag("approach")
	@TestMethodOrder(OrderAnnotation.class)
	public class F_ApproachTests {
		/*
		 * These only approximately determine if a lambda function was used and the
		 * File class was NOT used.
		 */

		/**
		 * Tests that the {@link FileFinder#IS_TEXT} is not an anonymous class.
		 */
		@Test
		@Order(1)
		public void testAnonymous() {
			Assertions.assertFalse(FileFinder.IS_TEXT.getClass().isAnonymousClass());
		}

		/**
		 * Tests that the {@link FileFinder#IS_TEXT} is not an enclosing class.
		 */
		@Test
		@Order(2)
		public void testEnclosingClass() {
			Assertions.assertNull(FileFinder.IS_TEXT.getClass().getEnclosingClass());
		}

		/**
		 * Tests that the {@link FileFinder#IS_TEXT} is not a synthetic class.
		 */
		@Test
		@Order(3)
		public void testSyntheticClass() {
			Assertions.assertTrue(FileFinder.IS_TEXT.getClass().isSynthetic());
		}

		/**
		 * Tests that the {@link FileFinder#IS_TEXT} is likely a lambda function.
		 */
		@Test
		@Order(4)
		public void testClassName() {
			String actual = FileFinder.IS_TEXT.getClass().getTypeName();
			String[] parts = actual.split("[$]+");
			Assertions.assertTrue(parts[1].contentEquals("Lambda"));
		}

		/**
		 * Tests that the java.io.File class does not appear in the implementation
		 * code.
		 *
		 * @throws IOException if an I/O error occurs
		 */
		@Test
		@Order(5)
		public void testFileClass() throws IOException {
			Path base = Path.of("src", "main", "java", "edu", "usfca", "cs272");
			Path file = base.resolve(FileFinder.class.getSimpleName() + ".java");
			String source = Files.readString(file, UTF_8);
			Assertions.assertFalse(source.contains("import java.io.File;"));
			Assertions.assertFalse(source.contains(".toFile()"));
		}

		/**
		 * Causes this group of tests to fail if the other non-approach tests are
		 * not yet passing.
		 */
		@Test
		@Order(6)
		public void testOthersPassing() {
			var request = LauncherDiscoveryRequestBuilder.request()
					.selectors(DiscoverySelectors.selectClass(FileFinderTest.class))
					.filters(TagFilter.excludeTags("approach"))
					.build();

			var launcher = LauncherFactory.create();
			var listener = new SummaryGeneratingListener();

			Logger logger = Logger.getLogger("org.junit.platform.launcher");
			logger.setLevel(Level.SEVERE);

			launcher.registerTestExecutionListeners(listener);
			launcher.execute(request);

			Assertions.assertEquals(0, listener.getSummary().getTotalFailureCount(),
					"Must pass other tests to earn credit for approach group!");
		}
	}

	/** Path to directory of text files */
	public static final Path root = Path.of("src", "test", "resources", "simple");

	/** Used to collect information into Strings with newlines */
	public static final Collector<CharSequence, ?, String> newLines = Collectors.joining("\n");

	/**
	 * Runs before any tests to make sure environment is setup.
	 */
	@BeforeAll
	public static void checkEnvironment() {
		Assumptions.assumeTrue(Files.isDirectory(root));
		Assumptions.assumeTrue(Files.exists(root.resolve("hello.txt")));
	}
}
