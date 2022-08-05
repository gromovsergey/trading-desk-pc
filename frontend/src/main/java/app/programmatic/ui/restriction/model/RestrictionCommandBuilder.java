package app.programmatic.ui.restriction.model;

import com.foros.rs.client.model.restriction.RestrictionCommand;
import com.foros.rs.client.model.restriction.RestrictionCommandsOperation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class RestrictionCommandBuilder {
    private List<RestrictionCommand> commands = new ArrayList<>();

    public void add(Restriction restriction) {
        add(restriction, null);
    }

    public void add(Restriction restriction, Long paramId) {
        if (restriction.getParam().isIdRequired() && paramId == null ||
                !restriction.getParam().isIdRequired() && paramId != null) {
            throw new IllegalArgumentException("Restriction " + restriction + " is added with incorrect param id");
        }

        commands.add(newCommand(
                restriction.getForosName(),
                restriction.getParam() == RestrictionParam.NOT_REQUIRED ? null : restriction.getParam().getForosName(),
                paramId
        ));
    }

    public RestrictionCommandsOperation build() {
        RestrictionCommandsOperation result = new RestrictionCommandsOperation();
        result.setRestrictionCommands(commands);
        return result;
    }

    private static RestrictionCommand newCommand(String forosRestrictionName, String paramName, Long paramValue) {
        RestrictionCommand command = new RestrictionCommand();
        command.setName(forosRestrictionName);

        if (paramName == null && paramValue == null) {
            return command;
        }

        com.foros.rs.client.model.restriction.RestrictionParam param = new com.foros.rs.client.model.restriction.RestrictionParam();
        command.setParams(Collections.singletonList(param));

        param.setName(paramName);
        if (paramValue != null) {
            param.setId(paramValue);
        }

        return command;
    }
}
