package dbfutils;

import java.sql.*;

import oracle.jdbc.OracleCallableStatement;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;

public class TestSqlArr {
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Connection connection = DbUtils.getConn();
		
		Object[] so1 = {1, 1, "aaa", 1, 1, "aaa", 1};
		Object[] so2 = {1, 2, "bbb", 2, 1, "aaa", 1};
		
		try {
			StructDescriptor st = new StructDescriptor("InPInsertStactP1", connection);
			STRUCT s1 = new STRUCT(st, connection, so1);
			STRUCT s2 = new STRUCT(st, connection, so2);
			STRUCT[] deptArray = {s1, s2};
			ArrayDescriptor arrayDept = ArrayDescriptor.createDescriptor("InPInsertStactP1List", connection);
			ARRAY deptArrayObject = new ARRAY(arrayDept, connection, deptArray);
			CallableStatement callStatement = (OracleCallableStatement)connection.prepareCall("{call insert_object(?)}"); 
            ((OracleCallableStatement)callStatement).setArray(1, deptArrayObject); 
            callStatement.executeUpdate(); 
            connection.commit(); 
            callStatement.close(); 
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
