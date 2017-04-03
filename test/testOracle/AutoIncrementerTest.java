package testOracle;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import me.corriekay.pokegoutil.utils.AutoIncrementer;

public class AutoIncrementerTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Purpose : Test AutoIncrementer class default constructor
     * Input : AutoIncrementer()
     * Expected : 
     *      return 0
     */
    @Test
    public void testConstructor() {
        AutoIncrementer autoIncrementer = new AutoIncrementer();
        assertEquals(0, autoIncrementer.get());
    }
    
    /**
     * Purpose : Test AutoIncrementer class constructor
     * Input : AutoIncrementer(Integer.MAX_VALUE)
     * Expected : 
     *      return Integer.MAX_VALUE
     */
    @Test
    public void testConstructor2() {
        AutoIncrementer autoIncrementer = new AutoIncrementer(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, autoIncrementer.get());
    }
    
    /**
     * Purpose : Test AutoIncrementer class get method
     * Input : AutoIncrementer() , get() , get(), get(), get()
     * Expected : 
     *      return 
     */
    @Test
    public void testAutoIncrementerGet() {
        AutoIncrementer autoIncrementer = new AutoIncrementer();
        assertEquals(0, autoIncrementer.get());
        assertEquals(1, autoIncrementer.get());
        assertEquals(2, autoIncrementer.get());
        assertEquals(3, autoIncrementer.get());
    }

}
