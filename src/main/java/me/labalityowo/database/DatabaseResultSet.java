package me.labalityowo.database;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DatabaseResultSet {

    public void processData(ResultSet resultSet) throws SQLException;
}
