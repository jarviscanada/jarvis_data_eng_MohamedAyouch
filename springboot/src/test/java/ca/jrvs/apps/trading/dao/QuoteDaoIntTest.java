package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.TestConfig;
import ca.jrvs.apps.trading.model.domain.Quote;
import java.util.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class})
@Sql({"classpath:schema.sql"})
public class QuoteDaoIntTest {

  @Autowired
  private QuoteDao quoteDao;

  private Quote savedQuote;

  @Before
  public void insertOne(){
    savedQuote = new Quote();
    savedQuote.setTicker("aapl");
    savedQuote.setAskPrice(10d);
    savedQuote.setAskSize(10);
    savedQuote.setBidPrice(10.2d);
    savedQuote.setBidSize(10);
    savedQuote.setLastPrice(10.1d);
    quoteDao.save(savedQuote);
  }

  @Test
  public void findByIdTest(){
    Quote quote = quoteDao.findById("aapl").get();

    assertTrue(quote.getTicker().contentEquals(savedQuote.getTicker()));

  }

  @Test
  public void updateByIdTest(){
   Quote quote = new Quote();
    quote.setTicker("aapl");
    quote.setAskPrice(8d);
    quote.setAskSize(7);
    quote.setBidPrice(8.5d);
    quote.setBidSize(9);
    quote.setLastPrice(12.3d);

    quoteDao.save(quote);

    Quote savedQuote = quoteDao.findById("aapl").get();

    assertEquals(Optional.ofNullable(savedQuote.getAskPrice()),Optional.of(8d));

  }
  @After
  public void deleteOne(){
    quoteDao.deleteById(savedQuote.getId());
  }
}
