package com.googlecode.test.spring.dbunit;

import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

/**
 * Base class for JUnit Tests cases that want to utilize the DataSet functionality.
 * 
 * @author Faisal Feroz
 * @see DataSet
 * @see DataSetTestExecutionListener
 */
@TestExecutionListeners( { DataSetTestExecutionListener.class } )
public abstract class AbstractTransactionalDataSetTestCase
    extends AbstractTransactionalJUnit4SpringContextTests
{
}
