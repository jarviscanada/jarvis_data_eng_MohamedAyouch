package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.config.MarketDataConfig;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public class MarketDataDao implements CrudRepository<IexQuote, String> {

  private static final String IEX_BATCH_PATH = "/stock/market/batch?symbols=%s&types=quote&token=";
  private final String IEX_BATCH_URL;

  private Logger logger = LoggerFactory.getLogger(MarketDataDao.class);

  private HttpClientConnectionManager httpClientConnectManager;

  @Autowired
  public MarketDataDao(HttpClientConnectionManager httpClientConnectManager,
      MarketDataConfig marketDataConfig) {

    this.httpClientConnectManager = httpClientConnectManager;
    IEX_BATCH_URL = marketDataConfig.getHost() + IEX_BATCH_PATH + marketDataConfig.getToken();
  }

  private Optional<String> executeHttpGet(String url) {
    HttpGet httpGet = new HttpGet(url);
    System.out.println("Inside httpGet");

    try {
      HttpResponse response = getHttpClient(url).execute(httpGet);
      int statusCode = response.getStatusLine().getStatusCode();

      if (statusCode == 200) {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
          String responseBody = EntityUtils.toString(entity);
          return Optional.of(responseBody);
        } else {
          return Optional.empty();
        }
      } else if (statusCode == 404) {
        return Optional.empty();
      } else {
        throw new DataRetrievalFailureException(
            "HTTP request failed with status code: " + statusCode);
      }
    } catch (IOException e) {
      throw new DataRetrievalFailureException("HTTP request failed with an exception", e);
    }
  }

  private CloseableHttpClient getHttpClient(String url) {
    return HttpClients.custom().setConnectionManager(httpClientConnectManager)
        .setConnectionManagerShared(true).build();
  }

  @Override
  public <S extends IexQuote> S save(S s) {
    return null;
  }

  @Override
  public <S extends IexQuote> Iterable<S> saveAll(Iterable<S> iterable) {
    return null;
  }

  @Override
  public Optional<IexQuote> findById(String ticker) {
    Optional<IexQuote> iexQuote;
    List<IexQuote> quotes = findAllById(Collections.singletonList(ticker));
    System.out.println("Inside marketDAO");
    if (quotes.size() == 0) {
      return Optional.empty();
    } else if (quotes.size() == 1) {
      System.out.println("MarketDAO works");
      iexQuote = Optional.of(quotes.get(0));
    } else {
      throw new DataRetrievalFailureException("Unexpected number of quotes");
    }
    System.out.println("IexQuote: " + iexQuote);
    return iexQuote;
  }

  @Override
  public boolean existsById(String s) {
    return false;
  }

  @Override
  public Iterable<IexQuote> findAll() {
    return null;
  }

  @Override
  public List<IexQuote> findAllById(Iterable<String> tickers) {
    System.out.println("Inside findAll");
    List<IexQuote> quotes = new ArrayList<>();

    for (String ticker : tickers) {
      if (ticker == null || ticker.trim().isEmpty()) {
        System.out.println("invalid or empty ticker");
        throw new IllegalArgumentException("Invalid or empty ticker: " + ticker);
      }

      String url = IEX_BATCH_URL.replace("symbols=%s","symbols="+ticker);

      System.out.println(url);
      Optional<String> response = executeHttpGet(url);

      if (response.isPresent()) {
        IexQuote quote = parseResponseBody(response.get(),ticker);
        quotes.add(quote);
        System.out.println("findAll works");
      } else {
        System.out.println("no response");
        throw new DataRetrievalFailureException("HTTP request failed for ticker: " + ticker);
      }
    }
    return quotes;
  }

  private IexQuote parseResponseBody(String responseBody, String ticker) {
    JSONObject jsonObject = new JSONObject(responseBody).getJSONObject(ticker).getJSONObject("quote");


    String symbol = jsonObject.optString("symbol", "");
    String companyName = jsonObject.optString("companyName", "");
    String primaryExchange = jsonObject.optString("primaryExchange", "");
    String calculationPrice = jsonObject.optString("calculationPrice", "");
    Double open = jsonObject.optDouble("open", Double.NaN);
    Long openTime = jsonObject.optLong("openTime", 0L);
    String openSource = jsonObject.optString("openSource", "");
    Double close = jsonObject.optDouble("close", Double.NaN);
    Long closeTime = jsonObject.optLong("closeTime", 0L);
    String closeSource = jsonObject.optString("closeSource", "");
    Double high = jsonObject.optDouble("high", Double.NaN);
    Long highTime = jsonObject.optLong("highTime", 0L);
    String highSource = jsonObject.optString("highSource", "");
    Double low = jsonObject.optDouble("low", Double.NaN);
    Long lowTime = jsonObject.optLong("lowTime", 0L);
    String lowSource = jsonObject.optString("lowSource", "");
    Double latestPrice = jsonObject.optDouble("latestPrice", Double.NaN);
    String latestSource = jsonObject.optString("latestSource", "");
    String latestTime = jsonObject.optString("latestTime", "");
    Long latestUpdate = jsonObject.optLong("latestUpdate", 0L);
    Integer latestVolume = jsonObject.optInt("latestVolume", 0);
    Double iexRealtimePrice = jsonObject.optDouble("iexRealtimePrice", Double.NaN);
    Integer iexRealtimeSize = jsonObject.optInt("iexRealtimeSize", 0);
    Long iexLastUpdated = jsonObject.optLong("iexLastUpdated", 0L);
    Double delayedPrice = jsonObject.optDouble("delayedPrice", Double.NaN);
    Long delayedPriceTime = jsonObject.optLong("delayedPriceTime", 0L);
    Double oddLotDelayedPrice = jsonObject.optDouble("oddLotDelayedPrice", Double.NaN);
    Long oddLotDelayedPriceTime = jsonObject.optLong("oddLotDelayedPriceTime", 0L);
    Double extendedPrice = jsonObject.optDouble("extendedPrice", Double.NaN);
    Double extendedChange = jsonObject.optDouble("extendedChange", Double.NaN);
    Double extendedChangePercent = jsonObject.optDouble("extendedChangePercent", Double.NaN);
    Long extendedPriceTime = jsonObject.optLong("extendedPriceTime", 0L);
    Double previousClose = jsonObject.optDouble("previousClose", Double.NaN);
    Integer previousVolume = jsonObject.optInt("previousVolume", 0);
    Double change = jsonObject.optDouble("change", Double.NaN);
    Double changePercent = jsonObject.optDouble("changePercent", Double.NaN);
    Integer volume = jsonObject.optInt("volume", 0);
    Double iexMarketPercent = jsonObject.optDouble("iexMarketPercent", Double.NaN);
    Integer iexVolume = jsonObject.optInt("iexVolume", 0);
    Integer avgTotalVolume = jsonObject.optInt("avgTotalVolume", 0);
    Integer iexBidPrice = jsonObject.optInt("iexBidPrice", 0);
    Integer iexBidSize = jsonObject.optInt("iexBidSize", 0);
    Integer iexAskPrice = jsonObject.optInt("iexAskPrice", 0);
    Integer iexAskSize = jsonObject.optInt("iexAskSize", 0);
    Double iexOpen = jsonObject.optDouble("iexOpen", Double.NaN);
    Long iexOpenTime = jsonObject.optLong("iexOpenTime", 0L);
    Double iexClose = jsonObject.optDouble("iexClose", Double.NaN);
    Long iexCloseTime = jsonObject.optLong("iexCloseTime", 0L);
    Long marketCap = jsonObject.optLong("marketCap", 0L);
    Double peRatio = jsonObject.optDouble("peRatio", Double.NaN);
    Double week52High = jsonObject.optDouble("week52High", Double.NaN);
    Double week52Low = jsonObject.optDouble("week52Low", Double.NaN);
    Double ytdChange = jsonObject.optDouble("ytdChange", Double.NaN);
    Long lastTradeTime = jsonObject.optLong("lastTradeTime", 0L);
    String currency = jsonObject.optString("currency", "");
    Boolean isUSMarketOpen = jsonObject.optBoolean("isUSMarketOpen", false);

    IexQuote iexQuote = new IexQuote(symbol, companyName, primaryExchange, calculationPrice, open,
        openTime,
        openSource, close, closeTime, closeSource, high, highTime, highSource, low, lowTime,
        lowSource,
        latestPrice, latestSource, latestTime, latestUpdate, latestVolume, iexRealtimePrice,
        iexRealtimeSize,
        iexLastUpdated, delayedPrice, delayedPriceTime, oddLotDelayedPrice, oddLotDelayedPriceTime,
        extendedPrice,
        extendedChange, extendedChangePercent, extendedPriceTime, previousClose, previousVolume,
        change,
        changePercent, volume, iexMarketPercent, iexVolume, avgTotalVolume, iexBidPrice, iexBidSize,
        iexAskPrice,
        iexAskSize, iexOpen, iexOpenTime, iexClose, iexCloseTime, marketCap, peRatio, week52High,
        week52Low,
        ytdChange, lastTradeTime, currency, isUSMarketOpen);

    return iexQuote;

  }

  @Override
  public long count() {
    return 0;
  }

  @Override
  public void deleteById(String s) {

  }

  @Override
  public void delete(IexQuote iexQuote) {

  }

  @Override
  public void deleteAll(Iterable<? extends IexQuote> iterable) {

  }

  @Override
  public void deleteAll() {

  }
}
