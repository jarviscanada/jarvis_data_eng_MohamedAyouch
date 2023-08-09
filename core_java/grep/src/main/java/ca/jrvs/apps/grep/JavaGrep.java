package ca.jrvs.apps.grep;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface JavaGrep {

  /**
   * Top level search workflow
   *
   * @throws IOException
   */
  void process() throws IOException;

  /**
   * Traverse a given directory and return all files
   *
   * @param rootDir input directory
   * @return files under the rootDir
   */
  List<File> listFiles(String rootDir);

  /**
   * Read a file and returns all the lines
   * <p>
   * FileReader and BufferedReader are 2 classes used to read characters from files, while character
   * encoding is the mapping of characters to binary values
   *
   * @param inputFile file to be read
   * @return lines
   * @throws IllegalArgumentException if a given input file is not a file
   */
  List<String> readLines(File inputFile);

  /**
   * checks if a line contains the regex pattern
   *
   * @param line input string
   * @return true if there's a match
   */
  boolean containsPattern(String line);

  /**
   * Write lines to a file
   * <p>
   * FileOutputStream is used to write bytes to a file, OutputStreamWriter bridges the gap between
   * character and byte streams while handling encoding, and BufferedWriter enhances writing
   * efficiency by using a buffer
   *
   * @param lines matched lines
   * @throws IOException
   */
  void writeToFile(List<String> lines) throws IOException;

  String getRootPath();

  void setRootPath(String rootPath);

  String getRegex();

  void setRegex(String regex);

  String getOutFile();

  void setOutFile(String outFile);
}
