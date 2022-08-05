package com.foros.reporting.tools.olap.query;

import com.phorm.oix.olap.MemberNotFoundException;
import com.phorm.oix.olap.OlapIdentifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.olap4j.OlapException;
import org.olap4j.mdx.IdentifierNode;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Level;
import org.olap4j.metadata.Measure;
import org.olap4j.metadata.Member;

public class CubeExplorer {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    private Cube cube;

    public CubeExplorer(Cube cube) {
        if (cube == null) {
            throw new NullPointerException("Cube must not be null.");
        }

        this.cube = cube;
    }

    public Measure lookupMeasure(OlapIdentifier memberDescriptor) {
        IdentifierNode identifierNode = memberDescriptor.toIdentifierNode();

        Member member = lookupMember(identifierNode);

        if (!(member instanceof Measure)) {
            throw new IllegalArgumentException(identifierNode + " not a measure");
        }

        return (Measure) member;
    }

    public Level lookupLevel(OlapIdentifier identifier) {
        List<String> segments = identifier.getSegments();

        if (segments.size() < 2) {
            throw new IllegalArgumentException("To address level need two or more segments identifier: " + identifier);
        }

        Iterator<String> iterator = segments.iterator();

        String hierarchyName = iterator.next();
        Hierarchy hierarchy = findHierarchy(hierarchyName);

        if (hierarchy == null) {
            throw new IllegalArgumentException("Hierarchy " + hierarchyName + " not found.");
        }

        Level level = hierarchy.getLevels().get(iterator.next());

        if (level != null) {
            return level;
        }

        throw new IllegalArgumentException("Level " + identifier.getUniqueName() + " not found.");
    }

    private Hierarchy findHierarchy(String dimensionAndHierarchyName) {
        return cube.getHierarchies().get(dimensionAndHierarchyName);
    }

    public List<Member> lookupDateMembers(OlapIdentifier level, List<LocalDate> dates) {
        ArrayList<Member> members = new ArrayList<>();

        for (LocalDate date : dates) {
            Member member = lookupDateMember(level, date);
            if (member != null) {
                members.add(member);
            }
        }

        return members;
    }

    public Member lookupDateMember(OlapIdentifier level, LocalDate date) {
        return lookupMemberSafely(level, dateTimeFormatter.print(date));
    }

    public Member lookupMember(OlapIdentifier member, String...value) {
        return lookupMember(member.append(value).toIdentifierNode());
    }

    public Member lookupMemberSafely(OlapIdentifier member, String... value) {
        try {
            return lookupMember(member, value);
        } catch (MemberNotFoundException e) {
            return null;
        }
    }

    private Member lookupMember(IdentifierNode identifierNode) {
        try {
            return cube.lookupMember(identifierNode.getSegmentList());
        } catch (OlapException e) {
            throw new MemberNotFoundException(identifierNode.toString(), e);
        }
    }

    public Cube getCube() {
        return cube;
    }

}
