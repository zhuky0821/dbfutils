package dbfutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.linuxense.javadbf.*;


public class DbfUtils {
	public static void dbfRead(String file){
		
	}
	
	/**
	 * dbf文件写入
	 * @param file        输出文件
	 */
	public static void dbfWrite(String file){
		
		String fileName = "";
		
		try{
			
			File filePath = new File(file);
			
			fileName = filePath.getName();
			
			fileName = fileName.substring(0, fileName.lastIndexOf("."));
			
			if(filePath.exists())
				filePath.delete();
			
			
			DBFWriter writer = new DBFWriter();
			DBFField[] fields = getDbfFields(fileName);
			writer.setFields(fields);
			
			String sql = getQuerySql(fileName);
			
			Connection connection = null;
			PreparedStatement pStatement = null;
			ResultSet rs = null;
			OutputStream outputStream = null;
			
			try {
				connection = DbUtils.getConn();
				
				pStatement = connection.prepareStatement(sql);
				
				rs = pStatement.executeQuery();
				
				
				while(rs.next()){
					Object[] rowdate = new Object[fields.length];
					
					for(int j=0; j<fields.length; j++){
						
						String cloName = fields[j].getName();
//						System.out.println(cloName);
						
						if(fields[j].getDataType() == DBFField.FIELD_TYPE_N){
							//浮点数
							rowdate[j] = rs.getDouble(cloName);
						}
						else if(fields[j].getDataType() == DBFField.FIELD_TYPE_D){
							//日期
							SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
							String data = rs.getString(cloName);
							
							Date date = sdf.parse(data);
							rowdate[j] = date;
						}
						else {
							//字符串
							
							String data = rs.getString(cloName).trim();
							if(data.length() > fields[j].getFieldLength()){
								data = data.substring(0, fields[j].getFieldLength() - 1);
							}
							
							rowdate[j] = data;
						}
						
					}
					
					writer.addRecord(rowdate);
				}
				
				outputStream = new FileOutputStream(file);
				writer.write(outputStream);
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				DbUtils.closeConn(connection);
				DbUtils.closePreStatement(pStatement);
				DbUtils.closeResultset(rs);
				
				try {  
	                outputStream.close();  
	            } catch (Exception e) {  
	            }
			}
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static DBFField[] getDbfFields(String name){
		//获取配置
		File configFile = ConfigRead.readConfig(name + ".txt");
		BufferedReader bufferedReader = null;
		DBFField[] fields = null;
		ArrayList<DBFField> dbfFieldList = new ArrayList<DBFField>();
		
		try {
			bufferedReader = new BufferedReader(new FileReader(configFile));
			
			if(bufferedReader != null){
				String line;
				while((line = bufferedReader.readLine()) != null){
					
					if(line.equals("sqlconfig"))
						break;
					
					DBFField field = new DBFField();
					String[] confs = line.split("\\|");
					
					if(confs.length < 2)
						continue;
					
					if((confs[1].equals("C") || confs[1].equals("c")) && confs.length >= 3){
						//字符串类型
						field.setName(confs[0]);
						field.setDataType(DBFField.FIELD_TYPE_C);
						field.setFieldLength(Integer.parseInt(confs[2]));
					}
					else if(confs[1].equals("D") || confs[1].equals("d")){
						//日期类型
						field.setName(confs[0]);
						field.setDataType(DBFField.FIELD_TYPE_D);
//						if(confs.length > 2){
//							field.setFieldLength(Integer.parseInt(confs[2]));
//						}
//						else {
//							field.setFieldLength(8);
//						}
					}
					else if((confs[1].equals("N") || confs[1].equals("n")) && confs.length >= 4){
						//浮点数类型
						field.setName(confs[0]);
						field.setDataType(DBFField.FIELD_TYPE_N);
						field.setFieldLength(Integer.parseInt(confs[2]));
						field.setDecimalCount(Integer.parseInt(confs[3]));
					}
					else {
						continue;
					}
					
					dbfFieldList.add(field);
					
				}
				
			}
			
			if(dbfFieldList.size() > 0)
				fields = new DBFField[dbfFieldList.size()];
			
			for(int i=0; i<dbfFieldList.size(); i++){
				fields[i] = dbfFieldList.get(i);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return fields;
	}
	
	public static String getQuerySql(String name){
		String sql = "";
		
		File configFile = ConfigRead.readConfig(name + ".txt");
		BufferedReader bufferedReader = null;
		
		try {
			bufferedReader = new BufferedReader(new FileReader(configFile));
			String line;
			
			Boolean issql = false;
			while((line = bufferedReader.readLine()) != null){
				
				if(issql)
					sql = sql + line + "\n";
				
				if(line.equals("sqlconfig"))
					issql = true;
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("获取到的查询sql：" + sql);
		return sql;
	}
	
	
	public static void main(String[] args) {
		dbfWrite("C:\\work\\BJZSMX.dbf");
		
	}
}
