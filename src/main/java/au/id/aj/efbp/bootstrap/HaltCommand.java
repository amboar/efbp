package au.id.aj.efbp.bootstrap;

import au.id.aj.efbp.bootstrap.Bootstrap.Control;
import au.id.aj.efbp.command.AbstractCommand;
import au.id.aj.efbp.command.CommandId;
import au.id.aj.efbp.node.Node;

public class HaltCommand extends AbstractCommand {
    private static final long serialVersionUID = 1341221514588307327L;

    public HaltCommand(final CommandId id) {
        super(id, Bootstrap.ID);
    }

    @Override
    public void execute(Node node) {
        final Control control = node.getLookup().lookup(Control.class);
        assert null != control;
        control.stop();
    }
}
