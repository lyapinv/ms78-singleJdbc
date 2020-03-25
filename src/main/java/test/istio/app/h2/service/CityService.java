package test.istio.app.h2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import test.istio.app.h2.model.City;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class CityService implements ICityService {

    @Autowired
    private JdbcTemplate jtm;

    @Value("${DB_CONNECTION_URI}")
    private String connectionUri;

//    private final String connectionUri = "jdbc:mysql://minishift.host:3306/test";
//    private final String connectionUri = "jdbc:mysql://mysql-ext-service:3306/test";


    @Override
    public List<City> findAll() {
        String sql = "SELECT * FROM cities";
//        createNumerousConnections(3);
        return jtm.query(sql, new BeanPropertyRowMapper<>(City.class));
    }

//    private List<Statement> createNumerousConnections(int numOfConnections) {
//        List<Statement> smtpList = new ArrayList<>();
//        for (int i = 0; i < numOfConnections; i++) {
//            smtpList.add(createConnection(i));
//        }
//        return smtpList;
//    }
//
//    private Statement createConnection(int currentConnectionNum) {
//        Statement stmt = null;
//        try {
//            System.out.println(" !!! connectionUri: " + connectionUri);
//            Class.forName("com.mysql.jdbc.Driver");
//            Connection con = DriverManager.
//                    getConnection(connectionUri
//                            ,"root","sa");
//            stmt = con.createStatement();
//            System.out.println("Created DB Connection.... NUM: " + currentConnectionNum);
//        } catch (ClassNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (SQLException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return stmt;
//    }

    @Override
    public City findById(Long id) {
        String sql = "SELECT * FROM cities WHERE id = ?";

        return jtm.queryForObject(sql, new Object[]{id},
                new BeanPropertyRowMapper<>(City.class));
    }
}