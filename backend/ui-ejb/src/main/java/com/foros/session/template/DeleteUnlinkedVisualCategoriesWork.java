package com.foros.session.template;

import com.foros.util.command.PreparedStatementWork;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteUnlinkedVisualCategoriesWork extends PreparedStatementWork {
    private Long templateId;

    public DeleteUnlinkedVisualCategoriesWork(Long id) {
        this.sql = createDeleteVisualCategoriesQuery();
        templateId = id;
    }

    @Override
    protected void prepareStatement(PreparedStatement statement) throws SQLException {
        statement.setLong(1, templateId);
    }

    private static String createDeleteVisualCategoriesQuery() {
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("delete from CreativeCategory_Creative ")
                .append(" where (creative_category_id, creative_id) = any ")
                .append(" (select distinct ccc.creative_category_id, ccc.creative_id ")
                .append(" from Creative c ")
                .append(" inner join CreativeCategory_Creative ccc on ccc.creative_id = c.creative_id ")
                .append(" inner join CreativeCategory cc on cc.creative_category_id = ccc.creative_category_id ")
                .append(" where ")
                .append(" c.template_id = ? and cc.cct_id = 0 ) ");
        return queryBuilder.toString();
    }

}
