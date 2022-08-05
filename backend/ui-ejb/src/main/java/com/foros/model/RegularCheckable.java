package com.foros.model;

import com.foros.model.security.User;

import java.util.Date;

public interface RegularCheckable  {
    public Integer getInterval();

    public void setInterval(Integer interval);

    public Date getLastCheckDate();

    public void setLastCheckDate(Date lastCheckDate);

    public Date getNextCheckDate();

    public void setNextCheckDate(Date nextCheckDate);

    public User getCheckUser();

    public void setCheckUser(User checkUser);

    public String getCheckNotes();

    public void setCheckNotes(String checkNotes);
}
