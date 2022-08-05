package com.foros.service.timed;

import com.foros.model.VersionEntityBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@NamedQueries({
    @NamedQuery(name = "ServiceModel.findAll", query = "SELECT sm FROM ServiceModel sm ")
})
@Table(name = "FOROS_TIMED_SERVICES")
public class ServiceModel extends VersionEntityBase {

    @Id
    @Column(name = "SERVICE_ID")
    private String serviceId;

    @Column(name = "INSTANCE_ID")
    private String instanceId;


    public ServiceModel() {
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
        this.registerChange("serviceId");
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
        this.registerChange("instanceId");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((serviceId == null) ? 0 : serviceId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ServiceModel other = (ServiceModel) obj;
        if (serviceId == null) {
            if (other.serviceId != null)
                return false;
        } else if (!serviceId.equals(other.serviceId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName());
        sb.append("serviceId=").append(getServiceId()).append(",");
        sb.append("instanceId=").append(getInstanceId()).append(",");
        sb.append("version=").append(getVersion()).append(",");
        return sb.toString();
    }
}
