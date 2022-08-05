package com.foros.reporting.serializer.xlsx;

import com.foros.AbstractUnitTest;
import com.foros.reporting.Row;
import com.foros.reporting.RowType;
import com.foros.reporting.RowTypes;
import com.foros.reporting.meta.AbstractDependentColumn;
import com.foros.reporting.meta.ColumnType;
import com.foros.reporting.meta.ColumnTypes;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.meta.ReportMetaData;
import com.foros.reporting.meta.ReportMetaDataImpl;
import com.foros.reporting.meta.SimpleDependenciesColumnResolver;
import com.foros.reporting.tools.subtotal.aggreagate.NullAggregateFunction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import group.Report;
import group.Unit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Unit.class, Report.class })
public class XlsxSerializerTest extends AbstractUnitTest {
    public static final TestColumn STRING = new TestColumn(0, ColumnTypes.string());
    public static final TestColumn NUMBER = new TestColumn(1, ColumnTypes.number());
    public static final TestColumn NUMBER2 = new TestColumn(2, ColumnTypes.number());
    public static final TestColumn DATE = new TestColumn(3, ColumnTypes.date());

    public static final ReportMetaData<TestColumn> META_DATA = new ReportMetaDataImpl<>(
            "", Collections.<TestColumn>emptyList(), Arrays.asList(STRING, NUMBER, DATE), new Object());
    public static final ReportMetaData<TestColumn> META_DATA1 = new ReportMetaDataImpl<>(
            "", Collections.<TestColumn>emptyList(), Arrays.asList(STRING, NUMBER), new Object());
    public static final ReportMetaData<TestColumn> META_DATA2 = new ReportMetaDataImpl<>(
            "", Collections.<TestColumn>emptyList(), Arrays.asList(STRING, NUMBER, NUMBER2), new Object());

    private File testFile;
    private ExcelStyles styles = ExcelStylesRegistry.DEFAULT_STYLES;

    @Before
    public void setUp() throws Exception {
        testFile = new File(getTargetFolder(), "XlsxSerializerTest.xlsx");
        if (testFile.exists()) {
            assertTrue(testFile.delete());
        }
    }

    @Test
    public void xlsxSerializer() throws Exception {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(testFile);
            writeTestFile(os);
        } finally {
            IOUtils.closeQuietly(os);
        }

        InputStream is = null;
        try {
            is = new FileInputStream(testFile);
            assertTestFile(is);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    private void writeTestFile(FileOutputStream out) throws IOException {
        XlsxSerializer serializer = new XlsxSerializer(out, null, styles);
        writeSheet(serializer, META_DATA);
        writeSheet(serializer, META_DATA1);
        writeSheet(serializer, META_DATA2);
        serializer.close();
    }

    private void writeSheet(XlsxSerializer serializer, final MetaData<TestColumn> metaData) {
        serializer.before(metaData);

        serializer.row(new Row<TestColumn>() {
            @Override
            public Object get(TestColumn column) {
                return new Object[] {"test", 7L, new BigDecimal("4567.123"), new LocalDate()}[column.index];
            }

            @Override
            public RowType getType() {
                return RowTypes.data();
            }
        });

        serializer.after();
    }

    private void assertTestFile(InputStream is) throws IOException, InvalidFormatException {
        XSSFWorkbook wb = (XSSFWorkbook) WorkbookFactory.create(is);
        XSSFSheet sheet = wb.getSheetAt(0);
        assertNotNull(sheet);

        assertEquals("two rows are expected", 1, sheet.getLastRowNum());

        XSSFCell cell;

        // header cell
        cell = sheet.getRow(0).getCell(0);
        assertTrue("header should be bold", wb.getFontAt(cell.getCellStyle().getFontIndex()).getBold());

        // number cell
        cell = sheet.getRow(1).getCell(1);
        assertFalse("data shouldn't be bold", wb.getFontAt(cell.getCellStyle().getFontIndex()).getBold());
    }

    private static class TestColumn extends AbstractDependentColumn<TestColumn> {
        private int index;

        private TestColumn(int index, ColumnType type) {
            super("", type, NullAggregateFunction.<TestColumn>instance(), new SimpleDependenciesColumnResolver<TestColumn>());
            this.index = index;
        }

        @Override
        public String getNameKey() {
            return "test" + index;
        }

        @Override
        public String toString() {
            return getType().getName();
        }
    }

}
