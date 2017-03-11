package edu.berkeley.cs186.database.query;

import edu.berkeley.cs186.database.Database;
import edu.berkeley.cs186.database.DatabaseException;
import edu.berkeley.cs186.database.TestUtils;
import edu.berkeley.cs186.database.datatypes.DataType;
import edu.berkeley.cs186.database.datatypes.IntDataType;
import edu.berkeley.cs186.database.query.QueryPlan.PredicateOperator;
import edu.berkeley.cs186.database.table.Schema;
import edu.berkeley.cs186.database.table.stats.StringHistogram;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class QueryPlanCostsTest {
  private Database database;
  private Random random = new Random();
  private String alphabet = StringHistogram.alphaNumeric;
  private String defaulTableName = "testAllTypes";
  private int defaultNumRecords = 1000;

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  @Before
  public void setUp() throws DatabaseException, IOException {
    File tempDir = tempFolder.newFolder("db");
    this.database = new Database(tempDir.getAbsolutePath());
    this.database.deleteAllTables();
  }

  @Test(timeout=1000)
  public void testIndexScanOperatorCost() throws DatabaseException, QueryPlanException {
    List<String> intTableNames = new ArrayList<String>();
    intTableNames.add("int");

    List<DataType> intTableTypes = new ArrayList<DataType>();
    intTableTypes.add(new IntDataType());

    String tableName = "tempIntTable";

    this.database.createTableWithIndices(
      new Schema(intTableNames, intTableTypes), tableName, intTableNames);

    Database.Transaction transaction = this.database.beginTransaction();

    for (int i = 0; i < 300; i++) {
      List<DataType> values = new ArrayList<DataType>();
      values.add(new IntDataType(i));

      transaction.addRecord(tableName, values);
    }

    QueryOperator indexScanOperator;

    indexScanOperator = new IndexScanOperator(
      transaction, tableName, "int", PredicateOperator.GREATER_THAN_EQUALS, new IntDataType(200));

    assertEquals(115, indexScanOperator.estimateIOCost());

    for (int i = 0; i < 700; i++) {
      List<DataType> values = new ArrayList<DataType>();
      values.add(new IntDataType(i));

      transaction.addRecord(tableName, values);
    }

    indexScanOperator = new IndexScanOperator(
      transaction, tableName, "int", PredicateOperator.GREATER_THAN_EQUALS, new IntDataType(500));

    assertEquals(380, indexScanOperator.estimateIOCost());

    transaction.end();
  }


  @Test(timeout=2000)
  public void testSNLJOperatorCost() throws DatabaseException, QueryPlanException {
    List<DataType> values = TestUtils.createRecordWithAllTypes().getValues();
    this.database.createTable(TestUtils.createSchemaWithAllTypes(), "tempIntTable1");
    this.database.createTable(TestUtils.createSchemaWithAllTypes(), "tempIntTable2");

    Database.Transaction transaction = this.database.beginTransaction();
    int numEntries = transaction.getNumEntriesPerPage("tempIntTable1");

    for (int i = 0; i < 2 * numEntries; i++) {
      transaction.addRecord("tempIntTable1", values);
    }
    for (int i = 0; i < 4 * numEntries; i++) {
      transaction.addRecord("tempIntTable2", values);
    }

    QueryOperator left = new SequentialScanOperator(transaction, "tempIntTable1");
    QueryOperator right = new SequentialScanOperator(transaction, "tempIntTable2");

    JoinOperator leftJoinRight = new SNLJOperator(left, right, "int", "int", transaction);
    assertEquals(2306, leftJoinRight.estimateIOCost());

    JoinOperator rightJoinLeft = new SNLJOperator(right, left, "int", "int", transaction);
    assertEquals(2308, rightJoinLeft.estimateIOCost());
  }



  @Test(timeout=2000)
  public void testPNLJOperatorCost() throws DatabaseException, QueryPlanException {
    List<DataType> values = TestUtils.createRecordWithAllTypes().getValues();
    this.database.createTable(TestUtils.createSchemaWithAllTypes(), "tempIntTable1");
    this.database.createTable(TestUtils.createSchemaWithAllTypes(), "tempIntTable2");

    Database.Transaction transaction = this.database.beginTransaction();
    int numEntries = transaction.getNumEntriesPerPage("tempIntTable1");

    for (int i = 0; i < 2 * numEntries; i++) {
      transaction.addRecord("tempIntTable1", values);
    }
    for (int i = 0; i < 3 * numEntries + 10; i++) {
      transaction.addRecord("tempIntTable2", values);
    }

    QueryOperator left = new SequentialScanOperator(transaction, "tempIntTable1");
    QueryOperator right = new SequentialScanOperator(transaction, "tempIntTable2");

    JoinOperator leftJoinRight = new PNLJOperator(left, right, "int", "int", transaction);
    assertEquals(10, leftJoinRight.estimateIOCost());

    JoinOperator rightJoinLeft = new PNLJOperator(right, left, "int", "int", transaction);
    assertEquals(12, rightJoinLeft.estimateIOCost());
  }


  @Test(timeout=2000)
  public void testBNLJOperatorCost() throws DatabaseException, QueryPlanException {
    List<DataType> values = TestUtils.createRecordWithAllTypes().getValues();
    this.database.createTable(TestUtils.createSchemaWithAllTypes(), "tempIntTable1");
    this.database.createTable(TestUtils.createSchemaWithAllTypes(), "tempIntTable2");

    Database.Transaction transaction = this.database.beginTransaction();
    int numEntries = transaction.getNumEntriesPerPage("tempIntTable1");

    for (int i = 0; i < 17 * numEntries + 100; i++) {
      transaction.addRecord("tempIntTable1", values);
    }
    for (int i = 0; i < 4 * numEntries; i++) {
      transaction.addRecord("tempIntTable2", values);
    }

    QueryOperator left = new SequentialScanOperator(transaction, "tempIntTable1");
    QueryOperator right = new SequentialScanOperator(transaction, "tempIntTable2");

    JoinOperator leftJoinRight = new BNLJOperator(left, right, "int", "int", transaction);
    assertEquals(42, leftJoinRight.estimateIOCost());

    JoinOperator rightJoinLeft = new BNLJOperator(right, left, "int", "int", transaction);
    assertEquals(40, rightJoinLeft.estimateIOCost());
  }


  @Test(timeout=2000)
  public void testGraceHashOperatorCost() throws DatabaseException, QueryPlanException {
    List<DataType> values = TestUtils.createRecordWithAllTypes().getValues();
    this.database.createTable(TestUtils.createSchemaWithAllTypes(), "tempIntTable1");
    this.database.createTable(TestUtils.createSchemaWithAllTypes(), "tempIntTable2");

    Database.Transaction transaction = this.database.beginTransaction();
    int numEntries = transaction.getNumEntriesPerPage("tempIntTable1");

    for (int i = 0; i < 18 * numEntries; i++) {
      transaction.addRecord("tempIntTable1", values);
    }
    for (int i = 0; i < 3 * numEntries + 287; i++) {
      transaction.addRecord("tempIntTable2", values);
    }

    QueryOperator left = new SequentialScanOperator(transaction, "tempIntTable1");
    QueryOperator right = new SequentialScanOperator(transaction, "tempIntTable2");

    JoinOperator leftJoinRight = new GraceHashOperator(left, right, "int", "int", transaction);
    assertEquals(66, leftJoinRight.estimateIOCost());

    JoinOperator rightJoinLeft = new GraceHashOperator(right, left, "int", "int", transaction);
    assertEquals(66, rightJoinLeft.estimateIOCost());
  }
}
