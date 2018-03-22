package sample.dao;

import com.mysql.jdbc.Driver;
import com.mysql.jdbc.JDBC42ResultSet;

import javax.swing.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;
 


/**
 * @author sims
 * @date 2018/2/5 10:38
 **/
public class JDBCMySql {
    private static final char BR_STR = '\n';
    private StringBuffer stringBuffer = new StringBuffer("");
    private StringBuffer stringBufferEnd = new StringBuffer("");
    private ArrayList<Method> _resultSetMetaData = null;
    public boolean operateMySql(String ip, String username, String password) throws SQLException, ClassNotFoundException{
        clearCacheTrace();
        String driverName ="com.mysql.jdbc.Driver";
        String URL = "jdbc:mysql://"+ip +":3306";
        Boolean result = false;
        Connection conn = null;
        try{

            //加载驱动
            Driver.class.forName(driverName);

            //建立连接
            conn = DriverManager.getConnection(URL,username,password);

            DatabaseMetaData dm = conn.getMetaData();
            trace(dm.getDriverName());
            trace(dm.getDatabaseMajorVersion()+"."+dm.getDatabaseMinorVersion());
            trace(dm.getMaxStatements());
            trace(dm.getJDBCMajorVersion());//jdbc4.0
            trace("--------------dbNames------------");

//返回所有数据库的名字
            ArrayList<String> dbNames = getDbNames(dm);

            trace("--------------tableNames------------");
//返回某个数据库的表名
//参数解析: 第1和第2个都是数据库的名字(2个，是为兼容不同数据库), 第3个参数是查询表名的过滤模式(null为不过滤即查所有，"%a%"为表名中包含字母'a'),最后一个参数是表类型如"TABLE"、"VIEW"等(这些值可查看API中getTableTypes()方法)
            HashMap<String , ArrayList<String>> dbTableName = getTablesNames(dm, dbNames);
            trace("--------------tableStructs------------");
            printTablesStruct(dbTableName, conn);

            result = true;
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally{
            if(conn!=null){
                try{
                    conn.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
    public String getCacheBuffString()
    {
        stringBuffer.append("============================================================");
        stringBuffer.append(this.stringBufferEnd);
        return stringBuffer.toString();
    }
    private void clearCacheTrace() {
        stringBufferEnd.setLength(0);
        stringBuffer.setLength(0);
    }

    private void trace(int args)
    {
        trace(String.valueOf(args));
    }
    private void trace(String ... args)
    {
        String argstr = argsToString(args);
        this.stringBuffer.append(argstr);
        this.stringBuffer.append(BR_STR);
        System.out.println(argstr);
    }
    private void traceToEnd(String ... args)
    {
        String argstr = argsToString(args);
        this.stringBufferEnd.append(argstr);
        this.stringBufferEnd.append(BR_STR);
        System.out.println(argstr);
    }
    private String argsToString(String[] args) {
        StringBuffer stringBuffer = new StringBuffer("");
        for(int i=0, len= args.length;i<len;i++)
        {
            stringBuffer.append(args[i]);
        }
        return stringBuffer.toString();
    }

    private void printTablesStruct(HashMap<String, ArrayList<String>> dbTableName, Connection conn) throws InvocationTargetException, IllegalAccessException {
        try{
            Iterator<Map.Entry<String, ArrayList<String>>> iterator = dbTableName.entrySet().iterator();
            while (iterator.hasNext())
            {
                Map.Entry<String, ArrayList<String>> entry = iterator.next();
                //如果已知数据库的名字，打开该数据库。如果还知道某个表的名字，那么可以操纵这个表
                String dbName = entry.getKey();
                trace("--------dbName: " + dbName + " -------");
                conn.createStatement().execute("use "+dbName);//该方法能够执行所有SQL语句，包括: use hncu; drop database hncu
                ArrayList<String> tableNames = entry.getValue();
                for(String tableName : tableNames)
                {
                    trace("----tableName: " + tableName + " ----");
                    ResultSet rs2 = conn.createStatement().executeQuery("select * from "+ tableName);

                    JDBC42ResultSet jdbc42ResultSet= (JDBC42ResultSet)rs2;
                    ResultSetMetaData metaData = jdbc42ResultSet.getMetaData();

                    ArrayList<Method> methods = getMethods();
                    int columnCount = metaData.getColumnCount();
                    for(int i = 0;i < columnCount;i++)
                    {
                        int index = i +1;
                        StringBuffer fieldStr = new StringBuffer("");
                        for(Method method : methods)
                        {

                           String retValue = null;
                           if(method.getReturnType() == String.class)
                           {
                               retValue = (String)method.invoke(metaData,index);
                           }
                           else if(method.getReturnType()==int.class)
                           {
                               retValue = String.valueOf((int)method.invoke(metaData, index));
                           }
                           if(retValue != null)
                           {
                               fieldStr.append(method.getName());
                               fieldStr.append('\t');
                               fieldStr.append(retValue);
                               fieldStr.append('\t');
                           }

                        }
                        traceToEnd("fieldStr: " +fieldStr.toString());
                        String columnName = metaData.getColumnName(index);
                        String typeName = metaData.getColumnTypeName(index);
                        int columnDisplaySize = metaData.getColumnDisplaySize(index);
                        trace( columnName + "  " + typeName +":" + columnDisplaySize);
                    }
                    trace("--data--");
                    while(rs2.next()){
//如果已经列数n，就可输出表格的所有数据
                        int n = 2;
                        for(int i=1;i<=n;i++){
                            Object obj = rs2.getObject(i);
                            System.out.print(obj+" ");
                        }
                        trace();
                    }
                }
                }


        }
        catch (SQLException e)
        {

        }

    }

    private ArrayList<Method> getMethods() {

        if(_resultSetMetaData != null)return _resultSetMetaData;
        Method[] methods = ResultSetMetaData.class.getDeclaredMethods();

        ArrayList<Method> arrayList = new ArrayList<>();
        StringBuffer stringBuffer = new StringBuffer("");
        for(int i = methods.length-1;i>=0;i--) {
            Method method = methods[i];
            if (method.getParameterCount() == 1) {

                if (method.getParameterTypes()[0] == int.class) {
                    if (method.getReturnType() == String.class || method.getReturnType() == int.class) {
                        arrayList.add(method);
                        stringBuffer.append(method.getName());
                        stringBuffer.append('\t');
                        stringBuffer.append("value");
                        stringBuffer.append('\t');
                    }
                }
            }
        }
        traceToEnd(stringBuffer.toString());
        _resultSetMetaData = arrayList;
        return arrayList;
    }

    private HashMap<String, ArrayList<String>> getTablesNames(DatabaseMetaData dm, ArrayList<String> dbNames) {
        HashMap<String, ArrayList<String>> dbTableNames = new HashMap<>();
        try
        {
            for(String dbName :dbNames)
            {
                ArrayList<String> tableNames = new ArrayList<>();
                dbTableNames.put(dbName, tableNames);
                trace("start------dbName:",dbName,"------");
                ResultSet rs = dm.getTables(dbName, dbName, null, new String[]{"TABLE", "VIEW"});
                while(rs.next()){
                    String name = rs.getString("TABLE_NAME"); //字符串参数的具体取值参看API中getTables()
//                    String name2 = rs.getString("TABLE_TYPE");
                    trace(name);
                    tableNames.add(name);
                }
                trace("end------dbName:",dbName,"------");
            }
        }
        catch (SQLException e)
        {

        }

        return dbTableNames;
    }

    private ArrayList<String> getDbNames(DatabaseMetaData dm) {
        ArrayList<String> dbNames = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = dm.getCatalogs();
            while(rs.next()){
                String name = rs.getString("TABLE_CAT");
                trace(name);
                dbNames.add(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dbNames;
    }
}
