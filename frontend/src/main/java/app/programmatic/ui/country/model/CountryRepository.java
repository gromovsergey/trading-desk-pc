package app.programmatic.ui.country.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CountryRepository extends CrudRepository<Country, String> {
    Country findByCountryCode(@Param("contry_code") String country_code  );

    List<Country> findBySortOrderIsNotNull();
}
