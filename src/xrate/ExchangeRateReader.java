package xrate;

import java.net.*;
import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
/**
 * Provide access to basic currency exchange rate services.
 * 
 * @author Danish Malik and Kyle DeBates
 */
public class ExchangeRateReader {
    private final String baseURL;
    /**
     * Construct an exchange rate reader using the given base URL. All requests
     * will then be relative to that URL. If, for example, your source is Xavier
     * Finance, the base URL is http://api.finance.xaviermedia.com/api/ Rates
     * for specific days will be constructed from that URL by appending the
     * year, month, and day; the URL for 25 June 2010, for example, would be
     * http://api.finance.xaviermedia.com/api/2010/06/25.xml
     * 
     * @param baseURL
     *            the base URL for requests
     */
    public ExchangeRateReader(String baseURL) {
        this.baseURL = baseURL;
    }

    /**
     * Get the exchange rate for the specified currency against the base
     * currency (the Euro) on the specified date.
     * 
     * @param currencyCode
     *            the currency code for the desired currency
     * @param year
     *            the year as a four digit integer
     * @param month
     *            the month as an integer (1=Jan, 12=Dec)
     * @param day
     *            the day of the month as an integer
     * @return the desired exchange rate
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public float getExchangeRate(String currencyCode, int year, int month, int day) {
          try{		  
              String fullURL = baseURL + year + "/" + String.format("%02d", month) + "/" + String.format("%02d", day) + ".xml";
	     
	      Document doc = createDocument(fullURL);

               NodeList nodeList = doc.getElementsByTagName("fx");

                for(int i = 0; i < nodeList.getLength(); i++){
          
			Element current = (Element) nodeList.item(i);
          		String currentCurrencyCode = getTagData(current, "currency_code");

          		if(currencyCode.equals(currentCurrencyCode)){
           	           return Float.parseFloat(getTagData(current, "rate"));
	  		}
                }
           
	  }catch(Exception ex){}
       		// Return -1.0 if the exchange rate could not be calculated
             return -1.0f;
    }

    /**
     * Get the exchange rate of the first specified currency against the second
     * on the specified date.
     * 
     * @param currencyCode
     *            the currency code for the desired currency
     * @param year
     *            the year as a four digit integer
     * @param month
     *            the month as an integer (1=Jan, 12=Dec)
     * @param day
     *            the day of the month as an integer
     * @return the desired exchange rate
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public float getExchangeRate(
            String fromCurrency, String toCurrency,
            int year, int month, int day) {
        
	    float fromCurrencyRate = -1.0f, toCurrencyRate = -1.0f;

	    try{
                String fullURL = baseURL + year + "/" + String.format("%02d", month) + "/" + String.format("%02d", day) + ".xml";

                Document doc = createDocument(fullURL);

                NodeList nodeList = doc.getElementsByTagName("fx");


               for(int i = 0; i < nodeList.getLength(); i++){

                Element current = (Element) nodeList.item(i);

                String currentCurrencyCode = getTagData(current, "currency_code");

                      if(fromCurrency.equals(currentCurrencyCode)){
                          System.out.println(getTagData(current,"rate"));
                          fromCurrencyRate = Float.parseFloat(getTagData(current, "rate"));
                       }
		      else if(toCurrency.equals(currentCurrencyCode)){
                          System.out.println(getTagData(current,"rate"));
                          toCurrencyRate = Float.parseFloat(getTagData(current, "rate"));
                      }
		}


             System.out.println(fromCurrencyRate + " " + toCurrencyRate);
                     if(fromCurrencyRate != -1.0 && toCurrencyRate != -1.0){
                      return fromCurrencyRate / toCurrencyRate;
		     }
                } catch(Exception ex){}

           // Return -1.0 if the exchange rate could not be calculated
           return fromCurrencyRate;
    }


       public String getTagData(Element fx, String tagName){
 	       NodeList elements = fx.getElementsByTagName(tagName);
              
	       Element firstElement = (Element) elements.item(0);
               NodeList children = firstElement.getChildNodes();
              
	       String componentText = children.item(0).getNodeValue();

          return componentText;
       }//end getTagData

        public Document createDocument(String stringURL) throws MalformedURLException, IOException, ParserConfigurationException, SAXException{
	       URL url = new URL(stringURL);
               InputStream inputStream = url.openStream();

               DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
               DocumentBuilder builder = builderFactory.newDocumentBuilder();
               Document doc = builder.parse(inputStream);
               doc.getDocumentElement().normalize();

           return doc;
        }//end createDocument
}

