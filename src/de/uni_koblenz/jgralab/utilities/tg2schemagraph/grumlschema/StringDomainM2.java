/*
 * This code was generated automatically.
 * Do NOT edit this file, changes will be lost.
 * Instead, change and commit the underlying schema.
 */

package de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Vertex;

import de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema.impl.StringDomainM2Impl;

public interface StringDomainM2 extends BasicDomainM2, DomainM2, Vertex {

	/**
	 * refers to the default implementation class of this interface
	 */
	public static final Class<StringDomainM2Impl> IMPLEMENTATION_CLASS = StringDomainM2Impl.class;

	/**
	 * @return the next BasicDomainM2 vertex in the global vertex sequence
	 */
	public BasicDomainM2 getNextBasicDomainM2();

	/**
	 * @return the next DomainM2 vertex in the global vertex sequence
	 */
	public DomainM2 getNextDomainM2();

	/**
	 * @return the next StringDomainM2 vertex in the global vertex sequence
	 */
	public StringDomainM2 getNextStringDomainM2();

	/**
	 * @return the next StringDomainM2 vertex in the global vertex sequence
	 * @param noSubClasses if set to <code>true</code>, no subclasses of StringDomainM2 are accepted
	 */
	public StringDomainM2 getNextStringDomainM2(boolean noSubClasses);

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

}
