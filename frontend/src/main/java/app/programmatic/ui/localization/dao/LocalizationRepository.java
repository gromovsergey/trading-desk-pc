package app.programmatic.ui.localization.dao;

import org.springframework.data.repository.CrudRepository;
import app.programmatic.ui.localization.dao.model.Localization;
import app.programmatic.ui.localization.dao.model.LocalizationId;
import app.programmatic.ui.localization.dao.model.LocalizationLanguage;

import java.util.List;

public interface LocalizationRepository extends CrudRepository<Localization, LocalizationId> {

    List<Localization> findByKey(String key);

    List<Localization> findByLangAndKeyIn(LocalizationLanguage lang, Iterable<String> keys);
}
