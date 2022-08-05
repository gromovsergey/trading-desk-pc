package app.programmatic.ui.flight.tool;

import app.programmatic.ui.common.tool.javabean.JavaBeanAccessor;
import app.programmatic.ui.common.tool.javabean.JavaBeanUtils;
import app.programmatic.ui.flight.dao.model.ConfigurableLineItemProperty;
import app.programmatic.ui.flight.dao.model.Flight;
import app.programmatic.ui.flight.dao.model.FlightBase;
import app.programmatic.ui.flight.dao.model.FlightSchedule;
import app.programmatic.ui.flight.dao.model.FrequencyCap;
import app.programmatic.ui.flight.dao.model.LineItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EffectiveLineItemTool {
    private static final JavaBeanAccessor<LineItem> lineItemAccessor = JavaBeanUtils.createJavaEntityBeanAccessor(LineItem.class);
    private static final JavaBeanAccessor<FlightBase> flightBaseAccessor = JavaBeanUtils.createJavaEntityBeanAccessor(FlightBase.class);
    private static final Map<String, Integer> configurableProperties = initConfigurableProperties();

    private static Map<String, Integer> initConfigurableProperties() {
        return Arrays.stream(ConfigurableLineItemProperty.values())
                .collect(Collectors.toMap( e -> e.getName(), e -> 1 << e.ordinal() ));
    }

    public static LineItem buildEffective(LineItem lineItem, Flight owner) {
        LineItem result = new LineItem();
        lineItemAccessor.getPropertyNames().stream().forEach(
            propertyName -> {
                ValueInfo<?> effectiveValueInfo = getEffectivePropValue(propertyName, lineItem, owner);
                lineItemAccessor.set(result, propertyName, effectiveValueInfo.getValue());
            }
        );
        return result;
    }

    public static List<String> getPropNamesFromFlight(LineItem lineItem) {
        return configurableProperties.keySet().stream()
                .filter(propertyName -> isPropFromFlight(propertyName, lineItem))
                .collect(Collectors.toList());
    }

    public static LineItemPropsInfo getLineItemPropsInfo(Flight owner, LineItem lineItem) {
        int maxConfigurablesSize = configurableProperties.keySet().size();
        ArrayList<String> propsWithFlightValues = new ArrayList<>(maxConfigurablesSize);
        ArrayList<String> emptyProps = new ArrayList<>(lineItemAccessor.getPropertyNames().size());
        ArrayList<String> resetAwareProps = new ArrayList<>(maxConfigurablesSize);

        for (String propertyName : lineItemAccessor.getPropertyNames()) {
            Boolean isPropFromFlight = isPropFromFlight(propertyName, lineItem);

            if (isPropFromFlight != null && isPropFromFlight) {
                propsWithFlightValues.add(propertyName);
                continue;
            }

            Object lineItemValue = lineItemAccessor.get(lineItem, propertyName);
            boolean lineItemValueEmpty = isEmpty(lineItemValue);

            if (lineItemValueEmpty) {
                emptyProps.add(propertyName);
            }

            if (isPropFromFlight != null) {
                Object flightValue = flightBaseAccessor.get(owner, propertyName);
                boolean flightValueEmpty = isEmpty(flightValue);

                if (lineItemValueEmpty != flightValueEmpty ||
                        !lineItemValueEmpty && !isEqual(lineItemValue, flightValue, propertyName)) {
                    resetAwareProps.add(propertyName);
                }
            }
        }

        return new LineItemPropsInfo(propsWithFlightValues, emptyProps, resetAwareProps);
    }

    public static int buildPropsSource(Collection<String> propsFromFlight) {
        int result = 0;
        for (String propertyName: propsFromFlight) {
            result |= configurableProperties.get(propertyName);
        }
        return result;
    }

    public static <T> ValueInfo<T> getEffectivePropValue(String propertyName, LineItem lineItem, Flight owner) {
        Boolean getFromFlight = isPropFromFlight(propertyName, lineItem);
        if (getFromFlight == null) {
            return new ValueInfo<>(lineItemAccessor.get(lineItem, propertyName), false);
        }

        T value;
        if (getFromFlight) {
            value = transformFlightValue(flightBaseAccessor.get(owner, propertyName), propertyName);
        } else {
            value = flightBaseAccessor.get(lineItem, propertyName);
        }
        return new ValueInfo<>(value, getFromFlight);
    }

    public static Boolean isPropFromFlight(String propertyName, LineItem lineItem) {
        Integer propertyPosition = configurableProperties.get(propertyName);
        return propertyPosition == null ? null : (lineItem.getPropertiesSource() & propertyPosition) != 0;
    }

    private static <T> T transformFlightValue(T value, String propertyName) {
        if (value == null || !ConfigurableLineItemProperty.FREQUENCY_CAP.getName().equals(propertyName)) {
            return value;
        }

        FrequencyCap frequencyCap = (FrequencyCap)value;
        FrequencyCap result = new FrequencyCap();
        result.setLifeCount(frequencyCap.getLifeCount());
        result.setPeriod(frequencyCap.getPeriod());
        result.setWindowCount(frequencyCap.getWindowCount());
        result.setWindowLength(frequencyCap.getWindowLength());

        return (T)result;
    }

    private static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }

        return (obj instanceof Collection) ? ((Collection) obj).isEmpty() : false;
    }

    private static boolean isEqual(Object /*Not Null*/ lineItemValue, Object flightValue, String propertyName) {
        if (flightValue == null) {
            return false;
        }

        if (propertyName.equals(ConfigurableLineItemProperty.BLACK_LIST_ID.getName()) ||
                propertyName.equals(ConfigurableLineItemProperty.WHITE_LIST_ID.getName())) {
            // We cannot compare lists, but compare ids is meaningless
            return true;
        }

        if (propertyName.equals(ConfigurableLineItemProperty.FREQUENCY_CAP.getName())) {
            return isFQEqual(lineItemValue, flightValue);
        }

        if (propertyName.equals(ConfigurableLineItemProperty.SCHEDULES.getName())) {
            return isSchedulesEqual(lineItemValue, flightValue);
        }

        if (lineItemValue instanceof Collection) {
            return isCollectionsEqual(lineItemValue, flightValue);
        }

        return lineItemValue.equals(flightValue);
    }

    private static boolean isCollectionsEqual(Object lineItemValue, Object flightValue) {
        Collection liCollection = (Collection)lineItemValue;
        Collection fCollection = (Collection)flightValue;

        return liCollection.size() == fCollection.size() && liCollection.containsAll(fCollection);
    }

    private static boolean isFQEqual(Object lineItemValue, Object flightValue) {
        FrequencyCap liFQ = (FrequencyCap)lineItemValue;
        FrequencyCap fFQ = (FrequencyCap)flightValue;

        return isOrdinaryFieldEqual(liFQ.getLifeCount(), fFQ.getLifeCount()) &&
                isOrdinaryFieldEqual(liFQ.getPeriod(), fFQ.getPeriod()) &&
                isOrdinaryFieldEqual(liFQ.getWindowCount(), fFQ.getWindowCount()) &&
                isOrdinaryFieldEqual(liFQ.getWindowLength(), fFQ.getWindowLength());
    }

    private static boolean isSchedulesEqual(Object lineItemValue, Object flightValue) {
        Collection<FlightSchedule> liSchedules = (Collection<FlightSchedule>)lineItemValue;
        Collection<FlightSchedule> fSchedules = (Collection<FlightSchedule>)flightValue;

        if (liSchedules.size() != fSchedules.size()) {
            return false;
        }

        for (FlightSchedule liSchedule: liSchedules) {
            boolean accordantNotFound = true;
            for (FlightSchedule fSchedule: fSchedules) {
                if (isOrdinaryFieldEqual(liSchedule.getTimeFrom(), fSchedule.getTimeFrom()) &&
                    isOrdinaryFieldEqual(liSchedule.getTimeTo(), fSchedule.getTimeTo())) {
                        accordantNotFound = false;
                        break;
                }
            }

            if (accordantNotFound) {
                return false;
            }
        }

        return true;
    }

    private static boolean isOrdinaryFieldEqual(Object lineItemValue, Object flightValue) {
        return lineItemValue == flightValue ||
                lineItemValue != null && lineItemValue.equals(flightValue);
    }

    public static class ValueInfo<T> {
        private T value;
        private boolean isFromFlight;

        public ValueInfo(T value, boolean isFromFlight) {
            this.value = value;
            this.isFromFlight = isFromFlight;
        }

        public T getValue() {
            return value;
        }

        public boolean isFromFlight() {
            return isFromFlight;
        }
    }

    public static class LineItemPropsInfo {
        private List<String> propsWithFlightValues;
        private List<String> emptyProps;
        private List<String> resetAwareProps;

        public LineItemPropsInfo(List<String> propsWithFlightValues, List<String> emptyProps, List<String> resetAwareProps) {
            this.propsWithFlightValues = propsWithFlightValues;
            this.emptyProps = emptyProps;
            this.resetAwareProps = resetAwareProps;
        }

        public List<String> getPropsWithFlightValues() {
            return propsWithFlightValues;
        }

        public List<String> getEmptyProps() {
            return emptyProps;
        }

        public List<String> getResetAwareProps() {
            return resetAwareProps;
        }
    }
}
