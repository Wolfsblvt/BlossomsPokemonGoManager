package testOracle;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import POGOProtos.Enums.PokemonMoveOuterClass.PokemonMove;
import me.corriekay.pokegoutil.utils.pokemon.PokemonPerformance;

public class PokemonPerformanceTest {

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
     * Purpose: Init PokemonPerformance<T> class
     * Input: PokemonPerformance<T>  Make variable
     * Expected: PokemonPerformance<T> variable(Id,T,MoveA,MoveB); 
     */

    @Test
    public void PokemonPerformanceInitTest() {
        PokemonId IdMock = EasyMock.createMock(PokemonId.class);
        PokemonMove MoveMockA = EasyMock.createMock(PokemonMove.class);
        PokemonMove MoveMockB = EasyMock.createMock(PokemonMove.class);
        
        EasyMock.replay(IdMock);
        EasyMock.replay(MoveMockA);
        EasyMock.replay(MoveMockB);
        
        Double a = new Double(0);
        
        //PokemonPerformance testPerformanceA = new PokemonPerformance(IdMock,a,MoveMockA,MoveMockB);
        PokemonPerformance<Long> testPerformanceB;
        
        EasyMock.verify(IdMock);
        EasyMock.verify(MoveMockA);
        EasyMock.verify(MoveMockB);
        
    }

}
