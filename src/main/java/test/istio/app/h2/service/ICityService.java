package test.istio.app.h2.service;

import test.istio.app.h2.model.City;
import java.util.List;

public interface ICityService {

    List<City> findAll();

    City findById(Long id);
}
