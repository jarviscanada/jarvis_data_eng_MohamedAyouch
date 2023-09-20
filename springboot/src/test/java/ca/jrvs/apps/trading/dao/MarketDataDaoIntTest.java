package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.config.MarketDataConfig;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class MarketDataDaoIntTest {

  private MarketDataDao dao;

  @Before
  public void init(){
    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    cm.setMaxTotal(50);
    cm.setDefaultMaxPerRoute(50);
    MarketDataConfig marketDataConfig = new MarketDataConfig();
    marketDataConfig.setHost("https://cloud.iexapis.com/v1/");
    marketDataConfig.setToken(System.getenv("IEX_PUB_TOKEN"));

    dao = new MarketDataDao(cm, marketDataConfig);
  }

  @Test
  public void findIexQuotesByTicker() throws IOException {
    List<IexQuote> quotes = dao.findAllById(Arrays.asList("AAPL","FB"));
    assertEquals(2,quotes.size());
    assertEquals("AAPL", quotes.get(0).getSymbol());

  }

  @Test
  public void findByTicker (){
    String ticker = "AAPL";
    IexQuote iexQuote = dao.findById(ticker).get();
    assertEquals(ticker, iexQuote.getSymbol());

  }
}
