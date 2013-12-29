package kz.bee.cloud.queue.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.auth.AuthProvider;
import org.jivesoftware.openfire.auth.ConnectionException;
import org.jivesoftware.openfire.auth.InternalUnauthenticatedException;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * */
public class QueueAuthProvider implements AuthProvider {

	private static final Logger log = LoggerFactory.getLogger(QueueAuthProvider.class);

	@Override
	public boolean isPlainSupported() {
		return false;
	}

	@Override
	public boolean isDigestSupported() {
		return true;
	}

	@Override
	public void authenticate(final String username, final String password) throws UnauthorizedException, ConnectionException, InternalUnauthenticatedException {
		Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
        	log.debug("Authenticating user with username:"+username);
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement("select passwordhash_ hash,passwordsalt_ salt from q_user where username_=? and status_='ENABLED'");
            pstmt.setString(1, username);

            rs = pstmt.executeQuery();

            // If the query had no results, the username and password
            // did not match a user record. Therefore, throw an exception.
            if (!rs.next()) {
                throw new UnauthorizedException("user.notfound:"+username);
            }
            String passwordHash = rs.getString("hash");
            String passwordSalt = rs.getString("salt");
            System.out.println(passwordHash+" / "+passwordSalt);
            String hash = HashUtils.getHashString(password, passwordSalt);
            System.out.println(hash);
            if(!hash.equalsIgnoreCase(passwordHash)){
            	throw new UnauthorizedException("user.wrong.password:"+username);	
            }
        }catch (Exception e) {
        	throw new UnauthorizedException("user.notfound:"+username);
        }finally {
            DbConnectionManager.closeConnection(rs, pstmt, con);
        }
	}

	@Override
	public void authenticate(String username, String token, String digest) throws UnauthorizedException, ConnectionException,InternalUnauthenticatedException {
		// TODO Auto-generated method stub
	}

	@Override
	public String getPassword(String username) throws UserNotFoundException, UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPassword(String username, String password) throws UserNotFoundException, UnsupportedOperationException {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean supportsPasswordRetrieval() {
		return false;
	}
	
	
}
