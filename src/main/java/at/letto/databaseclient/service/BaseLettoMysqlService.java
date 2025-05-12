package at.letto.databaseclient.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Service
public class BaseLettoMysqlService {

    @Autowired private DatabaseConnectionService databaseConnectionService;

    private Logger logger = LoggerFactory.getLogger(BaseLettoMysqlService.class);

    /**
     * Liefert eine JDBC-Verbindung zu einer MySQL-Datebank am Letto-Mysql-Server
     * @param database   Datenbankname
     * @param user       Benutzername
     * @param password   Klartextpasswort
     * @return           SQL-Connection oder null wenn die Connection nicht funktioniert hat
     */
    public Connection mysqlConnection(String database, String user, String password) {
        try {
            Connection con = databaseConnectionService.mysqlConnection(database,user,password);
            return con;
        } catch (SQLException throwables) {}
        return null;
    }

    /**
     * Liefert eine JDBC-Verbindung zu einer MySQL-Datebank
     * @param url        Datenbank URL : jdbc:mysql://adresse:port/datebank
     * @param user       Benutzername
     * @param password   Klartextpasswort
     * @return           SQL-Connection oder null wenn die Connection nicht funktioniert hat
     */
    public Connection mysqlUrlConnection(String url, String user, String password) {
        try {
            Connection con = databaseConnectionService.mysqlUrlConnection(url,user,password);
            return con;
        } catch (SQLException throwables) {}
        return null;
    }

    /**
     * Liefert eine JDBC-Verbindung zu einer MySQL-Datebank am Letto-Mysql-Server
     * @param database   Datenbankname
     * @return           SQL-Connection oder null wenn die Connection nicht funktioniert hat
     */
    public Connection mysqlRootConnection(String database) {
        try {
            Connection con = databaseConnectionService.mysqlRootConnection(database);
            return con;
        } catch (SQLException throwables) {}
        return null;
    }

    /**
     * Prüft ob eine JDBC-Verbindung zu einer MySQL-Datebank am Letto-Mysql-Server möglich ist
     * @param database   Datenbankname
     * @param user       Benutzername
     * @param password   Klartextpasswort
     * @return           true wenn erfolgreich
     * @throws SQLException Fehlermeldung wenn etwas nicht funktioniert hat
     */
    public boolean checklMysqlConnection(String database, String user, String password) {
        return mysqlConnection(database,user,password)!=null;
    }

    /**
     * Prüft ob eine JDBC-Verbindung zu einer MySQL-Datebank am Letto-Mysql-Server möglich ist
     * @param url        Datenbank URL : jdbc:mysql://adresse:port/datebank
     * @param user       Benutzername
     * @param password   Klartextpasswort
     * @return           true wenn erfolgreich
     * @throws SQLException Fehlermeldung wenn etwas nicht funktioniert hat
     */
    public boolean checklMysqlUrlConnection(String url, String user, String password) {
        return mysqlUrlConnection(url,user,password)!=null;
    }

    /**
     * Führt ein SQL-Statement am SQL-Server aus und liefert das Ergebnis in einer Tabelle
     * @param con            MySQL-Server-Verbindung
     * @param sqlStatement   SQL-Statement
     * @return               Ergebnis des Statements als Tabelle
     * @throws SQLException  Fehlermeldung wenn etwas nicht funktioniert hat
     */
    public Vector<Vector<String>> executeWithResultTable(Connection con, String sqlStatement) throws SQLException {
        Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
        if (stmt.execute( sqlStatement )) {
            ResultSet rs = stmt.getResultSet();
            ResultSetMetaData metaData = rs.getMetaData();
            int rows = metaData.getColumnCount();
            // Spaltennamen ermitteln
            Vector<String> row = new Vector<String>();
            for (int c=1; c<=rows; c++)
                row.add(metaData.getColumnName(c));
            Vector<Vector<String>> ret = new Vector<Vector<String>>();
            ret.add(row);
            while (rs.next()) {
                row = new Vector<String>();
                for (int c=1; c<=rows; c++)
                    row.add(rs.getString(c));
                ret.add(row);
            }
            return ret;
        } else return null;
    }

    /**
     * Führt ein SQL-Statement aus ohne ein Ergebnis auszuwerten
     * @param con            MySQL-Server-Verbindung
     * @param sqlStatement   SQL-Statement
     * @return               Leerstring wenn ok oder Fehlermeldung als String
     */
    public void execute(Connection con, String sqlStatement) throws SQLException {
        Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
        stmt.execute( sqlStatement );
    }

    /**
     * Führt ein SQL-Statement aus ohne ein Ergebnis auszuwerten
     * @param con            MySQL-Server-Verbindung
     * @param sqlStatement   SQL-Statement
     * @return               Leerstring wenn ok oder Fehlermeldung als String
     */
    public String executeMsg(Connection con, String sqlStatement) {
        try {
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            stmt.execute( sqlStatement );
            return "";
        } catch (SQLException throwables) {
            String msg = throwables.getMessage();
            msg += " Statement: "+sqlStatement+"! ";
            return msg;
        }
    }

    /**
     * Führt ein SQL-QUERY-Statement am SQL-Server aus und liefert das Ergebnis in einer Tabelle
     * @param con            MySQL-Server-Verbindung
     * @param sqlStatement   SQL-Statement
     * @return               Ergebnis des Statements als Tabelle
     * @throws SQLException  Fehlermeldung wenn etwas nicht funktioniert hat
     */
    public Vector<Vector<String>> executeQuery(Connection con, String sqlStatement) throws SQLException {
        Vector<Vector<String>> ret = new Vector<Vector<String>>();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery( sqlStatement );
        ResultSetMetaData metaData = rs.getMetaData();
        int rows = metaData.getColumnCount();
        // Spaltennamen ermitteln
        Vector<String> row = new Vector<String>();
        for (int c=1; c<=rows; c++)
            row.add(metaData.getColumnName(c));
        ret.add(row);
        while (rs.next()) {
            row = new Vector<String>();
            for (int c=1; c<=rows; c++)
                row.add(rs.getString(c));
            ret.add(row);
        }
        return ret;
    }

    /**
     * Wenn in SQL-Statements Namen verwendet werden müssen diese innerhalb von einfachen Hochkomma gesetzt werden.<br>
     * enthält der Namen Backslashes oder einfache Hochkomman müssen diese korrekt verblockt werden<br>
     * @param data Name in der SQL-Datenbank
     * @return     mit einfachen Hochkomma verblockter Name
     */
    public String quoteSql(String data) {
        data = data.replaceAll("\\\\","\\\\\\\\").replaceAll("'","\\\\'");
        data = "'"+data+"'";
        return data;
    }

    /**
     * Erzeugt einen Benutzer welcher von allen Rechnern Zugriff hat
     * @param con       MySQL-Server-Verbindung
     * @param username  Benutzername
     * @param password  Klartextpasswort welches sha2-Verschlüsselt gespeichert wird
     * @throws SQLException  Fehlermeldung wenn etwas nicht funktioniert hat
     */
    public void createUser(Connection con, String username, String password) throws SQLException {
        execute(con,"CREATE USER if not exists "+quoteSql(username)+"@'%' IDENTIFIED WITH caching_sha2_password BY "+quoteSql(password));
        execute(con,"GRANT USAGE ON *.* TO "+quoteSql(username)+"@'%'");
        execute(con,"ALTER USER "+quoteSql(username)+"@'%' REQUIRE NONE WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 MAX_USER_CONNECTIONS 0");
        execute(con,"flush privileges");
    }

    /**
     * Erzeugt einen Benutzer welcher von allen Rechnern Zugriff hat
     * @param username  Benutzername
     * @param password  Klartextpasswort welches sha2-Verschlüsselt gespeichert wird
     * @return true wenn alles funktioniert hat
     */
    public boolean createUser(String username, String password) throws SQLException {
        try {
            Connection con = databaseConnectionService.mysqlAdminMysqlConnection();
            createUser(con,username,password);
            return true;
        } catch (SQLException e) { }
        return false;
    }

    /**
     * Erzeugt einen Benutzer welcher von allen Rechnern Zugriff hat<br>
     * Setzt weiters alle Rechte auf die Datenbank 'username' und auf alle Datenbanken welche mit 'username_' beginnen
     * @param con       MySQL-Server-Verbindung
     * @param username  Benutzername
     * @param password  Klartextpasswort welches sha2-Verschlüsselt gespeichert wird
     * @throws SQLException  Fehlermeldung wenn etwas nicht funktioniert hat
     */
    public void createUserWithDatabaseAccess(Connection con, String username, String password) throws SQLException {
        execute(con,"CREATE USER if not exists "+quoteSql(username)+"@'%' IDENTIFIED WITH caching_sha2_password BY "+quoteSql(password));
        execute(con,"GRANT USAGE ON *.* TO "+quoteSql(username)+"@'%'");
        execute(con,"ALTER USER "+quoteSql(username)+"@'%' REQUIRE NONE WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 MAX_USER_CONNECTIONS 0");
        execute(con,"GRANT ALL PRIVILEGES ON `"+username+"`.* TO "+quoteSql(username)+"@'%'");
        execute(con,"GRANT ALL PRIVILEGES ON `"+username+"_%`.* TO "+quoteSql(username)+"@'%'");
        execute(con,"flush privileges");
    }

    /**
     * Erzeugt einen Benutzer welcher von allen Rechnern Zugriff hat<br>
     * Setzt weiters alle Rechte auf die Datenbank 'username' und auf alle Datenbanken welche mit 'username_' beginnen
     * @param username  Benutzername
     * @param password  Klartextpasswort welches sha2-Verschlüsselt gespeichert wird
     * @return true wenn alles funktioniert hat
     */
    public boolean createUserWithDatabaseAccess(String username, String password) {
        try {
            Connection con = databaseConnectionService.mysqlAdminMysqlConnection();
            createUserWithDatabaseAccess(con,username,password);
            return true;
        } catch (SQLException e) { }
        return false;
    }

    /**
     * Setzt alle Rechte für einen Benutzer an der angegebenen Datenbank
     * @param con       MySQL-Server-Verbindung
     * @param database  Datenbankname
     * @param username  Benutzername
     * @throws SQLException  Fehlermeldung wenn etwas nicht funktioniert hat
     */
    public void setAllRightsOnDatabase(Connection con, String database, String username) throws SQLException {
        execute(con,"GRANT ALL PRIVILEGES ON `"+database+"`.* TO "+quoteSql(username)+"@'%'");
        execute(con,"flush privileges");
    }

    /**
     * Setzt alle Rechte für einen Benutzer an der angegebenen Datenbank
     * @param database  Datenbankname
     * @param username  Benutzername
     * @return          true wenn alles funktioniert hat
     */
    public boolean setAllRightsOnDatabase(String database, String username) {
        try {
            Connection con = databaseConnectionService.mysqlAdminMysqlConnection();
            setAllRightsOnDatabase(con,database,username);
            return true;
        } catch (SQLException e) { }
        return false;
    }

    /**
     * Setzt das Passwort eines Benutzers
     * @param con       MySQL-Server-Verbindung
     * @param username  Benutzername
     * @param password  Passwort
     * @throws SQLException  Fehlermeldung wenn etwas nicht funktioniert hat
     */
    public void setPassword(Connection con, String username, String password) throws SQLException {
        execute(con,"SET PASSWORD FOR "+quoteSql(username)+"@'%' = "+quoteSql(password));
        //execute(con,"ALTER USER if exists "+quoteSql(username)+"@'%' IDENTIFIED WITH caching_sha2_password BY "+quoteSql(password));
    }

    /**
     * Setzt das Passwort eines Benutzers
     * @param username  Benutzername
     * @param password  Passwort
     * @return true wenn alles funktioniert hat
     */
    public boolean setPassword(String username, String password) throws SQLException {
        try {
            Connection con = databaseConnectionService.mysqlAdminMysqlConnection();
            setPassword(con,username,password);
            return true;
        } catch (SQLException e) { }
        return false;
    }

    /**
     * Erzeugt eine neue Datenbank
     * @param con      MySQL-Server-Verbindung
     * @param database Datenbankname
     * @throws SQLException  Fehlermeldung wenn etwas nicht funktioniert hat
     */
    public void createDatabase(Connection con, String database) throws SQLException {
        execute(con,"CREATE DATABASE if not exists "+database);
    }

    /**
     * Erzeugt eine neue Datenbank
     * @param database Datenbankname
     * @return true wenn alles funktioniert hat
     */
    public boolean createDatabase(String database){
        try {
            Connection con = databaseConnectionService.mysqlAdminMysqlConnection();
            createDatabase(con,database);
            return true;
        } catch (SQLException e) { }
        return false;
    }

    /**
     * Erzeugt eine neue Datenbank und löscht sie vorher falls sie schon existiert
     * @param con      MySQL-Server-Verbindung
     * @param database Datenbankname
     * @throws SQLException  Fehlermeldung wenn etwas nicht funktioniert hat
     */
    public void recreateDatabase(Connection con, String database) throws SQLException {
        execute(con,"DROP DATABASE if exists "+database);
        execute(con,"CREATE DATABASE if not exists "+database);
    }

    /**
     * Erzeugt eine neue Datenbank und löscht sie vorher falls sie schon existiert
     * @param database Datenbankname
     * @return true wenn alles funktioniert hat
     */
    public boolean recreateDatabase(String database){
        try {
            Connection con = databaseConnectionService.mysqlAdminMysqlConnection();
            recreateDatabase(con,database);
            return true;
        } catch (SQLException e) { }
        return false;
    }

    /**
     * Liefert eine Liste aller Datenbanken
     * @param con      MySQL-Server-Verbindung
     * @throws SQLException  Fehlermeldung wenn etwas nicht funktioniert hat
     */
    public List<String> showDatabases(Connection con) throws SQLException {
        List<String> databases=new ArrayList<String>();
        Vector<Vector<String>> data = executeQuery(con,"show databases");
        if (data!=null && data.size() > 1) {
            for (int r = 1; r < data.size(); r++) {
                String db = data.get(r).get(0);
                databases.add(db);
            }
        }
        return databases;
    }

    /**
     * @return Liefert eine Liste aller Datenbanken
     */
    public List<String> showDatabases() {
        try {
            Connection con = databaseConnectionService.mysqlAdminMysqlConnection();
            return showDatabases(con);
        } catch (SQLException e) { }
        return new ArrayList<>();
    }

    /**
     * Prüft ob eine Datenbank existiert
     * @param con      MySQL-Server-Verbindung
     * @param database Datebankname
     * @throws SQLException  Fehlermeldung wenn etwas nicht funktioniert hat
     */
    public boolean existDatabase(Connection con, String database) throws SQLException {
        for (String db:showDatabases(con)){
            if (db.trim().equalsIgnoreCase(database.trim()))
                return true;
        }
        return false;
    }

    /**
     * Prüft ob eine Datenbank existiert
     * @param database Datebankname
     */
    public boolean existDatabase(String database) {
        try {
            Connection con = databaseConnectionService.mysqlAdminMysqlConnection();
            return existDatabase(con,database);
        } catch (SQLException e) { }
        return false;
    }

    /**
     * Liefert eine Liste aller Tabellen einer Datenbank
     * @param  con      MySQL-Server-Verbindung
     * @throws SQLException  Fehlermeldung wenn etwas nicht funktioniert hat
     */
    public List<String> showTables(Connection con) throws SQLException {
        List<String> tables=new ArrayList<String>();
        Vector<Vector<String>> data = executeQuery(con,"show databases");
        if (data!=null && data.size() > 1) {
            for (int r = 1; r < data.size(); r++) {
                String db = data.get(r).get(0);
                tables.add(db);
            }
        }
        return tables;
    }

    /**
     * Liefert eine Liste aller Tabellen einer Datenbank
     * @param  database   Datenbankname
     */
    public List<String> showTables(String database) {
        try {
            Connection con = databaseConnectionService.mysqlRootConnection(database);
            return showTables(con);
        } catch (SQLException e) { }
        return new ArrayList<>();
    }

}
