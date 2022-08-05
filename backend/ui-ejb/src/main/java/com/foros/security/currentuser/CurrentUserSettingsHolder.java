package com.foros.security.currentuser;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.TimeZone;

public abstract class CurrentUserSettingsHolder {

    public static class Settings {

        private String ip;
        private TimeZone timeZone;
        private Locale locale;

        private Settings(String ip, TimeZone timeZone, Locale locale) {
            this.ip = ip;
            this.timeZone = timeZone;
            this.locale = locale;
        }

        public String getIp() {
            return ip;
        }

        public TimeZone getTimeZone() {
            return timeZone;
        }

        public Locale getLocale() {
            return locale;
        }

        private void setIp(String ip) {
            this.ip = ip;
        }

        private void setTimeZone(TimeZone timeZone) {
            this.timeZone = timeZone;
        }

        private void setLocale(Locale locale) {
            this.locale = locale;
        }
    }

    private static final ThreadLocal<Settings> settings = new ThreadLocal<Settings>();

    public static String getIp() {
        return getSettings().getIp();
    }

    public static String getIpOrDefault() {
        Settings info = settings.get();
        if (info != null) {
            return info.getIp();
        } else {
            return "127.0.0.1";
        }
    }

    public static TimeZone getTimeZoneOrDefault() {
        Settings info = settings.get();
        if (info != null) {
            return info.getTimeZone();
        } else {
            return TimeZone.getDefault();
        }
    }

    public static TimeZone getTimeZone() {
        return getSettings().getTimeZone();
    }

    public static Locale getLocaleOrDefault() {
        Settings info = settings.get();
        if (info != null) {
            return info.getLocale();
        } else {
            return Locale.getDefault();
        }
    }

    public static Locale getLocale() {
        return getSettings().getLocale();
    }

    public static NumberFormat getNumberFormat() {
        return NumberFormat.getInstance(getLocale());
    }

    public static boolean isSettingsSet() {
        return settings.get() != null;
    }

    public static Settings getSettings() {
        Settings info = settings.get();

        if (info == null) {
            throw new IllegalStateException("Settings not initialized, use CurrentUserSettingsFilter http filter");
        }

        return info;
    }

    public static void set(String ip, TimeZone timeZone, Locale locale) {
        Settings info = settings.get();

        if (info != null) {
            throw new IllegalStateException("Settings already initialized!");
        }

        settings.set(new Settings(ip, timeZone, locale));
    }

    public static void setIp(String ip) {
        getSettings().setIp(ip);
    }

    public static void setTimeZone(TimeZone timeZone) {
        getSettings().setTimeZone(timeZone);
    }

    public static void setLocale(Locale locale) {
        getSettings().setLocale(locale);
    }

    public static void clear() {
        Settings info = settings.get();

        if (info == null) {
            throw new IllegalStateException("Settings not initialized to clear!");
        }

        settings.remove();
    }

}
