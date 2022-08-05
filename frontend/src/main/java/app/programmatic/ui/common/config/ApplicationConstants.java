package app.programmatic.ui.common.config;

import com.foros.rs.client.model.operation.PagingSelector;

import java.util.Locale;

public class ApplicationConstants {
    public static final int MAX_RESULTS_SIZE = 1000;
    public static final PagingSelector MAX_RESULTS_SIZE_SELECTOR = initMaxPagingSelector();

    public static final String RS_API_SINGLE_OPERATION_PREFIX_REGEX = "operations\\[0\\][.]";
    public static final Integer RS_API_FORBIDDEN_CODE = 207001;
    public static final Integer RS_API_OPTIMISTIC_LOCK_CODE = 301003;

    public static final Locale LOCALE_RU = Locale.forLanguageTag("ru-RU");

    private static PagingSelector initMaxPagingSelector() {
        PagingSelector result = new PagingSelector();
        result.setFirst(0l);
        result.setCount(Long.valueOf(MAX_RESULTS_SIZE));
        return result;
    }
}
