package testOracle;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pokegoapi.api.pokemon.Pokemon;

import me.corriekay.pokegoutil.utils.pokemon.PokemonCalculationUtils;

public class PokemonCalculationUtilsTest {

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
    public void PokemonCalculationUtilsInitTest() {
        PokemonCalculationUtils testUtilsMock = EasyMock.createMock(PokemonCalculationUtils.class);
        EasyMock.replay(testUtilsMock);
        
        assertNotNull(testUtilsMock);
        
        EasyMock.verify(testUtilsMock);
    }
    
    @Test
    public void ivRatingTest() {
        PokemonCalculationUtils testUtilsMock = EasyMock.createMock(PokemonCalculationUtils.class);
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        EasyMock.replay(testUtilsMock);
        
        double result = testUtilsMock.ivRating(testPokemon);
        
        assertNotNull(result);
        
        EasyMock.verify(testUtilsMock);
    }
}
