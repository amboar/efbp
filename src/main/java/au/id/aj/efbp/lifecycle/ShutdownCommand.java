package au.id.aj.efbp.lifecycle;

import au.id.aj.efbp.command.AbstractCommand;
import au.id.aj.efbp.command.CommandId;
import au.id.aj.efbp.net.Consumer;
import au.id.aj.efbp.node.Node;

public class ShutdownCommand extends AbstractCommand {
    private static final long serialVersionUID = 6746111625648249744L;

    public ShutdownCommand(final CommandId id) {
        super(id, Consumer.class);
    }

    @Override
    public void execute(final Node node) {
        assert node instanceof Consumer;
        final Consumer<?> consumer = (Consumer<?>) node;
        consumer.shutdown();
    }
}
