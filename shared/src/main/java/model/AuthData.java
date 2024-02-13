package model;

public class AuthData {

    String AuthToken;
    String userName;

    public AuthData(String authToken, String userName) {
        AuthToken = authToken;
        this.userName = userName;
    }

    public String getAuthToken() {
        return AuthToken;
    }

    public String getUserName() {
        return userName;
    }

}
