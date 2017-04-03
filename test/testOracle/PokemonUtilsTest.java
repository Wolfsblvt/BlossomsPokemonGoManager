package testOracle;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pokegoapi.api.pokemon.Pokemon;

import POGOProtos.Enums.PokemonIdOuterClass;
import POGOProtos.Enums.PokemonMoveOuterClass.PokemonMove;
import POGOProtos.Inventory.Item.ItemIdOuterClass.ItemId;
import me.corriekay.pokegoutil.utils.pokemon.PokemonUtils;

public class PokemonUtilsTest {

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
     * Purpose: Init PokemonUtils class
     * Input: PokemonUtils  Make variable
     * Expected: PokemonUtils variable; 
     */
    
    @Test
    public void PokemonUtilsInitTest() {
        PokemonUtils testUtils = null;
        assertNull(testUtils);
        
    }
    
    /**
     * Purpose: Get LocalPokeName By Id
     * Input: getLocalPokeName  getLocalPokeName(30) -> Nidorina
     * Expected: Return "Nidorina"; 
     */

    @Test
    public void getLocalPokeNameIdTest() {
        PokemonUtils testUtils = null;
        String result = testUtils.getLocalPokeName(30);

        assertEquals(result,"Nidorina");
    }
    
    /**
     * Purpose: Get LocalPokeName By Pokemon
     * Input: getLocalPokeName  getLocalPokeName(Pokemon) -> Venusaur
     * Expected: Return "Venusaur"; 
     */

    
    @Test
    public void getLocalPokeNamePokemonTest() {
        PokemonUtils testUtils = null;
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        EasyMock.replay(testPokemon);
        String result = testUtils.getLocalPokeName(testPokemon);

        assertEquals(result,"Venusaur");
        
        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Convert TeamColor
     * Input: convertTeamColorToName  
     *        convertTeamColorToName(0) -> None
     *        convertTeamColorToName(4) -> UNKNOWN_TEAM
     * Expected: 
     *        0 : Return "None"
     *        4 : Return "UNKNOWN_TEAM"
     */

    @Test
    public void convertTeamColorToNameTest() {
        PokemonUtils testUtils = null;
        
        String resultA = testUtils.convertTeamColorToName(0);      
        String resultB = testUtils.convertTeamColorToName(4);
        
        assertEquals(resultA,"None");
        assertEquals(resultB,"UNKNOWN_TEAM");
        
    }
    
    /**
     * Purpose: format Type
     * Input: formatType Null -> POKEMON_TYPE_GRASS
     * Expected: 
     *        Return "Grass"
     */
    
    @Test
    public void formatTypeTest() {
        PokemonUtils testUtils = null;
        POGOProtos.Enums.PokemonTypeOuterClass.PokemonType testPokemonType = POGOProtos.Enums.PokemonTypeOuterClass.PokemonType.POKEMON_TYPE_GRASS;
        
        String result = testUtils.formatType(testPokemonType);

        assertEquals(result,"Grass");
        
    }
    
    /**
     * Purpose: format Move
     * Input: formatMove Null -> TACKLE
     * Expected: 
     *        Return "Grass"
     */
    
    @Test
    public void formatMoveTest() {
        PokemonUtils testUtils = null;
        PokemonMove testMove = PokemonMove.TACKLE;
        
        String result = testUtils.formatMove(testMove);

        assertEquals(result,"Tackle");
        
    }
    
    /**
     * Purpose: format Dps
     * Input: formatDps Null -> 20.0
     * Expected: 
     *        Return "(20.00 dps)"
     */
    
    @Test
    public void formatDpsTest() {
        PokemonUtils testUtils = null;
        double testDps = 20.0;
        
        String result = testUtils.formatDps(testDps);

        assertEquals(result,"(20.00 dps)");
        
    }
    
    /**
     * Purpose: format Item
     * Input: formatItem Null -> ITEM_HYPER_POTION
     * Expected: 
     *        Return "Hyper Potion"
     */
    
    @Test
    public void formatItemTest() {
        PokemonUtils testUtils = null;
        ItemId testItemId = ItemId.ITEM_HYPER_POTION;
        
        String result = testUtils.formatItem(testItemId);

        assertEquals(result,"Hyper Potion");
        
    }
    
    /**
     * Purpose: Check Same Type Attack Bonus(Stab)
     * Input: hasStab hasStab(Golduck,Tackle) -> true/false
     * Expected: 
     *        Return "true" or "false"
     */
    
    
    @Test
    public void hasStabTest() {
        PokemonUtils testUtils = null;
        Pokemon testPokemonMock = EasyMock.createMock(Pokemon.class);
        
        EasyMock.expect(testPokemonMock.getPokemonId()).andReturn(PokemonIdOuterClass.PokemonId.GOLDUCK);
        EasyMock.expect(testPokemonMock.getMove1()).andReturn(PokemonMove.TACKLE);
        EasyMock.expect(testPokemonMock.getMove2()).andReturn(PokemonMove.AQUA_TAIL);
        
        EasyMock.replay(testPokemonMock);
        
        boolean resultA = testUtils.hasStab(PokemonIdOuterClass.PokemonId.GOLDUCK, PokemonMove.TACKLE);
        boolean resultB = testUtils.hasStab(testPokemonMock, true);
        
        assertTrue(resultA);
        assertTrue(resultB);
        
        EasyMock.verify(testPokemonMock);
        
    }
    
}
