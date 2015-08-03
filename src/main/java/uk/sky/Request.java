package uk.sky;

/**
 * Created by rcastro on 17/06/15.
 */
public class Request {

    private final long timestamp, timeTaken;
    private final CountryCode countryCode;




    public Long getTimestamp() {
        return timestamp;
    }


    public CountryCode getCountryCode() {
        return countryCode;
    }


    public Long getTimeTaken() {
        return timeTaken;
    }


    public Request(long timestamp, CountryCode countryCode, long timeTaken){
        this.timestamp = timestamp;
        this.countryCode = countryCode;
        this.timeTaken = timeTaken;
    }

    public enum CountryCode{
        US, GB, DE
    };
}
