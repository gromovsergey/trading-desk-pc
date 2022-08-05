package com.foros.reporting.tools.olap.query;

import com.phorm.oix.olap.EmptyRangeException;
import com.phorm.oix.olap.MemberNotFoundException;
import com.phorm.oix.olap.OlapIdentifier;
import com.phorm.oix.olap.QueryAxes;
import com.phorm.oix.olap.QueryHierarchy;
import com.phorm.oix.olap.QueryLevel;
import com.phorm.oix.olap.RangeMember;
import com.phorm.oix.olap.builder.QueryBuilder;
import com.phorm.oix.olap.mdx.BinaryOperatorElement;
import com.phorm.oix.olap.mdx.ConstantElement;
import com.phorm.oix.olap.mdx.CurrentMemberElement;
import com.phorm.oix.olap.mdx.FunctionElement;
import com.phorm.oix.olap.mdx.MdxExpressionElement;
import com.phorm.oix.olap.mdx.MemberElement;
import com.phorm.oix.olap.mdx.MemberWithPostfixElement;
import com.phorm.oix.olap.mdx.SeparatedContainerElement;
import com.phorm.oix.olap.mdx.TupleElement;
import com.foros.reporting.meta.ColumnOrder;
import com.foros.reporting.meta.olap.OlapColumn;
import com.foros.reporting.meta.olap.OlapColumnType;
import com.foros.reporting.tools.olap.query.builder.LevelAndOrder;
import com.foros.reporting.tools.olap.query.builder.MeasureAndOrder;
import com.foros.reporting.tools.olap.query.builder.MemberSetAndOrderHolder;
import com.foros.session.reporting.parameters.DateRange;

import org.joda.time.LocalDate;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Level;
import org.olap4j.metadata.Measure;
import org.olap4j.metadata.Member;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public abstract class AbstractOlapQuery implements OlapQuery {

    private static final Logger logger = Logger.getLogger(AbstractOlapQuery.class.getName());

    protected final CubeExplorer cubeExplorer;
    protected final Object context;

    private QueryAxes axes = new QueryAxes();
    private Map<String, Level> rowDeepestLevels = new HashMap<>();

    private final List<ColumnOrder<OlapColumn>> columnOrders = new ArrayList<>();
    private Integer limit = null;

    public AbstractOlapQuery(Object context, Cube cube) {
        this.context = context;
        this.cubeExplorer = new CubeExplorer(cube);
    }

    protected abstract void checkCancelled();

    @Override
    public OlapQuery limit(int limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public OlapQuery columns(Collection<OlapColumn> columns) {
        for (OlapColumn column : columns) {
            if (column.getOlapColumnType() == OlapColumnType.METRIC) {
                axes.getColAxis().addMember(lookupMeasure(column.getMember(context)));
            } else {
                axes.getRowAxis().addLevel(lookupLevel(column.getMember(context)));
            }
        }

        return this;
    }

    @Override
    public OlapQuery filter(OlapIdentifier level, DateRange dateRange) {
        Member from = lookupDateMember(level, dateRange.getBegin());
        Member to = lookupDateMember(level, dateRange.getEnd());

        if (from == null || to == null) {
            throw new EmptyRangeException("Empty members for range: " + dateRange);
        }

        axes.getFilterAxis().addMember(new RangeMember(from, to));
        return this;
    }

    @Override
    public OlapQuery rows(OlapIdentifier level, DateRange dateRange) {
        Member from = lookupDateMember(level, dateRange.getBegin());
        Member to = lookupDateMember(level, dateRange.getEnd());

        if (from == null || to == null) {
            throw new EmptyRangeException("Empty members for range: " + dateRange);
        }

        axes.getRowAxis().addMember(new RangeMember(from, to));
        return this;
    }

    @Override
    public OlapQuery filter(OlapIdentifier level, List<Long> values) {
        filter(level, values, true);
        return this;
    }

    @Override
    public OlapQuery filter(OlapIdentifier level, List<Long> values, boolean failIfNotFound) {
        if (values != null && !values.isEmpty()) {
            for (Long value : values) {
                try {
                    axes.getFilterAxis().addMember(lookupMember(level, value.toString()));
                } catch (MemberNotFoundException ex) {
                    if (failIfNotFound) {
                        throw ex;
                    }
                }
            }
        }
        return this;
    }

    @Override
    public OlapQuery row(OlapIdentifier level, List<Long> values, boolean failIfNotFound) {
        if (values != null && !values.isEmpty()) {
            for (Long value : values) {
                try {
                    axes.getRowAxis().addMember(lookupMember(level, value.toString()));
                } catch (MemberNotFoundException ex) {
                    if (failIfNotFound) {
                        throw ex;
                    }
                }
            }
        }
        return this;
    }

    @Override
    public OlapQuery filter(OlapIdentifier level, String value) {
        if (value != null && !value.isEmpty()) {
            axes.getFilterAxis().addMember(lookupMember(level, value));
        }

        return this;
    }

    @Override
    public OlapQuery row(OlapIdentifier level, String value) {
        if (value != null && !value.isEmpty()) {
            axes.getRowAxis().addMember(lookupMember(level, value));
        }

        return this;
    }

    @Override
    public OlapQuery filter(OlapIdentifier level, Long value) {
        if (value != null) {
            axes.getFilterAxis().addMember(lookupMember(level, value.toString()));
        }

        return this;
    }

    @Override
    public OlapQuery row(OlapIdentifier level, Long value) {
        if (value != null) {
            axes.getRowAxis().addMember(lookupMember(level, value.toString()));
        }

        return this;
    }

    @Override
    public OlapQuery order(List<ColumnOrder<OlapColumn>> columns) {
        columnOrders.addAll(columns);
        return this;
    }

    @Override
    public OlapQuery order(ColumnOrder<OlapColumn> columnOrder) {
        columnOrders.add(columnOrder);
        return this;
    }

    protected String generateMdx(String statementUuid) {
        fillRowDeepestLevels();
        axes.getFilterAxis().drainTo(axes.getRowAxis());

        QueryBuilder builder = new QueryBuilder(cubeExplorer.getCube().getName());

        builder.binding("MEMBERS_COLS_")
                .members(axes.getColAxis().getMembers())
                .end();

        List<String> rowsDataSetNames = new ArrayList<>();
        for (QueryHierarchy hierarchy : axes.getRowAxis().getHierarchies()) {
            rowsDataSetNames.add(createRowsForHierarchy(builder, hierarchy));
        }

        Map<String, String> filterDataSetNames = new HashMap<>();

        for (QueryHierarchy hierarchy : axes.getFilterAxis().getHierarchies()) {
            String name = generateDataSetName(hierarchy.getHierarchy());
            filterDataSetNames.put(hierarchy.getHierarchy().getUniqueName(), name);


            builder.binding(name)
                    .members(hierarchy.getDeepestMembers())
                    .end();
        }

        builder.binding("MEMBERS_ROWS_").dataSetsNonEmptyCrossJoin(rowsDataSetNames).end();
        builder.binding("MEMBERS_FILTERS_").dataSetsNonEmptyCrossJoin(filterDataSetNames.values()).end();

        MemberSetAndOrderHolder orderElements = fetchOrderLevels();

        /* cols */
        builder.columns().dataSets("MEMBERS_COLS_");

        /* rows */
        if (orderElements.hasMeasures() || orderElements.hasLevels()) {
            MdxExpressionElement membersRows = createFilterNotNullElement(new MemberElement("MEMBERS_ROWS_"), axes.getColAxis().getMembers());

            builder.binding("MEMBERS_FILTERED_ROWS_").expression(membersRows).end();

            MdxExpressionElement orderedRows = createColsOrderElement(new MemberElement("MEMBERS_FILTERED_ROWS_"), orderElements);

            builder.binding("MEMBERS_ORDERED_ROWS_").expression(orderedRows).end();

            builder.binding("MEMBERS_LIMITED_ROWS_").limit("MEMBERS_ORDERED_ROWS_", limit).end();
        } else {
            builder.binding("MEMBERS_LIMITED_ROWS_")
                    .limit("MEMBERS_ROWS_", limit)
                    .end();
        }

        builder.rows().dataSets("MEMBERS_LIMITED_ROWS_");

        if (!filterDataSetNames.isEmpty()) {
            builder.filter().dataSets("MEMBERS_FILTERS_");
        }

        String mdx = builder.toString();

        logger.info("Generated Mdx (uuid=" + statementUuid + "):\n" + mdx + "\n");

        return mdx;
    }

    private void fillRowDeepestLevels() {
        rowDeepestLevels = new HashMap<>();
        for (QueryHierarchy hierarchy : axes.getRowAxis().getHierarchies()) {
            Level deepestEmptyLevel = hierarchy.getDeepestEmptyLevel();
            rowDeepestLevels.put(hierarchy.getHierarchy().getUniqueName(), deepestEmptyLevel != null ? deepestEmptyLevel : hierarchy.getDeepestLevel());
        }
    }

    private String createRowsForHierarchy(QueryBuilder builder, QueryHierarchy hierarchy) {
        String hierarchyName = generateDataSetName(hierarchy.getHierarchy());

        ArrayList<QueryLevel> levels = new ArrayList<>(hierarchy.getLevels());
        Collections.sort(levels, new Comparator<QueryLevel>() {
            @Override
            public int compare(QueryLevel o1, QueryLevel o2) {
                return Integer.compare(o2.getLevel().getDepth(), o1.getLevel().getDepth());
            }
        });

        final Collection<Member> deepestMembers = hierarchy.getDeepestMembers();

        if (deepestMembers.isEmpty()) {
            builder.binding(hierarchyName).levels(hierarchy.getDeepestEmptyLevel()).end();
        } else if (hierarchy.getMembers().size() == 1 && !(hierarchy.getMembers().iterator().next() instanceof  RangeMember)) {
            Level rowDeepestLevel = getRowDeepestLevel(hierarchy);
            Member member = deepestMembers.iterator().next();
            if (member.getLevel().getDepth() >= rowDeepestLevel.getDepth()) {
                builder.binding(hierarchyName)
                        .members(member)
                        .end();
            } else {
                builder.binding(hierarchyName)
                        .descendants(member, rowDeepestLevel)
                        .end();
            }
        } else {
            final Level deepestMemberLevel = deepestMembers.iterator().next().getLevel();
            Set<Member> visitedMembers = new HashSet<>(deepestMembers);
            final List<String> membersDataSetNames = new ArrayList<>();

            for (QueryLevel level : levels) {
                for (Member member : level.getMembers()) {
                    if (!isParentOfMembers(member, visitedMembers)) {
                        String name = generateDataSetName(member);
                        builder.binding(name)
                                .descendants(member, deepestMemberLevel)
                                .end();
                        membersDataSetNames.add(name);
                        visitedMembers.add(member);
                    }
                }
            }
            Level rowLevel = getRowDeepestLevel(hierarchy);
            if (rowLevel.getDepth() > deepestMemberLevel.getDepth()) {
                String deepestMemberLevelDataSetName = generateDataSetName(deepestMemberLevel);
                builder.binding(deepestMemberLevelDataSetName)
                        .members(deepestMembers)
                        .rowSets(membersDataSetNames)
                        .end();

                builder.binding(hierarchyName)
                        .exists(deepestMemberLevelDataSetName, rowLevel)
                        .end();
            } else {
                builder.binding(hierarchyName)
                        .members(deepestMembers)
                        .rowSets(membersDataSetNames)
                        .end();
            }

        }
        return hierarchyName;
    }

    private Level getRowDeepestLevel(QueryHierarchy hierarchy) {
        return rowDeepestLevels.get(hierarchy.getHierarchy().getUniqueName());
    }

    private String generateDataSetName(Member member) {
        String uniqueName = member.getUniqueName().replace("].[", "_").replaceAll("[\\[\\]]", "");
        return "~" + uniqueName;
    }

    protected boolean isParentOfMembers(Member parentMember, Collection<Member> members) {
        for (Member member : members) {
            if (member.getUniqueName().startsWith(parentMember.getUniqueName()))
                return true;
        }
        return false;
    }

    private MdxExpressionElement createColsOrderElement(MdxExpressionElement dataSet, MemberSetAndOrderHolder orderElements) {
        FunctionElement order = new FunctionElement("ORDER", dataSet);

        for (LevelAndOrder levelAndOrder : orderElements.getLevels()) {
            FunctionElement ancestorFunc = new FunctionElement("ANCESTOR",
                    new CurrentMemberElement(OlapUtils.identifierNodeToPath(levelAndOrder.getLevel().getHierarchy())),
                    new MemberElement(OlapUtils.identifierNodeToPath(levelAndOrder.getLevel().getUniqueName()))
            );
            order.add(new MemberWithPostfixElement(ancestorFunc, "orderkey"));
            order.add(new ConstantElement<>(OlapUtils.getOlapOrderByOrder(levelAndOrder.getOrder())));
        }

        for (MeasureAndOrder mo : orderElements.getMeasures()) {
            order.add(new MemberElement(OlapUtils.identifierNodeToPath(mo.getMeasure())));
            order.add(new ConstantElement<>(OlapUtils.getOlapOrderByOrder(mo.getOrder())));
        }

        return order;
    }

    private MemberSetAndOrderHolder fetchOrderLevels() {
        MemberSetAndOrderHolder memberSetAndOrderHolder = new MemberSetAndOrderHolder();

        for (ColumnOrder<OlapColumn> columnOrder : columnOrders) {
            OlapColumn column = columnOrder.getColumn();
            if (column.getOlapColumnType() == OlapColumnType.METRIC) {
                Measure measure = lookupMeasure(column.getMember(context));
                memberSetAndOrderHolder.addMeasureAndOrder(new MeasureAndOrder(measure, columnOrder.getOrder()));
            } else {
                Level level = lookupLevel(column.getMember(context));
                memberSetAndOrderHolder.addLevelAndOrder(new LevelAndOrder(level, columnOrder.getOrder()));
            }
        }

        return memberSetAndOrderHolder;
    }

    private MdxExpressionElement createFilterNotNullElement(MdxExpressionElement filteredElement, Collection<Member> members) {
        SeparatedContainerElement<MdxExpressionElement> membersElement = new SeparatedContainerElement<>("OR");

        for (Member member : members) {
            membersElement.add(new BinaryOperatorElement(">",
                    new MemberElement(OlapUtils.identifierNodeToPath(member)),
                    new ConstantElement<>(0))
            );
        }

        return new FunctionElement("FILTER", filteredElement, new TupleElement(membersElement));
    }

    private String generateDataSetName(Hierarchy hierarchy) {
        String uniqueName = hierarchy.getUniqueName().replace("].[", "_").replaceAll("[\\[\\]]", "");
        return "~ROWS_" + uniqueName;
    }

    private String generateDataSetName(Level level) {
        String uniqueName = level.getUniqueName().replace("].[", "_").replaceAll("[\\[\\]]", "");
        return "~" + uniqueName;
    }

    private Member lookupDateMember(OlapIdentifier level, LocalDate date) {
        checkCancelled();
        return cubeExplorer.lookupDateMember(level, date);
    }

    private Measure lookupMeasure(OlapIdentifier memberDescriptor) {
        checkCancelled();
        return cubeExplorer.lookupMeasure(memberDescriptor);
    }

    private Level lookupLevel(OlapIdentifier identifier) {
        checkCancelled();
        return cubeExplorer.lookupLevel(identifier);
    }

    private Member lookupMember(OlapIdentifier member, String value) {
        checkCancelled();
        return cubeExplorer.lookupMember(member, value);
    }
}
