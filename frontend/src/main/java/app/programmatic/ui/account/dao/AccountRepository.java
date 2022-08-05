package app.programmatic.ui.account.dao;

import org.springframework.data.repository.CrudRepository;
import app.programmatic.ui.account.dao.model.AccountEntity;

public interface AccountRepository extends CrudRepository<AccountEntity, Long> {
}