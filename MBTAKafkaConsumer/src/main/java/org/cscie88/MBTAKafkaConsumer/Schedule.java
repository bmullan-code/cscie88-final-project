package org.cscie88.MBTAKafkaConsumer;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Schedule {

    private String trip;
    private String stop;
    private String json = null;
    private String url; 
    private final String urlTemplate = this.url = "https://api-v3.mbta.com/schedules?filter[trip]=%s&filter[stop]=%s";
    transient private DocumentContext ctx = null;

    private static final Logger logger = LoggerFactory.getLogger(Schedule.class);

    // https://api-v3.mbta.com/schedules?filter[trip]=50891559&filter[stop]=70072
    public Schedule(String trip, String stop) {

        this.trip = trip;
        this.stop = stop;
        this.url = String.format(urlTemplate, trip,stop);
        logger.info("Schedule url:{}",this.url);
        this.readSchedule();
        if (this.json!=null)
            this.ctx = JsonPath.parse(this.json);

    }

    // "updated_at": "2022-05-07T19:15:22-04:00"
    // "arrival_time": "2022-05-07T19:05:00-04:00",

    public String getArrivalTime() {

        if (this.json==null||this.ctx==null)
            return null;

        return ctx.read("$.data[0].attributes.arrival_time").toString();

    }

    public String getStatus(String updatedAt) {

        logger.info("ScheduleStatus: Called with {}",updatedAt);
        logger.info("ScheduleStatus: Called with {}",this.getArrivalTime());
        if (this.getArrivalTime()==null||this.getArrivalTime()==""||updatedAt==null||updatedAt=="")
            return "";


        Instant scheduleInstant = Instant.parse(this.getArrivalTime());
        Instant updateInstant = Instant.parse(updatedAt);
        
        //get date time only
        ZonedDateTime scheduleDateTime = ZonedDateTime.ofInstant(scheduleInstant, ZoneId.of(ZoneOffset.UTC.getId()));
        ZonedDateTime updateDateTime = ZonedDateTime.ofInstant(updateInstant, ZoneId.of(ZoneOffset.UTC.getId()));

        logger.info("ScheduleStatus: s:{} u:{}",scheduleDateTime,updateDateTime);

        if (updateDateTime.isAfter(scheduleDateTime)) {
            logger.info("ScheduleStatus: Late");
            Duration d = Duration.between( scheduleDateTime , updateDateTime );
            return "Late:"+String.valueOf(d.toSeconds());
        } else {
            return "";
        }
    }

    private String readSchedule() {

        // create the http client to send the rest request
        CloseableHttpClient httpSource = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response;
        String result=null;

        try {
            response = httpSource.execute(httpGet);
            logger.info("Schedule: {}", url);
            logger.info("Schedule: {} {} {} {}", response.getProtocolVersion(),
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

            if (response.getStatusLine().getStatusCode()!=200)
                return null;
    
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }


    
}
