package ca.jrvs.apps.trading.dao;

public class DataAccessException extends  Exception{

  public DataAccessException(){
    super();
  }

  public DataAccessException(String message){
    super(message);
  }

}
