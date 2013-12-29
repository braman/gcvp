package kz.bee.cloud.queue.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserAlreadyExistsException;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.jivesoftware.openfire.user.UserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueUserProvider implements UserProvider {
	
	private static final Logger log = LoggerFactory.getLogger(QueueUserProvider.class);

	private final String loadUserSQL = "SELECT" +
			"	username_ username," +
			"	email_ email," +
			"	created_ created," +
			"	modified_ modified," +
			"	lastname_||' '||firstname_ fullname" +
			" FROM" +
			"	q_user" +
			" WHERE" +
			"	username_=?";
	private final String createUserSQL = "INSERT INTO q_user(" +
			"discriminator_, userid_, email_, passwordhash_, passwordsalt_,"+ 
            "role_, status_, username_, firstname_, lastname_, group_, properties_, created_, modified_)"+
            "VALUES (?, nextval('hibernate_sequence'), ?, ?, ?, ?, ?, ?, null, null, ?, null, current_timestamp, current_timestamp)";
	private final String deleteUserSQL = "UPDATE q_user SET status_='DISABLED' WHERE username_=?";
	private final String userCountSQL = "select count(*) FROM q_user where status_='ENABLED'";
	private final String allUsersSQL = "SELECT" +
			"	username_ username," +
			"	email_ email," +
			"	created_ created," +
			"	modified_ modified," +
			"	lastname_||' '||firstname_ fullname" +
			" FROM" +
			"	q_user" +
			" ORDER BY username_";
	private final String allUsersPaginatedSQL = "SELECT" +
			"	username_ username," +
			"	email_ email," +
			"	created_ created," +
			"	modified_ modified," +
			"	lastname_||' '||firstname_ fullname" +
			" FROM" +
			"	q_user" +
			" ORDER BY username_"+
			" OFFSET ? LIMIT ?";
	private final String allUsernamesSQL ="SELECT" +
			"	username_" +
			" FROM" +
			"	q_user" +
			" ORDER BY username_";
	
	@Override
	public User loadUser(String username) throws UserNotFoundException {
		Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
        	log.debug("Loading user with username:"+username);
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(loadUserSQL);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            // If the query had no results, the username and password
            // did not match a user record. Therefore, throw an exception.
            if (!rs.next()) {
                throw new UserNotFoundException("user.notfound:"+username);
            }
            return new User(rs.getString("username"),rs.getString("fullname"),rs.getString("email"),rs.getDate("created"),rs.getDate("modified"));
        }catch (Exception e) {
        	e.printStackTrace();
        	return null;
        }finally {
            DbConnectionManager.closeConnection(rs, pstmt, con);
        }
	}

	@Override
	public User createUser(String username, String password, String name,String email) throws UserAlreadyExistsException {
		Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(createUserSQL);
            if(name!=null){
            	if(name.trim().toLowerCase().contains("kiosk")){
                	pstmt.setString(1,"U");
                	pstmt.setString(5,"KIOSK");
                }
            	if(name.trim().toLowerCase().contains("unit")){
                	pstmt.setString(1,"O");	
                	pstmt.setString(5,"UNIT");
                }
            	if(name.trim().toLowerCase().contains("dashboard")){
                	pstmt.setString(1,"D");
                	pstmt.setString(5,"DASHBOARD");
                }
            	if(name.contains("group")){//group:test:
            		pstmt.setString(8,name.substring(name.indexOf("group:")+6,name.lastIndexOf(":")));
            	}
            }else{
            	pstmt.setString(1,"U");
            }
            String salt = HashUtils.getSaltString();
            String hash = HashUtils.getHashString(password, salt);
            pstmt.setString(6,"ENABLED");
            pstmt.setString(7,username);
            pstmt.setString(2,email);
            
            pstmt.setString(3, hash);
            pstmt.setString(4, salt);
//          "discriminator_[1], userid_, email_[2], passwordhash_[3], passwordsalt_[4],"+ 
//          "role_[5], status_[6], username_[7], firstname_, lastname_, group_[8], properties_, created_, modified_
            
            int inserted = pstmt.executeUpdate();
            Date now = new Date();
            if (inserted==0) {
                throw new Exception();
            }
            return new User(username, name, email,now,now);
        }catch (Exception e) {
        	e.printStackTrace();
        	return null;	
        }finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
	}

	@Override
	public void deleteUser(String username) {
		Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(deleteUserSQL);
            pstmt.setString(1, username);
            int deleted = pstmt.executeUpdate();
        }catch (Exception e) {
        	e.printStackTrace();
        }finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
	}

	@Override
	public int getUserCount() {
		Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(userCountSQL);
            rs = pstmt.executeQuery();
            if(rs.next()){
            	return rs.getInt(1);
            }
        }catch (Exception e) {
        	e.printStackTrace();
        }finally {
            DbConnectionManager.closeConnection(rs,pstmt, con);
        }
        return 0;
	}

	@Override
	public Collection<User> getUsers() {
		Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<User> userList = new ArrayList<User>();
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(allUsersSQL);
            rs = pstmt.executeQuery();
            
            while(rs.next()){
            	userList.add(new User(rs.getString("username"),rs.getString("fullname"),rs.getString("email"),rs.getDate("created"),rs.getDate("modified")));
            }
        }catch (Exception e) {
        	e.printStackTrace();
        }finally {
            DbConnectionManager.closeConnection(rs, pstmt, con);
        }
        return userList;
	}

	@Override
	public Collection<String> getUsernames() {
		Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<String> userNameList = new ArrayList<String>();
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(allUsernamesSQL);
            rs = pstmt.executeQuery();
            
            while(rs.next()){
            	userNameList.add(rs.getString(1));
            }
        }catch (Exception e) {
        	e.printStackTrace();
        }finally {
            DbConnectionManager.closeConnection(rs, pstmt, con);
        }
        return userNameList;
	}

	@Override
	public Collection<User> getUsers(int startIndex, int numResults) {
		Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<User> userList = new ArrayList<User>();
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(allUsersPaginatedSQL);
            pstmt.setInt(1, startIndex);
            pstmt.setInt(2, numResults);
            rs = pstmt.executeQuery();
            
            while(rs.next()){
            	userList.add(new User(rs.getString("username"),rs.getString("fullname"),rs.getString("email"),rs.getDate("created"),rs.getDate("modified")));
            }
        }catch (Exception e) {
        	e.printStackTrace();
        }finally {
            DbConnectionManager.closeConnection(rs, pstmt, con);
        }
        return userList;
	}

	@Override
	public void setName(String username, String name) throws UserNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEmail(String username, String email) throws UserNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCreationDate(String username, Date creationDate) throws UserNotFoundException {
		// TODO Auto-generated method stub
	}

	@Override
	public void setModificationDate(String username, Date modificationDate) throws UserNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<String> getSearchFields() throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<User> findUsers(Set<String> fields, String query)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<User> findUsers(Set<String> fields, String query,
			int startIndex, int numResults)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNameRequired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmailRequired() {
		// TODO Auto-generated method stub
		return false;
	}
}
