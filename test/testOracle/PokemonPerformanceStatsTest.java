package testOracle;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import POGOProtos.Enums.PokemonIdOuterClass;
import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import me.corriekay.pokegoutil.utils.pokemon.PokemonPerformance;
import me.corriekay.pokegoutil.utils.pokemon.PokemonPerformanceStats;

public class PokemonPerformanceStatsTest {

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
     * Purpose: Init PokemonPerformanceStats class
     * Input: PokemonPerformanceStats Make variable
     * Expected: PokemonPerformanceStats variable(VENUSAUR,null,null,null)
     */

    @Test
    public void PokemonPerformanceStatsInitTest() {
        PokemonId pokemonId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        PokemonPerformance<Long> PerformanceLongA = null;
        PokemonPerformance<Double> PerformanceDouble = null;
        PokemonPerformance<Long> PerformanceLongB = null;

        
        //PokemonPerformanceStats testStats = new PokemonPerformanceStats(pokemonId,PerformanceLongA,PerformanceDouble,PerformanceLongB);
    
    }

}
