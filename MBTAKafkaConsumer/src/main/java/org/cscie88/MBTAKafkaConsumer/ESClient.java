package org.cscie88.MBTAKafkaConsumer;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

// Barry Mullan CSCI-E88 Final Project
// The ESClient class interacts with the Elastic Search service
// every 10 seconds, it gets all the route data and writes them to 
// elastic search using the ES rest api 

@Service
public class ESClient {

    private static final Logger logger = LoggerFactory.getLogger(ESClient.class);

    @Value("${MBTA_VIEW_SOURCE_URL:http://localhost:8080/routes/}")
    String mbtaViewSourceUrl;
    @Value("${ES_DEST_URL:http://es-container:9200/mbta_route_data/_doc/}")
    String mbtaElasticSearchDestUrl;

    @Autowired
    RouteService routeService;

    Gson gson = new GsonBuilder().create();

    // using the spring @scheduled annotation this method will be called automatically
    // every 10 seconds
    @Scheduled(fixedDelay = 10000)
    public void run() throws ClientProtocolException, IOException {

        // get the complete list of route id's
        List<String> routes = routeService.findAllRoutes();

        // for each route, get the route data and send it to elastic search
        routes.stream().forEach(
            route -> {         
                String routeData = getRouteData(route);
                sendRouteData(route,routeData);
            }
        );
    }

    // returns the route data for the specified route id
    private String getRouteData(String routeId)  {

        String result = null;
        String url = mbtaViewSourceUrl+routeId;

        // create the http client to send the rest request
        CloseableHttpClient httpSource = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response;

        try {
            response = httpSource.execute(httpGet);
            logger.info("ESClient Route Source URL: {}", url);
            logger.info("ESClient Route Source: {} {} {} {}", response.getProtocolVersion(),
                response.getStatusLine().getStatusCode(),
                response.getStatusLine().getReasonPhrase(),
                response.getStatusLine().toString());
    
            // get the entity response data
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }
            logger.info("ESClient Route Source Result: {}", result);
            httpSource.close();
    
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    // sends the route data to elastic search by doing a put 
    // on the index document with an id of the route id
    public void sendRouteData(String routeId, String routes) {

        // create the http client to send the rest request
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // create a post object
        // HttpPost httpPost = new HttpPost(mbtaElasticSearchDestUrl);
        String url = mbtaElasticSearchDestUrl+routeId;

        // HttpPut httpPost = new HttpPost(mbtaElasticSearchDestUrl);
        HttpPut httpPut = new HttpPut(url);
        
        try {
            httpPut.setEntity(new StringEntity(routes));
            // set the json headers
            httpPut.setHeader("Accept", "application/json");
            httpPut.setHeader("Content-type", "application/json");
            logger.info("ESCLient Dest URL:{}",url);
            // send the request to elastic search
            CloseableHttpResponse response = httpClient.execute(httpPut);
            // log the response code
            logger.info("ESCLient Dest Response:"+ response.getStatusLine().getStatusCode()+ ":" + response.getStatusLine().getReasonPhrase());
            httpClient.close();
        } catch (ClientProtocolException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
