FileFinder
=================================================

![Points](../../blob/badges/points.svg)

For this homework assignment, you will create a class that finds all the text files (not directories) that end in the `.txt` or `.text` extension (case-insensitive). You must use streams and lambda functions for this assignment. You cannot use the `File` class in Java!

## Hints ##

Below are some hints that may help with this homework assignment:

  - If you are more comfortable with anonymous classes and the `File` class, start there. You will fail some of the tests, but you will be able to see whether you are finding the expected text files. Then, convert one thing at a time to use what is required.

  - Check out the [`Files`](https://www.cs.usfca.edu/~cs272/javadoc/api/java.base/java/nio/file/Files.html) class in Java. The `walk(...)` or `find(...)` methods may be helpful here.

  - Use the [`FileVisitOption.FOLLOW_LINKS`](https://www.cs.usfca.edu/~cs272/javadoc/api/java.base/java/nio/file/FileVisitOption.html#FOLLOW_LINKS) option when using the `walk(...)` or `find(...)` method to follow symbolic links. Symbolic links (Unix) are essentially shortcuts (Windows) or aliases (MacOS) to other files.

These hints are *optional*. There may be multiple approaches to solving this homework.

## Instructions ##

Use the "Tasks" view in Eclipse to find the `TODO` comments for what need to be implemented and the "Javadoc" view to see additional details.

The tests are provided in the `src/test/` directory; do not modify any of the files in that directory. Check the run details on GitHub Actions for how many points each test group is worth. 

See the [Homework Resources](https://usf-cs272-spring2023.github.io/resources/homework/) for additional details on homework requirements and submission.
