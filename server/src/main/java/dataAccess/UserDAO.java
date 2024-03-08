package dataAccess;
import model.UserData;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class UserDAO {

    static Map<String, UserData> userMap = new HashMap<>();

    public static void insertUser(UserData userInfo) throws DataAccessException, SQLException {
        userMap.put(userInfo.getUserName(), userInfo);
    }

    public UserData findUser(String userName) throws DataAccessException {
        return userMap.get(userName);
    }

    public void clearTokens() throws DataAccessException {
        userMap.clear();
    }

}
