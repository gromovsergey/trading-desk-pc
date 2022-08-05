package app.programmatic.ui.user.dao;

import app.programmatic.ui.user.dao.model.User;
import app.programmatic.ui.user.dao.model.UserRole;

import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findByEmailIgnoreCase(String email);

    User findByUserCredentialRsToken(String rsToken);

    List<User> findByAccountIdAndUserRoleIn(Long accountId, Iterable<UserRole> userRoles);
}
