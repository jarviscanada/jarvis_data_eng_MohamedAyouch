package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.TestConfig;
import ca.jrvs.apps.trading.dao.DataAccessException;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.dao.ResourceNotFoundException;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import ca.jrvs.apps.trading.model.domain.Quote;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class})
@Sql({"classpath:schema.sql"})
public class QuoteServiceIntTest {

  @Autowired
  private QuoteService quoteService;

  @Autowired
  private QuoteDao quoteDao;

  @Before
  public void Setup(){quoteDao.deleteAll();}

  @Test
  public void findIexQuoteByTicker(){
    IexQuote iexQuote = quoteService.findIexQuoteByTicker("AAPL");

    assertEquals(iexQuote.getSymbol(),"AAPL" );
  }

  @Test
  public void updateMarketData() throws ResourceNotFoundException, DataAccessException {
    quoteService.updateMarketData();

    assertTrue(quoteDao.existsById("AAPL"));
    assertTrue(quoteDao.existsById("FB"));
    assertTrue(quoteDao.existsById("AMZN"));
    assertTrue(quoteDao.existsById("MSFT"));
  }

  @Test
  public void saveQuotes() throws ResourceNotFoundException {
    List<String> tickers = new ArrayList<>();
    tickers.add("FB");
    tickers.add("AMZN");

    List<Quote> quotes = quoteService.saveQuotes(tickers);

    assertEquals(quotes.get(0).getTicker(),"FB");
    assertEquals(quotes.get(1).getTicker(),"AMZN");
  }

  @Test
  public void saveQuote() throws ResourceNotFoundException {
    String ticker = "MSFT";
    Quote quote = quoteService.saveQuote(ticker);
    assertEquals(quote.getTicker(),"MSFT");
  }

  @Test
  public void findAllQuotes() throws ResourceNotFoundException, DataAccessException {

    quoteService.updateMarketData();
    List<Quote> quotes = quoteService.findAllQuotes();

    assertTrue(quotes.size() == 4);
  }

}
