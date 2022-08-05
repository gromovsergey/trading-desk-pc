package app.programmatic.ui.channel.dao.model;

public class ChannelName {
    private String name;
    private String accountName;

    public ChannelName() {
    }

    public ChannelName(String name, String accountName) {
        this.name = name;
        this.accountName = accountName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChannelName that = (ChannelName) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (accountName != null ? !accountName.equals(that.accountName) : that.accountName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int nameHashCode = name != null ? name.hashCode() : 0;
        int accountNameHashCode = accountName != null ? accountName.hashCode() : 0;
        return 31 * nameHashCode + accountNameHashCode;
    }
}
