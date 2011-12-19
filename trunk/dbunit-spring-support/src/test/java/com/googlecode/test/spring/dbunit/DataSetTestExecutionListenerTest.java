package com.googlecode.test.spring.dbunit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.junit.Test;

import com.googlecode.test.spring.dbunit.DataSet;
import com.googlecode.test.spring.dbunit.DataSetTestExecutionListener;

public class DataSetTestExecutionListenerTest
{
    @Test
    public void parentClassesCanBeAnnotated()
        throws NoSuchMethodException
    {
        DataSetTestExecutionListener listener = new DataSetTestExecutionListener();
        DataSetTestExecutionListener.DatasetConfiguration datasetConfiguration =
            listener.determineConfiguration( C.class, C.class.getMethod( "testSomething" ) );

        assertThat( datasetConfiguration, not( nullValue() ) );
        assertThat( datasetConfiguration.getLocations(), is( notNullValue() ) );
        assertThat( datasetConfiguration.getLocations().length, is( 1 ) );
        assertThat( datasetConfiguration.getLocations()[0], containsString( "sample.xml" ) );
        assertThat( datasetConfiguration.getSetupOperation(), is( "INSERT" ) );
        assertThat( datasetConfiguration.getTeardownOperation(), is( "DELETE" ) );
    }

    @DataSet( value = "sample.xml", setupOperation = "INSERT", teardownOperation = "DELETE" )
    static class A
    {
    }

    static class B
        extends A
    {
    }

    static class C
        extends B
    {
        public void testSomething()
        {
        }
    }
}
