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

    @Test
    public void PokeNickInitTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        EasyMock.replay(testPokemon);
        
        PokeNick testNick = new PokeNick("pattern123",testPokemon);
        
        assertNotNull(testNick);
        
        EasyMock.verify(testPokemon);
    }

    @Test
    public void toStringTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        EasyMock.replay(testPokemon);
        
        PokeNick testNick = new PokeNick("pattern123",testPokemon);
        String testString = testNick.toString();
        
        assertNotNull(testString);
        
        EasyMock.verify(testPokemon);
    }
    
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
