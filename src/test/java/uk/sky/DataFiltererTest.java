package uk.sky;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.Collection;

import static org.junit.Assert.assertTrue;
import static uk.sky.DataFilterer.calculateAvgResponseTime;
import static uk.sky.DataFilterer.readRequests;

public class DataFiltererTest {
    @Test
    public void whenEmpty() throws FileNotFoundException {
        assertTrue(DataFilterer.filterByCountry(openFile("src/test/resources/empty"), "GB").isEmpty());
    }

    private FileReader openFile(String filename) throws FileNotFoundException {
        return new FileReader(new File(filename));
    }

    @Test
    public void whenFilteredByACountryOnlySameCountryRequestsAreReturned() throws FileNotFoundException {
        Collection<?> reqs = DataFilterer.filterByCountry(openFile("src/test/resources/multi-lines"), "GB");
        assertTrue(allRequestsFromSameCountry(reqs, Request.CountryCode.GB));
        reqs = DataFilterer.filterByCountry(openFile("src/test/resources/single-line"), "GB");
        assertTrue(allRequestsFromSameCountry(reqs, Request.CountryCode.GB));
    }

    private boolean allRequestsFromSameCountry(Collection<?> requests, Request.CountryCode gb) {
        for (Object req:requests){
            if (((Request)req).getCountryCode()!=gb){
                return false;
            }
        }
        return true;
    }


    @Test
    public void whenFilteredByACountryWithResponseTimeAboveLimitOnlySameCountryRequestsWithTimeAboveLimitAreReturned() throws FileNotFoundException {
        long responseTimeLimit = 190L;
        Collection<?> reqs = DataFilterer.filterByCountryWithResponseTimeAboveLimit(openFile("src/test/resources/multi-lines"), "GB", responseTimeLimit);
        assertTrue(allRequestsFromSameCountry(reqs, Request.CountryCode.GB));
        assertTrue(allRequestsHaveResponseTimeAboveLimit(reqs, responseTimeLimit));

        reqs = DataFilterer.filterByCountryWithResponseTimeAboveLimit(openFile("src/test/resources/single-line"), "GB", responseTimeLimit);
        assertTrue(allRequestsFromSameCountry(reqs, Request.CountryCode.GB));
        assertTrue(allRequestsHaveResponseTimeAboveLimit(reqs, responseTimeLimit));

        //TODO move into separate test, tests with empty collection
        responseTimeLimit = 200L;
        reqs = DataFilterer.filterByCountryWithResponseTimeAboveLimit(openFile("src/test/resources/single-line"), "GB", responseTimeLimit);
        assertTrue(allRequestsFromSameCountry(reqs, Request.CountryCode.GB));
        assertTrue(allRequestsHaveResponseTimeAboveLimit(reqs, responseTimeLimit));

    }

    private boolean allRequestsHaveResponseTimeAboveLimit(Collection<?> requests, double responseTimeLimit) {
        for (Object req:requests){
            if (((Request)req).getTimeTaken()<=responseTimeLimit){
                return false;
            }
        }
        return true;
    }

    @Test
    public void whenFilteredByAverageResponseTimeAllRequestsHaveGreaterTime() throws FileNotFoundException {
        Collection<Request> reqs = readRequests(openFile("src/test/resources/multi-lines"));
        double average = calculateAvgResponseTime(reqs);
        Collection<?> avgRequests = DataFilterer.filterByResponseTimeAboveAverage(openFile("src/test/resources/multi-lines"));

        assertTrue(allRequestsHaveResponseTimeAboveLimit(avgRequests, average));
        reqs = readRequests(openFile("src/test/resources/single-line"));
        average = calculateAvgResponseTime(reqs);
        avgRequests = DataFilterer.filterByResponseTimeAboveAverage(openFile("src/test/resources/single-line"));

        assertTrue(allRequestsHaveResponseTimeAboveLimit(avgRequests, average));

    }


    //TODO test corner cases
}
