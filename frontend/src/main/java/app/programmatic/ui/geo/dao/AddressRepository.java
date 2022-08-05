package app.programmatic.ui.geo.dao;

import org.springframework.data.repository.CrudRepository;
import app.programmatic.ui.geo.dao.model.Address;

public interface AddressRepository extends CrudRepository<Address, Long> {
}