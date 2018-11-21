package types;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

    private long userID;
    private long sessionID;

    public User(){

    }
    public User(long userID, long sessionID){
        this.userID = userID;
        this.sessionID = sessionID;
    }

    @JsonProperty
    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    @JsonProperty
    public long getSessionID() {
        return sessionID;
    }

    public void setSessionID(long sessionID) {
        this.sessionID = sessionID;
    }
}
