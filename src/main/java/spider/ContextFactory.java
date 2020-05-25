package spider;

/**
 * Interface that gives opportunity to change html processing by peeking behaviors
 */
interface ContextFactory {
    /**
     * @return context of html processing
     */
    Context createContext();
}
