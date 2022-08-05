package app.programmatic.ui.user.dao;

import app.programmatic.ui.user.dao.model.UserRole;

import org.springframework.data.repository.CrudRepository;

public interface UserRoleRepository extends CrudRepository<UserRole, Long> {
}
