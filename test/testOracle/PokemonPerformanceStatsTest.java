package testOracle;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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

    @Test
    public void PokemonPerformanceStatsInitTest() {
        PokemonId pokemonIdMock = EasyMock.createMock(PokemonId.class);
        PokemonPerformance<Long> PerformanceLongA = null;
        PokemonPerformance<Double> PerformanceDouble = null;
        PokemonPerformance<Long> PerformanceLongB = null;
        
        EasyMock.replay(pokemonIdMock);
        
        //PokemonPerformanceStats testStats = new PokemonPerformanceStats(pokemonIdMock,PerformanceLongA,PerformanceDouble,PerformanceLongB);
    
    }

}
