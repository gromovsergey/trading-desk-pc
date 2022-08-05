package com.foros.session.channel.targeting;

public class TargetingStatsTO {
    private Long monthlyUsers;
    private Long dailyUsers;

    public TargetingStatsTO(Long monthlyUsers, Long dailyUsers) {
        this.monthlyUsers = monthlyUsers;
        this.dailyUsers = dailyUsers;
    }

    public Long getMonthlyUsers() {
        return monthlyUsers;
    }

    public void setMonthlyUsers(Long monthlyUsers) {
        this.monthlyUsers = monthlyUsers;
    }

    public Long getDailyUsers() {
        return dailyUsers;
    }

    public void setDailyUsers(Long dailyUsers) {
        this.dailyUsers = dailyUsers;
    }

    @Override
    public String toString() {
        return String.format("(%s,%s)", dailyUsers, monthlyUsers);
    }
}
