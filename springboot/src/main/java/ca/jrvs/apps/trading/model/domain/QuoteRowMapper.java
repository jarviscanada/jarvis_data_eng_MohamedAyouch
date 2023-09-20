package ca.jrvs.apps.trading.model.domain;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class QuoteRowMapper implements RowMapper<Quote> {

  @Override
  public Quote mapRow(ResultSet resultSet, int i) throws SQLException {
    Quote quote = new Quote();
    quote.setTicker(resultSet.getString("ticker"));
    quote.setAskPrice(resultSet.getDouble("ask_price"));
    quote.setAskSize(resultSet.getInt("ask_size"));
    quote.setBidPrice(resultSet.getDouble("bid_price"));
    quote.setLastPrice(resultSet.getDouble("last_price"));
    quote.setBidSize(resultSet.getInt("bid_size"));
    return quote;
  }
}
