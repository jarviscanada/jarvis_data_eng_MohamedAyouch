package ca.jrvs.apps.grep;

import ca.jrvs.apps.practice.LambdaStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaGrepLambdaImp extends JavaGrepImp {

  private LambdaStream lambdaStream = new LambdaStream();

  public static void main(String[] args) {
    if (args.length != 3) {
      throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile");
    }
    JavaGrepLambdaImp javaGrepLambdaImp = new JavaGrepLambdaImp();
    javaGrepLambdaImp.setRegex(args[0]);
    javaGrepLambdaImp.setRootPath(args[1]);
    javaGrepLambdaImp.setOutFile(args[2]);

    try {
      javaGrepLambdaImp.process();
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<File> listFiles(String rootDir) {
    File dir = new File(rootDir);
    List<File> output = null;

    if (dir.exists() && dir.isDirectory()) {
      Stream<File> stream = Stream.of(dir.listFiles());
      output = lambdaStream.toList(stream);
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

    try {
      return Files.lines(inputFile.toPath()).collect(Collectors.toList());
    } catch (IOException e) {
      logger.error("Error: Unable to read this file");
      throw new RuntimeException(e);
    }
  }

  @Override
  public void writeToFile(List<String> lines) throws IOException {
    File file = new File(getOutFile());

    try (BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(file)))) {
     lines.forEach(line ->{
       try {
         writer.write(line);
         writer.newLine();
       } catch (IOException e) {
         logger.error("Error: Unable to read this file");
         throw new RuntimeException(e);
       }
     });
    } catch (IOException e) {
      logger.error("Error: Unable to write to this file");
      throw new RuntimeException(e);
    }
  }
}

