package spider;

/**
 * Interface that gives opportunity to change html processing by peeking behaviors
 */
interface ContextFactory {
    Context createContext();
}
