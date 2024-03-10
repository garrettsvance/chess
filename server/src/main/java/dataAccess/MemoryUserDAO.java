package dataAccess;
import model.UserData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;


public class MemoryUserDAO implements UserDAO {

    static Set<UserData> userMap = new HashSet<>();

    public void insertUser(UserData userInfo) throws DataAccessException, SQLException {
        userMap.add(userInfo);
    }

    public UserData verifyUser(UserData userInfo) throws DataAccessException {
        for (UserData account : userMap) {
            if (account.getUserName().equals(userInfo.getUserName()) && account.getPassword().equals(userInfo.getPassword())) {
                return account;
            }
        }
        return null;
    }

    public UserData findUser(String userName) throws DataAccessException {
        for (UserData account : userMap) {
            if (account.getUserName().equals(userName)) {
                return account;
            }
        }
        return null;
    }

    public void clearTokens() throws DataAccessException {
        userMap.clear();
    }

}
