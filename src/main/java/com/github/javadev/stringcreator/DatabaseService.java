package com.github.javadev.stringcreator;

import com.github.underscore.Template;
import com.github.underscore.U;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseService {
    private static final String SELECT_SQL = "SELECT <%=ID_NAME%>, <%=VALUE_NAME%> FROM <%=TABLE_NAME%>";
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private final String hostName;
    private final String dbName;
    private final String user;
    private final String pass;
    private final String idName;
    private final String valueName;
    private final String tableName;

    public DatabaseService(String hostName, String dbName, String user, String pass,
            String idName, String valueName, String tableName) {
        this.hostName = !U.isString(hostName) || hostName.trim().isEmpty() ? "localhost" : U.escape(hostName);
        this.dbName = !U.isString(dbName) || dbName.trim().isEmpty() ? "dictionary" : U.escape(dbName);
        this.user = !U.isString(user) || user.trim().isEmpty() ? "root" : user;
        this.pass = !U.isString(pass) || pass.trim().isEmpty() ? "" : pass;
        this.idName = !U.isString(idName) || idName.trim().isEmpty() ? "id" : idName;
        this.valueName = !U.isString(valueName) || valueName.trim().isEmpty() ? "value" : valueName;
        this.tableName = !U.isString(tableName) || tableName.trim().isEmpty() ? "dictionary" : tableName;
    }

    private String getDbUrl() {
        return "jdbc:mysql://" + hostName + "/" + dbName + "?useUnicode=true&characterEncoding=utf-8";
    }

    public List<Map<String, Object>> readAll() {
        List<Map<String, Object>> result = new ArrayList<>();
        Connection conn = null;
        Statement stmt1 = null;
        try {
            conn = createConnection();
            stmt1 = conn.createStatement();
            Map<String, Object> params = new LinkedHashMap<String, Object>() { {
                put("ID_NAME", idName);
                put("VALUE_NAME", valueName);
                put("TABLE_NAME", tableName);
            } };
            Template<Map<String, Object>> template = U.template(SELECT_SQL);
            try (ResultSet resultSet = stmt1.executeQuery(template.apply(params))) {
                while (resultSet.next()) {
                    Map<String, Object> data = new LinkedHashMap<>();
                    data.put("id", resultSet.getString(idName));
                    data.put("value", resultSet.getString(valueName));
                    result.add(data);
                }
            }
        } catch (SQLException | ClassNotFoundException se) {
            checkExceptionAndCreateTable(se, conn);
        } finally {
            closeStatement(stmt1);
            try {
                if (conn != null) {
                    conn.rollback();
                    conn.close();
                }
            } catch (SQLException se) {
            }
        }
        return result;
    }
    
    private void closeStatement(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException se) {
        }
    }

    private void checkExceptionAndCreateTable(final Exception se, Connection conn) {
        Logger.getLogger(DatabaseService.class.getName()).log(Level.SEVERE, null, se);
    }

    private Connection createConnection() throws ClassNotFoundException, SQLException {
        Connection conn;
        Class.forName(JDBC_DRIVER);
        conn = DriverManager.getConnection(getDbUrl(), user, pass);
        return conn;
    }
}
