package test.istio.app.h2.controller;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.istio.app.h2.model.City;
import test.istio.app.h2.service.ICityService;

import java.net.ConnectException;
import java.util.List;

@RestController
public class CityController {

    @Autowired
    private ICityService cityService;

    @RequestMapping("/ping")
    public String ping() {
        System.out.println("!!! Call ping method. Reply PONG");
        return "pong";
    }

    @RequestMapping("/cities")
    public List<City> findCities() {

        return cityService.findAll();
    }

    @RequestMapping("/cities/{cityId}")
    public City findCity(@PathVariable Long cityId) {

        return cityService.findById(cityId);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<String> noCityFound(EmptyResultDataAccessException e) {

        System.out.println("!!! Error on handleJdbcConnectionException(): "  + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(value = {CommunicationsException.class})
    public ResponseEntity<Object> handleJdbcConnectionException(Exception e) {

        System.out.println("!!! Error on noCityFound(): " + e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
    }

    @ExceptionHandler(value = {ConnectException.class})
    public ResponseEntity<Object> handleConnectException(Exception e) {

        System.out.println("!!! Error on noCityFound(): " + e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
    }
}