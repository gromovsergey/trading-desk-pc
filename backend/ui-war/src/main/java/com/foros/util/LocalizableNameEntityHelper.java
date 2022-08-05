package com.foros.util;

import com.foros.action.IdNameBean;
import com.foros.model.LocalizableNameEntity;
import com.foros.model.StatusEntityBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LocalizableNameEntityHelper {
    public static List<IdNameBean> convertToIdNameBeans(Collection<? extends LocalizableNameEntity> localizableCollection){
        if (localizableCollection == null) {
            return null;
        }

        List<IdNameBean> idNameCollection = new ArrayList<IdNameBean>(localizableCollection.size());

        for (LocalizableNameEntity entity : localizableCollection) {
            String name = LocalizableNameUtil.getLocalizedValue(entity.getName());

            if (entity instanceof StatusEntityBase && ((StatusEntityBase) entity).getStatus() != null) {
                name = EntityUtils.appendStatusSuffix(name, ((StatusEntityBase) entity).getStatus());
            }

            idNameCollection.add(new IdNameBean(String.valueOf(entity.getId()), name));
        }
        
        return idNameCollection;
    }
    
    private LocalizableNameEntityHelper() {
    }
}
