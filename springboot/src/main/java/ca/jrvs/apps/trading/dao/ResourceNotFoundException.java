package ca.jrvs.apps.trading.dao;

public class ResourceNotFoundException extends Exception{

  public ResourceNotFoundException(){
    super();
  }

  public ResourceNotFoundException(String message){
    super(message);
  }
}
