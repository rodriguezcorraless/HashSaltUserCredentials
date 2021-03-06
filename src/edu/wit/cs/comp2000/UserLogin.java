/**
 * A program to read in and modify password files using salted SHA-512 hashes
 */
package edu.wit.cs.comp2000;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class UserLogin {
	String string;
	static Hashtable<String, Password> userTable = new Hashtable<>();	// stores all relevant info about authentication
	private static final Random RandomObj = new SecureRandom();

	/**
	 * Reads lines from the 'Users', parses them, and adds the results to the userTable
	 * 
	 * @param userFile A File object to read from
	 */
	private static void AddUsersToTable(File userFile) {
		// STUB
		try {
			
		Scanner s1= new Scanner(userFile);
			
			
			String username = null;
			String salt = null;
			String hash = null;
		
			while(s1.hasNext()) {
		String nextString = s1.nextLine();
		String user1[]= nextString.split(":");
		username = user1[0];
		salt = user1[1];
		hash = user1[2];
		Password p1 = new Password(hash, salt);
		userTable.put(username, p1);
		
			}
		
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
		System.err.println("File not found");
		}
	}
		

		
	
	/**
	 * Iterates through all key values in userTable and outputs lines
	 * to userFile formatted like user:salt:hash
	 * 
	 * @param userFile A File object that the table should be written to
	 */
	private static void WriteFile(File userFile) {
		// STUB
		Set<String>usernames =userTable.keySet();
		try {
			PrintWriter a = new PrintWriter(userFile);
			
			for(String username: usernames){
				
				Password password = userTable.get(username);
				String saltForUname = userTable.get(username).getSalt();
				String hashForPassword = password.getHash();
				a.println(username+":"+saltForUname+":"+hashForPassword);
				System.out.println(username+":"+saltForUname+":"+hashForPassword); 
			}
			a.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	
	/**
	 * Prompts the user for a username/password and attempts to add the user
	 * to the userTable. Fails if the user is already present in the table.
	 * 
	 * @param s A Scanner to read from the console
	 * @return boolean based on if the user credentials are added to table
	 */
	private static boolean AddUser(Scanner s) {
		// STUB
			System.out.println("Enter user to add:");
			String username = s.nextLine();

			System.out.println("Enter password:");
			String password = s.nextLine();
			String salt = genSalt();
			String pw = salt + password;
			Password p = new Password(genHash(pw), salt);
			userTable.put(username, p);
	
		return true;
	}
	
	
	/**
	 * Prompts the user for a username/password and checks the userTable for
	 * the resulting combination
	 * 
	 * @param s A Scanner to read from the console
	 * @return boolean based on if the user credentials are accurate
	 */
	private static boolean Login(Scanner s) {
		// STUB
		Scanner s1 = new Scanner(System.in);
		
		System.out.println("Username?");
		String unameInput = s1.nextLine();
		
		System.out.println("Password?");
		String passInput = s1.nextLine();
				
				if(userTable.containsKey(unameInput)) { //If the username matches, check if the password matches
				
				Password password = userTable.get(unameInput);
				String saltForUname = userTable.get(unameInput).getSalt();
				String hashForPassword = password.getHash(); //Getting the hash of the password 
				
				String newPassword = saltForUname + passInput;
				String hashForPasswordUserInput = genHash(newPassword); //
				
				String concadenatedsaltAndHash = saltForUname+":"+hashForPassword;
				String concadenatedUserInput = saltForUname+":"+hashForPasswordUserInput;
		

				return concadenatedUserInput.equals(concadenatedsaltAndHash); //compare hashes instead
			}
			
			System.out.println("Username not found");// if the while loop runs out without finding the username this line is printed out.
			return false;
		
		}
		
	


	/**
	 * Generates a salt value based on the SecureRandom object
	 * 
	 * @return Returns an 8-character string that represents a random value
	 */
	private static String genSalt() {
		// TODO Auto-generated method stub
		byte[] salt = new byte[8];
		RandomObj.nextBytes(salt);

		return byteArrayToStr(salt);
	}
	
	/**
	 * Converts an array of bytes to the corresponding hex String
	 * 
	 * @param b An array of bytes
	 * @return A String that represents the array of bytes in hex
	 */
	private static String byteArrayToStr(byte[] b) {
		StringBuffer hexHash =  new StringBuffer();
		for (int i = 0; i < b.length; i++)
		{
			String hexChar = Integer.toHexString(0xff & b[i]);
			if (hexChar.length() == 1)
			{
				hexHash.append('0');
			} // end if
			hexHash.append(hexChar);
		} // end for
		return hexHash.toString();
	}

	/**
	 * Generates a hash for a given String
	 * 
	 * @param p A String to calculate the hash from
	 * @return The hash value as a String
	 */
	private static String genHash(String p) {
		// Create the MessageDigest object
		MessageDigest myDigest = null;
		try {
			myDigest = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			System.err.println("SHA-512 not available");
			System.exit(1);
		}
		// Update the object with the hash of ???????someStringToHash????????
		myDigest.update(p.getBytes());
		
		// Get the SHA-512 hash from the object
		byte hashCode[] = myDigest.digest();

		return byteArrayToStr(hashCode);
	}

	
	public static void main(String[] args) {

		Scanner s = new Scanner(System.in);
		System.out.println("Enter user file, default is userFiles\\Users :");
	
		File userFile = new File(s.nextLine());
		AddUsersToTable(userFile);

		while (true) {
			System.out.print ("Would you like to (A)dd a new user, (L)og in,  or (Q)uit? ");
			char choice = s.nextLine().charAt(0);

			switch (choice) {
			case 'L':
				if (Login(s))
					System.out.println("Login successful.");
				else
					System.out.println("Username and password did not match.");
				break;
			case 'A':
				if (AddUser(s))
					System.out.println("User successfully added.");
				else
					System.out.println("User not added.");
				break;
			case 'Q':
				WriteFile(userFile);
				s.close();
				System.out.println("Exiting.");
				System.exit(0);
			}
		}

	}

}
