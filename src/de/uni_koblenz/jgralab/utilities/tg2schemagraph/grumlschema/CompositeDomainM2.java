/*
 * This code was generated automatically.
 * Do NOT edit this file, changes will be lost.
 * Instead, change and commit the underlying schema.
 */

package de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.EdgeVertexPair;
import de.uni_koblenz.jgralab.Vertex;

public interface CompositeDomainM2 extends DomainM2, Vertex {

	/**
	 * @return the next CompositeDomainM2 vertex in the global vertex sequence
	 */
	public CompositeDomainM2 getNextCompositeDomainM2();

	/**
	 * @return the next DomainM2 vertex in the global vertex sequence
	 */
	public DomainM2 getNextDomainM2();

	/**
	 * @return the first edge of class HasBaseDomainM2 at this vertex
	 */
	public HasBaseDomainM2 getFirstHasBaseDomainM2();

	/**
	 * @return the first edge of class HasBaseDomainM2 at this vertex
	 * @param orientation the orientation of the edge
	 */
	public HasBaseDomainM2 getFirstHasBaseDomainM2(EdgeDirection orientation);

	/**
	 * @return the first edge of class HasBaseDomainM2 at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of HasBaseDomainM2 are accepted
	 */
	public HasBaseDomainM2 getFirstHasBaseDomainM2(boolean noSubClasses);

	/**
	 * @return the first edge of class HasBaseDomainM2 at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of HasBaseDomainM2 are accepted
	 */
	public HasBaseDomainM2 getFirstHasBaseDomainM2(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class HasDomainM2 at this vertex
	 */
	public HasDomainM2 getFirstHasDomainM2();

	/**
	 * @return the first edge of class HasDomainM2 at this vertex
	 * @param orientation the orientation of the edge
	 */
	public HasDomainM2 getFirstHasDomainM2(EdgeDirection orientation);

	/**
	 * @return the first edge of class HasDomainM2 at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of HasDomainM2 are accepted
	 */
	public HasDomainM2 getFirstHasDomainM2(boolean noSubClasses);

	/**
	 * @return the first edge of class HasDomainM2 at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of HasDomainM2 are accepted
	 */
	public HasDomainM2 getFirstHasDomainM2(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class HasRecordDomainComponentM2 at this vertex
	 */
	public HasRecordDomainComponentM2 getFirstHasRecordDomainComponentM2();

	/**
	 * @return the first edge of class HasRecordDomainComponentM2 at this vertex
	 * @param orientation the orientation of the edge
	 */
	public HasRecordDomainComponentM2 getFirstHasRecordDomainComponentM2(EdgeDirection orientation);

	/**
	 * @return the first edge of class HasRecordDomainComponentM2 at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of HasRecordDomainComponentM2 are accepted
	 */
	public HasRecordDomainComponentM2 getFirstHasRecordDomainComponentM2(boolean noSubClasses);

	/**
	 * @return the first edge of class HasRecordDomainComponentM2 at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of HasRecordDomainComponentM2 are accepted
	 */
	public HasRecordDomainComponentM2 getFirstHasRecordDomainComponentM2(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * Returns an iterable for all incidence edges of this vertex that are of type HasBaseDomainM2 or subtypes
	 */
	public Iterable<EdgeVertexPair<? extends HasBaseDomainM2, ? extends Vertex>> getHasBaseDomainM2Incidences();
	
	/**
	 * Returns an iterable for all incidence edges of this vertex that are of type HasBaseDomainM2
	 * @param noSubClasses toggles wether subclasses of HasBaseDomainM2 should be excluded
	 */
	public Iterable<EdgeVertexPair<? extends HasBaseDomainM2, ? extends Vertex>> getHasBaseDomainM2Incidences(boolean noSubClasses);
	/**
	 * Returns an iterable for all incidence edges of this vertex that are of type HasBaseDomainM2
	 * @param direction EdgeDirection.IN or EdgeDirection.OUT, only edges of this direction will be included in the iterable
	 * @param noSubClasses toggles wether subclasses of HasBaseDomainM2 should be excluded
	 */
	public Iterable<EdgeVertexPair<? extends HasBaseDomainM2, ? extends Vertex>> getHasBaseDomainM2Incidences(EdgeDirection direction, boolean noSubClasses);
	
	/**
	 * Returns an iterable for all incidence edges of this vertex that are of type HasBaseDomainM2
	 * @param direction EdgeDirection.IN or EdgeDirection.OUT, only edges of this direction will be included in the iterable
	 */
	public Iterable<EdgeVertexPair<? extends HasBaseDomainM2, ? extends Vertex>> getHasBaseDomainM2Incidences(EdgeDirection direction);

}
