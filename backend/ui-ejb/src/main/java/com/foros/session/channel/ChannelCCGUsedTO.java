package com.foros.session.channel;

import com.foros.model.channel.Channel;
import com.foros.session.NamedTO;
import com.foros.util.StringUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

public class ChannelCCGUsedTO extends ChannelTO implements Serializable {
    private Collection<NamedTO> ccgs = new TreeSet<NamedTO>(new Comparator<NamedTO>() {
        @Override
        public int compare(NamedTO o1, NamedTO o2) {
            int res = StringUtil.compareToIgnoreCase(o1.getName(), o2.getName());
            if (res == 0) {
                res = o1.getId().compareTo(o2.getId());
            }

            return res;
        }
    });

    public ChannelCCGUsedTO(){
       super();
    }

    public ChannelCCGUsedTO(Long id, String name, Long accountId, String accountName, Long displayStatusId) {
        super();
        setId(id);
        setName(name);
        setAccountId(accountId);
        setAccountName(accountName);
        setDisplayStatus(Channel.getDisplayStatus(displayStatusId));
    }

    public Collection<NamedTO> getCcgs() {
        return ccgs;
    }

    public void setCcgs(Collection<NamedTO> ccgs) {
        this.ccgs = ccgs;
    }
}
