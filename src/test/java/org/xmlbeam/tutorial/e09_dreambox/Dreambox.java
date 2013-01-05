package org.xmlbeam.tutorial.e09_dreambox;

import java.util.List;

import org.xmlbeam.URL;
import org.xmlbeam.Xpath;

/**
 * A Dreambox is a hard disk video recorder with a web based interface that works with XML data
 * exchange from multiple URLs. This example is provided to show a real life use case for
 * integration of multiple external documents to one Java interface. You are surly not able to run
 * this example without access to specific version of this hard disk recorder. Therefore there is no
 * example code provided which uses this interface.
 */
public interface Dreambox {

    // This is the root URL of my device.
    final static String BASE_URL = "http://192.168.1.44/web";

    @URL(BASE_URL + "/movielist?dirname={0}")
    @Xpath(value = "/e2movielist/e2movie", targetComponentType = Movie.class)
    List<Movie> getMovies(String location);

    @URL(BASE_URL + "/getservices?sRef=1%3A7%3A1%3A0%3A0%3A0%3A0%3A0%3A0%3A0%3AFROM%20BOUQUET%20%22userbouquet.favourites.tv%22%20ORDER%20BY%20bouquet")
    @Xpath(value = "/e2servicelist/e2service", targetComponentType = Service.class)
    List<Service> getServices();

    @URL(BASE_URL + "/web/epgservice?sRef={0}")
    @Xpath(value = "/e2eventlist/e2event", targetComponentType = Event.class)
    List<Event> getEvents(String serviceReference);

    @URL(BASE_URL + "/web/getlocations")
    @Xpath(value = "e2locations/e2location", targetComponentType = String.class)
    List<String> getLocations();

}
