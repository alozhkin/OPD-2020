package splash;

public class SplashScriptExecutionException extends RuntimeException {
    private final Response400Info info;

    public SplashScriptExecutionException(Response400Info info) {
        this.info = info;
    }

    public Response400Info getInfo() {
        return info;
    }
}
