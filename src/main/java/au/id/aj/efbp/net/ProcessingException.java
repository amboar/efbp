package au.id.aj.efbp.net;

public class ProcessingException extends Exception {
    private static final long serialVersionUID = 2985124723334087068L;

    public ProcessingException() {
        super();
    }

    public ProcessingException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public ProcessingException(Throwable arg0) {
        super(arg0);
    }

    public ProcessingException(String arg0) {
        super(arg0);
    }
}
