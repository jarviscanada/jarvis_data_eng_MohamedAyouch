package ca.jrvs.apps.practice;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import org.junit.Test;
import java.util.stream.Stream;
import java.util.function.Consumer;


public class LambdaStreamTest {

  LambdaStream test = new LambdaStream();

  @Test
  public void createStrStreamTest() {
    String[] inputStrings = {"Lebron", "MJ", "Kobe"};
    Stream<String> stream = test.createStrStream(inputStrings);
    assertArrayEquals(stream.toArray(), inputStrings);
  }

  @Test
  public void toUpperCaseTest() {
    String[] inputStrings = {"Lebron", "MJ", "Kobe"};
    Stream<String> stream = test.toUpperCase(inputStrings);
    String[] expectedResult = {"LEBRON", "MJ", "KOBE"};

    assertArrayEquals(stream.toArray(), expectedResult);
  }

  @Test
  public void filterTest() {
    Stream<String> stream = test.createStrStream("Lebron", "MJ", "Kobe");

    Stream<String> output = test.filter(stream, "e");

    String[] expectedResult = {"MJ"};

    assertArrayEquals(output.toArray(), expectedResult);
  }

  @Test
  public void createIntStreamTest() {
    int[] input = {1, 2, 3};
    IntStream stream = test.createIntStream(input);

    assertArrayEquals(stream.toArray(), input);
  }

  @Test
  public void toListTest() {
    Stream<String> stream = test.createStrStream("Hello", "Bye");
    List<String> output = test.toList(stream);
    List<String> expectedResult = Arrays.asList("Hello", "Bye");

    assertEquals(output, expectedResult);
  }

  @Test
  public void toListIntTest() {
    int[] values = {1, 2, 3};
    IntStream stream = test.createIntStream(values);
    List<Integer> output = test.toList(stream);
    List<Integer> expectedResult = Arrays.asList(1, 2, 3);

    assertEquals(output, expectedResult);
  }

  @Test
  public void createIntStreamRangeTest() {
    IntStream stream = test.createIntStream(1, 5);
    int[] expectedResult = {1, 2, 3, 4, 5};

    assertArrayEquals(stream.toArray(), expectedResult);
  }

  @Test
  public void squareRootIntStreamTest() {
    IntStream stream = test.createIntStream(1, 5);
    DoubleStream streamDoubled = test.squareRootIntStream(stream);

    double[] expectedResult = {1, 4, 9, 16, 25};

    assertTrue(Arrays.equals(streamDoubled.toArray(), expectedResult));
  }

  @Test
  public void getOddTest() {
    IntStream stream = test.createIntStream(1, 5);
    IntStream output = test.getOdd(stream);

    int[] expectedResult = {1, 3, 5};

    assertArrayEquals(output.toArray(), expectedResult);
  }

  @Test
  public void getLambdaPrinterTest() {
    String prefix = "Start>";
    String suffix = "<end";
    Consumer<String> printer = test.getlambdaPrinter(prefix, suffix);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream originalSystemOut = System.out;
    System.setOut(new PrintStream(outputStream));

    printer.accept("Message Body");

    System.setOut(originalSystemOut);

    String expectedResult = prefix + "Message Body" + suffix;
    String actualOutput = outputStream.toString().trim();
    assertEquals(expectedResult, actualOutput);
  }

  @Test
  public void printMessagesTest() {
    String prefix = "msg:";
    String suffix = "!";
    Consumer<String> printer = test.getlambdaPrinter(prefix, suffix);

    String[] messages = {"a", "b", "c"};

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream originalSystemOut = System.out;
    System.setOut(new PrintStream(outputStream));

    for(String message: messages){
      printer.accept(message);
    }

    System.setOut(originalSystemOut);
    StringBuilder expectedOutput = new StringBuilder();
    for(String message: messages){
      expectedOutput.append(prefix).append(message).append(suffix).append("\n");
    }

    String expectedOutputTrimmed = expectedOutput.toString().trim();
    String actualOutput = outputStream.toString().trim();

    assertEquals(actualOutput,expectedOutputTrimmed);

  }

  @Test
  public void printOddTest(){
    int [] values = {1,2,3,4,5};
    IntStream stream = test.createIntStream(values);

    String prefix = "odd number:";
    String suffix = "!";

    Consumer<String> printer = test.getlambdaPrinter(prefix,suffix);

    IntStream oddStream = test.getOdd(stream);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream originalSystemOut = System.out;
    System.setOut(new PrintStream(outputStream));

    int [] oddValues = oddStream.toArray();

    for(int value: oddValues){
      printer.accept(Integer.toString(value));
    }

    System.setOut(originalSystemOut);
    StringBuilder expectedOutput = new StringBuilder();
    for(int value: oddValues){
      expectedOutput.append(prefix).append(Integer.toString(value)).append(suffix).append("\n");
    }

    String expectedOutputTrimmed = expectedOutput.toString().trim();
    String actualOutput = outputStream.toString().trim();
    assertEquals(expectedOutputTrimmed,actualOutput);

  }

  @Test
  public void flatNestedListTest(){
    List<Integer> list1 = Arrays.asList(1,2,3);
    List<Integer> list2 = Arrays.asList(4,5);
    List<Integer> list3 = Arrays.asList(6,7,8);

    Stream<List<Integer>> nestedInts = Stream.of(list1, list2, list3);

    Stream<Integer> result = test.flatNestedInt(nestedInts);

    int [] expectedOutput = {1,4,9,16,25,36,49,64};

    assertArrayEquals(result.mapToInt(Integer::intValue).toArray(),expectedOutput);
  }

}
