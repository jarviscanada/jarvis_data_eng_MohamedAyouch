package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.IexQuote;
import ca.jrvs.apps.trading.model.domain.Quote;
import ca.jrvs.apps.trading.model.domain.QuoteRowMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public class QuoteDao implements CrudRepository<Quote,String> {

  private static final String TABLE_NAME = "quote";
  private static final String ID_COLUMN_NAME = "ticker";

  private static final Logger logger = LoggerFactory.getLogger(QuoteDao.class);
  private JdbcTemplate jdbcTemplate;
  private SimpleJdbcInsert simpleJdbcInsert;

  @Autowired
  public QuoteDao(DataSource dataSource){
    jdbcTemplate = new JdbcTemplate(dataSource);
    simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName(TABLE_NAME);
  }

  /**
   *
   * @param quote
   * @return saved quote
   */
  @Override
  public Quote  save(Quote quote) {
    if(existsById(quote.getId())){
      int updateRowNo = updateOne(quote);
      if(updateRowNo != 1){
        throw new DataRetrievalFailureException("Unable to update quote");
      }
    }else{
      addOne(quote);
    }
    return quote;
  }

  /**
   *
   * @param quotes
   * @return list of all the saved quotes
   * @param <S> list of quotes to be saved
   */
  @Override
  public <S extends Quote> List<S> saveAll(Iterable<S> quotes) {
    List<S> quoteList = new ArrayList<>();

    for (S quote:quotes) {
     if(existsById(quote.getId())){
       int updateRowNo = updateOne(quote);
       if(updateRowNo !=1){
         throw new DataRetrievalFailureException("Unable to update quote "+ quote.getId());
       }
     }
     else{
       addOne(quote);
       quoteList.add(quote);
     }
    }
    return quoteList;
  }

  /**
   *
   * @param ticker to be found
   * @return the quote associated with the ticker
   */

  @Override
  public Optional<Quote> findById(String ticker) {
    Optional<Quote> quote;
    List<Quote> quotes = new ArrayList<>();
    Iterator<Quote> quoteIterator = findAll().iterator();

    while(quoteIterator.hasNext()){
      Quote q = quoteIterator.next();
      if(!q.getTicker().contentEquals(ticker)) quoteIterator.remove();
      else quotes.add(q);
    }

    if (quotes.size() == 0) {
      return Optional.empty();
    } else if (quotes.size() == 1) {
      quote = Optional.of(quotes.get(0));
    } else {
      throw new DataRetrievalFailureException("Unexpected number of quotes");
    }
    return quote;
  }

  /**
   *
   * @param ticker that is queried
   * @return
   */
  @Override
  public boolean existsById(String ticker) {
    String sql = "SELECT COUNT(*) FROM quote WHERE ticker=?";
    List <Integer> results = jdbcTemplate.queryForList(sql, Integer.class, ticker);
    if(results.get(0) >1)  return false;
    else if(results.get(0) == 0) return false;
    else return true;
  }

  /**
   *
   * @return a list of all the quotes found
   */
  @Override
  public List<Quote> findAll() {
    String sql = "SELECT * FROM quote";
    return jdbcTemplate.query(sql,new QuoteRowMapper());
  }

  @Override
  public Iterable<Quote> findAllById(Iterable<String> iterable) {
    throw new UnsupportedOperationException("Not Implemented");
  }

  @Override
  public long count() {
    return 0;
  }

  @Override
  public void deleteById(String ticker) {
  String sql = "DELETE FROM quote WHERE ticker=?";
  jdbcTemplate.update(sql,ticker);
  }

  @Override
  public void delete(Quote quote) {

  }

  @Override
  public void deleteAll(Iterable<? extends Quote> iterable) {

  }

  @Override
  public void deleteAll() {
    String deleteSql = "DELETE FROM quote";
    jdbcTemplate.update(deleteSql);
  }

  /**
   * Helper method that updates one quote
   * @param quote
   * @return
   */
  private int updateOne(Quote quote){
    String updateSql = "UPDATE quote SET last_price=?, bid_price=?, bid_size=?, ask_price=?, ask_size=? WHERE ticker=?";
    return jdbcTemplate.update(updateSql,makeUpdateValues(quote));
  }

  /**
   * Helper method that saves one quote
   * @param quote
   */
  private void addOne(Quote quote){
    SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(quote);
    int row = simpleJdbcInsert.execute(sqlParameterSource);
    if(row!=1){
      throw new IncorrectResultSizeDataAccessException("Failed to insert", 1, row);
    }
  }
  private Object[] makeUpdateValues(Quote quote){
    Object obj[] = new Object[6];
    obj[0]= quote.getLastPrice();
    obj[1] = quote.getBidPrice();
    obj[2] = quote.getBidSize();
    obj[3] = quote.getAskPrice();
    obj[4] = quote.getAskSize();
    obj[5] = quote.getTicker();
    return obj;
  }
}
