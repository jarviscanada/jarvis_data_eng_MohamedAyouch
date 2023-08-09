package ca.jrvs.apps.grep;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.BasicConfigurator;

public class JavaGrepImp implements JavaGrep {

  final Logger logger = LoggerFactory.getLogger(JavaGrep.class);
  private String rootPath, regex, outFile;


  @Override
  public void process() throws IOException {
    List <String> matchedLines = new ArrayList<String>();

    for(File file:listFiles(getRootPath())){
      for(String line: readLines(file)){
        if(containsPattern(line)) matchedLines.add(line);
      }
    }
    writeToFile(matchedLines);
  }

  @Override
  public List<File> listFiles(String rootDir) {
    File dir = new File(rootDir);
    List<File> output = null;

    if (dir.exists() && dir.isDirectory()) {
      output = Arrays.asList(dir.listFiles());
      if (output.isEmpty()) {
        logger.error("Error: No files found in this directory");
      }
    } else {
      logger.error("Error: The specified directory doesn't exist or is not a directory");
    }
    return output;
  }

  @Override
  public List<String> readLines(File inputFile) {
    List<String> lines = new ArrayList<String>();

    try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
      String line;
      while ((line = reader.readLine()) != null) {
        lines.add(line);
      }

    } catch (FileNotFoundException e) {
      logger.error("Error: This file doesn't exist");
      throw new RuntimeException(e);
    } catch (IOException e) {
      logger.error("Error: Unable to read this file");
      throw new RuntimeException(e);
    }
    return lines;
  }

  @Override
  public boolean containsPattern(String line) {
    return line.matches(this.getRegex());
  }

  @Override
  public void writeToFile(List<String> lines) throws IOException {
    File file = new File(getOutFile());

    try (BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(file)))) {
      for (String line : lines) {
        writer.write(line);
        writer.newLine();
      }
    } catch (IOException e) {
      logger.error("Error: Unable to write to this file");
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getRootPath() {
    return rootPath;
  }

  @Override
  public void setRootPath(String rootPath) {
    this.rootPath = rootPath;
  }

  @Override
  public String getRegex() {
    return this.regex;
  }

  @Override
  public void setRegex(String regex) {
    this.regex = regex;
  }

  @Override
  public String getOutFile() {
    return this.outFile;
  }

  @Override
  public void setOutFile(String outFile) {
    this.outFile = outFile;
  }

  public static void main(String[] args) {
    if (args.length != 3) {
      throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile");
    }

    BasicConfigurator.configure();

    JavaGrepImp javaGrepImp = new JavaGrepImp();
    javaGrepImp.setRegex(args[0]);
    javaGrepImp.setRootPath(args[1]);
    javaGrepImp.setOutFile(args[2]);

    try {
      javaGrepImp.process();
    } catch (Exception e) {
      javaGrepImp.logger.error("Error: Unable to process");
    }
  }
}
