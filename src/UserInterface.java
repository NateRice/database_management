import java.sql.*;
import java.util.Scanner;
import java.text.NumberFormat;
public class UserInterface {
	final static String driverClass = "org.sqlite.JDBC";
	final static String url = "jdbc:sqlite:autosDB.sqlite";

	public static void main(String[] args) 
	{
		Scanner in = new Scanner(System.in);
		String input="";
		int value;
		System.out.println("*****************************************");
		System.out.println("***** Autos Database User Interface *****");
		
		while(!input.equals("q"))
		{
		System.out.println("Select an option (q to quit):\n\n1. Add a new accident to the database.\n2. Accidents Table Check AID > 100 \n3. Involvements Table Check AID > 100 \n4. Delete added data(AID > 100)\n5. Get Accident By ID\n6. Search for Accidents\n");
		
		input = in.nextLine();
		try {
			value = Integer.parseInt(input);
	    	
			switch (value) 
	        {
	            case 1:
	            		UserInterface.insertAccident();
	                    break;
	            case 2: 
	            	    UserInterface.accidentsQuery();
	            		break;
	            case 3: 
	            		UserInterface.involvementsQuery();
	            		
	                    break;
	            case 4:
	            		UserInterface.deleteAddedData();
	            		break;
	            case 5: 
	            		UserInterface.accidentsQueryByID();
	            		break;
	            case 6:
	            		UserInterface.searchForAccidents();
	            		break;
	           default: System.out.println("Invalid choice\n");		
	        }
			}
		catch(NumberFormatException e)
		{
			if(input.equals("q"))
			{
				System.out.println("Quitting...");
				in.close();
				System.exit(0);
			}
			else
				System.out.println("Invalid input\n");
		}
		}
	in.close();
		
	}
	
	//method to get a new AID for the new row in the Accidents Table we are going to insert
	public static int maxAidQuery() 
	{
		Connection connection = null;
		String query = null;
		int insertedAid = 0;
		try 
		{
			// Load the JDBC drivers
			Class.forName(driverClass);

			// Open a DB Connection
			//connection = DriverManager.getConnection(url, username, password);
			connection = DriverManager.getConnection(url);

			// Create a Statement
			Statement stmnt = connection.createStatement();

			// Execute a query to get the maximum AID value from the accidents table
			query = "select max(aid) from Accidents";

			ResultSet results = stmnt.executeQuery(query);

			// move cursor to first line, set the max AID value to maxAid
			results.next(); 
			int maxAid = results.getInt(1);
			insertedAid = maxAid + 1; //set the AID for the inserted row in the Accidents table

			
		} 
		catch (Exception e) 
		{
			System.out.println(query);
			e.printStackTrace();
		} 
		finally 
		{
			try 
			{
				connection.close();
			} 
			catch (Exception e) 
			{
			}
		}
		return insertedAid;
	}
	
	//Add a new accident to the database:
	//The user needs to be able to enter the date and location of the accident, to be stored in the Accidents table.
	//For each automobile involved in the accident, the user must be able to enter the information to be stored in the Involvements table.
	public static void insertAccident() 
	{
		//get unique aid for the new accident entry 
		int newAid = maxAidQuery();
		//get input from user for data to be inserted in the Accidents table
		Scanner in = new Scanner(System.in);
		System.out.println("*** New Accident ***\n\nPlease enter the DATE of the accident (year-month-day xxxx-xx-xx): ");
		String accidentDate = in.nextLine();
		System.out.println("Please enter the CITY in which the accident occured: ");
		String accidentCity = in.nextLine();
		accidentCity = accidentCity.toUpperCase();
		System.out.println("Please enter the STATE in which the accident occured (state abbreviations only ex. Massachusetts = MA): ");
		String accidentState = in.nextLine();
		accidentState = accidentState.toUpperCase();
		String accidentsInsertStatement = String.format("insert into Accidents (aid, accident_date, city, state) values (%d, '%s', '%s', '%s')", newAid, accidentDate, accidentCity, accidentState);
		
		Connection connection = null;
		
		try 
		{
			// Load the JDBC drivers
			Class.forName(driverClass);

			// Open a DB Connection
			//connection = DriverManager.getConnection(url, username, password);
			connection = DriverManager.getConnection(url);

			// Create a Statement
			Statement stmnt = connection.createStatement();
			
			// Execute an Update
			stmnt.executeUpdate(accidentsInsertStatement);

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			try 
			{
				connection.close();
			} 
			catch (Exception e) 
			{
			}
		}
		
		//Involvements(aid - pk int, vin - pk varchar(12), damages - float, driver_ssn - char(11))
		//get input from user for data to be inserted
		boolean moreAutos = true; //bool to control while loop 
		
		while (moreAutos == true)
		{
			System.out.println("\n*** Automobiles Involved in the Accident ***\n");
			System.out.println("AID = " + newAid + "\n");
			System.out.println("Please enter the VIN of the automobile: "); 
			String vin = in.nextLine();
				
			System.out.println("Please enter the amount of damages: ");
			float damages = Float.parseFloat(in.nextLine());
					
			System.out.println("Please enter the driver ssn: ");
			String driverSSN = in.nextLine();
					
			String involvementsInsertStatement = String.format("insert into Involvements (aid, vin, damages, driver_ssn) values (%d, '%s', '%f', '%s')", newAid, vin, damages, driverSSN);
			
			connection = null;
			
			try 
			{
				// Load the JDBC drivers
				Class.forName(driverClass);

				// Open a DB Connection
				//connection = DriverManager.getConnection(url, username, password);
				connection = DriverManager.getConnection(url);

				// Create a Statement
				Statement stmnt = connection.createStatement();
				
				// Execute an Update				
				stmnt.executeUpdate(involvementsInsertStatement);

			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			} 
			finally 
			{
				try 
				{
					connection.close();		
				} 
				catch (Exception e) 
				{
				}
			}
			
			System.out.println("Enter another automobile? (y/n):");
			String yesNo = in.nextLine();
			if (yesNo.equals("n"))
			{
				moreAutos = false;
			}
			
		}
		
		
	}
	
	//check on accidents table for aid > 100 to see what we've added
	public static void accidentsQuery() 
	{
		Connection connection = null;
		String query = null;

		try {
			// Load the JDBC drivers
			Class.forName(driverClass);

			// Open a DB Connection
			//connection = DriverManager.getConnection(url, username, password);
			connection = DriverManager.getConnection(url);

			// Create a Statement
			Statement stmnt = connection.createStatement();

			// Execute a query
			query = "select * from Accidents where aid>100";
			ResultSet results = stmnt.executeQuery(query);

			// Print out the results
			while (results.next()) 
			{
				int aid = results.getInt(1);
				String date = results.getString(2);
				String city = results.getString(3);
				String state = results.getString(4);
				
				System.out.println("aid: " + aid);
				System.out.println("date: " + date);
				System.out.println("city: " + city);
				System.out.println("state: " + state);
				System.out.println();
			}

				} catch (Exception e) {
					System.out.println(query);
					e.printStackTrace();
				} finally {
					try {
						connection.close();
					} catch (Exception e) {
					}
				}
	}
	
	//check on involvements table for aid > 100 to see what we've added
	public static void involvementsQuery() 
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance();
		Connection connection = null;
		String query = null;

		try {
			// Load the JDBC drivers
			Class.forName(driverClass);

			// Open a DB Connection
			//connection = DriverManager.getConnection(url, username, password);
			connection = DriverManager.getConnection(url);

			// Create a Statement
			Statement stmnt = connection.createStatement();

			// Execute a query
			query = "select * from Involvements where aid>100";
			ResultSet results = stmnt.executeQuery(query);

			// Print out the results
			while (results.next()) 
			{
				int aid = results.getInt(1);
				String vin = results.getString(2);
				float damages = results.getFloat(3);
				String driverSSN = results.getString(4);
				
				System.out.println("aid: " + aid);
				System.out.println("vin: " + vin);
				System.out.println("damages: " + nf.format(damages));
				System.out.println("driver SSN: " + driverSSN);
				System.out.println();
			}

				} catch (Exception e) {
					System.out.println(query);
					e.printStackTrace();
				} finally {
					try {
						connection.close();
					} catch (Exception e) {
					}
				}
	}
	
	//delete added data to revert back to original database
	public static void deleteAddedData() 
	{
		Connection connection = null;

		try {
			// Load the JDBC drivers
			Class.forName(driverClass);

			// Open a DB Connection
			//connection = DriverManager.getConnection(url, username, password);
			connection = DriverManager.getConnection(url);

			// Create a Statement
			Statement stmnt = connection.createStatement();
			String deleteStatement = "delete from Accidents where aid > 100";
			String deleteStatement2 = "delete from Involvements where aid > 100";
			stmnt.executeUpdate(deleteStatement);
			stmnt.executeUpdate(deleteStatement2);

			} 
		catch (Exception e) 
		{
			System.out.println("Exception");
			e.printStackTrace();
		} 
		finally 
		{
			try 
			{
				connection.close();
			} 
			catch (Exception e) 
			{
			}
		}
	}
	/*
	Find the details about a given accident:
	    The user should enter an accident id number.
	    The application should display the date and location of the accident.
	    For each automobile involved in the accident, the application should display the vin number, damages, and driver ssn  (if applicable).
	*/
	//check on accidents table
	public static void accidentsQueryByID() 
	{
		Scanner in = new Scanner(System.in);
		String input = "";
		int id;
		Connection connection = null;
		String accidentsQuery = null;
		String involvementsQuery = null;
			
		System.out.println("*** Find Accident By ID ***\n");
		System.out.println("Enter Accident ID: ");
		input = in.nextLine();
		id = Integer.parseInt(input);
		
		accidentsQuery = String.format("select * from Accidents where aid == %d", id);
		involvementsQuery = String.format("select * from involvements where aid == %d", id);
			
		try {
			// Load the JDBC drivers
			Class.forName(driverClass);

			// Open a DB Connection
			//connection = DriverManager.getConnection(url, username, password);
			connection = DriverManager.getConnection(url);

			// Create a Statement
			Statement stmnt = connection.createStatement();

			// Execute a query
				
			ResultSet results = stmnt.executeQuery(accidentsQuery);
				
			// Print out the results
			while (results.next()) 
			{
				String date = results.getString(2);
				String city = results.getString(3);
				String state = results.getString(4);
					
				System.out.println(String.format("Date of Accident %d: %s", id, date));
				System.out.println(String.format("Location: %s, %s", city, state));
				System.out.println();
			}

			} 
		catch (Exception e) 
		{
			System.out.println(accidentsQuery);
			e.printStackTrace();
		} 
		finally {
					try {
							connection.close();
						} 
					catch (Exception e) 
					{
					}
				}
		System.out.println("Automobiles Involved In Accident " + id + ":");
		NumberFormat nf = NumberFormat.getCurrencyInstance();
		connection = null;

		try {
			
			// Load the JDBC drivers
			Class.forName(driverClass);

			// Open a DB Connection
			//connection = DriverManager.getConnection(url, username, password);
			connection = DriverManager.getConnection(url);

			// Create a Statement
			Statement stmnt = connection.createStatement();

			// Execute a query
			ResultSet results = stmnt.executeQuery(involvementsQuery);

			// Print out the results
			int i = 1;
			while (results.next()) 
			{
				String vin = results.getString(2);
				float damages = results.getFloat(3);
				String driverSSN = results.getString(4);
					
				System.out.println(String.format("%d.", i));
				System.out.println("vin: " + vin);
				System.out.println("damages: " + nf.format(damages));
				System.out.println("driver SSN: " + driverSSN);
				System.out.println();
				i++;
			}

			} 
		catch (Exception e) 
		{
			System.out.println(involvementsQuery);
			e.printStackTrace();
		} 
		finally 
		{
			try 
			{
				connection.close();
			} 
			catch (Exception e) 
			{
			}
		}
	}
	
	/*
	Search for accidents meeting the user's criteria:

    The user should be able to specify any or all of the following:
        a range of dates
        a range of average damages to the automobiles involved
        a range of total damages to the automobiles involved
    The application should display the accident id, date, and location for each accident meeting the user's search criteria.
    */
	public static void searchForAccidents() 
	{
		Scanner in = new Scanner(System.in);
		String input="";
		int value;
		System.out.println("*** Search For Accidents ***");
		System.out.println("Note: ranges are inclusive");
		while(!input.equals("q"))
		{
		System.out.println("Select an option (q to quit to main menu):\n\n1. Search using a range of DATES.\n2. Search using a range of AVERAGE DAMAGES to the automobiles "
				+ "involved. \n3. Search using a range of TOTAL DAMAGES to the automobiles involved\n4. 1) and 2) -> Search using a range of DATES AND a range of AVERAGE DAMAGES to "
				+ "the automobiles involved.\n5. 1) and 3) -> Search using a range of DATES AND a range of TOTAL DAMAGES to the automobiles involved.\n6. 2) and 3) -> Search using a range of "
				+ "AVERAGE DAMAGES and a range of TOTAL DAMAGES to the automobiles involved.\n7. 1), 2), and 3) -> Search using a range of DATES, a range of AVERAGE DAMAGES, AND a range "
				+ "of TOTAL DAMAGES to the automobiles involved.");
		input = in.nextLine();
		try {
			value = Integer.parseInt(input);
	    	
			switch (value) 
	        {
	            case 1:
	            		UserInterface.searchDates();
	                    break;
	            case 2: 
	            		UserInterface.searchAvgDamages();
	            		break;
	            case 3: 
	            		UserInterface.searchTotalDamages();
	                    break;
	            case 4:
	            		UserInterface.searchDateAndAvg();
	            		break;
	            case 5:
	            		UserInterface.searchDateAndTot();
	            		break;
	            case 6:
	            		UserInterface.searchAvgAndTot();
	            		break;
	            case 7:
	            		UserInterface.searchAll();
	            		break;

	           default: System.out.println("Invalid choice\n");		
	        }
			}
		catch(NumberFormatException e)
		{
			if(input.equals("q"))
			{
				System.out.println("*** Main Menu ***");
				break;
			}
			else
				System.out.println("Invalid input\n");
		}
		}

		
	}
	//search using a range of dates
	public static void searchDates()
	{
		Connection connection = null;
		String dateRangeQuery = null;
		
		Scanner in = new Scanner(System.in);
		String startDate="";
		String endDate="";
		System.out.println("*** Search using a range of dates ***");
		System.out.println("Enter a start date (yyyy-mm-dd): ");
		startDate = in.nextLine();
		System.out.println("Enter an end date (yyyy-mm-dd): ");
		endDate = in.nextLine();

		try {
			// Load the JDBC drivers
			Class.forName(driverClass);

			// Open a DB Connection
			//connection = DriverManager.getConnection(url, username, password);
			connection = DriverManager.getConnection(url);

			// Create a Statement
			Statement stmnt = connection.createStatement();

			// Execute a query
			dateRangeQuery = String.format("select * from Accidents where accident_date >= '%s' intersect select * from Accidents where accident_date <= '%s'", startDate, endDate);
			
			ResultSet results = stmnt.executeQuery(dateRangeQuery);

			// Print out the results
			while (results.next()) 
			{
				int aid = results.getInt(1);
				String date = results.getString(2);
				String city = results.getString(3);
				String state = results.getString(4);
				
				System.out.println("aid: " + aid);
				System.out.println(String.format("Date of Accident %d: %s", aid, date));
				System.out.println(String.format("Location: %s, %s", city, state));
				System.out.println();
			}

				} catch (Exception e) {
					System.out.println(dateRangeQuery);
					e.printStackTrace();
				} finally {
					try {
						connection.close();
					} catch (Exception e) {
					}
				}
	}
	
	public static void searchAvgDamages()
	{
		Connection connection = null;
		String damagesRangeQuery = null;
		
		Scanner in = new Scanner(System.in);
		String lowerBound="";
		int valueLowerBound;
		String upperBound="";
		int valueUpperBound;
		System.out.println("*** Search using a range of AVERAGE damages to the automobiles involved ***");
		System.out.println("Enter a lower bound: ");
		lowerBound = in.nextLine();
		valueLowerBound = Integer.parseInt(lowerBound);
		System.out.println("Enter an upper bound: ");
		upperBound = in.nextLine();
		valueUpperBound = Integer.parseInt(upperBound);

		try {
			// Load the JDBC drivers
			Class.forName(driverClass);

			// Open a DB Connection
			//connection = DriverManager.getConnection(url, username, password);
			connection = DriverManager.getConnection(url);

			// Create a Statement
			Statement stmnt = connection.createStatement();

			// Execute a query
			damagesRangeQuery = String.format("select a.aid, accident_date, city, state, avg(damages) from accidents a, involvements i where a.aid = i.aid group by i.aid having avg(damages) >= %d intersect select a.aid, accident_date, city, state, avg(damages) from accidents a, involvements i where a.aid = i.aid group by i.aid having avg(damages) <= %d", valueLowerBound, valueUpperBound);
			
			ResultSet results = stmnt.executeQuery(damagesRangeQuery);

			// Print out the results
			while (results.next()) 
			{
				int aid = results.getInt(1);
				String date = results.getString(2);
				String city = results.getString(3);
				String state = results.getString(4);
				
				System.out.println("aid: " + aid);
				System.out.println(String.format("Date of Accident %d: %s", aid, date));
				System.out.println(String.format("Location: %s, %s", city, state));
				System.out.println();
			}

				} catch (Exception e) {
					System.out.println(damagesRangeQuery);
					e.printStackTrace();
				} finally {
					try {
						connection.close();
					} catch (Exception e) {
					}
				}
	}
	
	public static void searchTotalDamages()
	{
		Connection connection = null;
		String damagesRangeQuery = null;
		
		Scanner in = new Scanner(System.in);
		String lowerBound="";
		int valueLowerBound;
		String upperBound="";
		int valueUpperBound;
		System.out.println("*** Search using a range of TOTAL damages to the automobiles involved ***");
		System.out.println("Enter a lower bound: ");
		lowerBound = in.nextLine();
		valueLowerBound = Integer.parseInt(lowerBound);
		System.out.println("Enter an upper bound: ");
		upperBound = in.nextLine();
		valueUpperBound = Integer.parseInt(upperBound);

		try {
			// Load the JDBC drivers
			Class.forName(driverClass);

			// Open a DB Connection
			//connection = DriverManager.getConnection(url, username, password);
			connection = DriverManager.getConnection(url);

			// Create a Statement
			Statement stmnt = connection.createStatement();

			// Execute a query
			damagesRangeQuery = String.format("select a.aid, accident_date, city, state, sum(damages) from accidents a, involvements i where a.aid = i.aid group by i.aid having sum(damages) >= %d intersect select a.aid, accident_date, city, state, sum(damages) from accidents a, involvements i where a.aid = i.aid group by i.aid having sum(damages) <= %d", valueLowerBound, valueUpperBound);
			
			ResultSet results = stmnt.executeQuery(damagesRangeQuery);

			// Print out the results
			while (results.next()) 
			{
				int aid = results.getInt(1);
				String date = results.getString(2);
				String city = results.getString(3);
				String state = results.getString(4);
				
				System.out.println("aid: " + aid);
				System.out.println(String.format("Date of Accident %d: %s", aid, date));
				System.out.println(String.format("Location: %s, %s", city, state));
				System.out.println();
			}

				} catch (Exception e) {
					System.out.println(damagesRangeQuery);
					e.printStackTrace();
				} finally {
					try {
						connection.close();
					} catch (Exception e) {
					}
				}
	}
	//1+2date range combined with avg damages
	public static void searchDateAndAvg()
	{
		Connection connection = null;
		String dateAndAvgQuery = null;
		
		Scanner in = new Scanner(System.in);
		String startDate="";
		String endDate="";
		String lowerBound = "";
		String upperBound = "";
		int valueLowerBound;
		int valueUpperBound;
		
		System.out.println("*** Search using a range of DATES AND a range of AVERAGE DAMAGES to the automobiles involved***");
		System.out.println("Enter a start date (yyyy-mm-dd): ");
		startDate = in.nextLine();
		System.out.println("Enter an end date (yyyy-mm-dd): ");
		endDate = in.nextLine();
		System.out.println("Enter a lower bound for the range of AVERAGE DAMAGES: ");
		lowerBound = in.nextLine();
		valueLowerBound = Integer.parseInt(lowerBound);
		System.out.println("Enter an upper bound: ");
		upperBound = in.nextLine();
		valueUpperBound = Integer.parseInt(upperBound);
		
		try {
			// Load the JDBC drivers
			Class.forName(driverClass);

			// Open a DB Connection
			//connection = DriverManager.getConnection(url, username, password);
			connection = DriverManager.getConnection(url);

			// Create a Statement
			Statement stmnt = connection.createStatement();

			// Execute a query
			dateAndAvgQuery = String.format("select a.aid, accident_date, city, state, avg(damages) from Accidents a, Involvements i where a.aid = i.aid and accident_date >= '%s' group by i.aid intersect select a.aid, accident_date, city, state, avg(damages) from Accidents a, Involvements i where a.aid = i.aid and accident_date <= '%s' group by i.aid intersect select a.aid, accident_date, city, state, avg(damages) from accidents a, involvements i where a.aid = i.aid group by i.aid having avg(damages) >= %d intersect select a.aid, accident_date, city, state, avg(damages) from accidents a, involvements i where a.aid = i.aid group by i.aid having avg(damages) <= %d", startDate, endDate, valueLowerBound, valueUpperBound);
			
			ResultSet results = stmnt.executeQuery(dateAndAvgQuery);

			// Print out the results
			while (results.next()) 
			{
				int aid = results.getInt(1);
				String date = results.getString(2);
				String city = results.getString(3);
				String state = results.getString(4);
				
				System.out.println("aid: " + aid);
				System.out.println(String.format("Date of Accident %d: %s", aid, date));
				System.out.println(String.format("Location: %s, %s", city, state));
				System.out.println();
			}

				} catch (Exception e) {
					System.out.println(dateAndAvgQuery);
					e.printStackTrace();
				} finally {
					try {
						connection.close();
					} catch (Exception e) {
					}
				}
	}
	//2+3avg with total
	public static void searchAvgAndTot()
	{
		Connection connection = null;
		String avgAndSumQuery = null;
		
		Scanner in = new Scanner(System.in);
		String avgLowerBound="";
		int avgLower;
		String avgUpperBound="";
		int avgUpper;
		String sumLowerBound="";
		int sumLower;
		String sumUpperBound="";
		int sumUpper;
		
		System.out.println("*** Search using a range of AVERAGE damages AND a range of TOTAL damages to the automobiles involved ***");
		System.out.println("Enter a lower bound for a range of AVERAGE damages: ");
		avgLowerBound = in.nextLine();
		avgLower = Integer.parseInt(avgLowerBound);
		System.out.println("Enter an upper bound: ");
		avgUpperBound = in.nextLine();
		avgUpper = Integer.parseInt(avgUpperBound);
		System.out.println("Enter a lower bound for a range of TOTAL damages: ");
		sumLowerBound = in.nextLine();
		sumLower = Integer.parseInt(sumLowerBound);
		System.out.println("Enter an upper bound: ");
		sumUpperBound = in.nextLine();
		sumUpper = Integer.parseInt(sumUpperBound);
		
		try {
			// Load the JDBC drivers
			Class.forName(driverClass);

			// Open a DB Connection
			//connection = DriverManager.getConnection(url, username, password);
			connection = DriverManager.getConnection(url);

			// Create a Statement
			Statement stmnt = connection.createStatement();

			// Execute a query
			avgAndSumQuery = String.format("select a.aid, accident_date, city, state, avg(damages), sum(damages) from accidents a, involvements i where a.aid = i.aid group by i.aid having avg(damages) >= %d intersect select a.aid, accident_date, city, state, avg(damages), sum(damages) from accidents a, involvements i where a.aid = i.aid group by i.aid having avg(damages) <= %d intersect select a.aid, accident_date, city, state, avg(damages), sum(damages) from accidents a, involvements i where a.aid = i.aid group by i.aid having sum(damages) >= %d intersect select a.aid, accident_date, city, state, avg(damages), sum(damages) from accidents a, involvements i where a.aid = i.aid group by i.aid having sum(damages) <= %d", avgLower, avgUpper, sumLower, sumUpper);
			
			ResultSet results = stmnt.executeQuery(avgAndSumQuery);

			// Print out the results
			while (results.next()) 
			{
				int aid = results.getInt(1);
				String date = results.getString(2);
				String city = results.getString(3);
				String state = results.getString(4);
				
				System.out.println("aid: " + aid);
				System.out.println(String.format("Date of Accident %d: %s", aid, date));
				System.out.println(String.format("Location: %s, %s", city, state));
				System.out.println();
			}

				} catch (Exception e) {
					System.out.println(avgAndSumQuery);
					e.printStackTrace();
				} finally {
					try {
						connection.close();
					} catch (Exception e) {
					}
				}
	}
	
	//1+3date range with total damages	
	public static void searchDateAndTot()
	{
		Connection connection = null;
		String dateAndSumQuery = null;
		
		Scanner in = new Scanner(System.in);
		String startDate="";
		String endDate="";
		String lowerBound = "";
		String upperBound = "";
		int valueLowerBound;
		int valueUpperBound;
		
		System.out.println("*** Search using a range of DATES AND a range of TOTAL DAMAGES to the automobiles involved***");
		System.out.println("Enter a start date (yyyy-mm-dd): ");
		startDate = in.nextLine();
		System.out.println("Enter an end date (yyyy-mm-dd): ");
		endDate = in.nextLine();
		System.out.println("Enter a lower bound for the range of TOTAL DAMAGES: ");
		lowerBound = in.nextLine();
		valueLowerBound = Integer.parseInt(lowerBound);
		System.out.println("Enter an upper bound: ");
		upperBound = in.nextLine();
		valueUpperBound = Integer.parseInt(upperBound);
		
		try {
			// Load the JDBC drivers
			Class.forName(driverClass);

			// Open a DB Connection
			//connection = DriverManager.getConnection(url, username, password);
			connection = DriverManager.getConnection(url);

			// Create a Statement
			Statement stmnt = connection.createStatement();

			// Execute a query
			dateAndSumQuery = String.format("select a.aid, accident_date, city, state, sum(damages) from Accidents a, Involvements i where a.aid = i.aid and accident_date >= '%s' group by i.aid intersect select a.aid, accident_date, city, state, sum(damages) from Accidents a, Involvements i where a.aid = i.aid and accident_date <= '%s' group by i.aid intersect select a.aid, accident_date, city, state, sum(damages) from accidents a, involvements i where a.aid = i.aid group by i.aid having sum(damages) >= %d intersect select a.aid, accident_date, city, state, sum(damages) from accidents a, involvements i where a.aid = i.aid group by i.aid having sum(damages) <= %d", startDate, endDate, valueLowerBound, valueUpperBound);
			ResultSet results = stmnt.executeQuery(dateAndSumQuery);

			// Print out the results
			while (results.next()) 
			{
				int aid = results.getInt(1);
				String date = results.getString(2);
				String city = results.getString(3);
				String state = results.getString(4);
				
				System.out.println("aid: " + aid);
				System.out.println(String.format("Date of Accident %d: %s", aid, date));
				System.out.println(String.format("Location: %s, %s", city, state));
				System.out.println();
			}

				} catch (Exception e) {
					System.out.println(dateAndSumQuery);
					e.printStackTrace();
				} finally {
					try {
						connection.close();
					} catch (Exception e) {
					}
				}
	}
	//1+2+3 (date,avg,total)
	public static void searchAll()
	{
		Connection connection = null;
		String allQuery = null;
		
		Scanner in = new Scanner(System.in);
		String startDate="";
		String endDate="";
		String avgLowerBound="";
		int avgLower;
		String avgUpperBound="";
		int avgUpper;
		String sumLowerBound="";
		int sumLower;
		String sumUpperBound="";
		int sumUpper;

		
		System.out.println("*** Search using a range of DATES AND a range of AVERAGE DAMAGES to the automobiles involved***");
		System.out.println("Enter a start date (yyyy-mm-dd): ");
		startDate = in.nextLine();
		System.out.println("Enter an end date (yyyy-mm-dd): ");
		endDate = in.nextLine();
		System.out.println("Enter a lower bound for the range of AVERAGE DAMAGES: ");
		avgLowerBound = in.nextLine();
		avgLower = Integer.parseInt(avgLowerBound);
		System.out.println("Enter an upper bound: ");
		avgUpperBound = in.nextLine();
		avgUpper = Integer.parseInt(avgUpperBound);
		System.out.println("Enter a lower bound for a range of TOTAL damages: ");
		sumLowerBound = in.nextLine();
		sumLower = Integer.parseInt(sumLowerBound);
		System.out.println("Enter an upper bound: ");
		sumUpperBound = in.nextLine();
		sumUpper = Integer.parseInt(sumUpperBound);
		
		try {
			// Load the JDBC drivers
			Class.forName(driverClass);

			// Open a DB Connection
			//connection = DriverManager.getConnection(url, username, password);
			connection = DriverManager.getConnection(url);

			// Create a Statement
			Statement stmnt = connection.createStatement();

			// Execute a query
			allQuery = String.format("select a.aid, accident_date, city, state, avg(damages), sum(damages) from Accidents a, Involvements i where a.aid = i.aid and accident_date >= '%s' group by i.aid intersect select a.aid, accident_date, city, state, avg(damages), sum(damages) from Accidents a, Involvements i where a.aid = i.aid and accident_date <= '%s' group by i.aid intersect select a.aid, accident_date, city, state, avg(damages), sum(damages) from accidents a, involvements i where a.aid = i.aid group by i.aid having avg(damages) >= %d intersect select a.aid, accident_date, city, state, avg(damages), sum(damages) from accidents a, involvements i where a.aid = i.aid group by i.aid having avg(damages) <= %d intersect select a.aid, accident_date, city, state, avg(damages), sum(damages) from accidents a, involvements i where a.aid = i.aid group by i.aid having sum(damages) >= %d intersect select a.aid, accident_date, city, state, avg(damages), sum(damages) from accidents a, involvements i where a.aid = i.aid group by i.aid having sum(damages) <= %d", startDate, endDate, avgLower, avgUpper, sumLower, sumUpper);
			
			ResultSet results = stmnt.executeQuery(allQuery);

			// Print out the results
			while (results.next()) 
			{
				int aid = results.getInt(1);
				String date = results.getString(2);
				String city = results.getString(3);
				String state = results.getString(4);
				
				System.out.println("aid: " + aid);
				System.out.println(String.format("Date of Accident %d: %s", aid, date));
				System.out.println(String.format("Location: %s, %s", city, state));
				System.out.println();
			}

				} catch (Exception e) {
					System.out.println(allQuery);
					e.printStackTrace();
				} finally {
					try {
						connection.close();
					} catch (Exception e) {
					}
				}
	}
}

