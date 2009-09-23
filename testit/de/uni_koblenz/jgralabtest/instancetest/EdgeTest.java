package de.uni_koblenz.jgralabtest.instancetest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.trans.CommitFailedException;
import de.uni_koblenz.jgralabtest.schemas.vertextest.AbstractSuperNode;
import de.uni_koblenz.jgralabtest.schemas.vertextest.DoubleSubNode;
import de.uni_koblenz.jgralabtest.schemas.vertextest.Link;
import de.uni_koblenz.jgralabtest.schemas.vertextest.LinkBack;
import de.uni_koblenz.jgralabtest.schemas.vertextest.SubLink;
import de.uni_koblenz.jgralabtest.schemas.vertextest.SubNode;
import de.uni_koblenz.jgralabtest.schemas.vertextest.SuperNode;
import de.uni_koblenz.jgralabtest.schemas.vertextest.VertexTestGraph;
import de.uni_koblenz.jgralabtest.schemas.vertextest.VertexTestSchema;

@RunWith(Parameterized.class)
public class EdgeTest extends InstanceTest {

	private static final int RANDOM_EDGE_COUNT = 30;
	private static final int RANDOM_GRAPH_COUNT = 10;
	private static final int RANDOM_VERTEX_COUNT = 1000;

	public EdgeTest(boolean transactionsEnabled) {
		super(transactionsEnabled);
	}

	@Parameters
	public static Collection<Object[]> configure() {
		return getParameters();
	}

	private VertexTestGraph g;
	private Random rand;

	/**
	 * Creates a new graph for each test.
	 */
	@Before
	public void setUp() {
		g = transactionsEnabled ? VertexTestSchema.instance()
				.createVertexTestGraphWithTransactionSupport()
				: VertexTestSchema.instance().createVertexTestGraph();
		rand = new Random(System.currentTimeMillis());
		createTransaction(g);
	}

	@After
	public void tearDown() throws CommitFailedException {
		commit(g);
	}

	/*
	 * Test of the Interface Edge
	 */

	// tests for the method int getId();
	/**
	 * If you create several edges and you delete one, the next edge should get
	 * the id of the deleted edge. If you create a further edge it should get
	 * the next free id.
	 * 
	 * @throws Exception
	 */
	@Test
	public void getIdTest0() throws Exception {
		DoubleSubNode v0 = g.createDoubleSubNode();
		DoubleSubNode v1 = g.createDoubleSubNode();
		Edge e0 = g.createLink(v0, v1);
		assertEquals(1, e0.getId());
		Edge e1 = g.createLink(v0, v1);
		assertEquals(2, e1.getId());
		commit(g);

		createTransaction(g);
		g.deleteEdge(e0);
		commit(g);

		createTransaction(g);
		e0 = g.createLink(v1, v0);
		assertEquals(1, e0.getId());
		e0 = g.createLink(v1, v0);
		assertEquals(3, e0.getId());
	}

	// tests of the method Edge getNextEdge();
	// (tested in IncidenceListTest)

	// tests of the method Edge getPrevEdge();
	// (tested in IncidenceListTest)

	// tests of the method Edge getNextEdge(EdgeDirection orientation);
	/**
	 * There exists only one edge in the graph
	 */
	@Test
	public void getNextEdgeTestEdgeDirection0() {
		DoubleSubNode v0 = g.createDoubleSubNode();
		DoubleSubNode v1 = g.createDoubleSubNode();
		Edge e0 = g.createLink(v0, v1);
		// edges of vertex v0
		assertNull(e0.getNextEdge(EdgeDirection.INOUT));
		assertNull(e0.getNextEdge(EdgeDirection.OUT));
		assertNull(e0.getNextEdge(EdgeDirection.IN));
		// edges of vertex v1
		assertNull(e0.getReversedEdge().getNextEdge(EdgeDirection.INOUT));
		assertNull(e0.getReversedEdge().getNextEdge(EdgeDirection.OUT));
		assertNull(e0.getReversedEdge().getNextEdge(EdgeDirection.IN));
	}

	/**
	 * Test in a manually built graph.
	 */
	@Test
	public void getNextEdgeTestEdgeDirection1() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		Edge e2 = g.createLink(v1, v2);
		Edge e3 = g.createLink(v2, v1);
		Edge e4 = g.createSubLink(v1, v2);
		Edge e5 = g.createLinkBack(v2, v1);
		// edges of vertex v0
		assertEquals(e2, e1.getNextEdge(EdgeDirection.INOUT));
		assertEquals(e2, e1.getNextEdge(EdgeDirection.OUT));
		assertEquals(e3.getReversedEdge(), e1.getNextEdge(EdgeDirection.IN));
		assertEquals(e3.getReversedEdge(), e2.getNextEdge(EdgeDirection.INOUT));
		assertEquals(e4, e2.getNextEdge(EdgeDirection.OUT));
		assertEquals(e3.getReversedEdge(), e2.getNextEdge(EdgeDirection.IN));
		assertEquals(e4, e3.getReversedEdge().getNextEdge(EdgeDirection.INOUT));
		assertEquals(e4, e3.getReversedEdge().getNextEdge(EdgeDirection.OUT));
		assertEquals(e5.getReversedEdge(), e3.getReversedEdge().getNextEdge(
				EdgeDirection.IN));
		assertEquals(e5.getReversedEdge(), e4.getNextEdge(EdgeDirection.INOUT));
		assertNull(e4.getNextEdge(EdgeDirection.OUT));
		assertEquals(e5.getReversedEdge(), e4.getNextEdge(EdgeDirection.IN));
		assertNull(e5.getNextEdge(EdgeDirection.INOUT));
		assertNull(e5.getNextEdge(EdgeDirection.OUT));
		assertNull(e5.getNextEdge(EdgeDirection.IN));
		// edges of vertex v1
		assertEquals(e2.getReversedEdge(), e1.getReversedEdge().getNextEdge(
				EdgeDirection.INOUT));
		assertEquals(e3, e1.getReversedEdge().getNextEdge(EdgeDirection.OUT));
		assertEquals(e2.getReversedEdge(), e1.getReversedEdge().getNextEdge(
				EdgeDirection.IN));
		assertEquals(e3, e2.getReversedEdge().getNextEdge(EdgeDirection.INOUT));
		assertEquals(e3, e2.getReversedEdge().getNextEdge(EdgeDirection.OUT));
		assertEquals(e4.getReversedEdge(), e2.getReversedEdge().getNextEdge(
				EdgeDirection.IN));
		assertEquals(e4.getReversedEdge(), e3.getNextEdge(EdgeDirection.INOUT));
		assertEquals(e5, e3.getNextEdge(EdgeDirection.OUT));
		assertEquals(e4.getReversedEdge(), e3.getNextEdge(EdgeDirection.IN));
		assertEquals(e5, e4.getReversedEdge().getNextEdge(EdgeDirection.INOUT));
		assertEquals(e5, e4.getReversedEdge().getNextEdge(EdgeDirection.OUT));
		assertNull(e4.getReversedEdge().getNextEdge(EdgeDirection.IN));
		assertNull(e5.getReversedEdge().getNextEdge(EdgeDirection.INOUT));
		assertNull(e5.getReversedEdge().getNextEdge(EdgeDirection.OUT));
		assertNull(e5.getReversedEdge().getNextEdge(EdgeDirection.IN));
	}

	/**
	 * Test in a randomly built graph
	 * 
	 * @throws Exception
	 */
	@Test
	public void getNextEdgeTestEdgeDirection2() throws Exception {
		for (int i = 0; i < RANDOM_GRAPH_COUNT; i++) {
			g = transactionsEnabled ? VertexTestSchema.instance()
					.createVertexTestGraphWithTransactionSupport()
					: VertexTestSchema.instance().createVertexTestGraph();
			createTransaction(g);
			DoubleSubNode v0 = g.createDoubleSubNode();
			DoubleSubNode v1 = g.createDoubleSubNode();
			Edge[] edges = new Edge[RANDOM_EDGE_COUNT];
			Edge[] v0inout = new Edge[RANDOM_EDGE_COUNT];
			Edge[] v0out = new Edge[RANDOM_EDGE_COUNT];
			Edge[] v0in = new Edge[RANDOM_EDGE_COUNT];
			Edge[] v1inout = new Edge[RANDOM_EDGE_COUNT];
			Edge[] v1out = new Edge[RANDOM_EDGE_COUNT];
			Edge[] v1in = new Edge[RANDOM_EDGE_COUNT];
			int lastv0o = 0;
			int lastv0i = 0;
			int lastv1o = 0;
			int lastv1i = 0;
			int dir = 0;
			boolean first = true;
			for (int j = 0; j < RANDOM_EDGE_COUNT; j++) {
				dir = rand.nextInt(2);
				if (dir == 0) {
					Edge e = g.createLink(v0, v1);
					edges[j] = e;
					if (!first) {
						v0inout[j - 1] = e;
						v1inout[j - 1] = e.getReversedEdge();
						while (lastv0o < j) {
							v0out[lastv0o] = e;
							lastv0o++;
						}
						while (lastv1i < j) {
							v1in[lastv1i] = e.getReversedEdge();
							lastv1i++;
						}
					}
				} else {
					Edge e = g.createLink(v1, v0);
					edges[j] = e;
					if (!first) {
						v0inout[j - 1] = e.getReversedEdge();
						v1inout[j - 1] = e;
						while (lastv1o < j) {
							v1out[lastv1o] = e;
							lastv1o++;
						}
						while (lastv0i < j) {
							v0in[lastv0i] = e.getReversedEdge();
							lastv0i++;
						}
					}
				}
				first = false;
			}
			commit(g);
			createReadOnlyTransaction(g);
			for (int k = 0; k < edges.length; k++) {
				Edge e = edges[k];
				if (e.getAlpha() == v0) {
					assertEquals(v0inout[k], e.getNextEdge(EdgeDirection.INOUT));
					assertEquals(v0out[k], e.getNextEdge(EdgeDirection.OUT));
					assertEquals(v0in[k], e.getNextEdge(EdgeDirection.IN));
					assertEquals(v1inout[k], e.getReversedEdge().getNextEdge(
							EdgeDirection.INOUT));
					assertEquals(v1out[k], e.getReversedEdge().getNextEdge(
							EdgeDirection.OUT));
					assertEquals(v1in[k], e.getReversedEdge().getNextEdge(
							EdgeDirection.IN));
				} else {
					assertEquals(v0inout[k], e.getReversedEdge().getNextEdge(
							EdgeDirection.INOUT));
					assertEquals(v0out[k], e.getReversedEdge().getNextEdge(
							EdgeDirection.OUT));
					assertEquals(v0in[k], e.getReversedEdge().getNextEdge(
							EdgeDirection.IN));
					assertEquals(v1inout[k], e.getNextEdge(EdgeDirection.INOUT));
					assertEquals(v1out[k], e.getNextEdge(EdgeDirection.OUT));
					assertEquals(v1in[k], e.getNextEdge(EdgeDirection.IN));
				}
			}
			commit(g);
		}
	}

	// tests for the method Edge getNextEdgeOfClass(EdgeClass anEdgeClass);

	/**
	 * Creates an array of the EdgeClasses.
	 * 
	 * @return {Link, SubLink, LinkBack}
	 */
	private EdgeClass[] getEdgeClasses() {
		EdgeClass[] ecs = new EdgeClass[3];
		List<EdgeClass> a = g.getSchema().getEdgeClassesInTopologicalOrder();
		for (EdgeClass ec : a) {
			if (ec.getSimpleName().equals("Link")) {
				ecs[0] = ec;
			} else if (ec.getSimpleName().equals("SubLink")) {
				ecs[1] = ec;
			}
			if (ec.getSimpleName().equals("LinkBack")) {
				ecs[2] = ec;
			}
		}
		return ecs;
	}

	/**
	 * An edge which has no following edges.
	 */
	@Test
	public void getNextEdgeOfClassTestEdgeClass0() {
		EdgeClass[] ecs = getEdgeClasses();
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		assertNull(e1.getNextEdgeOfClass(ecs[0]));
		assertNull(e1.getNextEdgeOfClass(ecs[1]));
		assertNull(e1.getNextEdgeOfClass(ecs[2]));
	}

	/**
	 * Test in a manually built graph.
	 */
	@Test
	public void getNextEdgeOfClassTestEdgeClass1() {
		EdgeClass[] ecs = getEdgeClasses();
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		Edge e2 = g.createLinkBack(v1, v2);
		Edge e3 = g.createSubLink(v2, v1);
		Edge e4 = g.createLink(v1, v1);
		Edge e5 = g.createLinkBack(v1, v2);
		// test of edge e1
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(ecs[0]));
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(ecs[1]));
		assertEquals(e2, e1.getNextEdgeOfClass(ecs[2]));
		// test of edge e2
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(ecs[0]));
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(ecs[1]));
		assertEquals(e5, e2.getNextEdgeOfClass(ecs[2]));
		// test of edge e3
		assertEquals(e4, e3.getReversedEdge().getNextEdgeOfClass(ecs[0]));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(ecs[1]));
		assertEquals(e5, e3.getReversedEdge().getNextEdgeOfClass(ecs[2]));
		// test of edge e4
		assertEquals(e4.getReversedEdge(), e4.getNextEdgeOfClass(ecs[0]));
		assertNull(e4.getNextEdgeOfClass(ecs[1]));
		assertEquals(e5, e4.getNextEdgeOfClass(ecs[2]));
		// test of edge e4.getReversedEdge
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[0]));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[1]));
		assertEquals(e5, e4.getReversedEdge().getNextEdgeOfClass(ecs[2]));
		// test of edge e5
		assertNull(e5.getNextEdgeOfClass(ecs[0]));
		assertNull(e5.getNextEdgeOfClass(ecs[1]));
		assertNull(e5.getNextEdgeOfClass(ecs[2]));
	}

	/**
	 * Test in a randomly built graph
	 * 
	 * @throws CommitFailedException
	 */
	@Test
	public void getNextEdgeOfClassTestEdgeClass2() throws CommitFailedException {
		EdgeClass[] ecs = getEdgeClasses();
		for (int i = 0; i < RANDOM_GRAPH_COUNT; i++) {
			g = transactionsEnabled ? VertexTestSchema.instance()
					.createVertexTestGraphWithTransactionSupport()
					: VertexTestSchema.instance().createVertexTestGraph();
			createTransaction(g);
			DoubleSubNode v0 = g.createDoubleSubNode();
			DoubleSubNode v1 = g.createDoubleSubNode();
			Edge[] edges = new Edge[RANDOM_EDGE_COUNT];
			Edge[] link = new Edge[RANDOM_EDGE_COUNT];
			Edge[] sublink = new Edge[RANDOM_EDGE_COUNT];
			Edge[] linkback = new Edge[RANDOM_EDGE_COUNT];
			int lastlink = 0;
			int lastsublink = 0;
			int lastlinkback = 0;
			int edgetype = 0;
			boolean first = true;
			for (int j = 0; j < RANDOM_EDGE_COUNT; j++) {
				edgetype = rand.nextInt(2);
				if (edgetype == 0) {
					Edge e = g.createLink(v0, v1);
					edges[j] = e;
					if (!first) {
						while (lastlink < j) {
							link[lastlink] = e;
							lastlink++;
						}
					}
				}
				if (edgetype == 1) {
					Edge e = g.createSubLink(v0, v1);
					edges[j] = e;
					if (!first) {
						while (lastlink < j) {
							link[lastlink] = e;
							lastlink++;
						}
						while (lastsublink < j) {
							sublink[lastsublink] = e;
							lastsublink++;
						}
					}
				} else {
					Edge e = g.createLinkBack(v0, v1);
					edges[j] = e;
					if (!first) {
						while (lastlinkback < j) {
							linkback[lastlinkback] = e;
							lastlinkback++;
						}
					}
				}
				first = false;
			}
			commit(g);
			createReadOnlyTransaction(g);
			for (int k = 0; k < edges.length; k++) {
				Edge e = edges[k];
				assertEquals(link[k], e.getNextEdgeOfClass(ecs[0]));
				assertEquals(sublink[k], e.getNextEdgeOfClass(ecs[1]));
				assertEquals(linkback[k], e.getNextEdgeOfClass(ecs[2]));
			}
			commit(g);
		}
	}

	// tests for the method Edge getNextEdgeOfClass(Class<? extends Edge>
	// anEdgeClass);

	/**
	 * An edge which has no following edges.
	 */
	@Test
	public void getNextEdgeOfClassTestClass0() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		assertNull(e1.getNextEdgeOfClass(Link.class));
		assertNull(e1.getNextEdgeOfClass(SubLink.class));
		assertNull(e1.getNextEdgeOfClass(LinkBack.class));
	}

	/**
	 * Test in a manually built graph.
	 */
	@Test
	public void getNextEdgeOfClassTestClass1() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		Edge e2 = g.createLinkBack(v1, v2);
		Edge e3 = g.createSubLink(v2, v1);
		Edge e4 = g.createLink(v1, v1);
		Edge e5 = g.createLinkBack(v1, v2);
		// test of edge e1
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(Link.class));
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(SubLink.class));
		assertEquals(e2, e1.getNextEdgeOfClass(LinkBack.class));
		// test of edge e2
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(Link.class));
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(SubLink.class));
		assertEquals(e5, e2.getNextEdgeOfClass(LinkBack.class));
		// test of edge e3
		assertEquals(e4, e3.getReversedEdge().getNextEdgeOfClass(Link.class));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(SubLink.class));
		assertEquals(e5, e3.getReversedEdge()
				.getNextEdgeOfClass(LinkBack.class));
		// test of edge e4
		assertEquals(e4.getReversedEdge(), e4.getNextEdgeOfClass(Link.class));
		assertNull(e4.getNextEdgeOfClass(SubLink.class));
		assertEquals(e5, e4.getNextEdgeOfClass(LinkBack.class));
		// test of edge e4.getReversedEdge
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(Link.class));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(SubLink.class));
		assertEquals(e5, e4.getReversedEdge()
				.getNextEdgeOfClass(LinkBack.class));
		// test of edge e5
		assertNull(e5.getNextEdgeOfClass(Link.class));
		assertNull(e5.getNextEdgeOfClass(SubLink.class));
		assertNull(e5.getNextEdgeOfClass(LinkBack.class));
	}

	/**
	 * Test in a randomly built graph
	 * 
	 * @throws CommitFailedException
	 */
	@Test
	public void getNextEdgeOfClassTestClass2() throws CommitFailedException {
		for (int i = 0; i < RANDOM_GRAPH_COUNT; i++) {
			g = transactionsEnabled ? VertexTestSchema.instance()
					.createVertexTestGraphWithTransactionSupport()
					: VertexTestSchema.instance().createVertexTestGraph();
			createTransaction(g);
			DoubleSubNode v0 = g.createDoubleSubNode();
			DoubleSubNode v1 = g.createDoubleSubNode();
			Edge[] edges = new Edge[RANDOM_EDGE_COUNT];
			Edge[] link = new Edge[RANDOM_EDGE_COUNT];
			Edge[] sublink = new Edge[RANDOM_EDGE_COUNT];
			Edge[] linkback = new Edge[RANDOM_EDGE_COUNT];
			int lastlink = 0;
			int lastsublink = 0;
			int lastlinkback = 0;
			int edgetype = 0;
			boolean first = true;
			for (int j = 0; j < RANDOM_EDGE_COUNT; j++) {
				edgetype = rand.nextInt(2);
				if (edgetype == 0) {
					Edge e = g.createLink(v0, v1);
					edges[j] = e;
					if (!first) {
						while (lastlink < j) {
							link[lastlink] = e;
							lastlink++;
						}
					}
				}
				if (edgetype == 1) {
					Edge e = g.createSubLink(v0, v1);
					edges[j] = e;
					if (!first) {
						while (lastlink < j) {
							link[lastlink] = e;
							lastlink++;
						}
						while (lastsublink < j) {
							sublink[lastsublink] = e;
							lastsublink++;
						}
					}
				} else {
					Edge e = g.createLinkBack(v0, v1);
					edges[j] = e;
					if (!first) {
						while (lastlinkback < j) {
							linkback[lastlinkback] = e;
							lastlinkback++;
						}
					}
				}
				first = false;
			}
			commit(g);
			createReadOnlyTransaction(g);
			for (int k = 0; k < edges.length; k++) {
				Edge e = edges[k];
				assertEquals(link[k], e.getNextEdgeOfClass(Link.class));
				assertEquals(sublink[k], e.getNextEdgeOfClass(SubLink.class));
				assertEquals(linkback[k], e.getNextEdgeOfClass(LinkBack.class));
			}
			commit(g);
		}
	}

	// tests for the method Edge getNextEdgeOfClass(EdgeClass anEdgeClass,
	// EdgeDirection orientation);

	/**
	 * An edge which has no following edges.
	 */
	@Test
	public void getNextEdgeOfClassTestEdgeClassEdgeDirection0() {
		EdgeClass[] ecs = getEdgeClasses();
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		assertNull(e1.getNextEdgeOfClass(ecs[0], EdgeDirection.INOUT));
		assertNull(e1.getNextEdgeOfClass(ecs[1], EdgeDirection.INOUT));
		assertNull(e1.getNextEdgeOfClass(ecs[2], EdgeDirection.INOUT));
		assertNull(e1.getNextEdgeOfClass(ecs[0], EdgeDirection.OUT));
		assertNull(e1.getNextEdgeOfClass(ecs[1], EdgeDirection.OUT));
		assertNull(e1.getNextEdgeOfClass(ecs[2], EdgeDirection.OUT));
		assertNull(e1.getNextEdgeOfClass(ecs[0], EdgeDirection.IN));
		assertNull(e1.getNextEdgeOfClass(ecs[1], EdgeDirection.IN));
		assertNull(e1.getNextEdgeOfClass(ecs[2], EdgeDirection.IN));
	}

	/**
	 * Test in a manually built graph.
	 */
	@Test
	public void getNextEdgeOfClassTestEdgeClassEdgeDirection1() {
		EdgeClass[] ecs = getEdgeClasses();
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		Edge e2 = g.createLinkBack(v1, v2);
		Edge e3 = g.createSubLink(v2, v1);
		Edge e4 = g.createLink(v1, v1);
		Edge e5 = g.createLinkBack(v1, v2);
		// test of edge e1
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(ecs[0],
				EdgeDirection.INOUT));
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(ecs[1],
				EdgeDirection.INOUT));
		assertEquals(e2, e1.getNextEdgeOfClass(ecs[2], EdgeDirection.INOUT));
		assertEquals(e4, e1.getNextEdgeOfClass(ecs[0], EdgeDirection.OUT));
		assertNull(e1.getNextEdgeOfClass(ecs[1], EdgeDirection.OUT));
		assertEquals(e2, e1.getNextEdgeOfClass(ecs[2], EdgeDirection.OUT));
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(ecs[0],
				EdgeDirection.IN));
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(ecs[1],
				EdgeDirection.IN));
		assertNull(e1.getNextEdgeOfClass(ecs[2], EdgeDirection.IN));
		// test of edge e2
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(ecs[0],
				EdgeDirection.INOUT));
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(ecs[1],
				EdgeDirection.INOUT));
		assertEquals(e5, e2.getNextEdgeOfClass(ecs[2], EdgeDirection.INOUT));
		assertEquals(e4, e2.getNextEdgeOfClass(ecs[0], EdgeDirection.OUT));
		assertNull(e2.getNextEdgeOfClass(ecs[1], EdgeDirection.OUT));
		assertEquals(e5, e2.getNextEdgeOfClass(ecs[2], EdgeDirection.OUT));
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(ecs[0],
				EdgeDirection.IN));
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(ecs[1],
				EdgeDirection.IN));
		assertNull(e2.getNextEdgeOfClass(ecs[2], EdgeDirection.IN));
		// test of edge e3
		assertEquals(e4, e3.getReversedEdge().getNextEdgeOfClass(ecs[0],
				EdgeDirection.INOUT));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(ecs[1],
				EdgeDirection.INOUT));
		assertEquals(e5, e3.getReversedEdge().getNextEdgeOfClass(ecs[2],
				EdgeDirection.INOUT));
		assertEquals(e4, e3.getReversedEdge().getNextEdgeOfClass(ecs[0],
				EdgeDirection.OUT));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(ecs[1],
				EdgeDirection.OUT));
		assertEquals(e5, e3.getReversedEdge().getNextEdgeOfClass(ecs[2],
				EdgeDirection.OUT));
		assertEquals(e4.getReversedEdge(), e3.getReversedEdge()
				.getNextEdgeOfClass(ecs[0], EdgeDirection.IN));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(ecs[1],
				EdgeDirection.IN));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(ecs[2],
				EdgeDirection.IN));
		// test of edge e4
		assertEquals(e4.getReversedEdge(), e4.getNextEdgeOfClass(ecs[0],
				EdgeDirection.INOUT));
		assertNull(e4.getNextEdgeOfClass(ecs[1], EdgeDirection.INOUT));
		assertEquals(e5, e4.getNextEdgeOfClass(ecs[2], EdgeDirection.INOUT));
		assertNull(e4.getNextEdgeOfClass(ecs[0], EdgeDirection.OUT));
		assertNull(e4.getNextEdgeOfClass(ecs[1], EdgeDirection.OUT));
		assertEquals(e5, e4.getNextEdgeOfClass(ecs[2], EdgeDirection.OUT));
		assertEquals(e4.getReversedEdge(), e4.getNextEdgeOfClass(ecs[0],
				EdgeDirection.IN));
		assertNull(e4.getNextEdgeOfClass(ecs[1], EdgeDirection.IN));
		assertNull(e4.getNextEdgeOfClass(ecs[2], EdgeDirection.IN));
		// test of edge e4.getReversedEdge
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[0],
				EdgeDirection.INOUT));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[1],
				EdgeDirection.INOUT));
		assertEquals(e5, e4.getReversedEdge().getNextEdgeOfClass(ecs[2],
				EdgeDirection.INOUT));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[0],
				EdgeDirection.OUT));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[1],
				EdgeDirection.OUT));
		assertEquals(e5, e4.getReversedEdge().getNextEdgeOfClass(ecs[2],
				EdgeDirection.OUT));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[0],
				EdgeDirection.IN));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[1],
				EdgeDirection.IN));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[2],
				EdgeDirection.IN));
		// test of edge e5
		assertNull(e5.getNextEdgeOfClass(ecs[0], EdgeDirection.INOUT));
		assertNull(e5.getNextEdgeOfClass(ecs[1], EdgeDirection.INOUT));
		assertNull(e5.getNextEdgeOfClass(ecs[2], EdgeDirection.INOUT));
		assertNull(e5.getNextEdgeOfClass(ecs[0], EdgeDirection.OUT));
		assertNull(e5.getNextEdgeOfClass(ecs[1], EdgeDirection.OUT));
		assertNull(e5.getNextEdgeOfClass(ecs[2], EdgeDirection.OUT));
		assertNull(e5.getNextEdgeOfClass(ecs[0], EdgeDirection.IN));
		assertNull(e5.getNextEdgeOfClass(ecs[1], EdgeDirection.IN));
		assertNull(e5.getNextEdgeOfClass(ecs[2], EdgeDirection.IN));
	}

	/**
	 * Test in a randomly built graph
	 * 
	 * @throws CommitFailedException
	 */
	@Test
	public void getNextEdgeOfClassTestEdgeClassEdgeDirection2()
			throws CommitFailedException {
		EdgeClass[] ecs = getEdgeClasses();
		for (int i = 0; i < RANDOM_GRAPH_COUNT; i++) {
			g = transactionsEnabled ? VertexTestSchema.instance()
					.createVertexTestGraphWithTransactionSupport()
					: VertexTestSchema.instance().createVertexTestGraph();
			createTransaction(g);
			DoubleSubNode v0 = g.createDoubleSubNode();
			DoubleSubNode v1 = g.createDoubleSubNode();
			Edge[] edges = new Edge[RANDOM_EDGE_COUNT];
			Edge[] linkinout = new Edge[RANDOM_EDGE_COUNT];
			Edge[] sublinkinout = new Edge[RANDOM_EDGE_COUNT];
			Edge[] linkbackinout = new Edge[RANDOM_EDGE_COUNT];
			int lastlinkinout = 0;
			int lastsublinkinout = 0;
			int lastlinkbackinout = 0;
			Edge[] linkout = new Edge[RANDOM_EDGE_COUNT];
			Edge[] sublinkout = new Edge[RANDOM_EDGE_COUNT];
			Edge[] linkbackout = new Edge[RANDOM_EDGE_COUNT];
			int lastlinkout = 0;
			int lastsublinkout = 0;
			int lastlinkbackout = 0;
			Edge[] linkin = new Edge[RANDOM_EDGE_COUNT];
			Edge[] sublinkin = new Edge[RANDOM_EDGE_COUNT];
			Edge[] linkbackin = new Edge[RANDOM_EDGE_COUNT];
			int lastlinkin = 0;
			int lastsublinkin = 0;
			int lastlinkbackin = 0;
			int edgetype = 0;
			boolean direction = false;
			boolean first = true;
			for (int j = 0; j < RANDOM_EDGE_COUNT; j++) {
				edgetype = rand.nextInt(2);
				direction = rand.nextBoolean();
				if (edgetype == 0) {
					Edge e = direction ? g.createLink(v0, v1) : g.createLink(
							v1, v0);
					if (direction) {
						edges[j] = e;
						if (!first) {
							while (lastlinkinout < j) {
								linkinout[lastlinkinout] = e;
								lastlinkinout++;
							}
							while (lastlinkout < j) {
								linkout[lastlinkout] = e;
								lastlinkout++;
							}
						}
					} else {
						edges[j] = e.getReversedEdge();
						if (!first) {
							while (lastlinkinout < j) {
								linkinout[lastlinkinout] = e.getReversedEdge();
								lastlinkinout++;
							}
							while (lastlinkin < j) {
								linkin[lastlinkin] = e.getReversedEdge();
								lastlinkin++;
							}
						}
					}
				}
				if (edgetype == 1) {
					Edge e = direction ? g.createSubLink(v0, v1) : g
							.createSubLink(v1, v0);
					if (direction) {
						edges[j] = e;
						if (!first) {
							while (lastlinkinout < j) {
								linkinout[lastlinkinout] = e;
								lastlinkinout++;
							}
							while (lastlinkout < j) {
								linkout[lastlinkout] = e;
								lastlinkout++;
							}
							while (lastsublinkinout < j) {
								sublinkinout[lastsublinkinout] = e;
								lastsublinkinout++;
							}
							while (lastsublinkout < j) {
								sublinkout[lastsublinkout] = e;
								lastsublinkout++;
							}
						}
					} else {
						edges[j] = e.getReversedEdge();
						if (!first) {
							while (lastlinkinout < j) {
								linkinout[lastlinkinout] = e.getReversedEdge();
								lastlinkinout++;
							}
							while (lastlinkin < j) {
								linkin[lastlinkin] = e.getReversedEdge();
								lastlinkin++;
							}
							while (lastsublinkinout < j) {
								sublinkinout[lastsublinkinout] = e
										.getReversedEdge();
								lastsublinkinout++;
							}
							while (lastsublinkin < j) {
								sublinkin[lastsublinkin] = e.getReversedEdge();
								lastsublinkin++;
							}
						}
					}
				} else {
					Edge e = direction ? g.createLinkBack(v0, v1) : g
							.createLinkBack(v1, v0);
					if (direction) {
						edges[j] = e;
						if (!first) {
							while (lastlinkbackinout < j) {
								linkbackinout[lastlinkbackinout] = e;
								lastlinkbackinout++;
							}
							while (lastlinkbackout < j) {
								linkbackout[lastlinkbackout] = e;
								lastlinkbackout++;
							}
						}
					} else {
						edges[j] = e.getReversedEdge();
						if (!first) {
							while (lastlinkbackinout < j) {
								linkbackinout[lastlinkbackinout] = e
										.getReversedEdge();
								lastlinkbackinout++;
							}
							while (lastlinkbackin < j) {
								linkbackin[lastlinkbackin] = e
										.getReversedEdge();
								lastlinkbackin++;
							}
						}
					}
				}
				first = false;
			}
			commit(g);
			createReadOnlyTransaction(g);
			for (int k = 0; k < edges.length; k++) {
				Edge e = edges[k];
				assertEquals(linkinout[k], e.getNextEdgeOfClass(ecs[0],
						EdgeDirection.INOUT));
				assertEquals(sublinkinout[k], e.getNextEdgeOfClass(ecs[1],
						EdgeDirection.INOUT));
				assertEquals(linkbackinout[k], e.getNextEdgeOfClass(ecs[2],
						EdgeDirection.INOUT));
				assertEquals(linkout[k], e.getNextEdgeOfClass(ecs[0],
						EdgeDirection.OUT));
				assertEquals(sublinkout[k], e.getNextEdgeOfClass(ecs[1],
						EdgeDirection.OUT));
				assertEquals(linkbackout[k], e.getNextEdgeOfClass(ecs[2],
						EdgeDirection.OUT));
				assertEquals(linkin[k], e.getNextEdgeOfClass(ecs[0],
						EdgeDirection.IN));
				assertEquals(sublinkin[k], e.getNextEdgeOfClass(ecs[1],
						EdgeDirection.IN));
				assertEquals(linkbackin[k], e.getNextEdgeOfClass(ecs[2],
						EdgeDirection.IN));
			}
			commit(g);
		}
	}

	// tests for the method Edge getNextEdgeOfClass(Class<? extends Edge>
	// anEdgeClass, EdgeDirection orientation);

	/**
	 * An edge which has no following edges.
	 */
	@Test
	public void getNextEdgeOfClassTestClassEdgeDirection0() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		assertNull(e1.getNextEdgeOfClass(Link.class, EdgeDirection.INOUT));
		assertNull(e1.getNextEdgeOfClass(SubLink.class, EdgeDirection.INOUT));
		assertNull(e1.getNextEdgeOfClass(LinkBack.class, EdgeDirection.INOUT));
		assertNull(e1.getNextEdgeOfClass(Link.class, EdgeDirection.OUT));
		assertNull(e1.getNextEdgeOfClass(SubLink.class, EdgeDirection.OUT));
		assertNull(e1.getNextEdgeOfClass(LinkBack.class, EdgeDirection.OUT));
		assertNull(e1.getNextEdgeOfClass(Link.class, EdgeDirection.IN));
		assertNull(e1.getNextEdgeOfClass(SubLink.class, EdgeDirection.IN));
		assertNull(e1.getNextEdgeOfClass(LinkBack.class, EdgeDirection.IN));
	}

	/**
	 * Test in a manually built graph.
	 */
	@Test
	public void getNextEdgeOfClassTestClassEdgeDirection1() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		Edge e2 = g.createLinkBack(v1, v2);
		Edge e3 = g.createSubLink(v2, v1);
		Edge e4 = g.createLink(v1, v1);
		Edge e5 = g.createLinkBack(v1, v2);
		// test of edge e1
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(Link.class,
				EdgeDirection.INOUT));
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(SubLink.class,
				EdgeDirection.INOUT));
		assertEquals(e2, e1.getNextEdgeOfClass(LinkBack.class,
				EdgeDirection.INOUT));
		assertEquals(e4, e1.getNextEdgeOfClass(Link.class, EdgeDirection.OUT));
		assertNull(e1.getNextEdgeOfClass(SubLink.class, EdgeDirection.OUT));
		assertEquals(e2, e1.getNextEdgeOfClass(LinkBack.class,
				EdgeDirection.OUT));
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(Link.class,
				EdgeDirection.IN));
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(SubLink.class,
				EdgeDirection.IN));
		assertNull(e1.getNextEdgeOfClass(LinkBack.class, EdgeDirection.IN));
		// test of edge e2
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(Link.class,
				EdgeDirection.INOUT));
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(SubLink.class,
				EdgeDirection.INOUT));
		assertEquals(e5, e2.getNextEdgeOfClass(LinkBack.class,
				EdgeDirection.INOUT));
		assertEquals(e4, e2.getNextEdgeOfClass(Link.class, EdgeDirection.OUT));
		assertNull(e2.getNextEdgeOfClass(SubLink.class, EdgeDirection.OUT));
		assertEquals(e5, e2.getNextEdgeOfClass(LinkBack.class,
				EdgeDirection.OUT));
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(Link.class,
				EdgeDirection.IN));
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(SubLink.class,
				EdgeDirection.IN));
		assertNull(e2.getNextEdgeOfClass(LinkBack.class, EdgeDirection.IN));
		// test of edge e3
		assertEquals(e4, e3.getReversedEdge().getNextEdgeOfClass(Link.class,
				EdgeDirection.INOUT));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(SubLink.class,
				EdgeDirection.INOUT));
		assertEquals(e5, e3.getReversedEdge().getNextEdgeOfClass(
				LinkBack.class, EdgeDirection.INOUT));
		assertEquals(e4, e3.getReversedEdge().getNextEdgeOfClass(Link.class,
				EdgeDirection.OUT));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(SubLink.class,
				EdgeDirection.OUT));
		assertEquals(e5, e3.getReversedEdge().getNextEdgeOfClass(
				LinkBack.class, EdgeDirection.OUT));
		assertEquals(e4.getReversedEdge(), e3.getReversedEdge()
				.getNextEdgeOfClass(Link.class, EdgeDirection.IN));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(SubLink.class,
				EdgeDirection.IN));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(LinkBack.class,
				EdgeDirection.IN));
		// test of edge e4
		assertEquals(e4.getReversedEdge(), e4.getNextEdgeOfClass(Link.class,
				EdgeDirection.INOUT));
		assertNull(e4.getNextEdgeOfClass(SubLink.class, EdgeDirection.INOUT));
		assertEquals(e5, e4.getNextEdgeOfClass(LinkBack.class,
				EdgeDirection.INOUT));
		assertNull(e4.getNextEdgeOfClass(Link.class, EdgeDirection.OUT));
		assertNull(e4.getNextEdgeOfClass(SubLink.class, EdgeDirection.OUT));
		assertEquals(e5, e4.getNextEdgeOfClass(LinkBack.class,
				EdgeDirection.OUT));
		assertEquals(e4.getReversedEdge(), e4.getNextEdgeOfClass(Link.class,
				EdgeDirection.IN));
		assertNull(e4.getNextEdgeOfClass(SubLink.class, EdgeDirection.IN));
		assertNull(e4.getNextEdgeOfClass(LinkBack.class, EdgeDirection.IN));
		// test of edge e4.getReversedEdge
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(Link.class,
				EdgeDirection.INOUT));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(SubLink.class,
				EdgeDirection.INOUT));
		assertEquals(e5, e4.getReversedEdge().getNextEdgeOfClass(
				LinkBack.class, EdgeDirection.INOUT));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(Link.class,
				EdgeDirection.OUT));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(SubLink.class,
				EdgeDirection.OUT));
		assertEquals(e5, e4.getReversedEdge().getNextEdgeOfClass(
				LinkBack.class, EdgeDirection.OUT));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(Link.class,
				EdgeDirection.IN));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(SubLink.class,
				EdgeDirection.IN));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(LinkBack.class,
				EdgeDirection.IN));
		// test of edge e5
		assertNull(e5.getNextEdgeOfClass(Link.class, EdgeDirection.INOUT));
		assertNull(e5.getNextEdgeOfClass(SubLink.class, EdgeDirection.INOUT));
		assertNull(e5.getNextEdgeOfClass(LinkBack.class, EdgeDirection.INOUT));
		assertNull(e5.getNextEdgeOfClass(Link.class, EdgeDirection.OUT));
		assertNull(e5.getNextEdgeOfClass(SubLink.class, EdgeDirection.OUT));
		assertNull(e5.getNextEdgeOfClass(LinkBack.class, EdgeDirection.OUT));
		assertNull(e5.getNextEdgeOfClass(Link.class, EdgeDirection.IN));
		assertNull(e5.getNextEdgeOfClass(SubLink.class, EdgeDirection.IN));
		assertNull(e5.getNextEdgeOfClass(LinkBack.class, EdgeDirection.IN));
	}

	/**
	 * Test in a randomly built graph
	 * 
	 * @throws CommitFailedException
	 */
	@Test
	public void getNextEdgeOfClassTestClassEdgeDirection2()
			throws CommitFailedException {
		for (int i = 0; i < RANDOM_GRAPH_COUNT; i++) {
			g = transactionsEnabled ? VertexTestSchema.instance()
					.createVertexTestGraphWithTransactionSupport()
					: VertexTestSchema.instance().createVertexTestGraph();
			createTransaction(g);
			DoubleSubNode v0 = g.createDoubleSubNode();
			DoubleSubNode v1 = g.createDoubleSubNode();
			Edge[] edges = new Edge[RANDOM_EDGE_COUNT];
			Edge[] linkinout = new Edge[RANDOM_EDGE_COUNT];
			Edge[] sublinkinout = new Edge[RANDOM_EDGE_COUNT];
			Edge[] linkbackinout = new Edge[RANDOM_EDGE_COUNT];
			int lastlinkinout = 0;
			int lastsublinkinout = 0;
			int lastlinkbackinout = 0;
			Edge[] linkout = new Edge[RANDOM_EDGE_COUNT];
			Edge[] sublinkout = new Edge[RANDOM_EDGE_COUNT];
			Edge[] linkbackout = new Edge[RANDOM_EDGE_COUNT];
			int lastlinkout = 0;
			int lastsublinkout = 0;
			int lastlinkbackout = 0;
			Edge[] linkin = new Edge[RANDOM_EDGE_COUNT];
			Edge[] sublinkin = new Edge[RANDOM_EDGE_COUNT];
			Edge[] linkbackin = new Edge[RANDOM_EDGE_COUNT];
			int lastlinkin = 0;
			int lastsublinkin = 0;
			int lastlinkbackin = 0;
			int edgetype = 0;
			boolean direction = false;
			boolean first = true;
			for (int j = 0; j < RANDOM_EDGE_COUNT; j++) {
				edgetype = rand.nextInt(2);
				direction = rand.nextBoolean();
				if (edgetype == 0) {
					Edge e = direction ? g.createLink(v0, v1) : g.createLink(
							v1, v0);
					if (direction) {
						edges[j] = e;
						if (!first) {
							while (lastlinkinout < j) {
								linkinout[lastlinkinout] = e;
								lastlinkinout++;
							}
							while (lastlinkout < j) {
								linkout[lastlinkout] = e;
								lastlinkout++;
							}
						}
					} else {
						edges[j] = e.getReversedEdge();
						if (!first) {
							while (lastlinkinout < j) {
								linkinout[lastlinkinout] = e.getReversedEdge();
								lastlinkinout++;
							}
							while (lastlinkin < j) {
								linkin[lastlinkin] = e.getReversedEdge();
								lastlinkin++;
							}
						}
					}
				}
				if (edgetype == 1) {
					Edge e = direction ? g.createSubLink(v0, v1) : g
							.createSubLink(v1, v0);
					if (direction) {
						edges[j] = e;
						if (!first) {
							while (lastlinkinout < j) {
								linkinout[lastlinkinout] = e;
								lastlinkinout++;
							}
							while (lastlinkout < j) {
								linkout[lastlinkout] = e;
								lastlinkout++;
							}
							while (lastsublinkinout < j) {
								sublinkinout[lastsublinkinout] = e;
								lastsublinkinout++;
							}
							while (lastsublinkout < j) {
								sublinkout[lastsublinkout] = e;
								lastsublinkout++;
							}
						}
					} else {
						edges[j] = e.getReversedEdge();
						if (!first) {
							while (lastlinkinout < j) {
								linkinout[lastlinkinout] = e.getReversedEdge();
								lastlinkinout++;
							}
							while (lastlinkin < j) {
								linkin[lastlinkin] = e.getReversedEdge();
								lastlinkin++;
							}
							while (lastsublinkinout < j) {
								sublinkinout[lastsublinkinout] = e
										.getReversedEdge();
								lastsublinkinout++;
							}
							while (lastsublinkin < j) {
								sublinkin[lastsublinkin] = e.getReversedEdge();
								lastsublinkin++;
							}
						}
					}
				} else {
					Edge e = direction ? g.createLinkBack(v0, v1) : g
							.createLinkBack(v1, v0);
					if (direction) {
						edges[j] = e;
						if (!first) {
							while (lastlinkbackinout < j) {
								linkbackinout[lastlinkbackinout] = e;
								lastlinkbackinout++;
							}
							while (lastlinkbackout < j) {
								linkbackout[lastlinkbackout] = e;
								lastlinkbackout++;
							}
						}
					} else {
						edges[j] = e.getReversedEdge();
						if (!first) {
							while (lastlinkbackinout < j) {
								linkbackinout[lastlinkbackinout] = e
										.getReversedEdge();
								lastlinkbackinout++;
							}
							while (lastlinkbackin < j) {
								linkbackin[lastlinkbackin] = e
										.getReversedEdge();
								lastlinkbackin++;
							}
						}
					}
				}
				first = false;
			}
			commit(g);
			createReadOnlyTransaction(g);
			for (int k = 0; k < edges.length; k++) {
				Edge e = edges[k];
				assertEquals(linkinout[k], e.getNextEdgeOfClass(Link.class,
						EdgeDirection.INOUT));
				assertEquals(sublinkinout[k], e.getNextEdgeOfClass(
						SubLink.class, EdgeDirection.INOUT));
				assertEquals(linkbackinout[k], e.getNextEdgeOfClass(
						LinkBack.class, EdgeDirection.INOUT));
				assertEquals(linkout[k], e.getNextEdgeOfClass(Link.class,
						EdgeDirection.OUT));
				assertEquals(sublinkout[k], e.getNextEdgeOfClass(SubLink.class,
						EdgeDirection.OUT));
				assertEquals(linkbackout[k], e.getNextEdgeOfClass(
						LinkBack.class, EdgeDirection.OUT));
				assertEquals(linkin[k], e.getNextEdgeOfClass(Link.class,
						EdgeDirection.IN));
				assertEquals(sublinkin[k], e.getNextEdgeOfClass(SubLink.class,
						EdgeDirection.IN));
				assertEquals(linkbackin[k], e.getNextEdgeOfClass(
						LinkBack.class, EdgeDirection.IN));
			}
		}
		commit(g);
	}

	// tests for the method Edge getNextEdgeOfClass(EdgeClass anEdgeClass,
	// boolean noSubclasses);

	/**
	 * An edge which has no following edges.
	 */
	@Test
	public void getNextEdgeOfClassTestEdgeClassBoolean0() {
		EdgeClass[] ecs = getEdgeClasses();
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		assertNull(e1.getNextEdgeOfClass(ecs[0], false));
		assertNull(e1.getNextEdgeOfClass(ecs[1], false));
		assertNull(e1.getNextEdgeOfClass(ecs[2], false));
		assertNull(e1.getNextEdgeOfClass(ecs[0], true));
		assertNull(e1.getNextEdgeOfClass(ecs[1], true));
		assertNull(e1.getNextEdgeOfClass(ecs[2], true));
	}

	/**
	 * Test in a manually built graph.
	 */
	@Test
	public void getNextEdgeOfClassTestEdgeClassBoolean1() {
		EdgeClass[] ecs = getEdgeClasses();
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		Edge e2 = g.createLinkBack(v1, v2);
		Edge e3 = g.createSubLink(v2, v1);
		Edge e4 = g.createLink(v1, v1);
		Edge e5 = g.createLinkBack(v1, v2);
		// test of edge e1
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(ecs[0], false));
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(ecs[1], false));
		assertEquals(e2, e1.getNextEdgeOfClass(ecs[2], false));
		assertEquals(e4, e1.getNextEdgeOfClass(ecs[0], true));
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(ecs[1], true));
		assertEquals(e2, e1.getNextEdgeOfClass(ecs[2], true));
		// test of edge e2
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(ecs[0], false));
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(ecs[1], false));
		assertEquals(e5, e2.getNextEdgeOfClass(ecs[2], false));
		assertEquals(e4, e2.getNextEdgeOfClass(ecs[0], true));
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(ecs[1], true));
		assertEquals(e5, e2.getNextEdgeOfClass(ecs[2], true));
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(ecs[0],
				EdgeDirection.IN));
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(ecs[1],
				EdgeDirection.IN));
		// test of edge e3
		assertEquals(e4, e3.getReversedEdge().getNextEdgeOfClass(ecs[0], false));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(ecs[1], false));
		assertEquals(e5, e3.getReversedEdge().getNextEdgeOfClass(ecs[2], false));
		assertEquals(e4, e3.getReversedEdge().getNextEdgeOfClass(ecs[0], true));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(ecs[1], true));
		assertEquals(e5, e3.getReversedEdge().getNextEdgeOfClass(ecs[2], true));
		// test of edge e4
		assertEquals(e4.getReversedEdge(), e4.getNextEdgeOfClass(ecs[0], false));
		assertNull(e4.getNextEdgeOfClass(ecs[1], false));
		assertEquals(e5, e4.getNextEdgeOfClass(ecs[2], false));
		assertEquals(e4.getReversedEdge(), e4.getNextEdgeOfClass(ecs[0], true));
		assertNull(e4.getNextEdgeOfClass(ecs[1], true));
		assertEquals(e5, e4.getNextEdgeOfClass(ecs[2], true));
		// test of edge e4.getReversedEdge
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[0], false));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[1], false));
		assertEquals(e5, e4.getReversedEdge().getNextEdgeOfClass(ecs[2], false));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[0], true));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[1], true));
		assertEquals(e5, e4.getReversedEdge().getNextEdgeOfClass(ecs[2], true));
		// test of edge e5
		assertNull(e5.getNextEdgeOfClass(ecs[0], false));
		assertNull(e5.getNextEdgeOfClass(ecs[1], false));
		assertNull(e5.getNextEdgeOfClass(ecs[2], false));
		assertNull(e5.getNextEdgeOfClass(ecs[0], true));
		assertNull(e5.getNextEdgeOfClass(ecs[1], true));
		assertNull(e5.getNextEdgeOfClass(ecs[2], true));
	}

	/**
	 * Test in a randomly built graph
	 * 
	 * @throws CommitFailedException
	 */
	@Test
	public void getNextEdgeOfClassTestEdgeClassBoolean2()
			throws CommitFailedException {
		EdgeClass[] ecs = getEdgeClasses();
		for (int i = 0; i < RANDOM_GRAPH_COUNT; i++) {
			g = transactionsEnabled ? VertexTestSchema.instance()
					.createVertexTestGraphWithTransactionSupport()
					: VertexTestSchema.instance().createVertexTestGraph();
			createTransaction(g);
			DoubleSubNode v0 = g.createDoubleSubNode();
			DoubleSubNode v1 = g.createDoubleSubNode();
			Edge[] edges = new Edge[RANDOM_EDGE_COUNT];
			Edge[] linkfalse = new Edge[RANDOM_EDGE_COUNT];
			Edge[] sublinkfalse = new Edge[RANDOM_EDGE_COUNT];
			Edge[] linkbackfalse = new Edge[RANDOM_EDGE_COUNT];
			int lastlinkfalse = 0;
			int lastsublinkfalse = 0;
			int lastlinkbackfalse = 0;
			Edge[] linktrue = new Edge[RANDOM_EDGE_COUNT];
			Edge[] sublinktrue = new Edge[RANDOM_EDGE_COUNT];
			Edge[] linkbacktrue = new Edge[RANDOM_EDGE_COUNT];
			int lastlinktrue = 0;
			int lastsublinktrue = 0;
			int lastlinkbacktrue = 0;
			int edgetype = 0;
			boolean direction = false;
			boolean first = true;
			for (int j = 0; j < RANDOM_EDGE_COUNT; j++) {
				edgetype = rand.nextInt(2);
				direction = rand.nextBoolean();
				if (edgetype == 0) {
					Edge e = direction ? g.createLink(v0, v1) : g.createLink(
							v1, v0).getReversedEdge();
					edges[j] = e;
					if (!first) {
						while (lastlinkfalse < j) {
							linkfalse[lastlinkfalse] = e;
							lastlinkfalse++;
						}
						while (lastlinktrue < j) {
							linktrue[lastlinktrue] = e;
							lastlinktrue++;
						}
					}
				}
				if (edgetype == 1) {
					Edge e = direction ? g.createSubLink(v0, v1) : g
							.createSubLink(v1, v0).getReversedEdge();
					edges[j] = e;
					if (!first) {
						while (lastlinkfalse < j) {
							linkfalse[lastlinkfalse] = e;
							lastlinkfalse++;
						}
						while (lastsublinkfalse < j) {
							sublinkfalse[lastsublinkfalse] = e;
							lastsublinkfalse++;
						}
						while (lastsublinktrue < j) {
							sublinktrue[lastsublinktrue] = e;
							lastsublinktrue++;
						}
					}
				} else {
					Edge e = direction ? g.createLinkBack(v0, v1) : g
							.createLinkBack(v1, v0).getReversedEdge();
					edges[j] = e;
					if (!first) {
						while (lastlinkbackfalse < j) {
							linkbackfalse[lastlinkbackfalse] = e;
							lastlinkbackfalse++;
						}
						while (lastlinkbacktrue < j) {
							linkbacktrue[lastlinkbacktrue] = e;
							lastlinkbacktrue++;
						}
					}
				}
				first = false;
			}
			commit(g);
			createReadOnlyTransaction(g);
			for (int k = 0; k < edges.length; k++) {
				Edge e = edges[k];
				assertEquals(linkfalse[k], e.getNextEdgeOfClass(ecs[0], false));
				assertEquals(sublinkfalse[k], e.getNextEdgeOfClass(ecs[1],
						false));
				assertEquals(linkbackfalse[k], e.getNextEdgeOfClass(ecs[2],
						false));
				assertEquals(linktrue[k], e.getNextEdgeOfClass(ecs[0], true));
				assertEquals(sublinktrue[k], e.getNextEdgeOfClass(ecs[1], true));
				assertEquals(linkbacktrue[k], e
						.getNextEdgeOfClass(ecs[2], true));
			}
			commit(g);
		}
	}

	// tests for the method Edge getNextEdgeOfClass(Class<? extends Edge>
	// anEdgeClass, boolean noSubclasses);

	/**
	 * An edge which has no following edges.
	 */
	@Test
	public void getNextEdgeOfClassTestClassBoolean0() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		assertNull(e1.getNextEdgeOfClass(Link.class, false));
		assertNull(e1.getNextEdgeOfClass(SubLink.class, false));
		assertNull(e1.getNextEdgeOfClass(LinkBack.class, false));
		assertNull(e1.getNextEdgeOfClass(Link.class, true));
		assertNull(e1.getNextEdgeOfClass(SubLink.class, true));
		assertNull(e1.getNextEdgeOfClass(LinkBack.class, true));
	}

	/**
	 * Test in a manually built graph.
	 */
	@Test
	public void getNextEdgeOfClassTestClassBoolean1() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		Edge e2 = g.createLinkBack(v1, v2);
		Edge e3 = g.createSubLink(v2, v1);
		Edge e4 = g.createLink(v1, v1);
		Edge e5 = g.createLinkBack(v1, v2);
		// test of edge e1
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(Link.class,
				false));
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(SubLink.class,
				false));
		assertEquals(e2, e1.getNextEdgeOfClass(LinkBack.class, false));
		assertEquals(e4, e1.getNextEdgeOfClass(Link.class, true));
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(SubLink.class,
				true));
		assertEquals(e2, e1.getNextEdgeOfClass(LinkBack.class, true));
		// test of edge e2
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(Link.class,
				false));
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(SubLink.class,
				false));
		assertEquals(e5, e2.getNextEdgeOfClass(LinkBack.class, false));
		assertEquals(e4, e2.getNextEdgeOfClass(Link.class, true));
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(SubLink.class,
				true));
		assertEquals(e5, e2.getNextEdgeOfClass(LinkBack.class, true));
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(Link.class,
				EdgeDirection.IN));
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(SubLink.class,
				EdgeDirection.IN));
		// test of edge e3
		assertEquals(e4, e3.getReversedEdge().getNextEdgeOfClass(Link.class,
				false));
		assertNull(e3.getReversedEdge()
				.getNextEdgeOfClass(SubLink.class, false));
		assertEquals(e5, e3.getReversedEdge().getNextEdgeOfClass(
				LinkBack.class, false));
		assertEquals(e4, e3.getReversedEdge().getNextEdgeOfClass(Link.class,
				true));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(SubLink.class, true));
		assertEquals(e5, e3.getReversedEdge().getNextEdgeOfClass(
				LinkBack.class, true));
		// test of edge e4
		assertEquals(e4.getReversedEdge(), e4.getNextEdgeOfClass(Link.class,
				false));
		assertNull(e4.getNextEdgeOfClass(SubLink.class, false));
		assertEquals(e5, e4.getNextEdgeOfClass(LinkBack.class, false));
		assertEquals(e4.getReversedEdge(), e4.getNextEdgeOfClass(Link.class,
				true));
		assertNull(e4.getNextEdgeOfClass(SubLink.class, true));
		assertEquals(e5, e4.getNextEdgeOfClass(LinkBack.class, true));
		// test of edge e4.getReversedEdge
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(Link.class, false));
		assertNull(e4.getReversedEdge()
				.getNextEdgeOfClass(SubLink.class, false));
		assertEquals(e5, e4.getReversedEdge().getNextEdgeOfClass(
				LinkBack.class, false));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(Link.class, true));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(SubLink.class, true));
		assertEquals(e5, e4.getReversedEdge().getNextEdgeOfClass(
				LinkBack.class, true));
		// test of edge e5
		assertNull(e5.getNextEdgeOfClass(Link.class, false));
		assertNull(e5.getNextEdgeOfClass(SubLink.class, false));
		assertNull(e5.getNextEdgeOfClass(LinkBack.class, false));
		assertNull(e5.getNextEdgeOfClass(Link.class, true));
		assertNull(e5.getNextEdgeOfClass(SubLink.class, true));
		assertNull(e5.getNextEdgeOfClass(LinkBack.class, true));
	}

	/**
	 * Test in a randomly built graph
	 * 
	 * @throws CommitFailedException
	 */
	@Test
	public void getNextEdgeOfClassTestClassBoolean2()
			throws CommitFailedException {
		for (int i = 0; i < RANDOM_GRAPH_COUNT; i++) {
			g = transactionsEnabled ? VertexTestSchema.instance()
					.createVertexTestGraphWithTransactionSupport()
					: VertexTestSchema.instance().createVertexTestGraph();
			createTransaction(g);
			DoubleSubNode v0 = g.createDoubleSubNode();
			DoubleSubNode v1 = g.createDoubleSubNode();
			Edge[] edges = new Edge[RANDOM_EDGE_COUNT];
			Edge[] linkfalse = new Edge[RANDOM_EDGE_COUNT];
			Edge[] sublinkfalse = new Edge[RANDOM_EDGE_COUNT];
			Edge[] linkbackfalse = new Edge[RANDOM_EDGE_COUNT];
			int lastlinkfalse = 0;
			int lastsublinkfalse = 0;
			int lastlinkbackfalse = 0;
			Edge[] linktrue = new Edge[RANDOM_EDGE_COUNT];
			Edge[] sublinktrue = new Edge[RANDOM_EDGE_COUNT];
			Edge[] linkbacktrue = new Edge[RANDOM_EDGE_COUNT];
			int lastlinktrue = 0;
			int lastsublinktrue = 0;
			int lastlinkbacktrue = 0;
			int edgetype = 0;
			boolean direction = false;
			boolean first = true;
			for (int j = 0; j < RANDOM_EDGE_COUNT; j++) {
				edgetype = rand.nextInt(2);
				direction = rand.nextBoolean();
				if (edgetype == 0) {
					Edge e = direction ? g.createLink(v0, v1) : g.createLink(
							v1, v0).getReversedEdge();
					edges[j] = e;
					if (!first) {
						while (lastlinkfalse < j) {
							linkfalse[lastlinkfalse] = e;
							lastlinkfalse++;
						}
						while (lastlinktrue < j) {
							linktrue[lastlinktrue] = e;
							lastlinktrue++;
						}
					}
				}
				if (edgetype == 1) {
					Edge e = direction ? g.createSubLink(v0, v1) : g
							.createSubLink(v1, v0).getReversedEdge();
					edges[j] = e;
					if (!first) {
						while (lastlinkfalse < j) {
							linkfalse[lastlinkfalse] = e;
							lastlinkfalse++;
						}
						while (lastsublinkfalse < j) {
							sublinkfalse[lastsublinkfalse] = e;
							lastsublinkfalse++;
						}
						while (lastsublinktrue < j) {
							sublinktrue[lastsublinktrue] = e;
							lastsublinktrue++;
						}
					}
				} else {
					Edge e = direction ? g.createLinkBack(v0, v1) : g
							.createLinkBack(v1, v0).getReversedEdge();
					edges[j] = e;
					if (!first) {
						while (lastlinkbackfalse < j) {
							linkbackfalse[lastlinkbackfalse] = e;
							lastlinkbackfalse++;
						}
						while (lastlinkbacktrue < j) {
							linkbacktrue[lastlinkbacktrue] = e;
							lastlinkbacktrue++;
						}
					}
				}
				first = false;
			}
			commit(g);
			createReadOnlyTransaction(g);
			for (int k = 0; k < edges.length; k++) {
				Edge e = edges[k];
				assertEquals(linkfalse[k], e.getNextEdgeOfClass(Link.class,
						false));
				assertEquals(sublinkfalse[k], e.getNextEdgeOfClass(
						SubLink.class, false));
				assertEquals(linkbackfalse[k], e.getNextEdgeOfClass(
						LinkBack.class, false));
				assertEquals(linktrue[k], e
						.getNextEdgeOfClass(Link.class, true));
				assertEquals(sublinktrue[k], e.getNextEdgeOfClass(
						SubLink.class, true));
				assertEquals(linkbacktrue[k], e.getNextEdgeOfClass(
						LinkBack.class, true));
			}
			commit(g);
		}
	}

	// tests for the method Edge getNextEdgeOfClass(EdgeClass anEdgeClass,
	// EdgeDirection orientation, boolean noSubclasses);

	/**
	 * An edge which has no following edges.
	 */
	@Test
	public void getNextEdgeOfClassTestEdgeClassEdgeDirectionBoolean0() {
		EdgeClass[] ecs = getEdgeClasses();
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		assertNull(e1.getNextEdgeOfClass(ecs[0], EdgeDirection.INOUT, false));
		assertNull(e1.getNextEdgeOfClass(ecs[1], EdgeDirection.INOUT, false));
		assertNull(e1.getNextEdgeOfClass(ecs[2], EdgeDirection.INOUT, false));
		assertNull(e1.getNextEdgeOfClass(ecs[0], EdgeDirection.OUT, false));
		assertNull(e1.getNextEdgeOfClass(ecs[1], EdgeDirection.OUT, false));
		assertNull(e1.getNextEdgeOfClass(ecs[2], EdgeDirection.OUT, false));
		assertNull(e1.getNextEdgeOfClass(ecs[0], EdgeDirection.IN, false));
		assertNull(e1.getNextEdgeOfClass(ecs[1], EdgeDirection.IN, false));
		assertNull(e1.getNextEdgeOfClass(ecs[2], EdgeDirection.IN, false));
		assertNull(e1.getNextEdgeOfClass(ecs[0], EdgeDirection.INOUT, true));
		assertNull(e1.getNextEdgeOfClass(ecs[1], EdgeDirection.INOUT, true));
		assertNull(e1.getNextEdgeOfClass(ecs[2], EdgeDirection.INOUT, true));
		assertNull(e1.getNextEdgeOfClass(ecs[0], EdgeDirection.OUT, true));
		assertNull(e1.getNextEdgeOfClass(ecs[1], EdgeDirection.OUT, true));
		assertNull(e1.getNextEdgeOfClass(ecs[2], EdgeDirection.OUT, true));
		assertNull(e1.getNextEdgeOfClass(ecs[0], EdgeDirection.IN, true));
		assertNull(e1.getNextEdgeOfClass(ecs[1], EdgeDirection.IN, true));
		assertNull(e1.getNextEdgeOfClass(ecs[2], EdgeDirection.IN, true));
	}

	/**
	 * Test in a manually built graph.
	 */
	@Test
	public void getNextEdgeOfClassTestEdgeClassEdgeDirectionBoolean1() {
		EdgeClass[] ecs = getEdgeClasses();
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		Edge e2 = g.createLinkBack(v1, v2);
		Edge e3 = g.createSubLink(v2, v1);
		Edge e4 = g.createLink(v1, v1);
		Edge e5 = g.createLinkBack(v1, v2);
		// test of edge e1
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(ecs[0],
				EdgeDirection.INOUT, false));
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(ecs[1],
				EdgeDirection.INOUT, false));
		assertEquals(e2, e1.getNextEdgeOfClass(ecs[2], EdgeDirection.INOUT,
				false));
		assertEquals(e4, e1
				.getNextEdgeOfClass(ecs[0], EdgeDirection.OUT, false));
		assertNull(e1.getNextEdgeOfClass(ecs[1], EdgeDirection.OUT, false));
		assertEquals(e2, e1
				.getNextEdgeOfClass(ecs[2], EdgeDirection.OUT, false));
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(ecs[0],
				EdgeDirection.IN, false));
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(ecs[1],
				EdgeDirection.IN, false));
		assertNull(e1.getNextEdgeOfClass(ecs[2], EdgeDirection.IN, false));

		assertEquals(e4, e1.getNextEdgeOfClass(ecs[0], EdgeDirection.INOUT,
				true));
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(ecs[1],
				EdgeDirection.INOUT, true));
		assertEquals(e2, e1.getNextEdgeOfClass(ecs[2], EdgeDirection.INOUT,
				true));
		assertEquals(e4, e1.getNextEdgeOfClass(ecs[0], EdgeDirection.OUT, true));
		assertNull(e1.getNextEdgeOfClass(ecs[1], EdgeDirection.OUT, true));
		assertEquals(e2, e1.getNextEdgeOfClass(ecs[2], EdgeDirection.OUT, true));
		assertEquals(e4.getReversedEdge(), e1.getNextEdgeOfClass(ecs[0],
				EdgeDirection.IN, true));
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(ecs[1],
				EdgeDirection.IN, true));
		assertNull(e1.getNextEdgeOfClass(ecs[2], EdgeDirection.IN, true));

		// test of edge e2
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(ecs[0],
				EdgeDirection.INOUT, false));
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(ecs[1],
				EdgeDirection.INOUT, false));
		assertEquals(e5, e2.getNextEdgeOfClass(ecs[2], EdgeDirection.INOUT,
				false));
		assertEquals(e4, e2
				.getNextEdgeOfClass(ecs[0], EdgeDirection.OUT, false));
		assertNull(e2.getNextEdgeOfClass(ecs[1], EdgeDirection.OUT, false));
		assertEquals(e5, e2
				.getNextEdgeOfClass(ecs[2], EdgeDirection.OUT, false));
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(ecs[0],
				EdgeDirection.IN, false));
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(ecs[1],
				EdgeDirection.IN, false));
		assertNull(e2.getNextEdgeOfClass(ecs[2], EdgeDirection.IN, false));

		assertEquals(e4, e2.getNextEdgeOfClass(ecs[0], EdgeDirection.INOUT,
				true));
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(ecs[1],
				EdgeDirection.INOUT, true));
		assertEquals(e5, e2.getNextEdgeOfClass(ecs[2], EdgeDirection.INOUT,
				true));
		assertEquals(e4, e2.getNextEdgeOfClass(ecs[0], EdgeDirection.OUT, true));
		assertNull(e2.getNextEdgeOfClass(ecs[1], EdgeDirection.OUT, true));
		assertEquals(e5, e2.getNextEdgeOfClass(ecs[2], EdgeDirection.OUT, true));
		assertEquals(e4.getReversedEdge(), e2.getNextEdgeOfClass(ecs[0],
				EdgeDirection.IN, true));
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(ecs[1],
				EdgeDirection.IN, true));
		assertNull(e2.getNextEdgeOfClass(ecs[2], EdgeDirection.IN, true));

		// test of edge e3
		assertEquals(e4, e3.getReversedEdge().getNextEdgeOfClass(ecs[0],
				EdgeDirection.INOUT, false));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(ecs[1],
				EdgeDirection.INOUT, false));
		assertEquals(e5, e3.getReversedEdge().getNextEdgeOfClass(ecs[2],
				EdgeDirection.INOUT, false));
		assertEquals(e4, e3.getReversedEdge().getNextEdgeOfClass(ecs[0],
				EdgeDirection.OUT, false));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(ecs[1],
				EdgeDirection.OUT, false));
		assertEquals(e5, e3.getReversedEdge().getNextEdgeOfClass(ecs[2],
				EdgeDirection.OUT, false));
		assertEquals(e4.getReversedEdge(), e3.getReversedEdge()
				.getNextEdgeOfClass(ecs[0], EdgeDirection.IN, false));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(ecs[1],
				EdgeDirection.IN, false));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(ecs[2],
				EdgeDirection.IN, false));

		assertEquals(e4, e3.getReversedEdge().getNextEdgeOfClass(ecs[0],
				EdgeDirection.INOUT, true));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(ecs[1],
				EdgeDirection.INOUT, true));
		assertEquals(e5, e3.getReversedEdge().getNextEdgeOfClass(ecs[2],
				EdgeDirection.INOUT, true));
		assertEquals(e4, e3.getReversedEdge().getNextEdgeOfClass(ecs[0],
				EdgeDirection.OUT, true));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(ecs[1],
				EdgeDirection.OUT, true));
		assertEquals(e5, e3.getReversedEdge().getNextEdgeOfClass(ecs[2],
				EdgeDirection.OUT, true));
		assertEquals(e4.getReversedEdge(), e3.getReversedEdge()
				.getNextEdgeOfClass(ecs[0], EdgeDirection.IN, true));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(ecs[1],
				EdgeDirection.IN, true));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(ecs[2],
				EdgeDirection.IN, true));

		// test of edge e4
		assertEquals(e4.getReversedEdge(), e4.getNextEdgeOfClass(ecs[0],
				EdgeDirection.INOUT, false));
		assertNull(e4.getNextEdgeOfClass(ecs[1], EdgeDirection.INOUT, false));
		assertEquals(e5, e4.getNextEdgeOfClass(ecs[2], EdgeDirection.INOUT,
				false));
		assertNull(e4.getNextEdgeOfClass(ecs[0], EdgeDirection.OUT, false));
		assertNull(e4.getNextEdgeOfClass(ecs[1], EdgeDirection.OUT, false));
		assertEquals(e5, e4
				.getNextEdgeOfClass(ecs[2], EdgeDirection.OUT, false));
		assertEquals(e4.getReversedEdge(), e4.getNextEdgeOfClass(ecs[0],
				EdgeDirection.IN, false));
		assertNull(e4.getNextEdgeOfClass(ecs[1], EdgeDirection.IN, false));
		assertNull(e4.getNextEdgeOfClass(ecs[2], EdgeDirection.IN, false));

		assertEquals(e4.getReversedEdge(), e4.getNextEdgeOfClass(ecs[0],
				EdgeDirection.INOUT, true));
		assertNull(e4.getNextEdgeOfClass(ecs[1], EdgeDirection.INOUT, true));
		assertEquals(e5, e4.getNextEdgeOfClass(ecs[2], EdgeDirection.INOUT,
				true));
		assertNull(e4.getNextEdgeOfClass(ecs[0], EdgeDirection.OUT, true));
		assertNull(e4.getNextEdgeOfClass(ecs[1], EdgeDirection.OUT, true));
		assertEquals(e5, e4.getNextEdgeOfClass(ecs[2], EdgeDirection.OUT, true));
		assertEquals(e4.getReversedEdge(), e4.getNextEdgeOfClass(ecs[0],
				EdgeDirection.IN, true));
		assertNull(e4.getNextEdgeOfClass(ecs[1], EdgeDirection.IN, true));
		assertNull(e4.getNextEdgeOfClass(ecs[2], EdgeDirection.IN, true));

		// test of edge e4.getReversedEdge
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[0],
				EdgeDirection.INOUT, false));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[1],
				EdgeDirection.INOUT, false));
		assertEquals(e5, e4.getReversedEdge().getNextEdgeOfClass(ecs[2],
				EdgeDirection.INOUT, false));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[0],
				EdgeDirection.OUT, false));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[1],
				EdgeDirection.OUT, false));
		assertEquals(e5, e4.getReversedEdge().getNextEdgeOfClass(ecs[2],
				EdgeDirection.OUT, false));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[0],
				EdgeDirection.IN, false));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[1],
				EdgeDirection.IN, false));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[2],
				EdgeDirection.IN, false));

		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[0],
				EdgeDirection.INOUT, true));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[1],
				EdgeDirection.INOUT, true));
		assertEquals(e5, e4.getReversedEdge().getNextEdgeOfClass(ecs[2],
				EdgeDirection.INOUT, true));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[0],
				EdgeDirection.OUT, true));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[1],
				EdgeDirection.OUT, true));
		assertEquals(e5, e4.getReversedEdge().getNextEdgeOfClass(ecs[2],
				EdgeDirection.OUT, true));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[0],
				EdgeDirection.IN, true));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[1],
				EdgeDirection.IN, true));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(ecs[2],
				EdgeDirection.IN, true));

		// test of edge e5
		assertNull(e5.getNextEdgeOfClass(ecs[0], EdgeDirection.INOUT, false));
		assertNull(e5.getNextEdgeOfClass(ecs[1], EdgeDirection.INOUT, false));
		assertNull(e5.getNextEdgeOfClass(ecs[2], EdgeDirection.INOUT, false));
		assertNull(e5.getNextEdgeOfClass(ecs[0], EdgeDirection.OUT, false));
		assertNull(e5.getNextEdgeOfClass(ecs[1], EdgeDirection.OUT, false));
		assertNull(e5.getNextEdgeOfClass(ecs[2], EdgeDirection.OUT, false));
		assertNull(e5.getNextEdgeOfClass(ecs[0], EdgeDirection.IN, false));
		assertNull(e5.getNextEdgeOfClass(ecs[1], EdgeDirection.IN, false));
		assertNull(e5.getNextEdgeOfClass(ecs[2], EdgeDirection.IN, false));

		assertNull(e5.getNextEdgeOfClass(ecs[0], EdgeDirection.INOUT, true));
		assertNull(e5.getNextEdgeOfClass(ecs[1], EdgeDirection.INOUT, true));
		assertNull(e5.getNextEdgeOfClass(ecs[2], EdgeDirection.INOUT, true));
		assertNull(e5.getNextEdgeOfClass(ecs[0], EdgeDirection.OUT, true));
		assertNull(e5.getNextEdgeOfClass(ecs[1], EdgeDirection.OUT, true));
		assertNull(e5.getNextEdgeOfClass(ecs[2], EdgeDirection.OUT, true));
		assertNull(e5.getNextEdgeOfClass(ecs[0], EdgeDirection.IN, true));
		assertNull(e5.getNextEdgeOfClass(ecs[1], EdgeDirection.IN, true));
		assertNull(e5.getNextEdgeOfClass(ecs[2], EdgeDirection.IN, true));
	}

	/**
	 * Test in a randomly built graph
	 * 
	 * @throws CommitFailedException
	 */
	@Test
	public void getNextEdgeOfClassTestEdgeClassEdgeDirectionBoolean2()
			throws CommitFailedException {
		EdgeClass[] ecs = getEdgeClasses();
		for (int i = 0; i < RANDOM_GRAPH_COUNT; i++) {
			g = transactionsEnabled ? VertexTestSchema.instance()
					.createVertexTestGraphWithTransactionSupport()
					: VertexTestSchema.instance().createVertexTestGraph();
			createTransaction(g);
			DoubleSubNode v0 = g.createDoubleSubNode();
			DoubleSubNode v1 = g.createDoubleSubNode();
			Edge[] edges = new Edge[RANDOM_EDGE_COUNT];
			Edge[] linkinoutfalse = new Edge[RANDOM_EDGE_COUNT];
			Edge[] sublinkinout = new Edge[RANDOM_EDGE_COUNT];
			Edge[] linkbackinout = new Edge[RANDOM_EDGE_COUNT];
			int lastlinkinoutfalse = 0;
			int lastsublinkinout = 0;
			int lastlinkbackinout = 0;
			Edge[] linkoutfalse = new Edge[RANDOM_EDGE_COUNT];
			Edge[] sublinkout = new Edge[RANDOM_EDGE_COUNT];
			Edge[] linkbackout = new Edge[RANDOM_EDGE_COUNT];
			int lastlinkoutfalse = 0;
			int lastsublinkout = 0;
			int lastlinkbackout = 0;
			Edge[] linkinfalse = new Edge[RANDOM_EDGE_COUNT];
			Edge[] sublinkin = new Edge[RANDOM_EDGE_COUNT];
			Edge[] linkbackin = new Edge[RANDOM_EDGE_COUNT];
			int lastlinkinfalse = 0;
			int lastsublinkin = 0;
			int lastlinkbackin = 0;
			Edge[] linkinouttrue = new Edge[RANDOM_EDGE_COUNT];
			int lastlinkinouttrue = 0;
			Edge[] linkouttrue = new Edge[RANDOM_EDGE_COUNT];
			int lastlinkouttrue = 0;
			Edge[] linkintrue = new Edge[RANDOM_EDGE_COUNT];
			int lastlinkintrue = 0;
			int edgetype = 0;
			boolean direction = false;
			boolean first = true;
			for (int j = 0; j < RANDOM_EDGE_COUNT; j++) {
				edgetype = rand.nextInt(2);
				direction = rand.nextBoolean();
				if (edgetype == 0) {
					Edge e = direction ? g.createLink(v0, v1) : g.createLink(
							v1, v0);
					if (direction) {
						edges[j] = e;
						if (!first) {
							while (lastlinkinoutfalse < j) {
								linkinoutfalse[lastlinkinoutfalse] = e;
								lastlinkinoutfalse++;
							}
							while (lastlinkoutfalse < j) {
								linkoutfalse[lastlinkoutfalse] = e;
								lastlinkoutfalse++;
							}
							while (lastlinkinouttrue < j) {
								linkinouttrue[lastlinkinouttrue] = e;
								lastlinkinouttrue++;
							}
							while (lastlinkouttrue < j) {
								linkouttrue[lastlinkouttrue] = e;
								lastlinkouttrue++;
							}
						}
					} else {
						edges[j] = e.getReversedEdge();
						if (!first) {
							while (lastlinkinoutfalse < j) {
								linkinoutfalse[lastlinkinoutfalse] = e
										.getReversedEdge();
								lastlinkinoutfalse++;
							}
							while (lastlinkinfalse < j) {
								linkinfalse[lastlinkinfalse] = e
										.getReversedEdge();
								lastlinkinfalse++;
							}
							while (lastlinkinouttrue < j) {
								linkinouttrue[lastlinkinouttrue] = e
										.getReversedEdge();
								lastlinkinouttrue++;
							}
							while (lastlinkintrue < j) {
								linkintrue[lastlinkintrue] = e
										.getReversedEdge();
								lastlinkintrue++;
							}
						}
					}
				}
				if (edgetype == 1) {
					Edge e = direction ? g.createSubLink(v0, v1) : g
							.createSubLink(v1, v0);
					if (direction) {
						edges[j] = e;
						if (!first) {
							while (lastlinkinoutfalse < j) {
								linkinoutfalse[lastlinkinoutfalse] = e;
								lastlinkinoutfalse++;
							}
							while (lastlinkoutfalse < j) {
								linkoutfalse[lastlinkoutfalse] = e;
								lastlinkoutfalse++;
							}
							while (lastsublinkinout < j) {
								sublinkinout[lastsublinkinout] = e;
								lastsublinkinout++;
							}
							while (lastsublinkout < j) {
								sublinkout[lastsublinkout] = e;
								lastsublinkout++;
							}
						}
					} else {
						edges[j] = e.getReversedEdge();
						if (!first) {
							while (lastlinkinoutfalse < j) {
								linkinoutfalse[lastlinkinoutfalse] = e
										.getReversedEdge();
								lastlinkinoutfalse++;
							}
							while (lastlinkinfalse < j) {
								linkinfalse[lastlinkinfalse] = e
										.getReversedEdge();
								lastlinkinfalse++;
							}
							while (lastsublinkinout < j) {
								sublinkinout[lastsublinkinout] = e
										.getReversedEdge();
								lastsublinkinout++;
							}
							while (lastsublinkin < j) {
								sublinkin[lastsublinkin] = e.getReversedEdge();
								lastsublinkin++;
							}
						}
					}
				} else {
					Edge e = direction ? g.createLinkBack(v0, v1) : g
							.createLinkBack(v1, v0);
					if (direction) {
						edges[j] = e;
						if (!first) {
							while (lastlinkbackinout < j) {
								linkbackinout[lastlinkbackinout] = e;
								lastlinkbackinout++;
							}
							while (lastlinkbackout < j) {
								linkbackout[lastlinkbackout] = e;
								lastlinkbackout++;
							}
						}
					} else {
						edges[j] = e.getReversedEdge();
						if (!first) {
							while (lastlinkbackinout < j) {
								linkbackinout[lastlinkbackinout] = e
										.getReversedEdge();
								lastlinkbackinout++;
							}
							while (lastlinkbackin < j) {
								linkbackin[lastlinkbackin] = e
										.getReversedEdge();
								lastlinkbackin++;
							}
						}
					}
				}
				first = false;
			}
			commit(g);
			createReadOnlyTransaction(g);
			for (int k = 0; k < edges.length; k++) {
				Edge e = edges[k];
				assertEquals(linkinoutfalse[k], e.getNextEdgeOfClass(ecs[0],
						EdgeDirection.INOUT, false));
				assertEquals(sublinkinout[k], e.getNextEdgeOfClass(ecs[1],
						EdgeDirection.INOUT, false));
				assertEquals(linkbackinout[k], e.getNextEdgeOfClass(ecs[2],
						EdgeDirection.INOUT, false));
				assertEquals(linkoutfalse[k], e.getNextEdgeOfClass(ecs[0],
						EdgeDirection.OUT, false));
				assertEquals(sublinkout[k], e.getNextEdgeOfClass(ecs[1],
						EdgeDirection.OUT, false));
				assertEquals(linkbackout[k], e.getNextEdgeOfClass(ecs[2],
						EdgeDirection.OUT, false));
				assertEquals(linkinfalse[k], e.getNextEdgeOfClass(ecs[0],
						EdgeDirection.IN, false));
				assertEquals(sublinkin[k], e.getNextEdgeOfClass(ecs[1],
						EdgeDirection.IN, false));
				assertEquals(linkbackin[k], e.getNextEdgeOfClass(ecs[2],
						EdgeDirection.IN, false));

				assertEquals(linkinouttrue[k], e.getNextEdgeOfClass(ecs[0],
						EdgeDirection.INOUT, true));
				assertEquals(sublinkinout[k], e.getNextEdgeOfClass(ecs[1],
						EdgeDirection.INOUT, true));
				assertEquals(linkbackinout[k], e.getNextEdgeOfClass(ecs[2],
						EdgeDirection.INOUT, true));
				assertEquals(linkouttrue[k], e.getNextEdgeOfClass(ecs[0],
						EdgeDirection.OUT, true));
				assertEquals(sublinkout[k], e.getNextEdgeOfClass(ecs[1],
						EdgeDirection.OUT, true));
				assertEquals(linkbackout[k], e.getNextEdgeOfClass(ecs[2],
						EdgeDirection.OUT, true));
				assertEquals(linkintrue[k], e.getNextEdgeOfClass(ecs[0],
						EdgeDirection.IN, true));
				assertEquals(sublinkin[k], e.getNextEdgeOfClass(ecs[1],
						EdgeDirection.IN, true));
				assertEquals(linkbackin[k], e.getNextEdgeOfClass(ecs[2],
						EdgeDirection.IN, true));
			}
			commit(g);
		}
	}

	// tests for the method Edge getNextEdgeOfClass(Class<? extends Edge>
	// anEdgeClass, EdgeDirection orientation, boolean noSubclasses);

	/**
	 * An edge which has no following edges.
	 */
	@Test
	public void getNextEdgeOfClassTestClassEdgeDirectionBoolean0() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		assertNull(e1
				.getNextEdgeOfClass(Link.class, EdgeDirection.INOUT, false));
		assertNull(e1.getNextEdgeOfClass(SubLink.class, EdgeDirection.INOUT,
				false));
		assertNull(e1.getNextEdgeOfClass(LinkBack.class, EdgeDirection.INOUT,
				false));
		assertNull(e1.getNextEdgeOfClass(Link.class, EdgeDirection.OUT, false));
		assertNull(e1.getNextEdgeOfClass(SubLink.class, EdgeDirection.OUT,
				false));
		assertNull(e1.getNextEdgeOfClass(LinkBack.class, EdgeDirection.OUT,
				false));
		assertNull(e1.getNextEdgeOfClass(Link.class, EdgeDirection.IN, false));
		assertNull(e1
				.getNextEdgeOfClass(SubLink.class, EdgeDirection.IN, false));
		assertNull(e1.getNextEdgeOfClass(LinkBack.class, EdgeDirection.IN,
				false));
		assertNull(e1.getNextEdgeOfClass(Link.class, EdgeDirection.INOUT, true));
		assertNull(e1.getNextEdgeOfClass(SubLink.class, EdgeDirection.INOUT,
				true));
		assertNull(e1.getNextEdgeOfClass(LinkBack.class, EdgeDirection.INOUT,
				true));
		assertNull(e1.getNextEdgeOfClass(Link.class, EdgeDirection.OUT, true));
		assertNull(e1
				.getNextEdgeOfClass(SubLink.class, EdgeDirection.OUT, true));
		assertNull(e1.getNextEdgeOfClass(LinkBack.class, EdgeDirection.OUT,
				true));
		assertNull(e1.getNextEdgeOfClass(Link.class, EdgeDirection.IN, true));
		assertNull(e1.getNextEdgeOfClass(SubLink.class, EdgeDirection.IN, true));
		assertNull(e1
				.getNextEdgeOfClass(LinkBack.class, EdgeDirection.IN, true));
	}

	/**
	 * Test in a manually built graph.
	 */
	@Test
	public void getNextEdgeOfClassTestClassEdgeDirectionBoolean1() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		Edge e2 = g.createLinkBack(v1, v2);
		Edge e3 = g.createSubLink(v2, v1);
		Edge e4 = g.createLink(v1, v1);
		Edge e5 = g.createLinkBack(v1, v2);
		// test of edge e1
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(Link.class,
				EdgeDirection.INOUT, false));
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(SubLink.class,
				EdgeDirection.INOUT, false));
		assertEquals(e2, e1.getNextEdgeOfClass(LinkBack.class,
				EdgeDirection.INOUT, false));
		assertEquals(e4, e1.getNextEdgeOfClass(Link.class, EdgeDirection.OUT,
				false));
		assertNull(e1.getNextEdgeOfClass(SubLink.class, EdgeDirection.OUT,
				false));
		assertEquals(e2, e1.getNextEdgeOfClass(LinkBack.class,
				EdgeDirection.OUT, false));
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(Link.class,
				EdgeDirection.IN, false));
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(SubLink.class,
				EdgeDirection.IN, false));
		assertNull(e1.getNextEdgeOfClass(LinkBack.class, EdgeDirection.IN,
				false));

		assertEquals(e4, e1.getNextEdgeOfClass(Link.class, EdgeDirection.INOUT,
				true));
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(SubLink.class,
				EdgeDirection.INOUT, true));
		assertEquals(e2, e1.getNextEdgeOfClass(LinkBack.class,
				EdgeDirection.INOUT, true));
		assertEquals(e4, e1.getNextEdgeOfClass(Link.class, EdgeDirection.OUT,
				true));
		assertNull(e1
				.getNextEdgeOfClass(SubLink.class, EdgeDirection.OUT, true));
		assertEquals(e2, e1.getNextEdgeOfClass(LinkBack.class,
				EdgeDirection.OUT, true));
		assertEquals(e4.getReversedEdge(), e1.getNextEdgeOfClass(Link.class,
				EdgeDirection.IN, true));
		assertEquals(e3.getReversedEdge(), e1.getNextEdgeOfClass(SubLink.class,
				EdgeDirection.IN, true));
		assertNull(e1
				.getNextEdgeOfClass(LinkBack.class, EdgeDirection.IN, true));

		// test of edge e2
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(Link.class,
				EdgeDirection.INOUT, false));
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(SubLink.class,
				EdgeDirection.INOUT, false));
		assertEquals(e5, e2.getNextEdgeOfClass(LinkBack.class,
				EdgeDirection.INOUT, false));
		assertEquals(e4, e2.getNextEdgeOfClass(Link.class, EdgeDirection.OUT,
				false));
		assertNull(e2.getNextEdgeOfClass(SubLink.class, EdgeDirection.OUT,
				false));
		assertEquals(e5, e2.getNextEdgeOfClass(LinkBack.class,
				EdgeDirection.OUT, false));
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(Link.class,
				EdgeDirection.IN, false));
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(SubLink.class,
				EdgeDirection.IN, false));
		assertNull(e2.getNextEdgeOfClass(LinkBack.class, EdgeDirection.IN,
				false));

		assertEquals(e4, e2.getNextEdgeOfClass(Link.class, EdgeDirection.INOUT,
				true));
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(SubLink.class,
				EdgeDirection.INOUT, true));
		assertEquals(e5, e2.getNextEdgeOfClass(LinkBack.class,
				EdgeDirection.INOUT, true));
		assertEquals(e4, e2.getNextEdgeOfClass(Link.class, EdgeDirection.OUT,
				true));
		assertNull(e2
				.getNextEdgeOfClass(SubLink.class, EdgeDirection.OUT, true));
		assertEquals(e5, e2.getNextEdgeOfClass(LinkBack.class,
				EdgeDirection.OUT, true));
		assertEquals(e4.getReversedEdge(), e2.getNextEdgeOfClass(Link.class,
				EdgeDirection.IN, true));
		assertEquals(e3.getReversedEdge(), e2.getNextEdgeOfClass(SubLink.class,
				EdgeDirection.IN, true));
		assertNull(e2
				.getNextEdgeOfClass(LinkBack.class, EdgeDirection.IN, true));

		// test of edge e3
		assertEquals(e4, e3.getReversedEdge().getNextEdgeOfClass(Link.class,
				EdgeDirection.INOUT, false));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(SubLink.class,
				EdgeDirection.INOUT, false));
		assertEquals(e5, e3.getReversedEdge().getNextEdgeOfClass(
				LinkBack.class, EdgeDirection.INOUT, false));
		assertEquals(e4, e3.getReversedEdge().getNextEdgeOfClass(Link.class,
				EdgeDirection.OUT, false));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(SubLink.class,
				EdgeDirection.OUT, false));
		assertEquals(e5, e3.getReversedEdge().getNextEdgeOfClass(
				LinkBack.class, EdgeDirection.OUT, false));
		assertEquals(e4.getReversedEdge(), e3.getReversedEdge()
				.getNextEdgeOfClass(Link.class, EdgeDirection.IN, false));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(SubLink.class,
				EdgeDirection.IN, false));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(LinkBack.class,
				EdgeDirection.IN, false));

		assertEquals(e4, e3.getReversedEdge().getNextEdgeOfClass(Link.class,
				EdgeDirection.INOUT, true));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(SubLink.class,
				EdgeDirection.INOUT, true));
		assertEquals(e5, e3.getReversedEdge().getNextEdgeOfClass(
				LinkBack.class, EdgeDirection.INOUT, true));
		assertEquals(e4, e3.getReversedEdge().getNextEdgeOfClass(Link.class,
				EdgeDirection.OUT, true));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(SubLink.class,
				EdgeDirection.OUT, true));
		assertEquals(e5, e3.getReversedEdge().getNextEdgeOfClass(
				LinkBack.class, EdgeDirection.OUT, true));
		assertEquals(e4.getReversedEdge(), e3.getReversedEdge()
				.getNextEdgeOfClass(Link.class, EdgeDirection.IN, true));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(SubLink.class,
				EdgeDirection.IN, true));
		assertNull(e3.getReversedEdge().getNextEdgeOfClass(LinkBack.class,
				EdgeDirection.IN, true));

		// test of edge e4
		assertEquals(e4.getReversedEdge(), e4.getNextEdgeOfClass(Link.class,
				EdgeDirection.INOUT, false));
		assertNull(e4.getNextEdgeOfClass(SubLink.class, EdgeDirection.INOUT,
				false));
		assertEquals(e5, e4.getNextEdgeOfClass(LinkBack.class,
				EdgeDirection.INOUT, false));
		assertNull(e4.getNextEdgeOfClass(Link.class, EdgeDirection.OUT, false));
		assertNull(e4.getNextEdgeOfClass(SubLink.class, EdgeDirection.OUT,
				false));
		assertEquals(e5, e4.getNextEdgeOfClass(LinkBack.class,
				EdgeDirection.OUT, false));
		assertEquals(e4.getReversedEdge(), e4.getNextEdgeOfClass(Link.class,
				EdgeDirection.IN, false));
		assertNull(e4
				.getNextEdgeOfClass(SubLink.class, EdgeDirection.IN, false));
		assertNull(e4.getNextEdgeOfClass(LinkBack.class, EdgeDirection.IN,
				false));

		assertEquals(e4.getReversedEdge(), e4.getNextEdgeOfClass(Link.class,
				EdgeDirection.INOUT, true));
		assertNull(e4.getNextEdgeOfClass(SubLink.class, EdgeDirection.INOUT,
				true));
		assertEquals(e5, e4.getNextEdgeOfClass(LinkBack.class,
				EdgeDirection.INOUT, true));
		assertNull(e4.getNextEdgeOfClass(Link.class, EdgeDirection.OUT, true));
		assertNull(e4
				.getNextEdgeOfClass(SubLink.class, EdgeDirection.OUT, true));
		assertEquals(e5, e4.getNextEdgeOfClass(LinkBack.class,
				EdgeDirection.OUT, true));
		assertEquals(e4.getReversedEdge(), e4.getNextEdgeOfClass(Link.class,
				EdgeDirection.IN, true));
		assertNull(e4.getNextEdgeOfClass(SubLink.class, EdgeDirection.IN, true));
		assertNull(e4
				.getNextEdgeOfClass(LinkBack.class, EdgeDirection.IN, true));

		// test of edge e4.getReversedEdge
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(Link.class,
				EdgeDirection.INOUT, false));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(SubLink.class,
				EdgeDirection.INOUT, false));
		assertEquals(e5, e4.getReversedEdge().getNextEdgeOfClass(
				LinkBack.class, EdgeDirection.INOUT, false));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(Link.class,
				EdgeDirection.OUT, false));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(SubLink.class,
				EdgeDirection.OUT, false));
		assertEquals(e5, e4.getReversedEdge().getNextEdgeOfClass(
				LinkBack.class, EdgeDirection.OUT, false));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(Link.class,
				EdgeDirection.IN, false));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(SubLink.class,
				EdgeDirection.IN, false));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(LinkBack.class,
				EdgeDirection.IN, false));

		assertNull(e4.getReversedEdge().getNextEdgeOfClass(Link.class,
				EdgeDirection.INOUT, true));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(SubLink.class,
				EdgeDirection.INOUT, true));
		assertEquals(e5, e4.getReversedEdge().getNextEdgeOfClass(
				LinkBack.class, EdgeDirection.INOUT, true));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(Link.class,
				EdgeDirection.OUT, true));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(SubLink.class,
				EdgeDirection.OUT, true));
		assertEquals(e5, e4.getReversedEdge().getNextEdgeOfClass(
				LinkBack.class, EdgeDirection.OUT, true));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(Link.class,
				EdgeDirection.IN, true));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(SubLink.class,
				EdgeDirection.IN, true));
		assertNull(e4.getReversedEdge().getNextEdgeOfClass(LinkBack.class,
				EdgeDirection.IN, true));

		// test of edge e5
		assertNull(e5
				.getNextEdgeOfClass(Link.class, EdgeDirection.INOUT, false));
		assertNull(e5.getNextEdgeOfClass(SubLink.class, EdgeDirection.INOUT,
				false));
		assertNull(e5.getNextEdgeOfClass(LinkBack.class, EdgeDirection.INOUT,
				false));
		assertNull(e5.getNextEdgeOfClass(Link.class, EdgeDirection.OUT, false));
		assertNull(e5.getNextEdgeOfClass(SubLink.class, EdgeDirection.OUT,
				false));
		assertNull(e5.getNextEdgeOfClass(LinkBack.class, EdgeDirection.OUT,
				false));
		assertNull(e5.getNextEdgeOfClass(Link.class, EdgeDirection.IN, false));
		assertNull(e5
				.getNextEdgeOfClass(SubLink.class, EdgeDirection.IN, false));
		assertNull(e5.getNextEdgeOfClass(LinkBack.class, EdgeDirection.IN,
				false));

		assertNull(e5.getNextEdgeOfClass(Link.class, EdgeDirection.INOUT, true));
		assertNull(e5.getNextEdgeOfClass(SubLink.class, EdgeDirection.INOUT,
				true));
		assertNull(e5.getNextEdgeOfClass(LinkBack.class, EdgeDirection.INOUT,
				true));
		assertNull(e5.getNextEdgeOfClass(Link.class, EdgeDirection.OUT, true));
		assertNull(e5
				.getNextEdgeOfClass(SubLink.class, EdgeDirection.OUT, true));
		assertNull(e5.getNextEdgeOfClass(LinkBack.class, EdgeDirection.OUT,
				true));
		assertNull(e5.getNextEdgeOfClass(Link.class, EdgeDirection.IN, true));
		assertNull(e5.getNextEdgeOfClass(SubLink.class, EdgeDirection.IN, true));
		assertNull(e5
				.getNextEdgeOfClass(LinkBack.class, EdgeDirection.IN, true));
	}

	/**
	 * Test in a randomly built graph
	 * 
	 * @throws CommitFailedException
	 */
	@Test
	public void getNextEdgeOfClassTestClassEdgeDirectionBoolean2()
			throws CommitFailedException {
		for (int i = 0; i < RANDOM_GRAPH_COUNT; i++) {
			g = transactionsEnabled ? VertexTestSchema.instance()
					.createVertexTestGraphWithTransactionSupport()
					: VertexTestSchema.instance().createVertexTestGraph();
			createTransaction(g);
			DoubleSubNode v0 = g.createDoubleSubNode();
			DoubleSubNode v1 = g.createDoubleSubNode();
			Edge[] edges = new Edge[RANDOM_EDGE_COUNT];
			Edge[] linkinoutfalse = new Edge[RANDOM_EDGE_COUNT];
			Edge[] sublinkinout = new Edge[RANDOM_EDGE_COUNT];
			Edge[] linkbackinout = new Edge[RANDOM_EDGE_COUNT];
			int lastlinkinoutfalse = 0;
			int lastsublinkinout = 0;
			int lastlinkbackinout = 0;
			Edge[] linkoutfalse = new Edge[RANDOM_EDGE_COUNT];
			Edge[] sublinkout = new Edge[RANDOM_EDGE_COUNT];
			Edge[] linkbackout = new Edge[RANDOM_EDGE_COUNT];
			int lastlinkoutfalse = 0;
			int lastsublinkout = 0;
			int lastlinkbackout = 0;
			Edge[] linkinfalse = new Edge[RANDOM_EDGE_COUNT];
			Edge[] sublinkin = new Edge[RANDOM_EDGE_COUNT];
			Edge[] linkbackin = new Edge[RANDOM_EDGE_COUNT];
			int lastlinkinfalse = 0;
			int lastsublinkin = 0;
			int lastlinkbackin = 0;
			Edge[] linkinouttrue = new Edge[RANDOM_EDGE_COUNT];
			int lastlinkinouttrue = 0;
			Edge[] linkouttrue = new Edge[RANDOM_EDGE_COUNT];
			int lastlinkouttrue = 0;
			Edge[] linkintrue = new Edge[RANDOM_EDGE_COUNT];
			int lastlinkintrue = 0;
			int edgetype = 0;
			boolean direction = false;
			boolean first = true;
			for (int j = 0; j < RANDOM_EDGE_COUNT; j++) {
				edgetype = rand.nextInt(2);
				direction = rand.nextBoolean();
				if (edgetype == 0) {
					Edge e = direction ? g.createLink(v0, v1) : g.createLink(
							v1, v0);
					if (direction) {
						edges[j] = e;
						if (!first) {
							while (lastlinkinoutfalse < j) {
								linkinoutfalse[lastlinkinoutfalse] = e;
								lastlinkinoutfalse++;
							}
							while (lastlinkoutfalse < j) {
								linkoutfalse[lastlinkoutfalse] = e;
								lastlinkoutfalse++;
							}
							while (lastlinkinouttrue < j) {
								linkinouttrue[lastlinkinouttrue] = e;
								lastlinkinouttrue++;
							}
							while (lastlinkouttrue < j) {
								linkouttrue[lastlinkouttrue] = e;
								lastlinkouttrue++;
							}
						}
					} else {
						edges[j] = e.getReversedEdge();
						if (!first) {
							while (lastlinkinoutfalse < j) {
								linkinoutfalse[lastlinkinoutfalse] = e
										.getReversedEdge();
								lastlinkinoutfalse++;
							}
							while (lastlinkinfalse < j) {
								linkinfalse[lastlinkinfalse] = e
										.getReversedEdge();
								lastlinkinfalse++;
							}
							while (lastlinkinouttrue < j) {
								linkinouttrue[lastlinkinouttrue] = e
										.getReversedEdge();
								lastlinkinouttrue++;
							}
							while (lastlinkintrue < j) {
								linkintrue[lastlinkintrue] = e
										.getReversedEdge();
								lastlinkintrue++;
							}
						}
					}
				}
				if (edgetype == 1) {
					Edge e = direction ? g.createSubLink(v0, v1) : g
							.createSubLink(v1, v0);
					if (direction) {
						edges[j] = e;
						if (!first) {
							while (lastlinkinoutfalse < j) {
								linkinoutfalse[lastlinkinoutfalse] = e;
								lastlinkinoutfalse++;
							}
							while (lastlinkoutfalse < j) {
								linkoutfalse[lastlinkoutfalse] = e;
								lastlinkoutfalse++;
							}
							while (lastsublinkinout < j) {
								sublinkinout[lastsublinkinout] = e;
								lastsublinkinout++;
							}
							while (lastsublinkout < j) {
								sublinkout[lastsublinkout] = e;
								lastsublinkout++;
							}
						}
					} else {
						edges[j] = e.getReversedEdge();
						if (!first) {
							while (lastlinkinoutfalse < j) {
								linkinoutfalse[lastlinkinoutfalse] = e
										.getReversedEdge();
								lastlinkinoutfalse++;
							}
							while (lastlinkinfalse < j) {
								linkinfalse[lastlinkinfalse] = e
										.getReversedEdge();
								lastlinkinfalse++;
							}
							while (lastsublinkinout < j) {
								sublinkinout[lastsublinkinout] = e
										.getReversedEdge();
								lastsublinkinout++;
							}
							while (lastsublinkin < j) {
								sublinkin[lastsublinkin] = e.getReversedEdge();
								lastsublinkin++;
							}
						}
					}
				} else {
					Edge e = direction ? g.createLinkBack(v0, v1) : g
							.createLinkBack(v1, v0);
					if (direction) {
						edges[j] = e;
						if (!first) {
							while (lastlinkbackinout < j) {
								linkbackinout[lastlinkbackinout] = e;
								lastlinkbackinout++;
							}
							while (lastlinkbackout < j) {
								linkbackout[lastlinkbackout] = e;
								lastlinkbackout++;
							}
						}
					} else {
						edges[j] = e.getReversedEdge();
						if (!first) {
							while (lastlinkbackinout < j) {
								linkbackinout[lastlinkbackinout] = e
										.getReversedEdge();
								lastlinkbackinout++;
							}
							while (lastlinkbackin < j) {
								linkbackin[lastlinkbackin] = e
										.getReversedEdge();
								lastlinkbackin++;
							}
						}
					}
				}
				first = false;
			}
			commit(g);
			createReadOnlyTransaction(g);
			for (int k = 0; k < edges.length; k++) {
				Edge e = edges[k];
				assertEquals(linkinoutfalse[k], e.getNextEdgeOfClass(
						Link.class, EdgeDirection.INOUT, false));
				assertEquals(sublinkinout[k], e.getNextEdgeOfClass(
						SubLink.class, EdgeDirection.INOUT, false));
				assertEquals(linkbackinout[k], e.getNextEdgeOfClass(
						LinkBack.class, EdgeDirection.INOUT, false));
				assertEquals(linkoutfalse[k], e.getNextEdgeOfClass(Link.class,
						EdgeDirection.OUT, false));
				assertEquals(sublinkout[k], e.getNextEdgeOfClass(SubLink.class,
						EdgeDirection.OUT, false));
				assertEquals(linkbackout[k], e.getNextEdgeOfClass(
						LinkBack.class, EdgeDirection.OUT, false));
				assertEquals(linkinfalse[k], e.getNextEdgeOfClass(Link.class,
						EdgeDirection.IN, false));
				assertEquals(sublinkin[k], e.getNextEdgeOfClass(SubLink.class,
						EdgeDirection.IN, false));
				assertEquals(linkbackin[k], e.getNextEdgeOfClass(
						LinkBack.class, EdgeDirection.IN, false));

				assertEquals(linkinouttrue[k], e.getNextEdgeOfClass(Link.class,
						EdgeDirection.INOUT, true));
				assertEquals(sublinkinout[k], e.getNextEdgeOfClass(
						SubLink.class, EdgeDirection.INOUT, true));
				assertEquals(linkbackinout[k], e.getNextEdgeOfClass(
						LinkBack.class, EdgeDirection.INOUT, true));
				assertEquals(linkouttrue[k], e.getNextEdgeOfClass(Link.class,
						EdgeDirection.OUT, true));
				assertEquals(sublinkout[k], e.getNextEdgeOfClass(SubLink.class,
						EdgeDirection.OUT, true));
				assertEquals(linkbackout[k], e.getNextEdgeOfClass(
						LinkBack.class, EdgeDirection.OUT, true));
				assertEquals(linkintrue[k], e.getNextEdgeOfClass(Link.class,
						EdgeDirection.IN, true));
				assertEquals(sublinkin[k], e.getNextEdgeOfClass(SubLink.class,
						EdgeDirection.IN, true));
				assertEquals(linkbackin[k], e.getNextEdgeOfClass(
						LinkBack.class, EdgeDirection.IN, true));
			}
			commit(g);
		}
	}

	// tests for the method Vertex getThis();

	private Vertex[] createRandomGraph(boolean retThis) {
		Vertex[] nodes = new Vertex[] { g.createSubNode(),
				g.createDoubleSubNode(), g.createSuperNode() };
		Vertex[] ret = new Vertex[RANDOM_VERTEX_COUNT];
		for (int i = 0; i < RANDOM_VERTEX_COUNT; i++) {
			int edge = rand.nextInt(3);
			switch (edge) {
			case 0:
				Vertex start = nodes[rand.nextInt(2)];
				Vertex end = nodes[rand.nextInt(2) + 1];
				g.createLink((AbstractSuperNode) start, (SuperNode) end);
				ret[i] = retThis ? start : end;
				break;
			case 1:
				start = nodes[1];
				end = nodes[rand.nextInt(2) + 1];
				g.createSubLink((DoubleSubNode) start, (SuperNode) end);
				ret[i] = retThis ? start : end;
				break;
			case 2:
				start = nodes[rand.nextInt(2) + 1];
				end = nodes[rand.nextInt(2)];
				g.createLinkBack((SuperNode) start, (AbstractSuperNode) end);
				ret[i] = retThis ? start : end;
				break;
			}
		}
		return ret;
	}

	/**
	 * Test in a manually built graph.
	 */
	@Test
	public void getThisTest0() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		DoubleSubNode v3 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		Edge e2 = g.createLinkBack(v2, v3);
		Edge e3 = g.createSubLink(v1, v3);
		assertEquals(v1, e1.getThis());
		assertEquals(v2, e1.getReversedEdge().getThis());
		assertEquals(v2, e2.getThis());
		assertEquals(v3, e2.getReversedEdge().getThis());
		assertEquals(v1, e3.getThis());
		assertEquals(v3, e3.getReversedEdge().getThis());
	}

	/**
	 * Test in a randomly built graph.
	 */
	@Test
	public void getThisTest1() {
		Vertex[] thisVertices = createRandomGraph(true);
		for (int i = 0; i < g.getECount(); i++) {
			assertEquals(thisVertices[i], g.getEdge(i + 1).getThis());
		}
	}

	// tests for the method Vertex getThat();

	/**
	 * Test in a manually built graph.
	 */
	@Test
	public void getThatTest0() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		DoubleSubNode v3 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		Edge e2 = g.createLinkBack(v2, v3);
		Edge e3 = g.createSubLink(v1, v3);
		assertEquals(v2, e1.getThat());
		assertEquals(v1, e1.getReversedEdge().getThat());
		assertEquals(v3, e2.getThat());
		assertEquals(v2, e2.getReversedEdge().getThat());
		assertEquals(v3, e3.getThat());
		assertEquals(v1, e3.getReversedEdge().getThat());
	}

	/**
	 * Test in a randomly built graph.
	 */
	@Test
	public void getThatTest1() {
		Vertex[] thisVertices = createRandomGraph(false);
		for (int i = 0; i < g.getECount(); i++) {
			assertEquals(thisVertices[i], g.getEdge(i + 1).getThat());
		}
	}

	// tests for the method String getThisRole();

	/**
	 * Test in a manually built graph.
	 */
	@Test
	public void getThisRoleTest() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		Edge e2 = g.createSubLink(v1, v2);
		Edge e3 = g.createLinkBack(v1, v2);
		assertEquals("source", e1.getThisRole());
		assertEquals("target", e1.getReversedEdge().getThisRole());
		assertEquals("sourcec", e2.getThisRole());
		assertEquals("targetc", e2.getReversedEdge().getThisRole());
		assertEquals("sourceb", e3.getThisRole());
		assertEquals("targetb", e3.getReversedEdge().getThisRole());
	}

	// tests for the method String getThisRole();

	/**
	 * Test in a manually built graph.
	 */
	@Test
	public void getThatRoleTest() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		Edge e2 = g.createSubLink(v1, v2);
		Edge e3 = g.createLinkBack(v1, v2);
		assertEquals("target", e1.getThatRole());
		assertEquals("source", e1.getReversedEdge().getThatRole());
		assertEquals("targetc", e2.getThatRole());
		assertEquals("sourcec", e2.getReversedEdge().getThatRole());
		assertEquals("targetb", e3.getThatRole());
		assertEquals("sourceb", e3.getReversedEdge().getThatRole());
	}

	// tests for the method Edge getNextEdgeInGraph();
	// (already tested in LoadTest.java)

	/**
	 * Test for reversedEdge.
	 */
	@Test
	public void getNextEdgeInGraphTestR0() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		Edge e2 = g.createSubLink(v1, v2);
		Edge e3 = g.createLinkBack(v1, v2);
		Edge e1R = e1.getReversedEdge();
		Edge e2R = e2.getReversedEdge();
		Edge e3R = e3.getReversedEdge();
		assertEquals(e2, e1R.getNextEdgeInGraph());
		assertEquals(e3, e2R.getNextEdgeInGraph());
		assertNull(e3R.getNextEdgeInGraph());
	}

	// tests for the method Edge getPrevEdgeInGraph();

	/**
	 * Creates an randomly build graph an returns an 2-dim ArrayList of Edges,
	 * which are needed to check the equality in respect to the parameters of
	 * the methods.
	 * 
	 * @param classedge
	 * @param nosubclasses
	 * @return
	 */
	private ArrayList<ArrayList<Edge>> createRandomGraph(boolean edgeClass,
			boolean nosubclasses) {
		Vertex[] nodes = new Vertex[] { g.createSubNode(),
				g.createDoubleSubNode(), g.createSuperNode() };
		ArrayList<ArrayList<Edge>> ret = new ArrayList<ArrayList<Edge>>();
		if (!edgeClass) {
			// edges for getPrevEdgeInGraph() are needed
			ret.add(new ArrayList<Edge>());
		} else if (!nosubclasses) {
			// edges for getNextEdgeOfClassInGraph(EdgeClass anEdgeClass) are
			// needed
			// 0=Link
			ret.add(new ArrayList<Edge>());
			// 1=SubLink
			ret.add(new ArrayList<Edge>());
			// 2=LinkBack
			ret.add(new ArrayList<Edge>());
		} else {
			// edges for getNextEdgeOfClassInGraph(EdgeClass anEdgeClass,
			// boolean noSubclasses) are needed
			// 0=Link false
			ret.add(new ArrayList<Edge>());
			// 1=SubLink false
			ret.add(new ArrayList<Edge>());
			// 2=LinkBack false
			ret.add(new ArrayList<Edge>());
			// 3=Link true
			ret.add(new ArrayList<Edge>());
			// 4=SubLink true
			ret.add(new ArrayList<Edge>());
			// 5=LinkBack true
			ret.add(new ArrayList<Edge>());
		}
		for (int i = 0; i < RANDOM_VERTEX_COUNT; i++) {
			int edge = rand.nextInt(3);
			Edge e = null;
			switch (edge) {
			case 0:
				Vertex start = nodes[rand.nextInt(2)];
				Vertex end = nodes[rand.nextInt(2) + 1];
				e = g.createLink((AbstractSuperNode) start, (SuperNode) end);
				if (!edgeClass) {
					ret.get(0).add(e);
				} else if (!nosubclasses) {
					ret.get(0).add(e);
				} else {
					ret.get(0).add(e);
					ret.get(3).add(e);
				}
				break;
			case 1:
				start = nodes[1];
				end = nodes[rand.nextInt(2) + 1];
				e = g.createSubLink((DoubleSubNode) start, (SuperNode) end);
				if (!edgeClass) {
					ret.get(0).add(e);
				} else if (!nosubclasses) {
					ret.get(0).add(e);
					ret.get(1).add(e);
				} else {
					ret.get(0).add(e);
					ret.get(1).add(e);
					ret.get(4).add(e);
				}
				break;
			case 2:
				start = nodes[rand.nextInt(2) + 1];
				end = nodes[rand.nextInt(2)];
				e = g
						.createLinkBack((SuperNode) start,
								(AbstractSuperNode) end);
				if (!edgeClass) {
					ret.get(0).add(e);
				} else if (!nosubclasses) {
					ret.get(2).add(e);
				} else {
					ret.get(2).add(e);
					ret.get(5).add(e);
				}
				break;
			}
		}
		return ret;
	}

	/**
	 * Test for reversedEdge.
	 */
	@Test
	public void getPrevEdgeInGraphTestR0() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		Edge e2 = g.createSubLink(v1, v2);
		Edge e3 = g.createLinkBack(v1, v2);
		Edge e1R = e1.getReversedEdge();
		Edge e2R = e2.getReversedEdge();
		Edge e3R = e3.getReversedEdge();
		assertEquals(e2, e3R.getPrevEdgeInGraph());
		assertEquals(e1, e2R.getPrevEdgeInGraph());
		assertNull(e1R.getPrevEdgeInGraph());
	}

	/**
	 * Tests if an edge has no previous edge in graph.
	 */
	@Test
	public void getPrevEdgeInGraphTest0() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v1);
		assertNull(e1.getPrevEdgeInGraph());
	}

	/**
	 * Test in a manually built graph.
	 */
	@Test
	public void getPrevEdgeInGraphTest1() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v1);
		Edge e2 = g.createLink(v2, v2);
		Edge e3 = g.createLink(v1, v2);
		assertEquals(e2, e3.getPrevEdgeInGraph());
		assertEquals(e1, e2.getPrevEdgeInGraph());
		assertNull(e1.getPrevEdgeInGraph());
	}

	/**
	 * Test in a randomly built graph.
	 */
	@Test
	public void getPrevEdgeInGraphTest2() {
		ArrayList<ArrayList<Edge>> result = createRandomGraph(false, false);
		Edge current = g.getLastEdgeInGraph();
		for (int i = g.getECount() - 1; i >= 0; i--) {
			assertEquals(result.get(0).get(i), current);
			current = current.getPrevEdgeInGraph();
		}
	}

	// tests for the method Edge getNextEdgeOfClassInGraph(EdgeClass
	// anEdgeClass);

	/**
	 * An edge which has no following edges.
	 */
	@Test
	public void getNextEdgeOfClassInGraphTestEdgeClass0() {
		EdgeClass[] ecs = getEdgeClasses();
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		assertNull(e1.getNextEdgeOfClassInGraph(ecs[0]));
		assertNull(e1.getNextEdgeOfClassInGraph(ecs[1]));
		assertNull(e1.getNextEdgeOfClassInGraph(ecs[2]));
	}

	/**
	 * Test in a manually built graph.
	 */
	@Test
	public void getNextEdgeOfClassInGraphTestEdgeClass1() {
		EdgeClass[] ecs = getEdgeClasses();
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		Edge e2 = g.createLinkBack(v1, v2);
		Edge e3 = g.createSubLink(v2, v1);
		Edge e4 = g.createLink(v1, v1);
		Edge e5 = g.createLinkBack(v1, v2);
		// test of edge e1
		assertEquals(e3, e1.getNextEdgeOfClassInGraph(ecs[0]));
		assertEquals(e3, e1.getNextEdgeOfClassInGraph(ecs[1]));
		assertEquals(e2, e1.getNextEdgeOfClassInGraph(ecs[2]));
		// test of edge e2
		assertEquals(e3, e2.getNextEdgeOfClassInGraph(ecs[0]));
		assertEquals(e3, e2.getNextEdgeOfClassInGraph(ecs[1]));
		assertEquals(e5, e2.getNextEdgeOfClassInGraph(ecs[2]));
		// test of edge e3
		assertEquals(e4, e3.getNextEdgeOfClassInGraph(ecs[0]));
		assertNull(e3.getNextEdgeOfClassInGraph(ecs[1]));
		assertEquals(e5, e3.getNextEdgeOfClassInGraph(ecs[2]));
		// test of edge e4
		assertNull(e4.getNextEdgeOfClassInGraph(ecs[0]));
		assertNull(e4.getNextEdgeOfClassInGraph(ecs[1]));
		assertEquals(e5, e4.getNextEdgeOfClassInGraph(ecs[2]));
		// test of edge e5
		assertNull(e5.getNextEdgeOfClassInGraph(ecs[0]));
		assertNull(e5.getNextEdgeOfClassInGraph(ecs[1]));
		assertNull(e5.getNextEdgeOfClassInGraph(ecs[2]));
		// test for reversedEdge
		Edge e1R = e1.getReversedEdge();
		Edge e2R = e2.getReversedEdge();
		Edge e3R = e3.getReversedEdge();
		Edge e4R = e4.getReversedEdge();
		Edge e5R = e5.getReversedEdge();
		// test of edge e1
		assertEquals(e3, e1R.getNextEdgeOfClassInGraph(ecs[0]));
		assertEquals(e3, e1R.getNextEdgeOfClassInGraph(ecs[1]));
		assertEquals(e2, e1R.getNextEdgeOfClassInGraph(ecs[2]));
		// test of edge e2
		assertEquals(e3, e2R.getNextEdgeOfClassInGraph(ecs[0]));
		assertEquals(e3, e2R.getNextEdgeOfClassInGraph(ecs[1]));
		assertEquals(e5, e2R.getNextEdgeOfClassInGraph(ecs[2]));
		// test of edge e3
		assertEquals(e4, e3R.getNextEdgeOfClassInGraph(ecs[0]));
		assertNull(e3R.getNextEdgeOfClassInGraph(ecs[1]));
		assertEquals(e5, e3R.getNextEdgeOfClassInGraph(ecs[2]));
		// test of edge e4
		assertNull(e4R.getNextEdgeOfClassInGraph(ecs[0]));
		assertNull(e4R.getNextEdgeOfClassInGraph(ecs[1]));
		assertEquals(e5, e4R.getNextEdgeOfClassInGraph(ecs[2]));
		// test of edge e5
		assertNull(e5R.getNextEdgeOfClassInGraph(ecs[0]));
		assertNull(e5R.getNextEdgeOfClassInGraph(ecs[1]));
		assertNull(e5R.getNextEdgeOfClassInGraph(ecs[2]));
	}

	/**
	 * Test in a randomly built graph.
	 */
	@Test
	public void getNextEdgeOfClassInGraphTestEdgeClass2() {
		EdgeClass[] ecs = getEdgeClasses();
		ArrayList<ArrayList<Edge>> result = createRandomGraph(true, false);
		Edge counter = g.getFirstEdgeOfClassInGraph(ecs[0]);
		for (Edge e : result.get(0)) {
			assertEquals(e, counter);
			counter = counter.getNextEdgeOfClassInGraph(ecs[0]);
		}
		counter = g.getFirstEdgeOfClassInGraph(ecs[1]);
		for (Edge e : result.get(1)) {
			assertEquals(e, counter);
			counter = counter.getNextEdgeOfClassInGraph(ecs[1]);
		}
		counter = g.getFirstEdgeOfClassInGraph(ecs[2]);
		for (Edge e : result.get(2)) {
			assertEquals(e, counter);
			counter = counter.getNextEdgeOfClassInGraph(ecs[2]);
		}
	}

	// tests for the method Edge getNextEdgeOfClassInGraph(Class<? extends Edge>
	// anEdgeClass);

	/**
	 * An edge which has no following edges.
	 */
	@Test
	public void getNextEdgeOfClassInGraphTestClass0() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		assertNull(e1.getNextEdgeOfClassInGraph(Link.class));
		assertNull(e1.getNextEdgeOfClassInGraph(SubLink.class));
		assertNull(e1.getNextEdgeOfClassInGraph(LinkBack.class));
	}

	/**
	 * Test in a manually built graph.
	 */
	@Test
	public void getNextEdgeOfClassInGraphTestClass1() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		Edge e2 = g.createLinkBack(v1, v2);
		Edge e3 = g.createSubLink(v2, v1);
		Edge e4 = g.createLink(v1, v1);
		Edge e5 = g.createLinkBack(v1, v2);
		// test of edge e1
		assertEquals(e3, e1.getNextEdgeOfClassInGraph(Link.class));
		assertEquals(e3, e1.getNextEdgeOfClassInGraph(SubLink.class));
		assertEquals(e2, e1.getNextEdgeOfClassInGraph(LinkBack.class));
		// test of edge e2
		assertEquals(e3, e2.getNextEdgeOfClassInGraph(Link.class));
		assertEquals(e3, e2.getNextEdgeOfClassInGraph(SubLink.class));
		assertEquals(e5, e2.getNextEdgeOfClassInGraph(LinkBack.class));
		// test of edge e3
		assertEquals(e4, e3.getNextEdgeOfClassInGraph(Link.class));
		assertNull(e3.getNextEdgeOfClassInGraph(SubLink.class));
		assertEquals(e5, e3.getNextEdgeOfClassInGraph(LinkBack.class));
		// test of edge e4
		assertNull(e4.getNextEdgeOfClassInGraph(Link.class));
		assertNull(e4.getNextEdgeOfClassInGraph(SubLink.class));
		assertEquals(e5, e4.getNextEdgeOfClassInGraph(LinkBack.class));
		// test of edge e5
		assertNull(e5.getNextEdgeOfClassInGraph(Link.class));
		assertNull(e5.getNextEdgeOfClassInGraph(SubLink.class));
		assertNull(e5.getNextEdgeOfClassInGraph(LinkBack.class));
		// test of reversedEdges
		e1 = e1.getReversedEdge();
		e2 = e2.getReversedEdge();
		e3 = e3.getReversedEdge();
		e4 = e4.getReversedEdge();
		e5 = e5.getReversedEdge();
		// test of edge e1
		assertEquals(e3.getNormalEdge(), e1
				.getNextEdgeOfClassInGraph(Link.class));
		assertEquals(e3.getNormalEdge(), e1
				.getNextEdgeOfClassInGraph(SubLink.class));
		assertEquals(e2.getNormalEdge(), e1
				.getNextEdgeOfClassInGraph(LinkBack.class));
		// test of edge e2
		assertEquals(e3.getNormalEdge(), e2
				.getNextEdgeOfClassInGraph(Link.class));
		assertEquals(e3.getNormalEdge(), e2
				.getNextEdgeOfClassInGraph(SubLink.class));
		assertEquals(e5.getNormalEdge(), e2
				.getNextEdgeOfClassInGraph(LinkBack.class));
		// test of edge e3
		assertEquals(e4.getNormalEdge(), e3
				.getNextEdgeOfClassInGraph(Link.class));
		assertNull(e3.getNextEdgeOfClassInGraph(SubLink.class));
		assertEquals(e5.getNormalEdge(), e3
				.getNextEdgeOfClassInGraph(LinkBack.class));
		// test of edge e4
		assertNull(e4.getNextEdgeOfClassInGraph(Link.class));
		assertNull(e4.getNextEdgeOfClassInGraph(SubLink.class));
		assertEquals(e5.getNormalEdge(), e4
				.getNextEdgeOfClassInGraph(LinkBack.class));
		// test of edge e5
		assertNull(e5.getNextEdgeOfClassInGraph(Link.class));
		assertNull(e5.getNextEdgeOfClassInGraph(SubLink.class));
		assertNull(e5.getNextEdgeOfClassInGraph(LinkBack.class));
	}

	/**
	 * Test in a randomly built graph.
	 */
	@Test
	public void getNextEdgeOfClassInGraphTestClass2() {
		ArrayList<ArrayList<Edge>> result = createRandomGraph(true, false);
		Edge counter = g.getFirstEdgeOfClassInGraph(Link.class);
		for (Edge e : result.get(0)) {
			assertEquals(e, counter);
			counter = counter.getNextEdgeOfClassInGraph(Link.class);
		}
		counter = g.getFirstEdgeOfClassInGraph(SubLink.class);
		for (Edge e : result.get(1)) {
			assertEquals(e, counter);
			counter = counter.getNextEdgeOfClassInGraph(SubLink.class);
		}
		counter = g.getFirstEdgeOfClassInGraph(LinkBack.class);
		for (Edge e : result.get(2)) {
			assertEquals(e, counter);
			counter = counter.getNextEdgeOfClassInGraph(LinkBack.class);
		}
	}

	// tests for the method Edge getNextEdgeOfClassInGraph(EdgeClass
	// anEdgeClass);

	/**
	 * An edge which has no following edges.
	 */
	@Test
	public void getNextEdgeOfClassInGraphTestEdgeClassBoolean0() {
		EdgeClass[] ecs = getEdgeClasses();
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		assertNull(e1.getNextEdgeOfClassInGraph(ecs[0], false));
		assertNull(e1.getNextEdgeOfClassInGraph(ecs[1], false));
		assertNull(e1.getNextEdgeOfClassInGraph(ecs[2], false));
		assertNull(e1.getNextEdgeOfClassInGraph(ecs[0], true));
		assertNull(e1.getNextEdgeOfClassInGraph(ecs[1], true));
		assertNull(e1.getNextEdgeOfClassInGraph(ecs[2], true));
	}

	/**
	 * Test in a manually built graph.
	 */
	@Test
	public void getNextEdgeOfClassInGraphTestEdgeClassBoolean1() {
		EdgeClass[] ecs = getEdgeClasses();
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		Edge e2 = g.createLinkBack(v1, v2);
		Edge e3 = g.createSubLink(v2, v1);
		Edge e4 = g.createLink(v1, v1);
		Edge e5 = g.createLinkBack(v1, v2);
		// test of edge e1
		assertEquals(e3, e1.getNextEdgeOfClassInGraph(ecs[0], false));
		assertEquals(e3, e1.getNextEdgeOfClassInGraph(ecs[1], false));
		assertEquals(e2, e1.getNextEdgeOfClassInGraph(ecs[2], false));

		assertEquals(e4, e1.getNextEdgeOfClassInGraph(ecs[0], true));
		assertEquals(e3, e1.getNextEdgeOfClassInGraph(ecs[1], true));
		assertEquals(e2, e1.getNextEdgeOfClassInGraph(ecs[2], true));
		// test of edge e2
		assertEquals(e3, e2.getNextEdgeOfClassInGraph(ecs[0], false));
		assertEquals(e3, e2.getNextEdgeOfClassInGraph(ecs[1], false));
		assertEquals(e5, e2.getNextEdgeOfClassInGraph(ecs[2], false));

		assertEquals(e4, e2.getNextEdgeOfClassInGraph(ecs[0], true));
		assertEquals(e3, e2.getNextEdgeOfClassInGraph(ecs[1], true));
		assertEquals(e5, e2.getNextEdgeOfClassInGraph(ecs[2], true));
		// test of edge e3
		assertEquals(e4, e3.getNextEdgeOfClassInGraph(ecs[0], false));
		assertNull(e3.getNextEdgeOfClassInGraph(ecs[1], false));
		assertEquals(e5, e3.getNextEdgeOfClassInGraph(ecs[2], false));

		assertEquals(e4, e3.getNextEdgeOfClassInGraph(ecs[0], true));
		assertNull(e3.getNextEdgeOfClassInGraph(ecs[1], true));
		assertEquals(e5, e3.getNextEdgeOfClassInGraph(ecs[2], true));
		// test of edge e4
		assertNull(e4.getNextEdgeOfClassInGraph(ecs[0], false));
		assertNull(e4.getNextEdgeOfClassInGraph(ecs[1], false));
		assertEquals(e5, e4.getNextEdgeOfClassInGraph(ecs[2], false));

		assertNull(e4.getNextEdgeOfClassInGraph(ecs[0], true));
		assertNull(e4.getNextEdgeOfClassInGraph(ecs[1], true));
		assertEquals(e5, e4.getNextEdgeOfClassInGraph(ecs[2], true));
		// test of edge e5
		assertNull(e5.getNextEdgeOfClassInGraph(ecs[0], false));
		assertNull(e5.getNextEdgeOfClassInGraph(ecs[1], false));
		assertNull(e5.getNextEdgeOfClassInGraph(ecs[2], false));

		assertNull(e5.getNextEdgeOfClassInGraph(ecs[0], true));
		assertNull(e5.getNextEdgeOfClassInGraph(ecs[1], true));
		assertNull(e5.getNextEdgeOfClassInGraph(ecs[2], true));
		// test for reversedEdge
		Edge e1R = e1.getReversedEdge();
		Edge e2R = e2.getReversedEdge();
		Edge e3R = e3.getReversedEdge();
		Edge e4R = e4.getReversedEdge();
		Edge e5R = e5.getReversedEdge();
		// test of edge e1
		assertEquals(e3, e1R.getNextEdgeOfClassInGraph(ecs[0], false));
		assertEquals(e3, e1R.getNextEdgeOfClassInGraph(ecs[1], false));
		assertEquals(e2, e1R.getNextEdgeOfClassInGraph(ecs[2], false));

		assertEquals(e4, e1R.getNextEdgeOfClassInGraph(ecs[0], true));
		assertEquals(e3, e1R.getNextEdgeOfClassInGraph(ecs[1], true));
		assertEquals(e2, e1R.getNextEdgeOfClassInGraph(ecs[2], true));
		// test of edge e2
		assertEquals(e3, e2R.getNextEdgeOfClassInGraph(ecs[0], false));
		assertEquals(e3, e2R.getNextEdgeOfClassInGraph(ecs[1], false));
		assertEquals(e5, e2R.getNextEdgeOfClassInGraph(ecs[2], false));

		assertEquals(e4, e2R.getNextEdgeOfClassInGraph(ecs[0], true));
		assertEquals(e3, e2R.getNextEdgeOfClassInGraph(ecs[1], true));
		assertEquals(e5, e2R.getNextEdgeOfClassInGraph(ecs[2], true));
		// test of edge e3
		assertEquals(e4, e3R.getNextEdgeOfClassInGraph(ecs[0], false));
		assertNull(e3R.getNextEdgeOfClassInGraph(ecs[1], false));
		assertEquals(e5, e3R.getNextEdgeOfClassInGraph(ecs[2], false));

		assertEquals(e4, e3R.getNextEdgeOfClassInGraph(ecs[0], true));
		assertNull(e3R.getNextEdgeOfClassInGraph(ecs[1], true));
		assertEquals(e5, e3R.getNextEdgeOfClassInGraph(ecs[2], true));
		// test of edge e4
		assertNull(e4R.getNextEdgeOfClassInGraph(ecs[0], false));
		assertNull(e4R.getNextEdgeOfClassInGraph(ecs[1], false));
		assertEquals(e5, e4R.getNextEdgeOfClassInGraph(ecs[2], false));

		assertNull(e4R.getNextEdgeOfClassInGraph(ecs[0], true));
		assertNull(e4R.getNextEdgeOfClassInGraph(ecs[1], true));
		assertEquals(e5, e4R.getNextEdgeOfClassInGraph(ecs[2], true));
		// test of edge e5
		assertNull(e5R.getNextEdgeOfClassInGraph(ecs[0], false));
		assertNull(e5R.getNextEdgeOfClassInGraph(ecs[1], false));
		assertNull(e5R.getNextEdgeOfClassInGraph(ecs[2], false));

		assertNull(e5R.getNextEdgeOfClassInGraph(ecs[0], true));
		assertNull(e5R.getNextEdgeOfClassInGraph(ecs[1], true));
		assertNull(e5R.getNextEdgeOfClassInGraph(ecs[2], true));
	}

	/**
	 * Test in a randomly built graph.
	 */
	@Test
	public void getNextEdgeOfClassInGraphTestEdgeClassBoolean2() {
		EdgeClass[] ecs = getEdgeClasses();
		ArrayList<ArrayList<Edge>> result = createRandomGraph(true, true);
		Edge counter = g.getFirstEdgeOfClassInGraph(ecs[0], false);
		for (Edge e : result.get(0)) {
			assertEquals(e, counter);
			counter = counter.getNextEdgeOfClassInGraph(ecs[0], false);
		}
		counter = g.getFirstEdgeOfClassInGraph(ecs[0], true);
		for (Edge e : result.get(3)) {
			assertEquals(e, counter);
			counter = counter.getNextEdgeOfClassInGraph(ecs[0], true);
		}
		counter = g.getFirstEdgeOfClassInGraph(ecs[1], false);
		for (Edge e : result.get(1)) {
			assertEquals(e, counter);
			counter = counter.getNextEdgeOfClassInGraph(ecs[1], false);
		}
		counter = g.getFirstEdgeOfClassInGraph(ecs[1], true);
		for (Edge e : result.get(4)) {
			assertEquals(e, counter);
			counter = counter.getNextEdgeOfClassInGraph(ecs[1], true);
		}
		counter = g.getFirstEdgeOfClassInGraph(ecs[2], false);
		for (Edge e : result.get(2)) {
			assertEquals(e, counter);
			counter = counter.getNextEdgeOfClassInGraph(ecs[2], false);
		}
		counter = g.getFirstEdgeOfClassInGraph(ecs[2], true);
		for (Edge e : result.get(5)) {
			assertEquals(e, counter);
			counter = counter.getNextEdgeOfClassInGraph(ecs[2], true);
		}
	}

	// tests for the method Edge getNextEdgeOfClassInGraph(Class<? extends Edge>
	// anEdgeClass, boolean noSubclasses);

	/**
	 * An edge which has no following edges.
	 */
	@Test
	public void getNextEdgeOfClassInGraphTestClassBoolean0() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		assertNull(e1.getNextEdgeOfClassInGraph(Link.class, false));
		assertNull(e1.getNextEdgeOfClassInGraph(SubLink.class, false));
		assertNull(e1.getNextEdgeOfClassInGraph(LinkBack.class, false));
		assertNull(e1.getNextEdgeOfClassInGraph(Link.class, true));
		assertNull(e1.getNextEdgeOfClassInGraph(SubLink.class, true));
		assertNull(e1.getNextEdgeOfClassInGraph(LinkBack.class, true));
	}

	/**
	 * Test in a manually built graph.
	 */
	@Test
	public void getNextEdgeOfClassInGraphTestClassBoolean1() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		Edge e2 = g.createLinkBack(v1, v2);
		Edge e3 = g.createSubLink(v2, v1);
		Edge e4 = g.createLink(v1, v1);
		Edge e5 = g.createLinkBack(v1, v2);
		// test of edge e1
		assertEquals(e3, e1.getNextEdgeOfClassInGraph(Link.class, false));
		assertEquals(e3, e1.getNextEdgeOfClassInGraph(SubLink.class, false));
		assertEquals(e2, e1.getNextEdgeOfClassInGraph(LinkBack.class, false));

		assertEquals(e4, e1.getNextEdgeOfClassInGraph(Link.class, true));
		assertEquals(e3, e1.getNextEdgeOfClassInGraph(SubLink.class, true));
		assertEquals(e2, e1.getNextEdgeOfClassInGraph(LinkBack.class, true));
		// test of edge e2
		assertEquals(e3, e2.getNextEdgeOfClassInGraph(Link.class, false));
		assertEquals(e3, e2.getNextEdgeOfClassInGraph(SubLink.class, false));
		assertEquals(e5, e2.getNextEdgeOfClassInGraph(LinkBack.class, false));

		assertEquals(e4, e2.getNextEdgeOfClassInGraph(Link.class, true));
		assertEquals(e3, e2.getNextEdgeOfClassInGraph(SubLink.class, true));
		assertEquals(e5, e2.getNextEdgeOfClassInGraph(LinkBack.class, true));
		// test of edge e3
		assertEquals(e4, e3.getNextEdgeOfClassInGraph(Link.class, false));
		assertNull(e3.getNextEdgeOfClassInGraph(SubLink.class, false));
		assertEquals(e5, e3.getNextEdgeOfClassInGraph(LinkBack.class, false));

		assertEquals(e4, e3.getNextEdgeOfClassInGraph(Link.class, true));
		assertNull(e3.getNextEdgeOfClassInGraph(SubLink.class, true));
		assertEquals(e5, e3.getNextEdgeOfClassInGraph(LinkBack.class, true));
		// test of edge e4
		assertNull(e4.getNextEdgeOfClassInGraph(Link.class, false));
		assertNull(e4.getNextEdgeOfClassInGraph(SubLink.class, false));
		assertEquals(e5, e4.getNextEdgeOfClassInGraph(LinkBack.class, false));

		assertNull(e4.getNextEdgeOfClassInGraph(Link.class, true));
		assertNull(e4.getNextEdgeOfClassInGraph(SubLink.class, true));
		assertEquals(e5, e4.getNextEdgeOfClassInGraph(LinkBack.class, true));
		// test of edge e5
		assertNull(e5.getNextEdgeOfClassInGraph(Link.class, false));
		assertNull(e5.getNextEdgeOfClassInGraph(SubLink.class, false));
		assertNull(e5.getNextEdgeOfClassInGraph(LinkBack.class, false));

		assertNull(e5.getNextEdgeOfClassInGraph(Link.class, true));
		assertNull(e5.getNextEdgeOfClassInGraph(SubLink.class, true));
		assertNull(e5.getNextEdgeOfClassInGraph(LinkBack.class, true));
		// test for reversedEdge
		Edge e1R = e1.getReversedEdge();
		Edge e2R = e2.getReversedEdge();
		Edge e3R = e3.getReversedEdge();
		Edge e4R = e4.getReversedEdge();
		Edge e5R = e5.getReversedEdge();
		// test of edge e1
		assertEquals(e3, e1R.getNextEdgeOfClassInGraph(Link.class, false));
		assertEquals(e3, e1R.getNextEdgeOfClassInGraph(SubLink.class, false));
		assertEquals(e2, e1R.getNextEdgeOfClassInGraph(LinkBack.class, false));

		assertEquals(e4, e1R.getNextEdgeOfClassInGraph(Link.class, true));
		assertEquals(e3, e1R.getNextEdgeOfClassInGraph(SubLink.class, true));
		assertEquals(e2, e1R.getNextEdgeOfClassInGraph(LinkBack.class, true));
		// test of edge e2
		assertEquals(e3, e2R.getNextEdgeOfClassInGraph(Link.class, false));
		assertEquals(e3, e2R.getNextEdgeOfClassInGraph(SubLink.class, false));
		assertEquals(e5, e2R.getNextEdgeOfClassInGraph(LinkBack.class, false));

		assertEquals(e4, e2R.getNextEdgeOfClassInGraph(Link.class, true));
		assertEquals(e3, e2R.getNextEdgeOfClassInGraph(SubLink.class, true));
		assertEquals(e5, e2R.getNextEdgeOfClassInGraph(LinkBack.class, true));
		// test of edge e3
		assertEquals(e4, e3R.getNextEdgeOfClassInGraph(Link.class, false));
		assertNull(e3R.getNextEdgeOfClassInGraph(SubLink.class, false));
		assertEquals(e5, e3R.getNextEdgeOfClassInGraph(LinkBack.class, false));

		assertEquals(e4, e3R.getNextEdgeOfClassInGraph(Link.class, true));
		assertNull(e3R.getNextEdgeOfClassInGraph(SubLink.class, true));
		assertEquals(e5, e3R.getNextEdgeOfClassInGraph(LinkBack.class, true));
		// test of edge e4
		assertNull(e4R.getNextEdgeOfClassInGraph(Link.class, false));
		assertNull(e4R.getNextEdgeOfClassInGraph(SubLink.class, false));
		assertEquals(e5, e4R.getNextEdgeOfClassInGraph(LinkBack.class, false));

		assertNull(e4R.getNextEdgeOfClassInGraph(Link.class, true));
		assertNull(e4R.getNextEdgeOfClassInGraph(SubLink.class, true));
		assertEquals(e5, e4R.getNextEdgeOfClassInGraph(LinkBack.class, true));
		// test of edge e5
		assertNull(e5R.getNextEdgeOfClassInGraph(Link.class, false));
		assertNull(e5R.getNextEdgeOfClassInGraph(SubLink.class, false));
		assertNull(e5R.getNextEdgeOfClassInGraph(LinkBack.class, false));

		assertNull(e5R.getNextEdgeOfClassInGraph(Link.class, true));
		assertNull(e5R.getNextEdgeOfClassInGraph(SubLink.class, true));
		assertNull(e5R.getNextEdgeOfClassInGraph(LinkBack.class, true));
	}

	/**
	 * Test in a randomly built graph.
	 */
	@Test
	public void getNextEdgeOfClassInGraphTestClassBoolean2() {
		ArrayList<ArrayList<Edge>> result = createRandomGraph(true, true);
		Edge counter = g.getFirstEdgeOfClassInGraph(Link.class, false);
		for (Edge e : result.get(0)) {
			assertEquals(e, counter);
			counter = counter.getNextEdgeOfClassInGraph(Link.class, false);
		}
		counter = g.getFirstEdgeOfClassInGraph(Link.class, true);
		for (Edge e : result.get(3)) {
			assertEquals(e, counter);
			counter = counter.getNextEdgeOfClassInGraph(Link.class, true);
		}
		counter = g.getFirstEdgeOfClassInGraph(SubLink.class, false);
		for (Edge e : result.get(1)) {
			assertEquals(e, counter);
			counter = counter.getNextEdgeOfClassInGraph(SubLink.class, false);
		}
		counter = g.getFirstEdgeOfClassInGraph(SubLink.class, true);
		for (Edge e : result.get(4)) {
			assertEquals(e, counter);
			counter = counter.getNextEdgeOfClassInGraph(SubLink.class, true);
		}
		counter = g.getFirstEdgeOfClassInGraph(LinkBack.class, false);
		for (Edge e : result.get(2)) {
			assertEquals(e, counter);
			counter = counter.getNextEdgeOfClassInGraph(LinkBack.class, false);
		}
		counter = g.getFirstEdgeOfClassInGraph(LinkBack.class, true);
		for (Edge e : result.get(5)) {
			assertEquals(e, counter);
			counter = counter.getNextEdgeOfClassInGraph(LinkBack.class, true);
		}
	}

	// tests of the method Vertex getAlpha();
	// (tested in IncidenceListTest.java)

	// tests of the method Vertex getOmega();
	// (tested in IncidenceListTest.java)

	// tests of the method boolean isBefore(Edge e);

	/**
	 * Tests if an edge is before itself.
	 */
	@Test
	public void isBeforeTest0() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v1);
		assertFalse(e1.isBefore(e1));
	}

	/**
	 * Tests if an exception is thrown, when two edges have not the same
	 * this-vertex.
	 */
	@Test(expected = GraphException.class)
	public void isBeforeTest1() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		Edge e2 = g.createLink(v2, v1);
		assertFalse(e1.isBefore(e2));
	}

	/**
	 * Tests if an edge is direct before another.
	 */
	@Test
	public void isBeforeTest2() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		Edge e2 = g.createLink(v1, v2);
		assertTrue(e1.isBefore(e2));
	}

	/**
	 * Tests if an edge is before another.
	 */
	@Test
	public void isBeforeTest3() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		g.createLink(v1, v2);
		Edge e2 = g.createLink(v1, v2);
		assertTrue(e1.isBefore(e2));
		assertFalse(e2.isBefore(e1));
	}

	// tests of the method boolean isAfter(Edge e);

	/**
	 * Tests if an edge is after itself.
	 */
	@Test
	public void isAfterTest0() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v1);
		assertFalse(e1.isAfter(e1));
	}

	/**
	 * Tests if an exception is thrown, when two edges have not the same
	 * this-vertex.
	 */
	@Test(expected = GraphException.class)
	public void isAfterTest1() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		Edge e2 = g.createLink(v2, v1);
		assertFalse(e1.isAfter(e2));
	}

	/**
	 * Tests if an edge is direct after another.
	 */
	@Test
	public void isAfterTest2() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		Edge e2 = g.createLink(v1, v2);
		assertTrue(e2.isAfter(e1));
	}

	/**
	 * Tests if an edge is after another.
	 */
	@Test
	public void isAfterTest3() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		g.createLink(v1, v2);
		Edge e2 = g.createLink(v1, v2);
		assertTrue(e2.isAfter(e1));
		assertFalse(e1.isAfter(e2));
	}

	// tests of the method boolean isBeforeInGraph(Edge e);
	// (tested in EdgeListTest.java)

	/**
	 * Tests if an edge is before itself.
	 */
	@Test
	public void isBeforeInGraphTest0() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v1);
		assertFalse(e1.isBeforeInGraph(e1));
	}

	// tests of the method boolean isAfterInGraph(Edge e);
	// (tested in EdgeListTest.java)

	/**
	 * Tests if an edge is before itself.
	 */
	@Test
	public void isAfterInGraphTest0() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v1);
		assertFalse(e1.isAfterInGraph(e1));
	}

	// tests of the method void putBeforeInGraph(Edge e);
	// (tested in EdgeListTest.java)

	// tests of the method void putAfterInGraph(Edge e);
	// (tested in EdgeListTest.java)

	// tests of the method void delete();
	// (tested in EdgeListTest.java)

	// tests of the method void setAlpha(Vertex v);

	/**
	 * Tests if the incident edges of <code>c</code> equals the edges of
	 * <code>incidentEdges</code>.
	 * 
	 * @param v
	 * @param incidentEdges
	 */
	private void testIncidenceList(Vertex v, Edge... incidentEdges) {
		assertEquals(incidentEdges.length, v.getDegree());
		int i = 0;
		for (Edge e : v.incidences()) {
			assertEquals(incidentEdges[i], e);
			i++;
		}
	}

	/**
	 * Alpha of an edge is changed to another vertex.
	 */
	@Test
	public void setAlphaTest0() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		DoubleSubNode v3 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		long v1vers = v1.getIncidenceListVersion();
		long v2vers = v2.getIncidenceListVersion();
		long v3vers = v3.getIncidenceListVersion();
		e1.setAlpha(v3);
		assertEquals(v3, e1.getAlpha());
		assertTrue(v1.isIncidenceListModified(v1vers));
		assertFalse(v2.isIncidenceListModified(v2vers));
		assertTrue(v3.isIncidenceListModified(v3vers));
		testIncidenceList(v1);
		testIncidenceList(v2, e1.getReversedEdge());
		testIncidenceList(v3, e1);
	}

	/**
	 * Alpha of an reversedEdge is changed to another vertex.
	 */
	@Test
	public void setAlphaTestR0() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		DoubleSubNode v3 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2).getReversedEdge();
		long v1vers = v1.getIncidenceListVersion();
		long v2vers = v2.getIncidenceListVersion();
		long v3vers = v3.getIncidenceListVersion();
		e1.setAlpha(v3);
		assertEquals(v3, e1.getAlpha());
		assertTrue(v1.isIncidenceListModified(v1vers));
		assertFalse(v2.isIncidenceListModified(v2vers));
		assertTrue(v3.isIncidenceListModified(v3vers));
		testIncidenceList(v1);
		testIncidenceList(v2, e1);
		testIncidenceList(v3, e1.getReversedEdge());
	}

	/**
	 * Alpha of an edge is set to the previous alpha vertex.
	 */
	@Test
	public void setAlphaTest1() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		long v1vers = v1.getIncidenceListVersion();
		long v2vers = v2.getIncidenceListVersion();
		e1.setAlpha(v1);
		assertEquals(v1, e1.getAlpha());
		assertFalse(v1.isIncidenceListModified(v1vers));
		assertFalse(v2.isIncidenceListModified(v2vers));
		testIncidenceList(v1, e1);
		testIncidenceList(v2, e1.getReversedEdge());
	}

	/**
	 * Alpha of an edge is changed to the omega vertex.
	 */
	@Test
	public void setAlphaTest2() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		long v1vers = v1.getIncidenceListVersion();
		long v2vers = v2.getIncidenceListVersion();
		e1.setAlpha(v2);
		assertEquals(v2, e1.getAlpha());
		assertTrue(v1.isIncidenceListModified(v1vers));
		assertTrue(v2.isIncidenceListModified(v2vers));
		testIncidenceList(v1);
		testIncidenceList(v2, e1.getReversedEdge(), e1);
	}

	/**
	 * Alpha of an edge is changed to another vertex. And there exists further
	 * edges.
	 */
	@Test
	public void setAlphaTest3() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		DoubleSubNode v3 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v3, v1);
		Edge e2 = g.createLink(v1, v2);
		Edge e3 = g.createLink(v2, v3);
		long v1vers = v1.getIncidenceListVersion();
		long v2vers = v2.getIncidenceListVersion();
		long v3vers = v3.getIncidenceListVersion();
		e2.setAlpha(v3);
		assertEquals(v3, e2.getAlpha());
		assertTrue(v1.isIncidenceListModified(v1vers));
		assertFalse(v2.isIncidenceListModified(v2vers));
		assertTrue(v3.isIncidenceListModified(v3vers));
		testIncidenceList(v1, e1.getReversedEdge());
		testIncidenceList(v2, e2.getReversedEdge(), e3);
		testIncidenceList(v3, e1, e3.getReversedEdge(), e2);
	}

	/**
	 * An exception should occur if you try to set alpha to a vertex which type
	 * isn't allowed as an alpha vertex for that edge.
	 */
	@Test(expected = GraphException.class)
	public void setAlphaTest4() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		SuperNode v2 = g.createSuperNode();
		Edge e1 = g.createLink(v1, v2);
		e1.setAlpha(v2);
	}

	/**
	 * Creates a random graph and returns an 2-dim ArrayList ret.get(0) =
	 * incident edges of v1 ret.get(1) = incident edges of v2 ret.get(2) =
	 * incident edges of v3
	 * 
	 * @return ret
	 */
	private ArrayList<ArrayList<Edge>> createRandomGraph() {
		ArrayList<ArrayList<Edge>> ret = new ArrayList<ArrayList<Edge>>(6);
		ret.add(new ArrayList<Edge>());
		ret.add(new ArrayList<Edge>());
		ret.add(new ArrayList<Edge>());
		Vertex[] nodes = new Vertex[] { g.createSubNode(),
				g.createDoubleSubNode(), g.createSuperNode() };
		for (int i = 0; i < RANDOM_VERTEX_COUNT; i++) {
			int edge = rand.nextInt(3);
			switch (edge) {
			case 0:
				int start = rand.nextInt(2);
				int end = rand.nextInt(2) + 1;
				Edge e = g.createLink((AbstractSuperNode) nodes[start],
						(SuperNode) nodes[end]);
				ret.get(start).add(e);
				ret.get(end).add(e.getReversedEdge());
				break;
			case 1:
				start = 1;
				end = rand.nextInt(2) + 1;
				e = g.createSubLink((DoubleSubNode) nodes[start],
						(SuperNode) nodes[end]);
				ret.get(start).add(e);
				ret.get(end).add(e.getReversedEdge());
				break;
			case 2:
				start = rand.nextInt(2) + 1;
				end = rand.nextInt(2);
				e = g.createLinkBack((SuperNode) nodes[start],
						(AbstractSuperNode) nodes[end]);
				ret.get(start).add(e);
				ret.get(end).add(e.getReversedEdge());
				break;
			}
		}
		return ret;
	}

	/**
	 * Random Test
	 */
	@Test
	public void setAlphaTest5() {
		ArrayList<ArrayList<Edge>> incidences = createRandomGraph();
		for (int i = 0; i < RANDOM_VERTEX_COUNT; i++) {
			int edgeId = rand.nextInt(g.getECount()) + 1;
			Edge e = g.getEdge(edgeId);
			int oldAlphaId = e.getAlpha().getId();
			int newAlphaId = rand.nextInt(3) + 1;
			Vertex newAlpha = g.getVertex(newAlphaId);
			try {
				e.setAlpha(newAlpha);
				if (oldAlphaId != newAlphaId) {
					incidences.get(oldAlphaId - 1).remove(e);
					incidences.get(newAlphaId - 1).add(e);
				}
			} catch (GraphException ge) {
				if ((e instanceof SubLink)
						&& (newAlpha instanceof DoubleSubNode)) {
					fail("SubLink can have an alpha of type "
							+ newAlpha.getClass().getName());
				} else if ((e instanceof Link) && !(e instanceof SubLink)
						&& (newAlpha instanceof AbstractSuperNode)) {
					fail("Link can have an alpha of type "
							+ newAlpha.getClass().getName());
				} else if ((e instanceof LinkBack)
						&& (newAlpha instanceof SuperNode)) {
					fail("LinkBack can have an alpha of type "
							+ newAlpha.getClass().getName());
				}
			}
		}
		testIncidenceList(g.getVertex(1), incidences.get(0)
				.toArray(new Edge[0]));
		testIncidenceList(g.getVertex(2), incidences.get(1)
				.toArray(new Edge[0]));
		testIncidenceList(g.getVertex(3), incidences.get(2)
				.toArray(new Edge[0]));
	}

	// tests of the method void setOmega(Vertex v);

	/**
	 * Omega of an edge is changed to another vertex.
	 */
	@Test
	public void setOmegaTest0() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		DoubleSubNode v3 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		long v1vers = v1.getIncidenceListVersion();
		long v2vers = v2.getIncidenceListVersion();
		long v3vers = v3.getIncidenceListVersion();
		e1.setOmega(v3);
		assertEquals(v3, e1.getOmega());
		assertFalse(v1.isIncidenceListModified(v1vers));
		assertTrue(v2.isIncidenceListModified(v2vers));
		assertTrue(v3.isIncidenceListModified(v3vers));
		testIncidenceList(v1, e1);
		testIncidenceList(v2);
		testIncidenceList(v3, e1.getReversedEdge());
	}

	/**
	 * Omega of an reversedEdge is changed to another vertex.
	 */
	@Test
	public void setOmegaTestR0() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		DoubleSubNode v3 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2).getReversedEdge();
		long v1vers = v1.getIncidenceListVersion();
		long v2vers = v2.getIncidenceListVersion();
		long v3vers = v3.getIncidenceListVersion();
		e1.setOmega(v3);
		assertEquals(v3, e1.getOmega());
		assertFalse(v1.isIncidenceListModified(v1vers));
		assertTrue(v2.isIncidenceListModified(v2vers));
		assertTrue(v3.isIncidenceListModified(v3vers));
		testIncidenceList(v1, e1.getReversedEdge());
		testIncidenceList(v2);
		testIncidenceList(v3, e1);
	}

	/**
	 * Omega of an edge is set to the previous omega vertex.
	 */
	@Test
	public void setOmegaTest1() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		long v1vers = v1.getIncidenceListVersion();
		long v2vers = v2.getIncidenceListVersion();
		e1.setOmega(v2);
		assertEquals(v2, e1.getOmega());
		assertFalse(v1.isIncidenceListModified(v1vers));
		assertFalse(v2.isIncidenceListModified(v2vers));
		testIncidenceList(v1, e1);
		testIncidenceList(v2, e1.getReversedEdge());
	}

	/**
	 * Omega of an edge is changed to the alpha vertex.
	 */
	@Test
	public void setOmegaTest2() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		long v1vers = v1.getIncidenceListVersion();
		long v2vers = v2.getIncidenceListVersion();
		e1.setOmega(v1);
		assertEquals(v1, e1.getOmega());
		assertTrue(v1.isIncidenceListModified(v1vers));
		assertTrue(v2.isIncidenceListModified(v2vers));
		testIncidenceList(v1, e1, e1.getReversedEdge());
		testIncidenceList(v2);
	}

	/**
	 * Omega of an edge is changed to another vertex. And there exists further
	 * edges.
	 */
	@Test
	public void setOmegaTest3() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		DoubleSubNode v3 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v3, v1);
		Edge e2 = g.createLink(v1, v2);
		Edge e3 = g.createLink(v2, v3);
		long v1vers = v1.getIncidenceListVersion();
		long v2vers = v2.getIncidenceListVersion();
		long v3vers = v3.getIncidenceListVersion();
		e2.setOmega(v3);
		assertEquals(v3, e2.getOmega());
		assertFalse(v1.isIncidenceListModified(v1vers));
		assertTrue(v2.isIncidenceListModified(v2vers));
		assertTrue(v3.isIncidenceListModified(v3vers));
		testIncidenceList(v1, e1.getReversedEdge(), e2);
		testIncidenceList(v2, e3);
		testIncidenceList(v3, e1, e3.getReversedEdge(), e2.getReversedEdge());
	}

	/**
	 * An exception should occur if you try to set omega to a vertex which type
	 * isn't allowed as an omega vertex for that edge.
	 */
	@Test(expected = GraphException.class)
	public void setOmegaTest4() {
		SubNode v1 = g.createSubNode();
		SuperNode v2 = g.createSuperNode();
		Edge e1 = g.createLink(v1, v2);
		e1.setOmega(v1);
	}

	/**
	 * Random Test
	 */
	@Test
	public void setOmegaTest5() {
		ArrayList<ArrayList<Edge>> incidences = createRandomGraph();
		for (int i = 0; i < RANDOM_VERTEX_COUNT; i++) {
			int edgeId = rand.nextInt(g.getECount()) + 1;
			Edge e = g.getEdge(edgeId);
			int oldOmegaId = e.getOmega().getId();
			int newOmegaId = rand.nextInt(3) + 1;
			Vertex newOmega = g.getVertex(newOmegaId);
			try {
				e.setOmega(newOmega);
				if (oldOmegaId != newOmegaId) {
					incidences.get(oldOmegaId - 1).remove(e.getReversedEdge());
					incidences.get(newOmegaId - 1).add(e.getReversedEdge());
				}
			} catch (GraphException ge) {
				if ((e instanceof Link) && (newOmega instanceof SuperNode)) {
					fail("Link can have an alpha of type "
							+ newOmega.getClass().getName());
				} else if ((e instanceof LinkBack)
						&& (newOmega instanceof AbstractSuperNode)) {
					fail("LinkBack can have an alpha of type "
							+ newOmega.getClass().getName());
				}
			}
		}
		testIncidenceList(g.getVertex(1), incidences.get(0)
				.toArray(new Edge[0]));
		testIncidenceList(g.getVertex(2), incidences.get(1)
				.toArray(new Edge[0]));
		testIncidenceList(g.getVertex(3), incidences.get(2)
				.toArray(new Edge[0]));
	}

	// tests of the method void setThis(Vertex v);

	/**
	 * This of an edge is changed to another vertex. And there exists further
	 * edges.
	 */
	@Test
	public void setThisTest3() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		DoubleSubNode v3 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v3, v1);
		Edge e2 = g.createLink(v1, v2);
		Edge e3 = g.createLink(v2, v3);
		long v1vers = v1.getIncidenceListVersion();
		long v2vers = v2.getIncidenceListVersion();
		long v3vers = v3.getIncidenceListVersion();
		e2.setThis(v3);
		assertEquals(v3, e2.getThis());
		assertTrue(v1.isIncidenceListModified(v1vers));
		assertFalse(v2.isIncidenceListModified(v2vers));
		assertTrue(v3.isIncidenceListModified(v3vers));
		testIncidenceList(v1, e1.getReversedEdge());
		testIncidenceList(v2, e2.getReversedEdge(), e3);
		testIncidenceList(v3, e1, e3.getReversedEdge(), e2);
		// test ReversedEdge
		v1vers = v1.getIncidenceListVersion();
		v2vers = v2.getIncidenceListVersion();
		v3vers = v3.getIncidenceListVersion();
		e2.getReversedEdge().setThis(v3);
		assertEquals(v3, e2.getOmega());
		assertFalse(v1.isIncidenceListModified(v1vers));
		assertTrue(v2.isIncidenceListModified(v2vers));
		assertTrue(v3.isIncidenceListModified(v3vers));
		testIncidenceList(v1, e1.getReversedEdge());
		testIncidenceList(v2, e3);
		testIncidenceList(v3, e1, e3.getReversedEdge(), e2, e2
				.getReversedEdge());
	}

	// tests of the method void setThat(Vertex v);

	/**
	 * That of an edge is changed to another vertex. And there exists further
	 * edges.
	 */
	@Test
	public void setThatTest3() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		DoubleSubNode v3 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v3, v1);
		Edge e2 = g.createLink(v1, v2);
		Edge e3 = g.createLink(v2, v3);
		long v1vers = v1.getIncidenceListVersion();
		long v2vers = v2.getIncidenceListVersion();
		long v3vers = v3.getIncidenceListVersion();
		e2.getReversedEdge().setThat(v3);
		assertEquals(v3, e2.getAlpha());
		assertTrue(v1.isIncidenceListModified(v1vers));
		assertFalse(v2.isIncidenceListModified(v2vers));
		assertTrue(v3.isIncidenceListModified(v3vers));
		testIncidenceList(v1, e1.getReversedEdge());
		testIncidenceList(v2, e2.getReversedEdge(), e3);
		testIncidenceList(v3, e1, e3.getReversedEdge(), e2);
		// test ReversedEdge
		v1vers = v1.getIncidenceListVersion();
		v2vers = v2.getIncidenceListVersion();
		v3vers = v3.getIncidenceListVersion();
		e2.setThat(v3);
		assertEquals(v3, e2.getOmega());
		assertFalse(v1.isIncidenceListModified(v1vers));
		assertTrue(v2.isIncidenceListModified(v2vers));
		assertTrue(v3.isIncidenceListModified(v3vers));
		testIncidenceList(v1, e1.getReversedEdge());
		testIncidenceList(v2, e3);
		testIncidenceList(v3, e1, e3.getReversedEdge(), e2, e2
				.getReversedEdge());
	}

	// tests of the method void putEdgeBefore(Edge e);
	// (tested in IncidenceListTest.java)

	// tests of the method void putEdgeAfter(Edge e);
	// (tested in IncidenceListTest.java)

	// tests of the method Edge getNormalEdge();

	/**
	 * Tests on edges and reversedEdges.
	 */
	@Test
	public void getNormalEdgeTest0() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		SuperNode v2 = g.createSuperNode();
		SubNode v3 = g.createSubNode();
		Edge e1 = g.createLink(v3, v2);
		Edge e2 = g.createSubLink(v1, v2);
		Edge e3 = g.createLinkBack(v2, v3);
		assertEquals(e1, e1.getNormalEdge());
		assertEquals(e1, e1.getReversedEdge().getNormalEdge());
		assertEquals(e2, e2.getNormalEdge());
		assertEquals(e2, e2.getReversedEdge().getNormalEdge());
		assertEquals(e3, e3.getNormalEdge());
		assertEquals(e3, e3.getReversedEdge().getNormalEdge());
	}

	// tests of the method Edge getReversedEdge();

	/**
	 * Tests on edges and reversedEdges.
	 */
	@Test
	public void getReversedEdgeTest0() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		SuperNode v2 = g.createSuperNode();
		SubNode v3 = g.createSubNode();
		Edge e1 = g.createLink(v3, v2);
		Edge e2 = g.createSubLink(v1, v2);
		Edge e3 = g.createLinkBack(v2, v3);
		assertEquals(e1.getReversedEdge(), e1.getReversedEdge());
		assertEquals(e1, e1.getReversedEdge().getReversedEdge());
		assertEquals(e2.getReversedEdge(), e2.getReversedEdge());
		assertEquals(e2, e2.getReversedEdge().getReversedEdge());
		assertEquals(e3.getReversedEdge(), e3.getReversedEdge());
		assertEquals(e3, e3.getReversedEdge().getReversedEdge());
	}

	// tests of the method boolean isNormal();

	/**
	 * Tests on edges and reversedEdges.
	 */
	@Test
	public void isNormalTest0() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		SuperNode v2 = g.createSuperNode();
		SubNode v3 = g.createSubNode();
		Edge e1 = g.createLink(v3, v2);
		Edge e2 = g.createSubLink(v1, v2);
		Edge e3 = g.createLinkBack(v2, v3);
		assertFalse(e1.getReversedEdge().isNormal());
		assertTrue(e1.isNormal());
		assertFalse(e2.getReversedEdge().isNormal());
		assertTrue(e2.isNormal());
		assertFalse(e3.getReversedEdge().isNormal());
		assertTrue(e3.isNormal());
	}

	// tests of the method boolean isValid();

	/**
	 * Tests on edges and reversedEdges.
	 */
	@Test
	public void isValidTest0() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		SuperNode v2 = g.createSuperNode();
		SubNode v3 = g.createSubNode();
		Edge e1 = g.createLink(v3, v2);
		Edge e2 = g.createSubLink(v1, v2);
		Edge e3 = g.createLinkBack(v2, v3);
		e3.delete();
		g.deleteEdge(e2);
		assertTrue(e1.isValid());
		assertFalse(e2.isValid());
		assertFalse(e3.isValid());
	}

	/*
	 * Test of the Interface GraphElement
	 */

	// tests of the method Graph getGraph();
	/**
	 * Test with edges of two graphs.
	 */
	@Test
	public void getGraphTest() {
		VertexTestGraph anotherGraph = ((VertexTestSchema) g.getSchema())
				.createVertexTestGraph();
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		DoubleSubNode v1a = anotherGraph.createDoubleSubNode();
		DoubleSubNode v2a = anotherGraph.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v2);
		Edge e1a = anotherGraph.createLink(v1a, v2a);
		assertEquals(g, e1.getGraph());
		assertEquals(anotherGraph, e1a.getGraph());
		assertEquals(g, e1.getReversedEdge().getGraph());
		assertEquals(anotherGraph, e1a.getReversedEdge().getGraph());
	}

	// tests of the method void graphModified();

	/**
	 * Tests if the graphversion is increased if the method is called.
	 * 
	 * @throws CommitFailedException
	 */
	// @Test
	// public void graphModifiedTest0() throws CommitFailedException {
	// DoubleSubNode v = g.createDoubleSubNode();
	// Edge e1 = g.createLink(v, v);
	// commit(g);
	//
	// createTransaction(g);
	// long graphversion = g.getGraphVersion();
	// e1.graphModified();
	// commit(g);
	//
	// createReadOnlyTransaction(g);
	// assertEquals(1 + graphversion, g.getGraphVersion());
	// }

	/**
	 * Tests if the graphversion is increased if the method is called on
	 * ReversedEdge.
	 * 
	 * @throws CommitFailedException
	 */
	// @Test
	// public void graphModifiedTestR0() throws CommitFailedException {
	// DoubleSubNode v = g.createDoubleSubNode();
	// Edge e1 = g.createLink(v, v).getReversedEdge();
	// long graphversion = g.getGraphVersion();
	// e1.graphModified();
	// commit(g);
	// createReadOnlyTransaction(g);
	// assertEquals(++graphversion, g.getGraphVersion());
	// }

	/**
	 * Tests if the graphversion is increased by creating a new edge.
	 * 
	 * @throws CommitFailedException
	 */
	@Test
	public void graphModifiedTest1() throws CommitFailedException {
		DoubleSubNode v1 = g.createDoubleSubNode();
		long graphversion = g.getGraphVersion();
		g.createLink(v1, v1);
		commit(g);
		createReadOnlyTransaction(g);
		assertEquals(++graphversion, g.getGraphVersion());
	}

	/**
	 * Tests if the graphversion is increased by deleting an edge.
	 * 
	 * @throws CommitFailedException
	 */
	@Test
	public void graphModifiedTest2() throws CommitFailedException {
		DoubleSubNode v1 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v1);
		long graphversion = g.getGraphVersion();
		e1.delete();
		commit(g);
		createReadOnlyTransaction(g);
		assertEquals(++graphversion, g.getGraphVersion());
	}

	/**
	 * Tests if the graphversion is increased by changing the attributes of an
	 * edge.
	 * 
	 * @throws CommitFailedException
	 */
	@Test
	public void graphModifiedTest3() throws CommitFailedException {
		DoubleSubNode v1 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v1);
		long graphversion = g.getGraphVersion();
		((Link) e1).set_aString("Test");
		commit(g);
		createReadOnlyTransaction(g);
		assertEquals(++graphversion, g.getGraphVersion());
	}

	/*
	 * Test of the Interface AttributedElement
	 */

	// tests of the method AttributedElementClass getAttributedElementClass();
	/**
	 * Some test cases for getAttributedElementClass
	 */
	@Test
	public void getAttributedElementClassTest() {
		EdgeClass[] edges = getEdgeClasses();
		DoubleSubNode v1 = g.createDoubleSubNode();
		SubNode v2 = g.createSubNode();
		SuperNode v3 = g.createSuperNode();
		Edge e1 = g.createLink(v2, v3);
		Edge e2 = g.createSubLink(v1, v3);
		Edge e3 = g.createLinkBack(v3, v2);
		assertEquals(edges[0], e1.getAttributedElementClass());
		assertEquals(edges[1], e2.getAttributedElementClass());
		assertEquals(edges[2], e3.getAttributedElementClass());
	}

	// tests of the method Class<? extends AttributedElement> getM1Class();

	/**
	 * Some test cases for getM1Class
	 */
	@Test
	public void getM1ClassTest() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		SubNode v2 = g.createSubNode();
		SuperNode v3 = g.createSuperNode();
		Edge e1 = g.createLink(v2, v3);
		Edge e2 = g.createSubLink(v1, v3);
		Edge e3 = g.createLinkBack(v3, v2);
		assertEquals(Link.class, e1.getM1Class());
		assertEquals(SubLink.class, e2.getM1Class());
		assertEquals(LinkBack.class, e3.getM1Class());
	}

	// tests of the method GraphClass getGraphClass();

	/**
	 * Some test cases for getGraphClass
	 */
	@Test
	public void getGraphClassTest() {
		VertexTestGraph anotherGraph = ((VertexTestSchema) g.getSchema())
				.createVertexTestGraph();
		GraphClass gc = g.getSchema().getGraphClass();
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v1a = anotherGraph.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v1);
		Edge e1a = anotherGraph.createLink(v1a, v1a);
		assertEquals(gc, e1.getGraphClass());
		assertEquals(gc, e1a.getGraphClass());
		assertEquals(gc, e1.getReversedEdge().getGraphClass());
		assertEquals(gc, e1a.getReversedEdge().getGraphClass());
	}

	// tests of the methods
	// void writeAttributeValues(GraphIO io) throws IOException,
	// GraphIOException;
	// and
	// void readAttributeValues(GraphIO io) throws GraphIOException;

	/**
	 * Test with null values.
	 */
	@Test
	public void writeReadAttributeValues0() throws GraphIOException,
			IOException {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		SubLink e1 = g.createSubLink(v1, v2);
		// test of writeAttributeValues
		GraphIO.saveGraphToFile("test.tg", g, null);
		LineNumberReader reader = new LineNumberReader(
				new FileReader("test.tg"));
		String line = "";
		String[] parts = null;
		while ((line = reader.readLine()) != null) {
			if (line.length() > 0) {
				line = line.substring(0, line.length() - 1);
			}
			parts = line.split(" ");
			if (parts[0].equals(((Integer) e1.getId()).toString())
					&& parts[1].equals(e1.getClass().getName())) {
				break;
			}
		}
		assertEquals("n", parts[2]);
		assertEquals("0", parts[3]);
		// test of readAttributeValues
		VertexTestGraph loadedgraph = (VertexTestGraph) GraphIO
				.loadGraphFromFile("test.tg", null);
		SubLink loadede1 = loadedgraph.getFirstSubLinkInGraph();
		assertEquals(e1.get_aString(), loadede1.get_aString());
		assertEquals(e1.get_anInt(), loadede1.get_anInt());
		// delete created file
		System.gc();
		reader.close();
		File f = new File("test.tg");
		f.delete();
	}

	/**
	 * Test with values.
	 */
	@Test
	public void writeReadAttributeValues1() throws GraphIOException,
			IOException {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		SubLink e1 = g.createSubLink(v1, v2);
		e1.set_anInt(3);
		e1.set_aString("HelloWorld!");
		// test of writeAttributeValues
		GraphIO.saveGraphToFile("test.tg", g, null);
		LineNumberReader reader = new LineNumberReader(
				new FileReader("test.tg"));
		String line = "";
		String[] parts = null;
		while ((line = reader.readLine()) != null) {
			if (line.length() > 0) {
				line = line.substring(0, line.length() - 1);
			}
			parts = line.split(" ");
			if (parts[0].equals(((Integer) e1.getId()).toString())
					&& parts[1].equals(e1.getClass().getName())) {
				break;
			}
		}
		assertEquals("\"HelloWorld!\"", parts[2]);
		assertEquals("3", parts[3]);
		// test of readAttributeValues
		VertexTestGraph loadedgraph = (VertexTestGraph) GraphIO
				.loadGraphFromFile("test.tg", null);
		SubLink loadede1 = loadedgraph.getFirstSubLinkInGraph();
		assertEquals(e1.get_aString(), loadede1.get_aString());
		assertEquals(e1.get_anInt(), loadede1.get_anInt());
		// delete created file
		System.gc();
		reader.close();
		File f = new File("test.tg");
		f.delete();
	}

	// tests of the method Object getAttribute(String name) throws
	// NoSuchFieldException;

	/**
	 * Tests if the value of the correct attribute is returned.
	 */
	@Test
	public void getAttributeTest0() throws NoSuchFieldException {
		DoubleSubNode v = g.createDoubleSubNode();
		SubLink e = g.createSubLink(v, v);
		e.set_aString("test");
		e.set_anInt(4);
		assertEquals("test", e.getAttribute("aString"));
		assertEquals(4, e.getAttribute("anInt"));
	}

	/**
	 * Tests if the value of the correct attribute is returned. reversedEdge
	 */
	@Test
	public void getAttributeTestR0() throws NoSuchFieldException {
		DoubleSubNode v = g.createDoubleSubNode();
		SubLink e = (SubLink) g.createSubLink(v, v).getReversedEdge();
		e.set_aString("test");
		e.set_anInt(4);
		assertEquals("test", e.getAttribute("aString"));
		assertEquals(4, e.getAttribute("anInt"));
	}

	/**
	 * Tests if an exception is thrown if you want to get an attribute which
	 * doesn't exist.
	 */
	@Test(expected = NoSuchFieldException.class)
	public void getAttributeTest1() throws NoSuchFieldException {
		DoubleSubNode v = g.createDoubleSubNode();
		SubLink e = g.createSubLink(v, v);
		e.getAttribute("cd");
	}

	/**
	 * Tests if an exception is thrown if you want to get an attribute with an
	 * empty name.
	 */
	@Test(expected = NoSuchFieldException.class)
	public void getAttributeTest2() throws NoSuchFieldException {
		DoubleSubNode v = g.createDoubleSubNode();
		SubLink e = g.createSubLink(v, v);
		e.getAttribute("");
	}

	// tests of the method void setAttribute(String name, Object data) throws
	// NoSuchFieldException;

	/**
	 * Tests if an existing attribute is correct set.
	 */
	@Test
	public void setAttributeTest0() throws NoSuchFieldException {
		DoubleSubNode v = g.createDoubleSubNode();
		SubLink e = g.createSubLink(v, v);
		e.setAttribute("aString", "test");
		e.setAttribute("anInt", 4);
		assertEquals("test", e.getAttribute("aString"));
		assertEquals(4, e.getAttribute("anInt"));
	}

	/**
	 * Tests if an existing attribute is correct set. reversedEdge
	 */
	@Test
	public void setAttributeTestR0() throws NoSuchFieldException {
		DoubleSubNode v = g.createDoubleSubNode();
		SubLink e = (SubLink) g.createSubLink(v, v).getReversedEdge();
		e.setAttribute("aString", "test");
		e.setAttribute("anInt", 4);
		assertEquals("test", e.getAttribute("aString"));
		assertEquals(4, e.getAttribute("anInt"));
	}

	/**
	 * Tests if an existing attribute is set to null.
	 */
	@Test
	public void setAttributeTest1() throws NoSuchFieldException {
		DoubleSubNode v = g.createDoubleSubNode();
		SubLink e = g.createSubLink(v, v);
		e.setAttribute("aString", null);
		assertNull(e.getAttribute("aString"));
	}

	/**
	 * Tests if an exception is thrown if you want to get an attribute which
	 * doesn't exist.
	 */
	@Test(expected = NoSuchFieldException.class)
	public void setAttributeTest2() throws NoSuchFieldException {
		DoubleSubNode v = g.createDoubleSubNode();
		SubLink e = g.createSubLink(v, v);
		e.setAttribute("cd", "a");
	}

	/**
	 * Tests if an exception is thrown if you want to get an attribute with an
	 * empty name.
	 */
	@Test(expected = NoSuchFieldException.class)
	public void setAttributeTest3() throws NoSuchFieldException {
		DoubleSubNode v = g.createDoubleSubNode();
		SubLink e = g.createSubLink(v, v);
		e.setAttribute("", "a");
	}

	// tests of the method Schema getSchema();

	/**
	 * Some tests.
	 */
	@Test
	public void getSchemaTest() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		SubNode v2 = g.createSubNode();
		SuperNode v3 = g.createSuperNode();
		Edge e1 = g.createLink(v2, v3);
		Edge e2 = g.createSubLink(v1, v3);
		Edge e3 = g.createLinkBack(v3, v2);
		Schema schema = g.getSchema();
		assertEquals(schema, e1.getSchema());
		assertEquals(schema, e2.getSchema());
		assertEquals(schema, e3.getSchema());
		assertEquals(schema, e1.getReversedEdge().getSchema());
		assertEquals(schema, e2.getReversedEdge().getSchema());
		assertEquals(schema, e3.getReversedEdge().getSchema());
	}

	/*
	 * Test of the Interface Comparable
	 */

	// tests of the method int compareTo(AttributedElement a);
	/**
	 * Test if a vertex is equal to itself.
	 */
	@Test
	public void compareToTest0() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v1);
		assertEquals(0, e1.compareTo(e1));
	}

	/**
	 * Test if a vertex is smaller than another.
	 */
	@Test
	public void compareToTest1() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v1);
		Edge e2 = g.createLink(v1, v1);
		assertTrue(e1.compareTo(e2) < 0);
	}

	/**
	 * Test if a vertex is greater than another.
	 */
	@Test
	public void compareToTest2() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v1);
		Edge e2 = g.createLink(v1, v1);
		assertTrue(e2.compareTo(e1) > 0);
	}

	// tests of the method int compareTo(AttributedElement a); reversedEdge

	/**
	 * Test if a vertex is equal to itself.
	 */
	@Test
	public void compareToTestR0() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v1).getReversedEdge();
		assertEquals(0, e1.compareTo(e1));
	}

	/**
	 * Test if a vertex is smaller than another.
	 */
	@Test
	public void compareToTestR1() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v1).getReversedEdge();
		Edge e2 = g.createLink(v1, v1).getReversedEdge();
		assertTrue(e1.compareTo(e2) < 0);
	}

	/**
	 * Test if a vertex is greater than another.
	 */
	@Test
	public void compareToTestR2() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v1).getReversedEdge();
		Edge e2 = g.createLink(v1, v1).getReversedEdge();
		assertTrue(e2.compareTo(e1) > 0);
	}

	// tests of the method int compareTo(AttributedElement a); reversedEdge and
	// normalEdge
	/**
	 * Test if a vertex is equal to itself.
	 */
	@Test
	public void compareToTestM0() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v1);
		assertTrue(e1.compareTo(e1.getReversedEdge()) < 0);
	}

	/**
	 * Test if a vertex is equal to itself.
	 */
	@Test
	public void compareToTestM1() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		Edge e1 = g.createLink(v1, v1);
		assertTrue(e1.getReversedEdge().compareTo(e1) > 0);
	}

	/*
	 * Test of the generated methods.
	 */

	// test of the methods getAnInt and setAnInt
	@Test
	public void getAnIntTest() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		SubLink e1 = g.createSubLink(v1, v1);
		e1.set_anInt(1);
		assertEquals(1, e1.get_anInt());
		e1.set_anInt(2);
		assertEquals(2, e1.get_anInt());
		e1.set_anInt(3);
		assertEquals(3, e1.get_anInt());
	}

	// test of the methods getAString and setAString
	@Test
	public void getAStringTest() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		SubLink e1 = g.createSubLink(v1, v1);
		e1.set_aString("Test1");
		assertEquals("Test1", e1.get_aString());
		e1.set_aString("Test2");
		assertEquals("Test2", e1.get_aString());
		e1.set_aString("");
		assertEquals("", e1.get_aString());
	}

	// test of the method getNextLinkInGraph
	@Test
	public void getNextLinkInGraphTest() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		SubNode v2 = g.createSubNode();
		SuperNode v3 = g.createSuperNode();
		SubLink e1 = g.createSubLink(v1, v1);
		g.createLinkBack(v3, v2);
		SubLink e3 = g.createSubLink(v1, v3);
		Link e4 = g.createLink(v2, v1);
		assertEquals(e3, e1.getNextLinkInGraph());
		assertEquals(e4, e3.getNextLinkInGraph());
		assertEquals(null, e4.getNextLinkInGraph());
	}

	// test of the method getNextSubLinkInGraph
	@Test
	public void getNextSubLinkInGraphTest() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		SubNode v2 = g.createSubNode();
		SuperNode v3 = g.createSuperNode();
		SubLink e1 = g.createSubLink(v1, v1);
		g.createLinkBack(v3, v2);
		g.createLink(v2, v1);
		SubLink e4 = g.createSubLink(v1, v3);
		assertEquals(e4, e1.getNextSubLinkInGraph());
		assertEquals(null, e4.getNextSubLinkInGraph());
	}

	// test of the method getNextLink
	@Test
	public void getNextLinkTest() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		SubLink e1 = g.createSubLink(v1, v1);
		g.createLinkBack(v1, v2);
		Link e3 = g.createLink(v2, v1);
		g.createSubLink(v2, v2);
		assertEquals(e1.getReversedEdge(), e1.getNextLink());
		assertEquals(e3.getReversedEdge(), ((SubLink) e1.getReversedEdge())
				.getNextLink());
		assertEquals(null, ((Link) e3.getReversedEdge()).getNextLink());
	}

	// test of the method getNextSubLink
	@Test
	public void getNextSubLinkTest() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		SubLink e1 = g.createSubLink(v1, v1);
		g.createLinkBack(v1, v2);
		g.createLink(v2, v1);
		SubLink e4 = g.createSubLink(v1, v2);
		assertEquals(e1.getReversedEdge(), e1.getNextSubLink());
		assertEquals(e4, ((SubLink) e1.getReversedEdge()).getNextSubLink());
		assertEquals(null, e4.getNextSubLink());
	}

	// test of the method getNextLink(EdgeDirection orientation)
	@Test
	public void getNextLinkTestEdgeDirection() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		SubLink e1 = g.createSubLink(v1, v1);
		g.createLinkBack(v1, v2);
		Link e3 = g.createLink(v2, v1);
		g.createSubLink(v2, v2);
		Link e5 = g.createLink(v1, v2);
		assertEquals(e1.getReversedEdge(), e1.getNextLink(EdgeDirection.INOUT));
		assertEquals(e1.getReversedEdge(), e1.getNextLink(EdgeDirection.IN));
		assertEquals(e5, e1.getNextLink(EdgeDirection.OUT));
		assertEquals(e3.getReversedEdge(), ((SubLink) e1.getReversedEdge())
				.getNextLink(EdgeDirection.INOUT));
		assertEquals(e3.getReversedEdge(), ((SubLink) e1.getReversedEdge())
				.getNextLink(EdgeDirection.IN));
		assertEquals(e5, ((SubLink) e1.getReversedEdge())
				.getNextLink(EdgeDirection.OUT));
		assertEquals(e5, ((Link) e3.getReversedEdge())
				.getNextLink(EdgeDirection.INOUT));
		assertEquals(null, ((Link) e3.getReversedEdge())
				.getNextLink(EdgeDirection.IN));
		assertEquals(e5, ((Link) e3.getReversedEdge())
				.getNextLink(EdgeDirection.OUT));
		assertEquals(null, e5.getNextLink(EdgeDirection.INOUT));
		assertEquals(null, e5.getNextLink(EdgeDirection.IN));
		assertEquals(null, e5.getNextLink(EdgeDirection.OUT));
	}

	// test of the method getNextSubLink(EdgeDirection orientation)
	@Test
	public void getNextSubLinkTestEdgeDirection() {
		DoubleSubNode v1 = g.createDoubleSubNode();
		DoubleSubNode v2 = g.createDoubleSubNode();
		SubLink e1 = g.createSubLink(v1, v1);
		g.createLinkBack(v1, v2);
		g.createLink(v2, v1);
		g.createSubLink(v2, v2);
		SubLink e5 = g.createSubLink(v1, v2);
		SubLink e6 = g.createSubLink(v2, v1);
		assertEquals(e1.getReversedEdge(), e1
				.getNextSubLink(EdgeDirection.INOUT));
		assertEquals(e1.getReversedEdge(), e1.getNextSubLink(EdgeDirection.IN));
		assertEquals(e5, e1.getNextLink(EdgeDirection.OUT));
		assertEquals(e5, ((SubLink) e1.getReversedEdge())
				.getNextSubLink(EdgeDirection.INOUT));
		assertEquals(e6.getReversedEdge(), ((SubLink) e1.getReversedEdge())
				.getNextSubLink(EdgeDirection.IN));
		assertEquals(e5, ((SubLink) e1.getReversedEdge())
				.getNextSubLink(EdgeDirection.OUT));
		assertEquals(e6.getReversedEdge(), e5
				.getNextSubLink(EdgeDirection.INOUT));
		assertEquals(e6.getReversedEdge(), e5.getNextSubLink(EdgeDirection.IN));
		assertEquals(null, e5.getNextSubLink(EdgeDirection.OUT));
		assertEquals(null, ((SubLink) e6.getReversedEdge())
				.getNextSubLink(EdgeDirection.INOUT));
		assertEquals(null, ((SubLink) e6.getReversedEdge())
				.getNextSubLink(EdgeDirection.IN));
		assertEquals(null, ((SubLink) e6.getReversedEdge())
				.getNextSubLink(EdgeDirection.OUT));
	}
}
