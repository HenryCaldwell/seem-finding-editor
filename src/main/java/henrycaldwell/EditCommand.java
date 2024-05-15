package henrycaldwell;

/**
 * Interface for executable and undoable commands, following the command pattern.
 */
public interface EditCommand {
    /**
     * Executes the command, applying specific changes.
     */
    void execute();

    /**
     * Reverses the effects of the execute method.
     */
    void undo();
}
