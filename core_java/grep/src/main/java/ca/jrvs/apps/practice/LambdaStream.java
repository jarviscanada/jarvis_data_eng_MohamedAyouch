package ca.jrvs.apps.practice;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LambdaStream implements LambdaStreamEx{

  @Override
  public Stream<String> createStrStream(String... strings) {
    return Stream.of(strings);
  }

  @Override
  public Stream<String> toUpperCase(String... strings) {
    return createStrStream(strings).map(x->x.toUpperCase());
  }

  @Override
  public Stream<String> filter(Stream<String> stream, String pattern) {
    return stream.filter(x->!x.contains(pattern));
  }

  @Override
  public IntStream createIntStream(int[] arr) {
    return Arrays.stream(arr);
  }

  @Override
  public <E> List<E> toList(Stream<E> stream) {
    return stream.collect(Collectors.toList());
  }
  @Override
  public List<Integer> toList(IntStream intStream) {
    return intStream.boxed().collect(Collectors.toList());
  }

  @Override
  public IntStream createIntStream(int start, int end) {
    return IntStream.rangeClosed(start,end);
  }

  @Override
  public DoubleStream squareRootIntStream(IntStream intStream) {
    return intStream.asDoubleStream().map(x->x*x);
  }

  @Override
  public IntStream getOdd(IntStream intStream) {
    return intStream.filter(x->x%2!=0);
  }

  @Override
  public Consumer<String> getlambdaPrinter(String prefix, String suffix) {
    return (String input) ->{
      String modified = prefix + input + suffix;
      System.out.println(modified);
    };
  }

  @Override
  public void printMessages(String[] messages, Consumer<String> printer) {
    for(String message:messages){
      printer.accept(message);
    }
  }

  @Override
  public void printOdd(IntStream intStream, Consumer<String> printer) {
    int[] values = getOdd(intStream).toArray();

    for (int value: values){
      String valueString = Integer.toString(value);
      printer.accept(valueString);
    }
  }

  @Override
  public Stream<Integer> flatNestedInt(Stream<List<Integer>> ints) {
    return ints.flatMap(List::stream).map(x->x*x);
  }

}
