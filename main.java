import java.sql.*;
import java.io.File; 
import java.util.Scanner;


public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			final File myFile = new File("credentials.txt");
			final Scanner myScanner = new Scanner(myFile);
			final Connection myConn = DriverManager.getConnection(myScanner.nextLine());
			if (args[2] != null && args[2].equals("department")) {
				final String dept = args[3];
				showEmployees(myConn, dept);
			}  else if (args[0] != null && args[0].equals("delete")) {
				final String empNo = args[2];
				deleteEmployee(myConn, empNo);
			} else if (args[0] != null & args[0].equals("add")) {
				final String firstName = args[2];
				final String lastName = args[3];
				final String dept = args[4];
				final String bday = args[5];
				final String gender = args[6];
				final String salary = args[7];
				addEmployee(myConn, firstName, lastName, dept, bday, gender, salary);
			} else if (args[2] != null && args[2].equals("sum")) {
				showSum(myConn);
			}
			
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public static void showEmployees(final Connection myConn, final String dept) {
		try {

			  final PreparedStatement stmt = myConn.prepareStatement("SELECT employees.emp_no, first_name, last_name\r\n"
			  + "FROM employees NATURAL JOIN dept_emp NATURAL JOIN departments\r\n"
			  + "WHERE dept_name = ?");
			  stmt.setString(1, dept);
			  final ResultSet myRs = stmt.executeQuery();
			  while (myRs.next()) {
	                System.out.println(myRs.getString("emp_no") + "\t" + 
	                                   myRs.getString("first_name")  + "\t" +
	                                   myRs.getString("last_name"));
			  }
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public static void deleteEmployee(final Connection myConn, final String empNo) {
		try {

			
			PreparedStatement stmt = myConn.prepareStatement("SELECT first_name, last_name FROM employees WHERE emp_no = ?");
			stmt.setString(1, empNo);
			final ResultSet myRs = stmt.executeQuery();

			if (myRs.next() == false) {
				System.out.println("Employee with id " + empNo + "does not exist .");
			} else {
				String first_name = myRs.getString("first_name");
				String last_name = myRs.getString("last_name");
				stmt = myConn.prepareStatement("DELETE FROM employees WHERE emp_no = ?");
				stmt.setString(1, empNo);
				stmt.executeUpdate();

				stmt = myConn.prepareStatement("DELETE FROM salaries WHERE emp_no = ?");
				stmt.setString(1, empNo);
				stmt.executeUpdate();

				stmt = myConn.prepareStatement("DELETE FROM dept_emp WHERE emp_no = ?");
				stmt.setString(1, empNo);
				stmt.executeUpdate();

				System.out.println("Employee " + first_name + " " + last_name + " deleted!");
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public static void addEmployee(final Connection myConn, final String firstName, final String lastName, final String dept, final String bday, final String gender, final String salary) {
		try {
			final String sql = "SELECT MAX(emp_no) + 1 FROM employees";
			PreparedStatement stmt = myConn.prepareStatement(sql);
			ResultSet myRs = stmt.executeQuery();
			String idNum = "";
			if (myRs.next() != false) {
				idNum = myRs.getString("MAX(emp_no) + 1");
			}
			stmt = myConn.prepareStatement("SELECT dept_no FROM departments WHERE dept_name = ?");
			stmt.setString(1, dept);
			myRs = stmt.executeQuery();
			String deptNo = "";
			if (myRs.next() != false) {
				deptNo = myRs.getString("dept_no");
			}
			final String date = "9999-01-01";
			PreparedStatement stmt2 = myConn.prepareStatement("INSERT INTO employees VALUES(?, ?, ?, ?, ?, ?)");
			stmt2.setString(1, idNum);
			stmt2.setString(2, bday);
			stmt2.setString(3, firstName);
			stmt2.setString(4, lastName);
			stmt2.setString(5, gender);
			stmt2.setString(6, date);
			stmt2.executeUpdate();

			stmt2 = myConn.prepareStatement("INSERT INTO salaries VALUES(?, ?, ?, ?)");
			stmt2.setString(1, idNum);
			stmt2.setString(2, salary);
			stmt2.setString(3, date);
			stmt2.setString(4, date);
			stmt2.executeUpdate();

			stmt2 = myConn.prepareStatement("INSERT INTO dept_emp VALUES(?, ?, ?, ?)");
			stmt2.setString(1, idNum);
			stmt2.setString(2, deptNo);
			stmt2.setString(3, date);
			stmt2.setString(4, date);
			stmt2.executeUpdate();

			System.out.println("Employee " + firstName + " " + lastName + " added!");


		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public static void showSum(final Connection myConn) {
		try {
			final String sql = "SELECT sum(salary) FROM salaries WHERE to_date ='9999-01-01'";
			final PreparedStatement stmt = myConn.prepareStatement(sql);
			ResultSet myRs = stmt.executeQuery();
			if (myRs.next() != false) {
				final String sum = myRs.getString("sum(salary)");
				System.out.println("$" + sum);
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

}


