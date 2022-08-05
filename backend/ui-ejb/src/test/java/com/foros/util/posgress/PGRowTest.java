package com.foros.util.posgress;

import group.Unit;
import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Unit.class)
public class PGRowTest extends Assert {

    @Test
    public void parse() throws SQLException {
        //(293609,654654,14)
        assertArrayEquals(new Object[] {293609L, 654654L, 14L}, PGRow.read("(293609,654654,14)", new PGRow.Converter<Object[]>() {
            @Override
            public Object[] item(PGRow row) {
                return new Object[] {row.getLong(0), row.getLong(1), row.getLong(2)};
            }
        }));

        //(293609,654654,14)
        assertArrayEquals(new Object[] {293609L, 654654L, 14L}, PGRow.read("(\"293609\",\"654654\",\"14\")", new PGRow.Converter<Object[]>() {
            @Override
            public Object[] item(PGRow row) {
                return new Object[] {row.getLong(0), row.getLong(1), row.getLong(2)};
            }
        }));

        //("cp_te\\""st_1","test"",""ssss",13)
        assertArrayEquals(new Object[] {"cp_te\\\"st_1", "test\",\"ssss", 13L}, PGRow.read("(\"cp_te\\\\\"\"st_1\",\"test\"\",\"\"ssss\",13)", new PGRow.Converter<Object[]>() {
            @Override
            public Object[] item(PGRow row) {
                return new Object[] {row.getString(0), row.getString(1), row.getLong(2)};
            }
        }));

        //(,,)
        assertArrayEquals(new Object[] {"", "", null}, PGRow.read("(,,)", new PGRow.Converter<Object[]>() {
            @Override
            public Object[] item(PGRow row) {
                return new Object[] {row.getString(0), row.getString(1), row.getLong(2)};
            }
        }));
    }
}
