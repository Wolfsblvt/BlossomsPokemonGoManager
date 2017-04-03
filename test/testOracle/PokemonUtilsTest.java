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

    @Test
    public void PokemonUtilsInitTest() {
        PokemonUtils testUtils = null;
        assertNull(testUtils);
        
    }

    @Test
    public void getLocalPokeNameIdTest() {
        PokemonUtils testUtils = null;
        String result = testUtils.getLocalPokeName(30);
        
        assertNotNull(result);
    }
    
    @Test
    public void getLocalPokeNamePokemonTest() {
        PokemonUtils testUtils = null;
        PokemonIdOuterClass.PokemonId testIdMock = null;
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testIdMock);
        EasyMock.expect(testIdMock.getNumber()).andReturn(30);
        EasyMock.replay(testPokemon);
        String result = testUtils.getLocalPokeName(testPokemon);
        
        assertNotNull(result);
        
        EasyMock.verify(testPokemon);
    }
    
    @Test
    public void convertTeamColorToNameTest() {
        PokemonUtils testUtils = null;
        
        String result = testUtils.convertTeamColorToName(0);
        
        assertEquals(result,"None");
        
    }
    
    @Test
    public void convertTeamColorToNameFailTest() {
        PokemonUtils testUtils = null;
        
        String result = testUtils.convertTeamColorToName(4);
        
        assertEquals(result,"UNKNOWN_TEAM");
        
    }
    
    @Test
    public void formatTypeTest() {
        PokemonUtils testUtils = null;
        POGOProtos.Enums.PokemonTypeOuterClass.PokemonType testPokemonType = POGOProtos.Enums.PokemonTypeOuterClass.PokemonType.POKEMON_TYPE_GRASS;
        
        String result = testUtils.formatType(testPokemonType);
        
        assertNotNull(result);
        
    }
    
    @Test
    public void formatMoveTest() {
        PokemonUtils testUtils = null;
        PokemonMove testMove = PokemonMove.TACKLE;
        
        String result = testUtils.formatMove(testMove);
        
        assertNotNull(result);
        
    }
    
    @Test
    public void formatDpsTest() {
        PokemonUtils testUtils = null;
        double testDps = 20.0;
        
        String result = testUtils.formatDps(testDps);
        
        assertNotNull(result);
        
    }
    
    @Test
    public void formatItemTest() {
        PokemonUtils testUtils = null;
        ItemId testItemId = ItemId.ITEM_HYPER_POTION;
        
        String result = testUtils.formatItem(testItemId);
        
        assertNotNull(result);
        
    }
    
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
