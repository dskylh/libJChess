import com.github.bhlangonijr.chesslib.game.Game;

import java.sql.*;

public class DatabaseCon {

    public static void sqlConnection(Game game) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/localhost", "root", "");
            Statement stmt = con.createStatement();
            stmt.executeQuery("CREATE DATABASE IF NOT EXISTS jchess;" +
                    "CREATE TABLE IF NOT EXISTS jchess.games (`game_id` INT NOT NULL AUTO_INCREMENT, " +
                    "`game` VARCHAR, PRIMARY KEY (`game_id`)) ENGINE=MyISAM;");

            stmt.executeQuery(String.format("INSERT INTO jchess.games (game) VALUES ('%s')", game.toString()));
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
