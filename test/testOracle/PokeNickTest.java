package testOracle;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pokegoapi.api.pokemon.Pokemon;

import me.corriekay.pokegoutil.utils.pokemon.PokeNick;

public class PokeNickTest {

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
     * Purpose: Init PokeNick class
     * Input: PokeNick make PokeNick variable
     * Expected: PokeNick class
     */
    
    @Test
    public void PokeNickInitTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        EasyMock.replay(testPokemon);
        
        PokeNick testNick = new PokeNick("pattern123",testPokemon);
        
        assertNotNull(testNick);
        
        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: overwrite toString
     * Input: toString PokeNick("pattern123",testPokemon) -> "pattern123"
     * Expected: 
     *          Return "pattern123"
     */

    @Test
    public void toStringTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        EasyMock.replay(testPokemon);
        
        PokeNick testNick = new PokeNick("pattern123",testPokemon);
        String testString = testNick.toString();
        
        assertEquals(testString,"pattern123");
        
        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: check Nickname > MAX_NICKNAME_LENGTH
     * Input: isTooLong 
     *          1. "pattern123456789123" -> true
     *          2. "NICKern" -> false
     * Expected: 
     *          1.  Return true
     *          2.  Return false
     */
    
    @Test
    public void isTooLongTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        EasyMock.expect(testPokemon.getNickname()).andReturn("nickName");
        EasyMock.replay(testPokemon);
        
        PokeNick testNickA = new PokeNick("%NICK%ern%",testPokemon);
        PokeNick testNickB = new PokeNick("%pattern123456789%",testPokemon);
        
        boolean testTooLongA = testNickA.isTooLong();
        boolean testTooLongB = testNickB.isTooLong();
        
        assertFalse(testTooLongA);
        assertTrue(testTooLongB);
        
        EasyMock.verify(testPokemon);
    }
    

}
