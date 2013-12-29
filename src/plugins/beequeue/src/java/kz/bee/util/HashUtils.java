package kz.bee.util;


import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/*
 * Used Matthias Gartner's PKCS#5 implementation - see
 * http://rtner.de/software/PBKDF2.html
 * 
 * <p>
 * Free auxiliary functions. Copyright (c) 2007 Matthias G&auml;rtner
 * </p>
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * </p>
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * </p>
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 * </p>
 * <p>
 * For Details, see <a
 * href="http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html"
 * >http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html</a>.
 * </p>
 */

 /** 
 * Contains basic utility methods for hashing and converting binary arrays to hex strings.
 * Makes iterations iterations to find hash values
 * @author Matthias Gartner
 * @author Burmaganov Didar
 * @version 1.1 Beta
 */
public class HashUtils{
	public static final String hex = "0123456789ABCDEF";
	public static final int iterations = 1000;
	
	public static String getHashString(String password,String salt) throws NoSuchAlgorithmException, UnsupportedEncodingException, GeneralSecurityException{
		return bin2hex(getHash(iterations, createPasswordKey(password,salt), hex2bin(salt)));
	}
	
	public static String getSaltString(){
		return bin2hex(generateRandomSalt());
	}
	
	public static String getHashString(String password,byte[] salt) throws NoSuchAlgorithmException, UnsupportedEncodingException, GeneralSecurityException{
		return bin2hex(getHash(iterations, createPasswordKey(password,salt), salt));
	}
	
	/**
	 * Takes hash value of password n times using md5 and one more time using sha  
	 * */
	public static byte[] getHash(int iterationNb, String password, byte[] salt)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.reset();
		md5.update(salt);
		
		byte[] input = md5.digest(password.getBytes("UTF-8"));
		for (int i = 0; i < iterationNb; i++) {
			md5.reset();
			input = md5.digest(input);
		}
		
		MessageDigest sha = MessageDigest.getInstance("SHA");
		sha.reset();
		sha.update(salt);
		return sha.digest(input);
	}

	/**
	 * Simple binary-to-hexadecimal conversion.
	 * 
	 * @param b
	 *            Input bytes. May be <code>null</code>.
	 * @return Hexadecimal representation of b. Uppercase A-F, two characters
	 *         per byte. Empty string on <code>null</code> input.
	 */
	public static String bin2hex(final byte[] b) {
		if (b == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer(2 * b.length);
		for (int i = 0; i < b.length; i++) {
			int v = (256 + b[i]) % 256;
			sb.append(hex.charAt((v / 16) & 15));
			sb.append(hex.charAt((v % 16) & 15));
		}
		return sb.toString();
	}

	public static byte[] generateRandomSalt() {
		byte[] salt = new byte[8];
		new SecureRandom().nextBytes(salt);
		return salt;
	}
	
	public static String createPasswordKey(String password,String salt) throws UnsupportedEncodingException, GeneralSecurityException{
		return createPasswordKey(password, hex2bin(salt));
	}

	public static String createPasswordKey(String password, byte[] salt) throws GeneralSecurityException, UnsupportedEncodingException {
		byte[] generatedPassword = getHash(iterations, password, salt);
		byte[] generatedReversedPassword = getHash(iterations, bin2hex(salt), password.getBytes());
		byte[] result = new byte[generatedPassword.length+generatedReversedPassword.length];
		int c = 0;
		for(int i=0;i<generatedPassword.length;i++){
			result[c++] = generatedPassword[i];
		}
		for(int j=0;j<generatedReversedPassword.length;j++){
			result[c++] = generatedReversedPassword[j];
		}
		return bin2hex(result);
	}

	/**
	 * Convert hex string to array of bytes.
	 * 
	 * @param s
	 *            String containing hexadecimal digits. May be <code>null</code>
	 *            . On odd length leading zero will be assumed.
	 * @return Array on bytes, non-<code>null</code>.
	 * @throws IllegalArgumentException
	 *             when string contains non-hex character
	 */
	public static byte[] hex2bin(final String s) {
		String m = s;
		if (s == null) {
			// Allow empty input string.
			m = "";
		} else if (s.length() % 2 != 0) {
			// Assume leading zero for odd string length
			m = "0" + s;
		}
		byte r[] = new byte[m.length() / 2];
		for (int i = 0, n = 0; i < m.length(); n++) {
			char h = m.charAt(i++);
			char l = m.charAt(i++);
			r[n] = (byte) (hex2bin(h) * 16 + hex2bin(l));
		}
		return r;
	}

	/**
	 * Convert hex digit to numerical value.
	 * 
	 * @param c
	 *            0-9, a-f, A-F allowd.
	 * @return 0-15
	 * @throws IllegalArgumentException
	 *             on non-hex character
	 */
	public static int hex2bin(char c) {
		if (c >= '0' && c <= '9') {
			return (c - '0');
		}
		if (c >= 'A' && c <= 'F') {
			return (c - 'A' + 10);
		}
		if (c >= 'a' && c <= 'f') {
			return (c - 'a' + 10);
		}
		throw new IllegalArgumentException("Input string may only contain hex digits, but found '" +c+ "'");
	}
}