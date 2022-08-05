package com.foros.session;

import com.foros.model.DisplayStatus;
import com.foros.model.Status;
import com.foros.util.EntityUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.springframework.jdbc.core.RowMapper;

public class TreeFilterElementTOConverter implements RowMapper<TreeFilterElementTO> {

    private final boolean withChildren;
    private final Map<Long, DisplayStatus> displayStatusMap;

    public TreeFilterElementTOConverter(Map<Long, DisplayStatus> displayStatusMap) {
        this(displayStatusMap, false);
    }

    public TreeFilterElementTOConverter(Map<Long, DisplayStatus> displayStatusMap, boolean withChildren) {
        this.displayStatusMap = displayStatusMap;
        this.withChildren = withChildren;
    }

    @Override
    public TreeFilterElementTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");

        Status status = Status.valueOf(rs.getString("status").charAt(0));
        String name = EntityUtils.appendStatusSuffix(rs.getString("name"), status);

        DisplayStatus displayStatus = null;
        if (displayStatusMap != null) {
            displayStatus = displayStatusMap.get(rs.getLong("display_status_id"));
        }
        boolean hasChildren = withChildren || rs.getBoolean("hasChildren");

        return new TreeFilterElementTO(id, name, status, displayStatus, hasChildren);
    }
}
