package ca.jrvs.apps.grep;


import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
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
    try (Stream<Path> files = listFiles(getRootPath())) {
      List<String> matchedLines = files
          .flatMap(file -> readLines(file.toFile()).filter(this::containsPattern))
          .collect(Collectors.toList());

      writeToFile(matchedLines);
    }
  }

  @Override
  public Stream<Path> listFiles(String rootDir) {
    try {
      return Files.walk(Paths.get(rootDir), FileVisitOption.FOLLOW_LINKS)
          .filter(Files::isRegularFile);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public Stream<String> readLines(File inputFile) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(inputFile));
      return reader.lines().onClose(() -> closeReader(reader));
    } catch (FileNotFoundException e) {
      logger.error("Error: this file doesn't exist");
      throw new RuntimeException(e);
    }
  }

  private void closeReader(BufferedReader reader){
    try{
      if(reader!=null){
        reader.close();
      }
    } catch (IOException e) {
      logger.error("Error: Unable to close the file reader");
      throw new RuntimeException(e);
    }
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
