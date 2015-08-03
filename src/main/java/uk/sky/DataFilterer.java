package uk.sky;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class DataFilterer {
    public static Collection<?> filterByCountry(Reader source, String country) {
        BufferedReader reader = new BufferedReader(source);
        Collection<Request> res = new ArrayList<>();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!isHeaderLine(line)) {
                    Request req = DataFilterer.createRequestFromLogLineRequest(line);
                    if (req.getCountryCode().name().equals(country)) {
                        res.add(req);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    private static boolean isHeaderLine(String line) {
        return line.startsWith("REQUEST_TIMESTAMP");
    }

    private static Request createRequestFromLogLineRequest(String line) {
        try {
            String[] parts = line.split(",");
            return new Request(Long.parseLong(parts[0]), Request.CountryCode.valueOf(parts[1]), Long.parseLong(parts[2]));
        }
        catch(Exception e){
            //FIXME make dedicated exception class
            throw new RuntimeException("Could not parse Request from line"+line);
        }
    }

    public static Collection<?> filterByCountryWithResponseTimeAboveLimit(Reader source, String country, long limit) {
        BufferedReader reader = new BufferedReader(source);
        Collection<Request> res = new ArrayList<>();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!isHeaderLine(line)) {
                        Request req = DataFilterer.createRequestFromLogLineRequest(line);
                        //TODO could extract into separate method for the time comprison
                        if (req.getCountryCode().name().equals(country) && req.getTimeTaken() > limit) {
                            res.add(req);
                        }
                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public static Collection<?> filterByResponseTimeAboveAverage(Reader source) {
        BufferedReader reader = new BufferedReader(source);
        Collection<Request> res = new ArrayList<>();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!isHeaderLine(line)) {
                    Request req = DataFilterer.createRequestFromLogLineRequest(line);
                    res.add(req);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        double average = calculateAvgResponseTime(res);
        Collection<Request> averaged = new ArrayList<>(res.size());
        for (Request req:res){
            if (req.getTimeTaken()>average)
                averaged.add(req);
        }
        return averaged;
    }

    public static double calculateAvgResponseTime(Collection<Request> requests) {
        if (requests.size()==0)
            return 0;
        long sum = 0;
        for (Request req:requests){
            sum += req.getTimeTaken();
        }
        return sum/requests.size();
    }

    public static Collection<Request> readRequests(Reader source) {
        BufferedReader reader = new BufferedReader(source);
        Collection<Request> res = new ArrayList<>();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!isHeaderLine(line)) {
                    Request req = DataFilterer.createRequestFromLogLineRequest(line);
                    res.add(req);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }
}