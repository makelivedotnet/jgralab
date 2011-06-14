package de.uni_koblenz.jgralabtest.eca;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_koblenz.jgralab.eca.Action;
import de.uni_koblenz.jgralab.eca.ECARule;
import de.uni_koblenz.jgralab.eca.PrintAction;
import de.uni_koblenz.jgralab.eca.events.ChangeAttributeEvent;
import de.uni_koblenz.jgralab.eca.events.ChangeEdgeEvent;
import de.uni_koblenz.jgralab.eca.events.CreateEdgeEvent;
import de.uni_koblenz.jgralab.eca.events.CreateVertexEvent;
import de.uni_koblenz.jgralab.eca.events.DeleteEdgeEvent;
import de.uni_koblenz.jgralab.eca.events.DeleteVertexEvent;
import de.uni_koblenz.jgralab.eca.events.Event;
import de.uni_koblenz.jgralabtest.eca.schemas.simplelibrary.Book;
import de.uni_koblenz.jgralabtest.eca.schemas.simplelibrary.Library;
import de.uni_koblenz.jgralabtest.eca.schemas.simplelibrary.Loans;
import de.uni_koblenz.jgralabtest.eca.schemas.simplelibrary.Magazin;
import de.uni_koblenz.jgralabtest.eca.schemas.simplelibrary.MediaType;
import de.uni_koblenz.jgralabtest.eca.schemas.simplelibrary.NewMedia;
import de.uni_koblenz.jgralabtest.eca.schemas.simplelibrary.SimpleLibraryGraph;
import de.uni_koblenz.jgralabtest.eca.schemas.simplelibrary.SimpleLibrarySchema;
import de.uni_koblenz.jgralabtest.eca.schemas.simplelibrary.User;

public class ECATest {

	private static SimpleLibraryGraph simlibgraph;
	private static User user1;
	private static User user2;
	private static Book book1;
	private static NewMedia newmedia1;
	private static Loans loans_u1_b1;


	@BeforeClass
	public static void setUp() {
		System.out.println("Start ECA Test.");
		initGraph();
	}

	@AfterClass
	public static void tearDown() {
		System.out.println("Finish ECA Test.");
	}


	@Test
	public void testDeleteVertexEvent() {

		Book newBook = simlibgraph.createBook();

		Event bef_ev = new DeleteVertexEvent(Event.EventTime.BEFORE, Book.class);
		Action bef_act = new PrintAction(
				"ECA Test Message: Book Vertex will become deleted.");
		ECARule bef_rule = new ECARule(bef_ev, bef_act);
		simlibgraph.getECARuleManager().addECARule(bef_rule);

		Event aft_ev = new DeleteVertexEvent(Event.EventTime.AFTER, Book.class);
		Action aft_act = new PrintAction(
				"ECA Test Message: Book Vertex is deleted.");
		ECARule aft_rule = new ECARule(aft_ev, aft_act);
		simlibgraph.getECARuleManager().addECARule(aft_rule);

		simlibgraph.deleteVertex(newBook);

	}

	@Test
	public void testCreateVertexEvent() {
		Event bef_ev = new CreateVertexEvent(Event.EventTime.BEFORE, Book.class);
		Action bef_act = new PrintAction(
				"ECA Test Message: New Book Vertex will become created.");
		ECARule bef_rule = new ECARule(bef_ev, bef_act);
		simlibgraph.getECARuleManager().addECARule(bef_rule);

		Event aft_ev = new CreateVertexEvent(Event.EventTime.AFTER, Book.class);
		Action aft_act = new PrintAction(
				"ECA Test Message: New Book Vertex is created.");
		ECARule aft_rule = new ECARule(aft_ev, aft_act);
		simlibgraph.getECARuleManager().addECARule(aft_rule);

		simlibgraph.createBook();

	}

	@Test
	public void testDeleteEdgeEvent() {
		Event bef_ev = new DeleteEdgeEvent(Event.EventTime.BEFORE, Loans.class);
		Action bef_act = new PrintAction(
				"ECA Test Message: Loans Edge will become deleted.");
		ECARule bef_rule = new ECARule(bef_ev, bef_act);
		simlibgraph.getECARuleManager().addECARule(bef_rule);

		Event aft_ev = new DeleteEdgeEvent(Event.EventTime.AFTER, Loans.class);
		Action aft_act = new PrintAction(
				"ECA Test Message: Loans Edge is deleted.");
		ECARule aft_rule = new ECARule(aft_ev, aft_act);
		simlibgraph.getECARuleManager().addECARule(aft_rule);

		Loans newLoans = simlibgraph.createLoans(user1, newmedia1);
		simlibgraph.deleteEdge(newLoans);

	}

	@Test
	public void testCreateEdgeEvent() {
		Event bef_ev = new CreateEdgeEvent(Event.EventTime.BEFORE, Loans.class);
		Action bef_act = new PrintAction(
				"ECA Test Message: New Loans Edge will become created.");
		ECARule bef_rule = new ECARule(bef_ev, bef_act);
		simlibgraph.getECARuleManager().addECARule(bef_rule);

		Event aft_ev = new CreateEdgeEvent(Event.EventTime.AFTER, Loans.class);
		Action aft_act = new PrintAction(
				"ECA Test Message: New Loans Edge is created.");
		ECARule aft_rule = new ECARule(aft_ev, aft_act);
		simlibgraph.getECARuleManager().addECARule(aft_rule);

		simlibgraph.createLoans(user1, newmedia1);

	}

	@Test
	public void testChangeEdgeEvent() {
		Event bef_ev = new ChangeEdgeEvent(Event.EventTime.BEFORE, Loans.class);
		Action bef_act = new PrintAction(
				"ECA Test Message: Loans Edge will become changed.");
		ECARule bef_rule = new ECARule(bef_ev, bef_act);
		simlibgraph.getECARuleManager().addECARule(bef_rule);

		Event aft_ev = new ChangeEdgeEvent(Event.EventTime.AFTER, Loans.class);
		Action aft_act = new PrintAction(
				"ECA Test Message: Loans Edge is changed.");
		ECARule aft_rule = new ECARule(aft_ev, aft_act);
		simlibgraph.getECARuleManager().addECARule(aft_rule);

		loans_u1_b1.setAlpha(user2);
	}

	@Test
	public void testChangeAttributeEvent() {
		Event bef_ev = new ChangeAttributeEvent(Event.EventTime.BEFORE,
				Book.class, "title");
		Action bef_act = new PrintAction(
				"ECA Test Message: Title of Book Vertex will become changed.");
		ECARule bef_rule = new ECARule(bef_ev, bef_act);
		simlibgraph.getECARuleManager().addECARule(bef_rule);

		Event aft_ev = new ChangeAttributeEvent(Event.EventTime.AFTER,
				Book.class, "title");
		Action aft_act = new PrintAction(
				"ECA Test Message: Title of Book Vertex is changed.");
		ECARule aft_rule = new ECARule(aft_ev, aft_act);
		simlibgraph.getECARuleManager().addECARule(aft_rule);

		book1.set_title("NewTitle");
	}

	static void initGraph() {
		SimpleLibraryGraph graph = SimpleLibrarySchema.instance()
				.createSimpleLibraryGraph();
		graph.set_version("v1.0");

		// Library
		Library lib = graph.createLibrary();
		lib.set_name("Bibliothequa");

		// Users
		user1 = graph.createUser();
		user1.set_name("Martin King");

		user2 = graph.createUser();
		user2.set_name("Stephanie Plum");

		// Media
		book1 = graph.createBook();
		book1.set_title("Lord of the Rings");
		book1.set_author("J.R.R. Tokien");

		Magazin magazin1 = graph.createMagazin();
		magazin1.set_title("CT");
		magazin1.set_publisher("Blub");
		magazin1.set_year(2011);

		newmedia1 = graph.createNewMedia();
		newmedia1.set_title("Rush Hour 1");
		newmedia1.set_type(MediaType.DVD);

		// Media are in Library
		lib.add_media(book1);
		lib.add_media(magazin1);
		lib.add_media(newmedia1);

		// user1 loans book1
		loans_u1_b1 = graph.createLoans(user1, book1);
		loans_u1_b1.set_date(graph.createDate(1, 1, 2011));

		// user 2 loans magazin1
		Loans loans_u2_m1 = graph.createLoans(user2, magazin1);
		loans_u2_m1.set_date(graph.createDate(5, 5, 2011));

		simlibgraph = graph;
	}

}
