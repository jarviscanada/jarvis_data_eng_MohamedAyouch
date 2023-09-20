package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.DataAccessException;
import ca.jrvs.apps.trading.dao.MarketDataDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.dao.ResourceNotFoundException;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import ca.jrvs.apps.trading.model.domain.Quote;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class QuoteService {

  private static final Logger logger = LoggerFactory.getLogger(QuoteService.class);

  private QuoteDao quoteDao;
  private MarketDataDao marketDataDao;

  @Autowired
  public QuoteService(QuoteDao quoteDao, MarketDataDao marketDataDao) {
    this.quoteDao = quoteDao;
    this.marketDataDao = marketDataDao;
  }

  /**
   * Update quote table against IEX source
   *
   * @throws ResourceNotFoundException if ticker is not found from IEX
   * @throws DataAccessException       if unable to retrieve data
   * @throws IllegalArgumentException  for invalid input
   */
  public void updateMarketData()
      throws ResourceNotFoundException, DataAccessException, IllegalArgumentException {

    List<String> tickers = new ArrayList<>();
    tickers.add("FB");
    tickers.add("AMZN");
    tickers.add("MSFT");
    tickers.add("AAPL");

    List<Quote> quotes = saveQuotes(tickers);

    if (quotes == null) {
      throw new DataAccessException("Unable to retrieve data");
    }

  }

  /**
   * Helper function to convert a IexQuote to a Quote
   * @param iexQuote
   * @return quote
   */
  protected static Quote buildQuoteFromIexQuote (IexQuote iexQuote){
    Quote savedQuote = new Quote();
    savedQuote.setTicker(iexQuote.getSymbol());
    savedQuote.setBidSize(iexQuote.getIexBidSize());
    savedQuote.setBidPrice(iexQuote.getIexBidPrice());
    savedQuote.setAskPrice(iexQuote.getIexAskPrice());
    savedQuote.setAskSize(iexQuote.getIexAskSize());
    savedQuote.setLastPrice(iexQuote.getLatestPrice() !=null ? iexQuote.getLatestPrice() : 0);

    return savedQuote;
  }

  /**
   * Validate against IEX and save tickers to quote table
   * @param tickers
   * @return
   * @throws ResourceNotFoundException
   */
  public List<Quote> saveQuotes(List<String> tickers) throws ResourceNotFoundException {

    List<Quote> quotes = new ArrayList<>();

    for(String ticker: tickers){
      Quote quote = saveQuote(ticker);
      quotes.add(quote);
    }
    return quotes;
  }

  /**
   * Helper method
   * @param ticker
   * @return quote
   * @throws ResourceNotFoundException
   */
  public Quote saveQuote(String ticker) throws ResourceNotFoundException {

    IexQuote iexQuote = findIexQuoteByTicker(ticker);

    Quote quote = buildQuoteFromIexQuote(iexQuote);

    if (quoteDao.save(quote) == null) {
      throw new IllegalArgumentException("Invalid inputs");
    }

    return quote;
  }
  /**
   * @param ticker id
   * @return IexQuote object
   * @throws IllegalArgumentException if ticker is invalid
   */
  public IexQuote findIexQuoteByTicker(String ticker) {
    return marketDataDao.findById(ticker)
        .orElseThrow(() -> new IllegalArgumentException(ticker + " is invalid"));
  }

  /**
   * Update a given quote to quote table without validation
   * @param quote
   * @return
   */
  public Quote saveQuote(Quote quote){
    return quoteDao.save(quote);
  }

  /**
   * Find all quotes from the quote table
   * @return iterable of quotes
   */
  public List<Quote> findAllQuotes(){
    return quoteDao.findAll();
  }

}
