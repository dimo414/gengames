package gengames;

/**
 *  This interface indicates an object is a trait in the set of traits the player could use.  Traits are equivilent to genes. 
 *  Authors of third party players are welcome to use whatever objects they would like, though in general enums are suggested.
 * @author Michael Diamond
 * @author Blake Lavendar
 */
public interface Trait {
    /**
     * Mutates the current trait and returns a new trait randomly from the set of possible traits <em>excluiding</em> the current trait.
     * @return a mutated trait.  A suggested implementation is to use pickOne() and while pickOne() returns the same trait, call pickOne() again.
     * @see gengames.GAFrame#rnd
     */
    public Trait mutate();

    /**
     * Returns a trait randomly from the set of possible traits.  This could include the current trait.
     * @return a trait in the set of traits of this object.
     */
    public Trait pickOne();

    /**
     * The text representation of the current trait.
     * @return trait name.
     */
    public String toString();
}
