**21st Jan 2012** support for xls dataset has been added. Everything stays the same as for xml dataset. Each sheet is treated as a table name and the data is loaded into the database. Sheet's first row is treated as column names.

# Maven Repository #
Use the following repositories for maven:
```
<repositories>
  <repository>
    <id>dbunit-spring-support-repository</id>
    <url>http://dbunit-spring-support.googlecode.com/svn/repo</url>
  </repository>
  <repository>
    <id>c5-public-repository</id>
    <url>http://mvn.carbonfive.com/public</url>
  </repository>
</repositories>
```

# Dependency #
```
<dependency>
    <groupId>com.googlecode.dbunit-spring-support</groupId>
    <artifactId>dbunit-spring-support</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

# `DataSetTestExecutionListener` #
Loads DBUnit test fixtures before test methods flagged with the `@DataSet` annotation. Participates in an active transactions if available or in auto-commit mode if one is not.

```java

@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class TripRepositoryImplTest extends AbstractTransactionalDataSetTestCase
{
@Autowired TripRepository repository;

@Test
@DataSet public void forIdShouldFindTrip() throws Exception
{
Trip trip = repository.forId(2);
assertThat(trip, not(nullValue()));
}
}```

The high-level execution path for this example looks like:
  * Inject dependencies (`DependencyInjectionTestExecutionListener`)
  * Start transaction (`TransactionalTestExecutionListener`)
  * Load dbunit data set from `TripRepositoryImplTest.xml` (`DataSetTestExecutionListener`) using the setup operation (default is CLEAN\_INSERT)
  * Execute test
  * Optionally cleanup dbunit data using the tear down operation (default is NONE)
  * Rollback transaction (`TransactionalTestExecutionListener`)

Here's the trimmed down log output for this test:
```
INFO: Began transaction (1): transaction manager; rollback [true] (TransactionalTestExecutionListener.java:259)
INFO: Loading dataset from location 'classpath:/eg/domain/TripRepositoryImplTest.xml' using operation 'CLEAN_INSERT'. (DataSetTestExecutionListener.java:152)
INFO: Tearing down dataset using operation 'NONE', leaving database connection open. (DataSetTestExecutionListener.java:67)
INFO: Rolled back transaction after test execution for test context (TransactionalTestExecutionListener.java:279)
```

One of the interesting details is what to do with the connection used to load the data. The framework assumes that if it’s a transactional connection it should be left open because whatever started the transaction should do the closing. When it’s non-transactional it’s closed after the dataset is loaded. This convention works well for how I typically write my database tests.

In addition to the `@DataSet` annotation, we must add the `DataSetTestExecutionListener` to the set of listeners that are applied to the test class. As in the above example, you can extend `AbstractTransactionalDataSetTestCase` which does this for you or you can specify the listener using the class-level annotation `@TestExecutionListeners`. It’s important that the listener is triggered after the `TransactionalTestExecutionListener`.

If all test methods use the dataset, then the test class (or super class) can be annotated and every test will load the dataset. Also, if a different dataset should be loaded, the name of the resource can be specified in the annotation e.g.
```java

@DataSet(“TripRepositoryImplTest-foo.xml”)```
or
```java

@DataSet(“classpath:/db/trips.xml”))```

Lastly, the setup and teardown database operations can be overridden e.g.
```java

@DataSet(setupOperation = “INSERT”, teardownOperation=”DELETE”)```

There is also support for specifying more than one dataset locations in the Annotation. Internally the framework aggregates all the `DataSet` loaded from these locations in to a `CompositeDataSet`. This way we can have separate small datasets for each table and then combine them for a unit test. It also makes dataset management pretty easy. Here is an example
```java

@DataSet({“TripRepositoryImplTest-foo.xml”, “UserRepositoryImplTest-foo.xml”})```