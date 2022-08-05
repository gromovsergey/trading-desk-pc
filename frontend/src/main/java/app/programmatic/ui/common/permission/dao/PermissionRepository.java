package app.programmatic.ui.common.permission.dao;

import app.programmatic.ui.common.permission.dao.model.Permission;

import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface PermissionRepository extends CrudRepository<Permission, Long> {

    List<Permission> findByUserRoleId(Long userRoleId);

    List<Permission> findByUserRoleIdAndTypeAndActionAndParameter(Long userRoleId, String type, String action, String parameter);
}
