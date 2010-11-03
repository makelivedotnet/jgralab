/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2010 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7
 * 
 * If you modify this Program, or any covered work, by linking or combining
 * it with Eclipse (or a modified version of that program or an Eclipse
 * plugin), containing parts covered by the terms of the Eclipse Public
 * License (EPL), the licensors of this Program grant you additional
 * permission to convey the resulting work.  Corresponding Source for a
 * non-source form of such a combination shall include the source code for
 * the parts of JGraLab used as well as that of the covered work.
 */
package de.uni_koblenz.jgralab.impl.db;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.SortedSet;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.Schema;

/**
 * Factory that creates PostgreSql specific prepared statements.
 * 
 * @author ultbreit@uni-koblenz.de
 */
public class PostgreSqlStatementList extends SqlStatementList {

	public PostgreSqlStatementList(GraphDatabase graphDatabase)
			throws GraphDatabaseException {
		super(graphDatabase);
	}

	private String CREATE_GRAPH_SCHEMA_TABLE = "CREATE SEQUENCE \"schemaIdSequence\";"
			+ "CREATE TABLE \"GraphSchema\"("
			+ "\"schemaId\" INT4 PRIMARY KEY DEFAULT NEXTVAL('\"schemaIdSequence\"'),"
			+ "\"packagePrefix\" TEXT,"
			+ "name TEXT,"
			+ "\"serializedDefinition\" TEXT" + ");";

	@Override
	public PreparedStatement createGraphSchemaTable() throws SQLException {
		return this.connection.prepareStatement(CREATE_GRAPH_SCHEMA_TABLE);
	}

	private final String CREATE_TYPE_TABLE = "CREATE SEQUENCE type_id_seq;"
			+ "CREATE TABLE \"Type\"("
			+ "\"typeId\" INT4 PRIMARY KEY DEFAULT NEXTVAL('type_id_seq'),"
			+ "\"qualifiedName\" TEXT,"
			+ "\"schemaId\" INT4 REFERENCES \"GraphSchema\"" + ");";

	@Override
	public PreparedStatement createTypeTable() throws SQLException {
		return this.connection.prepareStatement(CREATE_TYPE_TABLE);
	}

	private final String CREATE_GRAPH_TABLE = "CREATE SEQUENCE \"graphIdSequence\";"
			+ "CREATE TABLE \"Graph\"("
			+ "\"gId\" INT4 PRIMARY KEY DEFAULT NEXTVAL('\"graphIdSequence\"'),"
			+ "uid TEXT,"
			+ "version INT8,"
			+ "\"vSeqVersion\" INT8,"
			+ "\"eSeqVersion\" INT8," + "\"typeId\" INT4" + ");";

	@Override
	public PreparedStatement createGraphTable() throws SQLException {
		return this.connection.prepareStatement(CREATE_GRAPH_TABLE);
	}

	private final String ADD_FOREIGN_KEY_CONSTRAINTS_ON_GRAPH_TABLE = "ALTER TABLE \"Graph\" ADD CONSTRAINT \"typeIdIsForeignKey\" FOREIGN KEY (\"typeId\") REFERENCES \"Type\" (\"typeId\")";

	@Override
	public PreparedStatement addForeignKeyConstraintsOnGraphTable()
			throws SQLException {
		return this.connection
				.prepareStatement(ADD_FOREIGN_KEY_CONSTRAINTS_ON_GRAPH_TABLE);
	}

	private final String DROP_FOREIGN_KEY_CONSTRAINTS_FROM_GRAPH_TABLE = "ALTER TABLE \"Graph\" DROP CONSTRAINT \"typeIdIsForeignKey\";";

	@Override
	public PreparedStatement dropForeignKeyConstraintsOnGraphTable()
			throws SQLException {
		return this.connection
				.prepareStatement(DROP_FOREIGN_KEY_CONSTRAINTS_FROM_GRAPH_TABLE);
	}

	private final String CREATE_VERTEX_TABLE = "CREATE TABLE \"Vertex\"("
			+ "\"vId\" INT4," + "\"gId\" INT4," + "\"typeId\" INT4,"
			+ "\"lambdaSeqVersion\" INT8," + "\"sequenceNumber\" INT8" + ");";

	@Override
	public PreparedStatement createVertexTable() throws SQLException {
		return this.connection.prepareStatement(CREATE_VERTEX_TABLE);
	}

	private final String ADD_PRIMARY_KEY_CONSTRAINT_ON_VERTEX_TABLE = "ALTER TABLE \"Vertex\" ADD CONSTRAINT \"vertexPrimaryKey\" PRIMARY KEY ( \"vId\", \"gId\" );";

	@Override
	public PreparedStatement addPrimaryKeyConstraintOnVertexTable()
			throws SQLException {
		return this.connection
				.prepareStatement(ADD_PRIMARY_KEY_CONSTRAINT_ON_VERTEX_TABLE);
	}

	private final String DROP_PRIMARY_KEY_CONSTRAINT_FROM_VERTEX_TABLE = "ALTER TABLE \"Vertex\" DROP CONSTRAINT \"vertexPrimaryKey\";";

	@Override
	public PreparedStatement dropPrimaryKeyConstraintFromVertexTable()
			throws SQLException {
		return this.connection
				.prepareStatement(DROP_PRIMARY_KEY_CONSTRAINT_FROM_VERTEX_TABLE);
	}

	private final String ADD_FOREIGN_KEY_CONSTRAINTS_ON_VERTEX_TABLE = "ALTER TABLE \"Vertex\" ADD CONSTRAINT \"gIdIsForeignKey\" FOREIGN KEY (\"gId\") REFERENCES \"Graph\" (\"gId\");"
			+ "ALTER TABLE \"Vertex\" ADD CONSTRAINT \"typeIdIsForeignKey\" FOREIGN KEY (\"typeId\") REFERENCES \"Type\" (\"typeId\");";

	@Override
	public PreparedStatement addForeignKeyConstraintsOnVertexTable()
			throws SQLException {
		return this.connection
				.prepareStatement(ADD_FOREIGN_KEY_CONSTRAINTS_ON_VERTEX_TABLE);
	}

	private final String DROP_FOREIGN_KEY_CONSTRAINTS_FROM_VERTEX_TABLE = "ALTER TABLE \"Vertex\" DROP CONSTRAINT \"gIdIsForeignKey\";"
			+ "ALTER TABLE \"Vertex\" DROP CONSTRAINT \"typeIdIsForeignKey\";";

	@Override
	public PreparedStatement dropForeignKeyConstraintFromVertexTable()
			throws SQLException {
		return this.connection
				.prepareStatement(DROP_FOREIGN_KEY_CONSTRAINTS_FROM_VERTEX_TABLE);
	}

	private final String CREATE_EDGE_TABLE = "CREATE TABLE \"Edge\"("
			+ "\"eId\" INT4," + "\"gId\" INT4," + "\"typeId\" INT4,"
			+ "\"sequenceNumber\" INT8" + ");";

	@Override
	public PreparedStatement createEdgeTable() throws SQLException {
		return this.connection.prepareStatement(CREATE_EDGE_TABLE);
	}

	private final String ADD_PRIMARY_KEY_CONSTRAINT_ON_EDGE_TABLE = "ALTER TABLE \"Edge\" ADD CONSTRAINT \"edgePrimaryKey\" PRIMARY KEY ( \"eId\", \"gId\" );";

	@Override
	public PreparedStatement addPrimaryKeyConstraintOnEdgeTable()
			throws SQLException {
		return this.connection
				.prepareStatement(ADD_PRIMARY_KEY_CONSTRAINT_ON_EDGE_TABLE);
	}

	private final String DROP_PRIMARY_KEY_CONSTRAINT_FROM_EDGE_TABLE = "ALTER TABLE \"Edge\" DROP CONSTRAINT \"edgePrimaryKey\";";

	@Override
	public PreparedStatement dropPrimaryKeyConstraintFromEdgeTable()
			throws SQLException {
		return this.connection
				.prepareStatement(DROP_PRIMARY_KEY_CONSTRAINT_FROM_EDGE_TABLE);
	}

	private final String ADD_FOREIGN_KEY_CONSTRAINTS_ON_EDGE_TABLE = "ALTER TABLE \"Edge\" ADD CONSTRAINT \"gIdIsForeignKey\" FOREIGN KEY (\"gId\") REFERENCES \"Graph\" (\"gId\");"
			+ "ALTER TABLE \"Edge\" ADD CONSTRAINT \"typeIdIsForeignKey\" FOREIGN KEY (\"typeId\") REFERENCES \"Type\" (\"typeId\");";

	@Override
	public PreparedStatement addForeignKeyConstraintsOnEdgeTable()
			throws SQLException {
		return this.connection
				.prepareStatement(ADD_FOREIGN_KEY_CONSTRAINTS_ON_EDGE_TABLE);
	}

	private final String DROP_FOREIGN_KEY_CONSTRAINTS_FROM_EDGE_TABLE = "ALTER TABLE \"Edge\" DROP CONSTRAINT \"gIdIsForeignKey\";"
			+ "ALTER TABLE \"Edge\" DROP CONSTRAINT \"typeIdIsForeignKey\";";

	@Override
	public PreparedStatement dropForeignKeyConstraintFromEdgeTable()
			throws SQLException {
		return this.connection
				.prepareStatement(DROP_FOREIGN_KEY_CONSTRAINTS_FROM_EDGE_TABLE);
	}

	private final String CREATE_INCIDENCE_TABLE = "CREATE TYPE \"DIRECTION\" AS ENUM( 'OUT', 'IN' );"
			+ "CREATE TABLE \"Incidence\"("
			+ "\"eId\" INT4,"
			+ "\"vId\" INT4,"
			+ "\"gId\" INT4,"
			+ "direction \"DIRECTION\","
			+ "\"sequenceNumber\" INT8" + ");";

	@Override
	public PreparedStatement createIncidenceTable() throws SQLException {
		return this.connection.prepareStatement(CREATE_INCIDENCE_TABLE);
	}

	private final String ADD_PRIMARY_KEY_CONSTRAINT_ON_INCIDENCE_TABLE = "ALTER TABLE \"Incidence\" ADD CONSTRAINT \"incidencePrimaryKey\" PRIMARY KEY ( \"eId\", \"gId\", direction );";

	@Override
	public PreparedStatement addPrimaryKeyConstraintOnIncidenceTable()
			throws SQLException {
		return this.connection
				.prepareStatement(ADD_PRIMARY_KEY_CONSTRAINT_ON_INCIDENCE_TABLE);
	}

	private final String DROP_PRIMARY_KEY_CONSTRAINT_FROM_INCIDENCE_TABLE = "ALTER TABLE \"Incidence\" DROP CONSTRAINT \"incidencePrimaryKey\";";

	@Override
	public PreparedStatement dropPrimaryKeyConstraintFromIncidenceTable()
			throws SQLException {
		return this.connection
				.prepareStatement(DROP_PRIMARY_KEY_CONSTRAINT_FROM_INCIDENCE_TABLE);
	}

	private final String ADD_FOREIGN_KEY_CONSTRAINTS_ON_INCIDENCE_TABLE = "ALTER TABLE \"Incidence\" ADD CONSTRAINT \"gIdIsForeignKey\" FOREIGN KEY (\"gId\") REFERENCES \"Graph\" (\"gId\");"
			+ "ALTER TABLE \"Incidence\" ADD CONSTRAINT \"eIdIsForeignKey\" FOREIGN KEY (\"eId\", \"gId\") REFERENCES \"Edge\" (\"eId\", \"gId\");"
			+ "ALTER TABLE \"Incidence\" ADD CONSTRAINT \"vIdIsForeignKey\" FOREIGN KEY (\"vId\", \"gId\") REFERENCES \"Vertex\" (\"vId\", \"gId\");";

	@Override
	public PreparedStatement addForeignKeyConstraintsOnIncidenceTable()
			throws SQLException {
		return this.connection
				.prepareStatement(ADD_FOREIGN_KEY_CONSTRAINTS_ON_INCIDENCE_TABLE);
	}

	private final String DROP_FOREIGN_KEY_CONSTRAINTS_FROM_INCIDENCE_TABLE = "ALTER TABLE \"Incidence\" DROP CONSTRAINT \"gIdIsForeignKey\";"
			+ "ALTER TABLE \"Incidence\" DROP CONSTRAINT \"eIdIsForeignKey\";"
			+ "ALTER TABLE \"Incidence\" DROP CONSTRAINT \"vIdIsForeignKey\";";

	@Override
	public PreparedStatement dropForeignKeyConstraintsFromIncidenceTable()
			throws SQLException {
		return this.connection
				.prepareStatement(DROP_FOREIGN_KEY_CONSTRAINTS_FROM_INCIDENCE_TABLE);
	}

	private String CREATE_CLUSTERED_INDEX_ON_LAMBDA_SEQ = "CREATE INDEX \"lambdaSeqIndex\" ON \"Incidence\"( \"vId\", \"gId\", \"sequenceNumber\" ASC ) WITH (FILLFACTOR=80);"
			+ "ALTER TABLE \"Incidence\" CLUSTER ON \"lambdaSeqIndex\";"
			+ "ANALYZE \"Incidence\";";

	@Override
	public PreparedStatement addClusteredIndexOnLambdaSeq() throws SQLException {
		return this.getPreparedStatement(CREATE_CLUSTERED_INDEX_ON_LAMBDA_SEQ);
	}

	private final String CLUSTER_INCIDENCES = "CLUSTER \"Incidence\";";

	@Override
	public PreparedStatement clusterIncidences() throws SQLException {
		return this.getPreparedStatement(CLUSTER_INCIDENCES);
	}

	private final String CREATE_ATTRIBUTE_TABLE = "CREATE SEQUENCE \"attributeIdSequence\";"
			+ "CREATE TABLE \"Attribute\"("
			+ "\"attributeId\" INT4 PRIMARY KEY DEFAULT NEXTVAL('\"attributeIdSequence\"'),"
			+ "name TEXT,"
			+ "\"schemaId\" INT4 REFERENCES \"GraphSchema\""
			+ ");";

	@Override
	public PreparedStatement createAttributeTable() throws SQLException {
		return this.connection.prepareStatement(CREATE_ATTRIBUTE_TABLE);
	}

	private final String CREATE_GRAPH_ATTRIBUTE_VALUE_TABLE = "CREATE TABLE \"GraphAttributeValue\"("
			+ "\"gId\" INT4,"
			+ "\"attributeId\" INT4,"
			+ "\"value\" TEXT,"
			+ "CONSTRAINT \"gaPrimaryKey\" PRIMARY KEY ( \"gId\", \"attributeId\" )"
			+ ");";

	@Override
	public PreparedStatement createGraphAttributeValueTable()
			throws SQLException {
		return this.connection
				.prepareStatement(CREATE_GRAPH_ATTRIBUTE_VALUE_TABLE);
	}

	private final String CREATE_VERTEX_ATTRIBUTE_VALUE_TABLE = "CREATE TABLE \"VertexAttributeValue\"("
			+ "\"vId\" INT4,"
			+ "\"gId\" INT4,"
			+ "\"attributeId\" INT4,"
			+ "\"value\" TEXT" + ");";

	@Override
	public PreparedStatement createVertexAttributeValueTable()
			throws SQLException {
		return this.connection
				.prepareStatement(CREATE_VERTEX_ATTRIBUTE_VALUE_TABLE);
	}

	private final String ADD_PRIMARY_KEY_CONSTRAINT_ON_VERTEX_ATTRIBUTE_VALUE_TABLE = "ALTER TABLE \"VertexAttributeValue\" ADD CONSTRAINT \"vertexAttributeValuePrimaryKey\" PRIMARY KEY ( \"vId\", \"gId\", \"attributeId\" );";

	@Override
	public PreparedStatement addPrimaryKeyConstraintOnVertexAttributeValueTable()
			throws SQLException {
		return this.connection
				.prepareStatement(ADD_PRIMARY_KEY_CONSTRAINT_ON_VERTEX_ATTRIBUTE_VALUE_TABLE);
	}

	private final String DROP_PRIMARY_KEY_CONSTRAINT_FROM_VERTEX_ATTRIBUTE_VALUE_TABLE = "ALTER TABLE \"VertexAttributeValue\" DROP CONSTRAINT \"vertexAttributeValuePrimaryKey\";";

	@Override
	public PreparedStatement dropPrimaryKeyConstraintFromVertexAttributeValueTable()
			throws SQLException {
		return this.connection
				.prepareStatement(DROP_PRIMARY_KEY_CONSTRAINT_FROM_VERTEX_ATTRIBUTE_VALUE_TABLE);
	}

	private final String ADD_FOREIGN_KEY_CONSTRAINT_ON_VERTEX_ATTRIBUTE_VALUE_TABLE = "ALTER TABLE \"VertexAttributeValue\" ADD CONSTRAINT \"gIdIsForeignKey\" FOREIGN KEY (\"gId\") REFERENCES \"Graph\" (\"gId\");"
			+ "ALTER TABLE \"VertexAttributeValue\" ADD CONSTRAINT \"vIdIsForeignKey\" FOREIGN KEY (\"vId\", \"gId\") REFERENCES \"Vertex\" (\"vId\", \"gId\");"
			+ "ALTER TABLE \"VertexAttributeValue\" ADD CONSTRAINT \"attributeIdIsForeignKey\" FOREIGN KEY (\"attributeId\" ) REFERENCES \"Attribute\" (\"attributeId\");";

	@Override
	public PreparedStatement addForeignKeyConstraintsOnVertexAttributeValueTable()
			throws SQLException {
		return this.connection
				.prepareStatement(ADD_FOREIGN_KEY_CONSTRAINT_ON_VERTEX_ATTRIBUTE_VALUE_TABLE);
	}

	private final String DROP_FOREIGN_KEY_CONSTRAINTS_FROM_VERTEX_ATTRIBUTE_VALUE_TABLE = "ALTER TABLE \"VertexAttributeValue\" DROP CONSTRAINT \"gIdIsForeignKey\";"
			+ "ALTER TABLE \"VertexAttributeValue\" DROP CONSTRAINT \"vIdIsForeignKey\";"
			+ "ALTER TABLE \"VertexAttributeValue\" DROP CONSTRAINT \"attributeIdIsForeignKey\";";

	@Override
	public PreparedStatement dropForeignKeyConstraintsFromVertexAttributeValueTable()
			throws SQLException {
		return this.connection
				.prepareStatement(DROP_FOREIGN_KEY_CONSTRAINTS_FROM_VERTEX_ATTRIBUTE_VALUE_TABLE);
	}

	private final String CREATE_EDGE_ATTRIBUTE_VALUE_TABLE = "CREATE TABLE \"EdgeAttributeValue\"("
			+ "\"eId\" INT4,"
			+ "\"gId\" INT4,"
			+ "\"attributeId\" INT4,"
			+ "\"value\" TEXT" + ");";

	@Override
	public PreparedStatement createEdgeAttributeValueTable()
			throws SQLException {
		return this.connection
				.prepareStatement(CREATE_EDGE_ATTRIBUTE_VALUE_TABLE);
	}

	private final String ADD_PRIMARY_KEY_CONSTRAINT_ON_EDGE_ATTRIBUTE_VALUE_TABLE = "ALTER TABLE \"EdgeAttributeValue\" ADD CONSTRAINT \"edgeAttributeValuePrimaryKey\" PRIMARY KEY ( \"eId\", \"gId\", \"attributeId\" );";

	@Override
	public PreparedStatement addPrimaryKeyConstraintOnEdgeAttributeValueTable()
			throws SQLException {
		return this.connection
				.prepareStatement(ADD_PRIMARY_KEY_CONSTRAINT_ON_EDGE_ATTRIBUTE_VALUE_TABLE);
	}

	private final String DROP_PRIMARY_KEY_CONSTRAINT_FROM_EDGE_ATTRIBUTE_VALUE_TABLE = "ALTER TABLE \"EdgeAttributeValue\" DROP CONSTRAINT \"edgeAttributeValuePrimaryKey\";";

	@Override
	public PreparedStatement dropPrimaryKeyConstraintFromEdgeAttributeValueTable()
			throws SQLException {
		return this.connection
				.prepareStatement(DROP_PRIMARY_KEY_CONSTRAINT_FROM_EDGE_ATTRIBUTE_VALUE_TABLE);
	}

	private final String ADD_FOREIGN_KEY_CONSTRAINT_ON_EDGE_ATTRIBUTE_VALUE_TABLE = "ALTER TABLE \"EdgeAttributeValue\" ADD CONSTRAINT \"gIdIsForeignKey\" FOREIGN KEY (\"gId\") REFERENCES \"Graph\" (\"gId\");"
			+ "ALTER TABLE \"EdgeAttributeValue\" ADD CONSTRAINT \"eIdIsForeignKey\" FOREIGN KEY (\"eId\", \"gId\") REFERENCES \"Edge\" (\"eId\", \"gId\");"
			+ "ALTER TABLE \"EdgeAttributeValue\" ADD CONSTRAINT \"attributeIdIsForeignKey\" FOREIGN KEY (\"attributeId\" ) REFERENCES \"Attribute\" (\"attributeId\");";

	@Override
	public PreparedStatement addForeignKeyConstraintsOnEdgeAttributeValueTable()
			throws SQLException {
		return this.connection
				.prepareStatement(ADD_FOREIGN_KEY_CONSTRAINT_ON_EDGE_ATTRIBUTE_VALUE_TABLE);
	}

	private final String DROP_FOREIGN_KEY_CONSTRAINTS_FROM_EDGE_ATTRIBUTE_VALUE_TABLE = "ALTER TABLE \"EdgeAttributeValue\" DROP CONSTRAINT \"gIdIsForeignKey\";"
			+ "ALTER TABLE \"EdgeAttributeValue\" DROP CONSTRAINT \"eIdIsForeignKey\";"
			+ "ALTER TABLE \"EdgeAttributeValue\" DROP CONSTRAINT \"attributeIdIsForeignKey\";";

	@Override
	public PreparedStatement dropForeignKeyConstraintsFromEdgeAttributeValueTable()
			throws SQLException {
		return this.connection
				.prepareStatement(DROP_FOREIGN_KEY_CONSTRAINTS_FROM_EDGE_ATTRIBUTE_VALUE_TABLE);
	}

	private final String ADD_CLUSTERED_INDEX_ON_EDGE_ATTRIBUTE_VALUES = "CREATE INDEX \"edgeAttributeValueIndex\" ON \"EdgeAttributeValue\"( \"eId\" ASC, \"gId\" ASC, \"attributeId\" ) WITH (FILLFACTOR=80);"
			+ "ALTER TABLE \"EdgeAttributeValue\" CLUSTER ON \"edgeAttributeValueIndex\";"
			+ "ANALYZE \"EdgeAttributeValue\";";

	@Override
	public PreparedStatement addClusteredIndexOnEdgeAttributeValues()
			throws SQLException {
		return this
				.getPreparedStatement(ADD_CLUSTERED_INDEX_ON_EDGE_ATTRIBUTE_VALUES);
	}

	private final String ADD_CLUSTERED_INDEX_ON_VERTEX_ATTRIBUTE_VALUES = "CREATE INDEX \"vertexAttributeValueIndex\" ON \"VertexAttributeValue\"( \"vId\" ASC, \"gId\" ASC, \"attributeId\" ) WITH (FILLFACTOR=80);"
			+ "ALTER TABLE \"VertexAttributeValue\" CLUSTER ON \"vertexAttributeValueIndex\";"
			+ "ANALYZE \"VertexAttributeValue\";";

	@Override
	public PreparedStatement addClusteredIndexOnVertexAttributeValues()
			throws SQLException {
		return this
				.getPreparedStatement(ADD_CLUSTERED_INDEX_ON_VERTEX_ATTRIBUTE_VALUES);
	}

	private final String ADD_CLUSTERED_INDEX_ON_GRAPH_ATTRIBUTE_VALUES = "CREATE INDEX \"graphAttributeValueIndex\" ON \"GraphAttributeValue\"( \"gId\" ASC, \"attributeId\" ) WITH (FILLFACTOR=80);"
			+ "ALTER TABLE \"GraphAttributeValue\" CLUSTER ON \"graphAttributeValueIndex\";"
			+ "ANALYZE \"GraphAttributeValue\";";

	@Override
	public PreparedStatement addClusteredIndexOnGraphAttributeValues()
			throws SQLException {
		return this
				.getPreparedStatement(ADD_CLUSTERED_INDEX_ON_GRAPH_ATTRIBUTE_VALUES);
	}

	private final String DROP_CLUSTERED_INDICES_FROM_ATTRIBUTE_VALUES = "DROP INDEX IF EXISTS \"edgeAttributeValueIndex\";"
			+ "DROP INDEX IF EXISTS \"vertexAttributeValueIndex\";"
			+ "DROP INDEX IF EXISTS \"graphAttributeValueIndex\";";

	@Override
	public PreparedStatement dropClusteredIndicesOnAttributeValues()
			throws SQLException {
		return this
				.getPreparedStatement(DROP_CLUSTERED_INDICES_FROM_ATTRIBUTE_VALUES);
	}

	private final String CLUSTER_ATTRIBUTE_VALUES = "CLUSTER \"GraphAttributeValue\";"
			+ "CLUSTER \"VertexAttributeValue\";"
			+ "CLUSTER \"EdgeAttributeValue\";";

	@Override
	public PreparedStatement clusterAttributeValues() throws SQLException {
		return this.getPreparedStatement(CLUSTER_ATTRIBUTE_VALUES);
	}

	/*
	 * public void applyGenericDatabaseSchema() throws SQLException {
	 * this.connection.setAutoCommit(false);
	 * 
	 * PreparedStatement statement = this.createGraphSchemaTable();
	 * statement.execute();
	 * 
	 * statement = this.createTypeTable(); statement.execute();
	 * 
	 * statement = this.createGraphTable(); statement.execute(); statement =
	 * this.addForeignKeyConstraintsOnGraphTable(); statement.execute();
	 * 
	 * statement = this.createVertexTable(); statement.execute(); statement =
	 * this.addPrimaryKeyConstraintOnVertexTable(); statement.execute();
	 * statement = this.addForeignKeyConstraintsOnVertexTable();
	 * statement.execute();
	 * 
	 * statement = this.createEdgeTable(); statement.execute(); statement =
	 * this.addPrimaryKeyConstraintOnEdgeTable(); statement.execute(); statement
	 * = this.addForeignKeyConstraintsOnEdgeTable(); statement.execute();
	 * 
	 * statement = this.createIncidenceTable(); statement.execute(); statement =
	 * this.addPrimaryKeyConstraintOnIncidenceTable(); statement.execute();
	 * statement = this.addForeignKeyConstraintsOnIncidenceTable();
	 * statement.execute(); statement = this.createClusteredIndexOnLambdaSeq();
	 * statement.execute();
	 * 
	 * statement = this.createAttributeTable(); statement.execute();
	 * 
	 * statement = this.createGraphAttributeValueTable(); statement.execute();
	 * 
	 * statement = this.createVertexAttributeValueTable(); statement.execute();
	 * statement = this.addPrimaryKeyConstraintOnVertexAttributeValueTable();
	 * statement.execute(); statement =
	 * this.addForeignKeyConstraintsOnVertexAttributeValueTable();
	 * 
	 * statement = this.createEdgeAttributeValueTable(); statement.execute();
	 * 
	 * statement = this.addClusteredIndexOnGraphAttributeValues();
	 * statement.execute();
	 * 
	 * statement = this.addClusteredIndexOnVertexAttributeValues();
	 * statement.execute();
	 * 
	 * statement = this.addClusteredIndexOnEdgeAttributeValues();
	 * statement.execute();
	 * 
	 * this.createStoredProcedures();
	 * 
	 * this.connection.commit(); this.connection.setAutoCommit(true); }
	 */

	private String INSERT_SCHEMA = "INSERT INTO \"GraphSchema\" ( \"packagePrefix\", name, \"serializedDefinition\" ) VALUES ( ?, ?, ? )";

	@Override
	public PreparedStatement insertSchema(Schema schema,
			String serializedDefinition) throws SQLException {
		PreparedStatement statement = this.connection.prepareStatement(
				INSERT_SCHEMA, Statement.RETURN_GENERATED_KEYS);
		statement.setString(1, schema.getPackagePrefix());
		statement.setString(2, schema.getName());
		statement.setString(3, serializedDefinition);
		return statement;
	}

	private String INSERT_TYPE = "INSERT INTO \"Type\"( \"qualifiedName\", \"schemaId\" ) VALUES ( ?, ? )";

	@Override
	public PreparedStatement insertType(String qualifiedName, int schemaId)
			throws SQLException {
		PreparedStatement statement = this.getPreparedStatement(INSERT_TYPE);
		statement.setString(1, qualifiedName);
		statement.setInt(2, schemaId);
		return statement;
	}

	private String INSERT_ATTRIBUTE = "INSERT INTO \"Attribute\" ( name, \"schemaId\" ) VALUES ( ?, ? )";

	@Override
	public PreparedStatement insertAttribute(String name, int schemaId)
			throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(INSERT_ATTRIBUTE);
		statement.setString(1, name);
		statement.setInt(2, schemaId);
		return statement;
	}

	// --- to insert a graph ------------------------------------------

	private String INSERT_GRAPH = "INSERT INTO \"Graph\" ( uid, version, \"vSeqVersion\", \"eSeqVersion\", \"typeId\" ) VALUES ( ?, ?, ?, ?, ? )";

	@Override
	public PreparedStatement insertGraph(String id, long graphVersion,
			long vertexListVersion, long edgeListVersion, int typeId)
			throws SQLException {
		PreparedStatement statement = this.connection.prepareStatement(
				INSERT_GRAPH, Statement.RETURN_GENERATED_KEYS);
		statement.setString(1, id);
		statement.setLong(2, graphVersion);
		statement.setLong(3, vertexListVersion);
		statement.setLong(4, edgeListVersion);
		statement.setInt(5, typeId);
		return statement;
	}

	private String INSERT_GRAPH_ATTRIBUTE_VALUE = "INSERT INTO \"GraphAttributeValue\" ( \"gId\", \"attributeId\", value ) VALUES ( ?, ?, ? )";

	@Override
	public PreparedStatement insertGraphAttributeValue(int gId,
			int attributeId, String value) throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(INSERT_GRAPH_ATTRIBUTE_VALUE);
		statement.setInt(1, gId);
		statement.setInt(2, attributeId);
		statement.setString(3, value);
		return statement;
	}

	// --- to insert a vertex ------------------------------------------

	private String INSERT_VERTEX = "INSERT INTO \"Vertex\" ( \"vId\", \"gId\", \"typeId\", \"lambdaSeqVersion\", \"sequenceNumber\" ) VALUES (?, ?, ?, ?, ?);";

	@Override
	public PreparedStatement insertVertex(int vId, int typeId, int gId,
			long incidenceListVersion, long sequenceNumberInVSeq)
			throws SQLException {
		PreparedStatement statement = this.getPreparedStatement(INSERT_VERTEX);
		statement.setInt(1, vId);
		statement.setInt(2, gId);
		statement.setInt(3, typeId);
		statement.setLong(4, incidenceListVersion);
		statement.setLong(5, sequenceNumberInVSeq);
		return statement;
	}

	@Override
	public PreparedStatement insertVertex(DatabasePersistableVertex vertex)
			throws SQLException, GraphIOException {
		String sqlStatement = this.createSqlInsertStatementFor(vertex);
		PreparedStatement statement = this.getPreparedStatement(sqlStatement);
		this.setParametersForVertex(statement, vertex);
		this.setAttributeValuesForVertex(statement, vertex);
		return statement;
	}

	private void setAttributeValuesForVertex(PreparedStatement statement,
			DatabasePersistableVertex vertex) throws SQLException,
			GraphIOException {
		int i = 6;
		SortedSet<Attribute> attributes = vertex.getAttributedElementClass()
				.getAttributeList();
		for (Attribute attribute : attributes) {
			statement.setInt(i, vertex.getId());
			i++;
			statement.setInt(i, vertex.getGId());
			i++;
			int attributeId = this.graphDatabase.getAttributeId(
					(DatabasePersistableGraph) vertex.getGraph(), attribute
							.getName());
			statement.setInt(i, attributeId);
			i++;
			String value = this.graphDatabase.convertToString(vertex, attribute
					.getName());
			statement.setString(i, value);
			i++;
		}
	}

	private void setParametersForVertex(PreparedStatement statement,
			DatabasePersistableVertex vertex) throws SQLException {
		statement.setInt(1, vertex.getId());
		statement.setInt(2, vertex.getGId());
		int typeId = this.graphDatabase.getTypeIdOf(vertex);
		statement.setInt(3, typeId);
		statement.setLong(4, vertex.getIncidenceListVersion());
		statement.setLong(5, vertex.getSequenceNumberInVSeq());
	}

	private String createSqlInsertStatementFor(DatabasePersistableVertex vertex) {
		String sqlStatement = INSERT_VERTEX;
		int attributeCount = vertex.getAttributedElementClass()
				.getAttributeList().size();
		for (int i = 0; i < attributeCount; i++) {
			sqlStatement += INSERT_VERTEX_ATTRIBUTE_VALUE;
		}
		return sqlStatement;
	}

	private String INSERT_VERTEX_ATTRIBUTE_VALUE = "INSERT INTO \"VertexAttributeValue\" ( \"vId\", \"gId\", \"attributeId\", value ) VALUES ( ?, ?, ?, ? );";

	@Override
	public PreparedStatement insertVertexAttributeValue(int vId, int gId,
			int attributeId, String value) throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(INSERT_VERTEX_ATTRIBUTE_VALUE);
		statement.setInt(1, vId);
		statement.setInt(2, gId);
		statement.setInt(3, attributeId);
		statement.setString(4, value);
		return statement;
	}

	// --- to insert an edge -------------------------------------------

	private String INSERT_EDGE = "INSERT INTO \"Edge\" ( \"eId\", \"gId\", \"typeId\", \"sequenceNumber\" ) VALUES ( ?, ?, ?, ? );";

	@Override
	public PreparedStatement insertEdge(int eId, int gId, int typeId,
			long sequenceNumberInLambdaSeq) throws SQLException {
		PreparedStatement statement = this.getPreparedStatement(INSERT_EDGE);
		statement.setInt(1, Math.abs(eId));
		statement.setInt(2, gId);
		statement.setInt(3, typeId);
		statement.setLong(4, sequenceNumberInLambdaSeq);
		return statement;
	}

	@Override
	public PreparedStatement insertEdge(DatabasePersistableEdge edge,
			DatabasePersistableVertex alpha, DatabasePersistableVertex omega)
			throws SQLException, GraphIOException {
		String sqlStatement = this.createSqlInsertStatementFor(edge);
		PreparedStatement statement = this.getPreparedStatement(sqlStatement);
		this.setParametersForEdge(statement, edge);

		// insert incidence: normal edge
		statement.setInt(5, edge.getId());
		statement.setInt(6, edge.getGId());
		statement.setInt(7, edge.getIncidentVId());
		statement.setString(8, EdgeDirection.OUT.name());
		statement.setLong(9, edge.getSequenceNumberInLambdaSeq());

		// insert incidence: reversed edge
		DatabasePersistableEdge reversedEdge = (DatabasePersistableEdge) edge
				.getReversedEdge();
		statement.setInt(10, Math.abs(reversedEdge.getId()));
		statement.setInt(11, reversedEdge.getGId());
		statement.setInt(12, reversedEdge.getIncidentVId());
		statement.setString(13, EdgeDirection.IN.name());
		statement.setLong(14, reversedEdge.getSequenceNumberInLambdaSeq());

		// insert attribute values
		int i = 15;
		SortedSet<Attribute> attributes = edge.getAttributedElementClass()
				.getAttributeList();
		for (Attribute attribute : attributes) {
			statement.setInt(i, edge.getId());
			i++;
			statement.setInt(i, edge.getGId());
			i++;
			int attributeId = this.graphDatabase.getAttributeId(
					(DatabasePersistableGraph) edge.getGraph(), attribute
							.getName());
			statement.setInt(i, attributeId);
			i++;
			String value = this.graphDatabase.convertToString(edge, attribute
					.getName());
			statement.setString(i, value);
			i++;
		}

		// update incidence list version of alpha
		statement.setLong(i, alpha.getIncidenceListVersion());
		i++;
		statement.setInt(i, alpha.getId());
		i++;
		statement.setInt(i, alpha.getGId());
		i++;

		// update incidence list version of omega
		statement.setLong(i, omega.getIncidenceListVersion());
		i++;
		statement.setInt(i, omega.getId());
		i++;
		statement.setInt(i, omega.getGId());

		return statement;
	}

	private void setParametersForEdge(PreparedStatement statement,
			DatabasePersistableEdge edge) throws SQLException {
		statement.setInt(1, Math.abs(edge.getId()));
		statement.setInt(2, edge.getGId());
		int typeId = this.graphDatabase.getTypeIdOf(edge);
		statement.setInt(3, typeId);
		statement.setLong(4, edge.getSequenceNumberInESeq());
	}

	private String createSqlInsertStatementFor(DatabasePersistableEdge edge) {
		String sqlStatement = INSERT_EDGE;
		sqlStatement += INSERT_INCIDENCE;
		sqlStatement += INSERT_INCIDENCE;
		int attributeCount = edge.getAttributedElementClass()
				.getAttributeList().size();
		for (int i = 0; i < attributeCount; i++) {
			sqlStatement += INSERT_EDGE_ATTRIBUTE_VALUE;
		}
		sqlStatement += UPDATE_INCIDENCE_LIST_VERSION;
		sqlStatement += UPDATE_INCIDENCE_LIST_VERSION;
		return sqlStatement;
	}

	private String INSERT_INCIDENCE = "INSERT INTO \"Incidence\" ( \"eId\", \"gId\", \"vId\", direction, \"sequenceNumber\" ) VALUES ( ?, ?, ?, ?::\"DIRECTION\", ? );";

	@Override
	public PreparedStatement insertIncidence(int eId, int vId, int gId,
			long sequenceNumberInLambdaSeq) throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(INSERT_INCIDENCE);
		statement.setInt(1, Math.abs(eId));
		statement.setInt(2, gId);
		statement.setInt(3, vId);
		if (eId > 0) {
			statement.setString(4, EdgeDirection.OUT.name());
		} else if (eId < 0) {
			statement.setString(4, EdgeDirection.IN.name());
		} else {
			throw new GraphException(
					"Cannot insert an incidence into database with incident edge id = 0.");
		}
		statement.setLong(5, sequenceNumberInLambdaSeq);
		return statement;
	}

	private String INSERT_EDGE_ATTRIBUTE_VALUE = "INSERT INTO \"EdgeAttributeValue\" ( \"eId\", \"gId\", \"attributeId\", value ) VALUES ( ?, ?, ?, ? );";

	@Override
	public PreparedStatement insertEdgeAttributeValue(int eId, int gId,
			int attributeId, String value) throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(INSERT_EDGE_ATTRIBUTE_VALUE);
		statement.setInt(1, eId);
		statement.setInt(2, gId);
		statement.setInt(3, attributeId);
		statement.setString(4, value);
		return statement;
	}

	// --- to open a graph schema -------------------------------------------

	private String SELECT_SCHEMA_ID = "SELECT \"schemaId\" FROM \"GraphSchema\" WHERE \"packagePrefix\" = ? AND name = ?";

	@Override
	public PreparedStatement selectSchemaId(String packagePrefix, String name)
			throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(SELECT_SCHEMA_ID);
		statement.setString(1, packagePrefix);
		statement.setString(2, name);
		return statement;
	}

	private String SELECT_SCHEMA_DEFINITION_FOR_GRAPH = "SELECT \"serializedDefinition\" FROM \"GraphSchema\" WHERE \"schemaId\" = ("
			+ "SELECT \"schemaId\" FROM \"Type\" WHERE \"typeId\" = ("
			+ "SELECT \"typeId\" FROM \"Graph\" WHERE uid = ?" + ")" + ")";

	@Override
	public PreparedStatement selectSchemaDefinitionForGraph(String uid)
			throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(SELECT_SCHEMA_DEFINITION_FOR_GRAPH);
		statement.setString(1, uid);
		return statement;
	}

	private String SELECT_SCHEMA_NAME = "SELECT \"packagePrefix\", name FROM \"GraphSchema\" WHERE \"schemaId\" = ("
			+ "SELECT \"schemaId\" FROM \"Type\" WHERE \"typeId\" = ("
			+ "SELECT \"typeId\" FROM \"Graph\" WHERE uid = ?" + ")" + ")";

	@Override
	public PreparedStatement selectSchemaNameForGraph(String uid)
			throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(SELECT_SCHEMA_NAME);
		statement.setString(1, uid);
		return statement;
	}

	private String SELECT_TYPES = "SELECT \"qualifiedName\", \"typeId\" FROM \"Type\" WHERE \"schemaId\" = "
			+ "(SELECT \"schemaId\" FROM \"GraphSchema\" WHERE \"packagePrefix\" = ? AND name = ?)";

	@Override
	public PreparedStatement selectTypesOfSchema(String packagePrefix,
			String name) throws SQLException {
		PreparedStatement statement = this.getPreparedStatement(SELECT_TYPES);
		statement.setString(1, packagePrefix);
		statement.setString(2, name);
		return statement;
	}

	private String SELECT_ATTRIBUTES = "SELECT name, \"attributeId\" FROM \"Attribute\" WHERE \"schemaId\" = "
			+ "(SELECT \"schemaId\" FROM \"GraphSchema\" WHERE \"packagePrefix\" = ? AND name = ?)";

	@Override
	public PreparedStatement selectAttributesOfSchema(String packagePrefix,
			String name) throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(SELECT_ATTRIBUTES);
		statement.setString(1, packagePrefix);
		statement.setString(2, name);
		return statement;
	}

	// --- to open a graph --------------------------------------------

	private String SELECT_GRAPH = "SELECT \"gId\", version, \"vSeqVersion\", \"eSeqVersion\" FROM \"Graph\" WHERE uid = ?";

	@Override
	public PreparedStatement selectGraph(String id) throws SQLException {
		PreparedStatement statement = this.getPreparedStatement(SELECT_GRAPH);
		statement.setString(1, id);
		return statement;
	}

	private String COUNT_VERTICES_IN_GRAPH = "SELECT COUNT (*) FROM \"Vertex\" WHERE \"gId\" = ?";

	@Override
	public PreparedStatement countVerticesOfGraph(int gId) throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(COUNT_VERTICES_IN_GRAPH);
		statement.setInt(1, gId);
		return statement;
	}

	private String COUNT_EDGES_IN_GRAPH = "SELECT COUNT (*) FROM \"Edge\" WHERE \"gId\" = ?";

	@Override
	public PreparedStatement countEdgesOfGraph(int gId) throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(COUNT_EDGES_IN_GRAPH);
		statement.setInt(1, gId);
		return statement;
	}

	private String SELECT_VERTICES = "SELECT \"vId\", \"sequenceNumber\"  FROM \"Vertex\" WHERE \"gId\" = ? ORDER BY \"sequenceNumber\" ASC";

	@Override
	public PreparedStatement selectVerticesOfGraph(int gId) throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(SELECT_VERTICES);
		statement.setInt(1, gId);
		return statement;
	}

	private String SELECT_EDGES = "SELECT \"eId\", \"sequenceNumber\"  FROM \"Edge\" WHERE \"gId\" = ? ORDER BY \"sequenceNumber\" ASC";

	@Override
	public PreparedStatement selectEdgesOfGraph(int gId) throws SQLException {
		PreparedStatement statement = this.getPreparedStatement(SELECT_EDGES);
		statement.setInt(1, gId);
		return statement;
	}

	private String SELECT_ATTRIBUTE_VALUES_OF_GRAPH = "SELECT name, \"value\" FROM \"GraphAttributeValue\" JOIN \"Attribute\" ON \"GraphAttributeValue\".\"attributeId\" = \"Attribute\".\"attributeId\" WHERE \"gId\" = ?";

	@Override
	public PreparedStatement selectAttributeValuesOfGraph(int gId)
			throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(SELECT_ATTRIBUTE_VALUES_OF_GRAPH);
		statement.setInt(1, gId);
		return statement;
	}

	// --- to get a vertex -------------------------------------------

	private String SELECT_VERTEX_WITH_INCIDENCES = "SELECT \"typeId\", \"lambdaSeqVersion\", \"Vertex\".\"sequenceNumber\", \"Incidence\".\"sequenceNumber\", direction, \"eId\" FROM"
			+ "\"Vertex\" LEFT OUTER JOIN \"Incidence\" ON ( \"Vertex\".\"vId\" = \"Incidence\".\"vId\" AND \"Vertex\".\"gId\" = \"Incidence\".\"gId\" )"
			+ "WHERE \"Vertex\".\"vId\" = ? AND \"Vertex\".\"gId\" = ?"
			+ "ORDER BY \"Incidence\".\"sequenceNumber\" ASC";

	@Override
	public PreparedStatement selectVertexWithIncidences(int vId, int gId)
			throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(SELECT_VERTEX_WITH_INCIDENCES);
		statement.setInt(1, vId);
		statement.setInt(2, gId);
		return statement;
	}

	private String SELECT_ATTRIBUTE_VALUES_OF_VERTEX = "SELECT \"attributeId\", \"value\" FROM \"VertexAttributeValue\" WHERE \"vId\" = ? AND \"gId\" = ?";

	@Override
	public PreparedStatement selectAttributeValuesOfVertex(int vId, int gId)
			throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(SELECT_ATTRIBUTE_VALUES_OF_VERTEX);
		statement.setInt(1, vId);
		statement.setInt(2, gId);
		return statement;
	}

	// --- to get an edge --------------------------------------------

	private String SELECT_EDGE_WITH_INCIDENCES = "SELECT \"typeId\", \"Edge\".\"sequenceNumber\", direction, \"vId\", \"Incidence\".\"sequenceNumber\" FROM"
			+ "\"Edge\" INNER JOIN \"Incidence\" ON ( \"Edge\".\"eId\" = \"Incidence\".\"eId\" AND \"Edge\".\"gId\" = \"Incidence\".\"gId\" )"
			+ "WHERE \"Edge\".\"eId\" = ? AND \"Edge\".\"gId\" = ?";

	@Override
	public PreparedStatement selectEdgeWithIncidences(int eId, int gId)
			throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(SELECT_EDGE_WITH_INCIDENCES);
		statement.setInt(1, eId);
		statement.setInt(2, gId);
		return statement;
	}

	private String SELECT_ATTRIBUTE_VALUES_OF_EDGE = "SELECT \"attributeId\", \"value\" FROM \"EdgeAttributeValue\" WHERE \"eId\" = ? AND \"gId\" = ?";

	@Override
	public PreparedStatement selectAttributeValuesOfEdge(int eId, int gId)
			throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(SELECT_ATTRIBUTE_VALUES_OF_EDGE);
		statement.setInt(1, eId);
		statement.setInt(2, gId);
		return statement;
	}

	// --- to delete a graph ------------------------------------------

	private String DELETE_ATTRIBUTE_VALUES_OF_GRAPH = "DELETE FROM \"GraphAttributeValue\" WHERE \"gId\" = ?";

	@Override
	public PreparedStatement deleteAttributeValuesOfGraph(int gId)
			throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(DELETE_ATTRIBUTE_VALUES_OF_GRAPH);
		statement.setInt(1, gId);
		return statement;
	}

	private String DELETE_EDGE_ATTRIBUTE_VALUES_OF_GRAPH = "DELETE FROM \"EdgeAttributeValue\" WHERE \"gId\" = ?";

	@Override
	public PreparedStatement deleteEdgeAttributeValuesOfGraph(int gId)
			throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(DELETE_EDGE_ATTRIBUTE_VALUES_OF_GRAPH);
		statement.setInt(1, gId);
		return statement;
	}

	private String DELETE_VERTEX_ATTRIBUTE_VALUES_OF_GRAPH = "DELETE FROM \"VertexAttributeValue\" WHERE \"gId\" = ?";

	@Override
	public PreparedStatement deleteVertexAttributeValuesOfGraph(int gId)
			throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(DELETE_VERTEX_ATTRIBUTE_VALUES_OF_GRAPH);
		statement.setInt(1, gId);
		return statement;
	}

	private String DELETE_INCIDENCES_OF_GRAPH = "DELETE FROM \"Incidence\" WHERE \"gId\" = ?";

	@Override
	public PreparedStatement deleteIncidencesOfGraph(int gId)
			throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(DELETE_INCIDENCES_OF_GRAPH);
		statement.setInt(1, gId);
		return statement;
	}

	private String DELETE_VERTICES_OF_GRAPH = "DELETE FROM \"Vertex\" WHERE \"gId\" = ?";

	@Override
	public PreparedStatement deleteVerticesOfGraph(int gId) throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(DELETE_VERTICES_OF_GRAPH);
		statement.setInt(1, gId);
		return statement;
	}

	private String DELETE_EDGES_OF_GRAPH = ""
			+ "DELETE FROM \"Edge\" WHERE \"gId\" = ?";

	@Override
	public PreparedStatement deleteEdgesOfGraph(int gId) throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(DELETE_EDGES_OF_GRAPH);
		statement.setInt(1, gId);
		return statement;
	}

	private String DELETE_GRAPH = "DELETE FROM \"Graph\" WHERE \"gId\" = ?";

	@Override
	public PreparedStatement deleteGraph(int gId) throws SQLException {
		PreparedStatement statement = this.getPreparedStatement(DELETE_GRAPH);
		statement.setInt(1, gId);
		return statement;
	}

	// --- to delete a vertex -----------------------------------------

	private String DELETE_ATTRIBUTE_VALUES_OF_VERTEX = "DELETE FROM \"VertexAttributeValue\" WHERE \"vId\" = ? AND \"gId\" = ?";

	@Override
	public PreparedStatement deleteAttributeValuesOfVertex(int vId, int gId)
			throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(DELETE_ATTRIBUTE_VALUES_OF_VERTEX);
		statement.setInt(1, vId);
		statement.setInt(2, gId);
		return statement;
	}

	private String SELECT_ID_OF_INCIDENT_EDGES_OF_VERTEX = "SELECT \"eId\" FROM \"Incidence\" WHERE \"vId\" = ? AND \"gId\" = ?";

	@Override
	public PreparedStatement selectIncidentEIdsOfVertex(int vId, int gId)
			throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(SELECT_ID_OF_INCIDENT_EDGES_OF_VERTEX);
		statement.setInt(1, vId);
		statement.setInt(2, gId);
		return statement;
	}

	private String DELETE_VERTEX = "DELETE FROM \"Vertex\" WHERE \"vId\" = ? AND \"gId\" = ?";

	@Override
	public PreparedStatement deleteVertex(int vId, int gId) throws SQLException {
		PreparedStatement statement = this.getPreparedStatement(DELETE_VERTEX);
		statement.setInt(1, vId);
		statement.setInt(2, gId);
		return statement;
	}

	// --- to delete an edge ------------------------------------------

	private String DELETE_ATTRIBUTE_VALUES_OF_EDGE = "DELETE FROM \"EdgeAttributeValue\" WHERE \"eId\" = ? AND \"gId\" = ?";

	@Override
	public PreparedStatement deleteAttributeValuesOfEdge(int eId, int gId)
			throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(DELETE_ATTRIBUTE_VALUES_OF_EDGE);
		statement.setInt(1, eId);
		statement.setInt(2, gId);
		return statement;
	}

	private String DELETE_INCIDENCES_OF_EDGE = "DELETE FROM \"Incidence\" WHERE \"eId\" = ? AND \"gId\" = ?";

	@Override
	public PreparedStatement deleteIncidencesOfEdge(int eId, int gId)
			throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(DELETE_INCIDENCES_OF_EDGE);
		statement.setInt(1, eId);
		statement.setInt(2, gId);
		return statement;
	}

	private String DELETE_EDGE = "DELETE FROM \"Edge\" WHERE \"eId\" = ? AND \"gId\" = ?";

	@Override
	public PreparedStatement deleteEdge(int eId, int gId) throws SQLException {
		PreparedStatement statement = this.getPreparedStatement(DELETE_EDGE);
		statement.setInt(1, eId);
		statement.setInt(2, gId);
		return statement;
	}

	// --- to update a graph ------------------------------------------

	private String UPDATE_ATTRIBUTE_VALUE_OF_GRAPH = "UPDATE \"GraphAttributeValue\" SET value = ? WHERE \"gId\" = ? AND \"attributeId\" = ?";

	@Override
	public PreparedStatement updateAttributeValueOfGraph(int gId,
			int attributeId, String serializedValue) throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(UPDATE_ATTRIBUTE_VALUE_OF_GRAPH);
		statement.setString(1, serializedValue);
		statement.setInt(2, gId);
		statement.setInt(3, attributeId);
		return statement;
	}

	private String UPDATE_ATTRIBUTE_VALUE_OF_GRAPH_AND_GRAPH_VERSION = "UPDATE \"GraphAttributeValue\" SET value = ? WHERE \"gId\" = ? AND \"attributeId\" = ?;"
			+ "UPDATE \"Graph\" SET version = ? WHERE \"gId\" = ?";

	@Override
	public PreparedStatement updateAttributeValueOfGraphAndGraphVersion(
			int gId, int attributeId, String serializedValue, long graphVersion)
			throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(UPDATE_ATTRIBUTE_VALUE_OF_GRAPH_AND_GRAPH_VERSION);
		statement.setString(1, serializedValue);
		statement.setInt(2, gId);
		statement.setInt(3, attributeId);
		statement.setLong(4, graphVersion);
		statement.setInt(5, gId);
		return statement;
	}

	private String UPDATE_GRAPH_UID = "UPDATE \"Graph\" SET uid = ? WHERE \"gId\" = ?";

	@Override
	public PreparedStatement updateGraphId(int gId, String uid)
			throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(UPDATE_GRAPH_UID);
		statement.setString(1, uid);
		statement.setInt(2, gId);
		return statement;
	}

	private String UPDATE_GRAPH_VERSION = "UPDATE \"Graph\" SET version = ? WHERE \"gId\" = ?";

	@Override
	public PreparedStatement updateGraphVersion(int gId, long version)
			throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(UPDATE_GRAPH_VERSION);
		statement.setLong(1, version);
		statement.setInt(2, gId);
		return statement;
	}

	private String UPDATE_VERTEX_LIST_VERSION = "UPDATE \"Graph\" SET \"vSeqVersion\" = ? WHERE \"gId\" = ?";

	@Override
	public PreparedStatement updateVertexListVersionOfGraph(int gId,
			long version) throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(UPDATE_VERTEX_LIST_VERSION);
		statement.setLong(1, version);
		statement.setInt(2, gId);
		return statement;
	}

	private String UPDATE_EDGE_LIST_VERSION = "UPDATE \"Graph\" SET \"eSeqVersion\" = ? WHERE \"gId\" = ?";

	@Override
	public PreparedStatement updateEdgeListVersionOfGraph(int gId, long version)
			throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(UPDATE_EDGE_LIST_VERSION);
		statement.setLong(1, version);
		statement.setInt(2, gId);
		return statement;
	}

	// --- to update a vertex -----------------------------------------

	private String UPDATE_VERTEX_ID = "UPDATE \"Vertex\" SET \"vId\" = ? WHERE \"vId\" = ? AND \"gId\" = ?";

	@Override
	public PreparedStatement updateIdOfVertex(int oldVId, int gId, int newVId)
			throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(UPDATE_VERTEX_ID);
		statement.setInt(1, newVId);
		statement.setInt(2, oldVId);
		statement.setInt(3, gId);
		return statement;
	}

	private String UPDATE_SEQUENCE_NUMBER_OF_VERTEX = "UPDATE \"Vertex\" SET \"sequenceNumber\" = ? WHERE \"vId\" = ? AND \"gId\" = ?";

	@Override
	public PreparedStatement updateSequenceNumberInVSeqOfVertex(int vId,
			int gId, long sequenceNumberInVSeq) throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(UPDATE_SEQUENCE_NUMBER_OF_VERTEX);
		statement.setLong(1, sequenceNumberInVSeq);
		statement.setInt(2, vId);
		statement.setInt(3, gId);
		return statement;
	}

	private String UPDATE_ATTRIBUTE_VALUE_OF_VERTEX = "UPDATE \"VertexAttributeValue\" SET value = ? WHERE \"vId\" = ? AND \"gId\" = ? AND \"attributeId\" = ?";

	@Override
	public PreparedStatement updateAttributeValueOfVertex(int vId, int gId,
			int attributeId, String serializedValue) throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(UPDATE_ATTRIBUTE_VALUE_OF_VERTEX);
		statement.setString(1, serializedValue);
		statement.setInt(2, vId);
		statement.setInt(3, gId);
		statement.setInt(4, attributeId);
		return statement;
	}

	private String UPDATE_ATTRIBUTE_VALUE_OF_VERTEX_AND_GRAPH_VERSION = "UPDATE \"VertexAttributeValue\" SET value = ? WHERE \"vId\" = ? AND \"gId\" = ? AND \"attributeId\" = ?;"
			+ "UPDATE \"Graph\" SET version = ? WHERE \"gId\" = ?";

	@Override
	public PreparedStatement updateAttributeValueOfVertexAndGraphVersion(
			int vId, int gId, int attributeId, String serializedValue,
			long graphVersion) throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(UPDATE_ATTRIBUTE_VALUE_OF_VERTEX_AND_GRAPH_VERSION);
		statement.setString(1, serializedValue);
		statement.setInt(2, vId);
		statement.setInt(3, gId);
		statement.setInt(4, attributeId);
		statement.setLong(5, graphVersion);
		statement.setInt(6, gId);
		return statement;
	}

	private String UPDATE_INCIDENCE_LIST_VERSION = "UPDATE \"Vertex\" SET \"lambdaSeqVersion\" = ? WHERE \"vId\" = ? AND \"gId\" = ?;";

	@Override
	public PreparedStatement updateLambdaSeqVersionOfVertex(int vId, int gId,
			long lambdaSeqVersion) throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(UPDATE_INCIDENCE_LIST_VERSION);
		statement.setLong(1, lambdaSeqVersion);
		statement.setInt(2, vId);
		statement.setInt(3, gId);
		return statement;
	}

	// --- to update an edge ------------------------------------------

	private String UPDATE_EDGE_ID = "UPDATE \"Edge\" SET \"eId\" = ? WHERE \"eId\" = ? AND \"gId\" = ?";

	@Override
	public PreparedStatement updateIdOfEdge(int oldEId, int gId, int newEId)
			throws SQLException {
		PreparedStatement statement = this.getPreparedStatement(UPDATE_EDGE_ID);
		statement.setInt(1, newEId);
		statement.setInt(2, oldEId);
		statement.setInt(3, gId);
		return statement;
	}

	private String UPDATE_SEQUENCE_NUMBER_IN_EDGE_LIST = "UPDATE \"Edge\" SET \"sequenceNumber\" = ? WHERE \"eId\" = ? AND \"gId\" = ?";

	@Override
	public PreparedStatement updateSequenceNumberInESeqOfEdge(int eId, int gId,
			long SequenceNumberInESeq) throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(UPDATE_SEQUENCE_NUMBER_IN_EDGE_LIST);
		statement.setLong(1, SequenceNumberInESeq);
		statement.setInt(2, eId);
		statement.setInt(3, gId);
		return statement;
	}

	private String UPDATE_ATTRIBUTE_VALUE_OF_EDGE = "UPDATE \"EdgeAttributeValue\" SET value = ? WHERE \"eId\" = ? AND \"gId\" = ? AND \"attributeId\" = ?";

	@Override
	public PreparedStatement updateAttributeValueOfEdge(int eId, int gId,
			int attributeId, String serializedValue) throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(UPDATE_ATTRIBUTE_VALUE_OF_EDGE);
		statement.setString(1, serializedValue);
		statement.setInt(2, eId);
		statement.setInt(3, gId);
		statement.setInt(4, attributeId);
		return statement;
	}

	private String UPDATE_ATTRIBUTE_VALUE_OF_EDGE_AND_INCREMENT_GRAPH_VERSION = "UPDATE \"EdgeAttributeValue\" SET value = ? WHERE \"eId\" = ? AND \"gId\" = ? AND \"attributeId\" = ?;"
			+ "UPDATE \"Graph\" SET version = ? WHERE \"gId\" = ?;";

	@Override
	public PreparedStatement updateAttributeValueOfEdgeAndGraphVersion(int eId,
			int gId, int attributeId, String serializedValue, long graphVersion)
			throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(UPDATE_ATTRIBUTE_VALUE_OF_EDGE_AND_INCREMENT_GRAPH_VERSION);
		statement.setString(1, serializedValue);
		statement.setInt(2, eId);
		statement.setInt(3, gId);
		statement.setInt(4, attributeId);
		statement.setLong(5, graphVersion);
		statement.setInt(6, gId);
		return statement;
	}

	private String UPDATE_INCIDENT_VERTEX = "UPDATE \"Incidence\" SET \"vId\" = ? WHERE \"eId\" = ? AND \"gId\" = ? AND direction = ?::\"DIRECTION\"";

	@Override
	public PreparedStatement updateIncidentVIdOfIncidence(int eId, int vId,
			int gId) throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(UPDATE_INCIDENT_VERTEX);
		statement.setInt(1, vId);
		statement.setInt(2, Math.abs(eId));
		statement.setInt(3, gId);
		if (eId > 0) {
			statement.setString(4, EdgeDirection.OUT.name());
		} else if (eId < 0) {
			statement.setString(4, EdgeDirection.IN.name());
		}
		return statement;
	}

	private String UPDATE_SEQUENCE_NUMBER_IN_INCIDENCE_LIST = "UPDATE \"Incidence\" SET \"sequenceNumber\" = ? WHERE \"eId\" = ? AND \"gId\" = ? AND \"vId\" = ?";

	@Override
	public PreparedStatement updateSequenceNumberInLambdaSeqOfIncidence(
			int eId, int vId, int gId, long sequenceNumberInLambdaSeq)
			throws SQLException {
		PreparedStatement statement = this
				.getPreparedStatement(UPDATE_SEQUENCE_NUMBER_IN_INCIDENCE_LIST);
		statement.setLong(1, sequenceNumberInLambdaSeq);
		statement.setInt(2, Math.abs(eId));
		statement.setInt(3, gId);
		statement.setInt(4, vId);
		return statement;
	}

	private String STORED_PROCEDURE_REORGANIZE_VERTEX_LIST = "CREATE FUNCTION \"reorganizeVertexListOf\"(\"graphId\" INT, start BIGINT) RETURNS INT AS $$\n"
			+ "DECLARE\n"
			+ "distance BIGINT := 4294967296;\n"
			+ "current BIGINT := start;\n"
			+ "vertex RECORD;\n"
			+ "BEGIN\n"
			+ "FOR vertex IN (SELECT \"vId\" FROM \"Vertex\" WHERE \"gId\" = \"graphId\" ORDER BY \"sequenceNumber\" ASC) LOOP\n"
			+ "EXECUTE 'UPDATE \"Vertex\" SET \"sequenceNumber\" = $1 WHERE \"vId\" = $2 AND \"gId\" =$3' USING current, vertex.\"vId\", \"graphId\";\n"
			+ "current := current + distance;\n"
			+ "END LOOP;\n"
			+ "RETURN 1;\n" + "END;\n" + "$$LANGUAGE plpgsql;";

	@Override
	public PreparedStatement createStoredProcedureToReorganizeVertexList()
			throws SQLException {
		return this
				.getPreparedStatement(STORED_PROCEDURE_REORGANIZE_VERTEX_LIST);
	}

	private String STORED_PROCEDURE_REORGANIZE_EDGE_LIST = "CREATE FUNCTION \"reorganizeEdgeListOf\"(\"graphId\" INT, start BIGINT) RETURNS INT AS $$\n"
			+ "DECLARE\n"
			+ "distance BIGINT := 4294967296;\n"
			+ "current BIGINT := start;\n"
			+ "edge RECORD;\n"
			+ "BEGIN\n"
			+ "FOR edge IN (SELECT \"eId\" FROM \"Edge\" WHERE \"gId\" = \"graphId\" ORDER BY \"sequenceNumber\" ASC) LOOP\n"
			+ "EXECUTE 'UPDATE \"Edge\" SET \"sequenceNumber\" = $1 WHERE \"eId\" = $2 AND \"gId\" = $3' USING current, edge.\"eId\", \"graphId\";\n"
			+ "current := current + distance;\n"
			+ "END LOOP;\n"
			+ "RETURN 1;\n" + "END;\n" + "$$ LANGUAGE plpgsql;";

	@Override
	public PreparedStatement createStoredProcedureToReorganizeEdgeList()
			throws SQLException {
		return this.getPreparedStatement(STORED_PROCEDURE_REORGANIZE_EDGE_LIST);
	}

	private String STORED_PROCEDURE_REORGANIZE_INCIDENCE_LIST = "CREATE FUNCTION \"reorganizeIncidenceListOf\"(\"vertexId\" INT, \"graphId\" INT, start BIGINT) RETURNS INT AS $$\n"
			+ "DECLARE\n"
			+ "distance BIGINT := 4294967296;\n"
			+ "current BIGINT := start;\n"
			+ "incidence RECORD;\n"
			+ "BEGIN\n"
			+ "FOR incidence IN (SELECT \"eId\" FROM \"Incidence\" WHERE \"vId\" = \"vertexId\" AND \"gId\" = \"graphId\" ORDER BY \"sequenceNumber\" ASC) LOOP\n"
			+ "EXECUTE 'UPDATE \"Incidence\" SET \"sequenceNumber\" = $1 WHERE \"vId\" = $2 AND \"gId\" = $3 AND \"eId\" = $4' USING current, \"vertexId\", \"graphId\", incidence.\"eId\";\n"
			+ "current := current + distance;\n"
			+ "END LOOP;\n"
			+ "RETURN 1;\n" + "END;\n" + "$$ LANGUAGE plpgsql;";

	@Override
	public PreparedStatement createStoredProcedureToReorganizeIncidenceList()
			throws SQLException {
		return this
				.getPreparedStatement(STORED_PROCEDURE_REORGANIZE_INCIDENCE_LIST);
	}

	private String STORED_PROCEDURE_INSERT_VERTEX = "CREATE FUNCTION \"insertVertex\"(\"vertexId\" INT, \"graphId\" INT, \"typeId\" INT, \"sequenceNumber\" BIGINT) RETURNS INT AS $$\n"
			+ "BEGIN\n"
			+ "EXECUTE 'INSERT INTO \"Vertex\" ( \"vId\", \"gId\", \"typeId\", \"lambdaSeqVersion\", \"sequenceNumber\" ) VALUES ($1, $2, $3, 0, $4)' USING \"vertexId\", \"graphId\", \"typeId\", \"sequenceNumber\";\n"
			+ "RETURN 1;\n" + "END;\n" + "$$ LANGUAGE plpgsql;";

	@Override
	public PreparedStatement createStoredProcedureToInsertVertex()
			throws SQLException {
		return this.getPreparedStatement(STORED_PROCEDURE_INSERT_VERTEX);
	}

	private String DELETE_SCHEMA = "DELETE FROM \"GraphSchema\" WHERE \"packagePrefix\" = ? AND name = ?";

	@Override
	public PreparedStatement deleteSchema(String prefix, String name)
			throws SQLException {
		PreparedStatement statement = this.getPreparedStatement(DELETE_SCHEMA);
		statement.setString(1, prefix);
		statement.setString(2, name);
		return statement;
	}

	private String SELECT_SCHEMA_DEFINITION = "SELECT \"serializedDefinition\" FROM \"GraphSchema\" WHERE \"packagePrefix\" = ? AND name = ?;";

	@Override
	public PreparedStatement selectSchemaDefinition(String packagePrefix,
			String schemaName) throws SQLException {
		PreparedStatement statement = this.connection
				.prepareStatement(SELECT_SCHEMA_DEFINITION);
		statement.setString(1, packagePrefix);
		statement.setString(2, schemaName);
		return statement;
	}

	@Override
	public CallableStatement createReorganizeVertexListCall(int gId, long start)
			throws SQLException {
		CallableStatement statement = this.connection
				.prepareCall("{ ? = call \"reorganizeVertexListOf\"(?, ?) }");
		statement.registerOutParameter(1, Types.INTEGER);
		statement.setInt(2, gId);
		statement.setLong(3, start);
		return statement;
	}

	@Override
	public CallableStatement createReorganizeEdgeListCall(int gId, long start)
			throws SQLException {
		CallableStatement statement = this.connection
				.prepareCall("{ ? = call \"reorganizeEdgeListOf\"(?, ?) }");
		statement.registerOutParameter(1, Types.INTEGER);
		statement.setInt(2, gId);
		statement.setLong(3, start);
		return statement;
	}

	@Override
	public CallableStatement createReorganizeIncidenceListCall(int vId,
			int gId, long start) throws SQLException {
		CallableStatement statement = this.connection
				.prepareCall("{ ? = call \"reorganizeIncidenceListOf\"(?, ?) }");
		statement.registerOutParameter(1, Types.INTEGER);
		statement.setInt(2, vId);
		statement.setInt(3, gId);
		statement.setLong(4, start);
		return statement;
	}

	@Override
	public PreparedStatement selectIdOfGraphs() throws SQLException {
		return this.getPreparedStatement("SELECT \"uid\" FROM \"Graph\";");
	}
}