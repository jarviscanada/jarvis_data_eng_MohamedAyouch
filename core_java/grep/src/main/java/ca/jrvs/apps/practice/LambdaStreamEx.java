package ca.jrvs.apps.practice;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface LambdaStreamEx {

  /**
   * Create a String stream from array
   *
   * note: arbitrary number of values will be stored in an array
   * @param strings
   * @return
   */
  Stream<String> createStrStream(String ... strings);

  /**
   * Convert all strings to uppercase
   * @param strings
   * @return
   */
  Stream<String> toUpperCase(String ... strings);

  /**
   * filter strings that contain the pattern
   * @param pattern
   * @return
   */
  Stream<String> filter(Stream<String>, String pattern);

  /**
   * Create an intStream from an array
   * @param arr
   * @return
   */
  IntStream createIntStream(int[] arr);

  /**
   * Converts Stream to list
   * @param stream
   * @return
   * @param <E>
   */
  <E> List<E> toList(Stream<E> stream);

  /**
   * Converts intStream to list
   * @param intStream
   * @return
   */
  List<Integer> toList(IntStream intStream);

  /**
   * Create an intStream range from start to end inclusive
   * @param start
   * @param end
   * @return
   */
  IntStream createIntStream(int start, int end);

  /**
   * Converts intStream to doubleStream and compute square root of each element
   * @param intStream
   * @return
   */
  DoubleStream squareRootIntStream(IntStream intStream);

  /**
   * filter even numbers from an intStream
   * @param intStream
   * @return
   */
  IntStream getOdd(IntStream intStream);

  /**
   * Return a lambda function that print a message with a prefix and suffix
   * This lambda can be useful to format logs
   *
   * @param prefix
   * @param suffix
   * @return
   */
  Consumer<String> getlambdaPrinter(String prefix, String suffix);

  /**
   * Print each message with a given printer
   *
   * @param messages
   * @param printer
   */
  void printMessages(String[] messages, Consumer<String> printer);

  /**
   * Print all odd number from a intStream.
   *
   * @param intStream
   * @param printer
   */
  void printOdd(IntStream intStream, Consumer<String> printer);

  /**
   * Square each number from the input.
   * 
   * @param ints
   * @return
   */
  Stream<Integer> flatNestedInt(Stream <List<Integer>> ints);
}
