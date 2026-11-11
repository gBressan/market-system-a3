package marketsystem.ui;

/** A entrada de dados terminou (ex.: Ctrl+Z / Ctrl+D ou fim de um arquivo redirecionado). */
public final class InputClosedException extends RuntimeException {

    public InputClosedException() {
        super("A entrada de dados foi encerrada.");
    }
}
