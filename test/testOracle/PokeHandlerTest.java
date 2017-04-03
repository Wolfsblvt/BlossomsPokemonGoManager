package testOracle;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.function.BiConsumer;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.CaptchaActiveException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.exceptions.hash.HashException;

import POGOProtos.Enums.PokemonIdOuterClass;
import POGOProtos.Enums.PokemonMoveOuterClass;
import POGOProtos.Networking.Responses.NicknamePokemonResponseOuterClass.NicknamePokemonResponse;
import me.corriekay.pokegoutil.utils.pokemon.PokeHandler;
import me.corriekay.pokegoutil.utils.pokemon.PokeNick;
import me.corriekay.pokegoutil.utils.pokemon.PokemonUtils;
import me.corriekay.pokegoutil.utils.pokemon.PokeHandler.ReplacePattern;

public class PokeHandlerTest {

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
    public void PokeHandlerInitTest() {
        Pokemon PokeMonMock = EasyMock.createMock(Pokemon.class);
        PokeHandler testPokeHandler = new PokeHandler(PokeMonMock);

        EasyMock.replay(PokeMonMock);
        assertNotNull(testPokeHandler);
        EasyMock.verify(PokeMonMock);
    }

    @Test
    public void generatePokemonNicknameTest() {
        Pokemon PokeMonMock = EasyMock.createMock(Pokemon.class);
        PokeHandler testPokeHandler = new PokeHandler(PokeMonMock);
        String pattern = "pattern";
        
        PokeNick testNickA;
        testNickA = testPokeHandler.generatePokemonNickname(pattern,PokeMonMock);
        
        EasyMock.replay(PokeMonMock);
        
        assertNotNull(testNickA);
        
        EasyMock.verify(PokeMonMock);
    }

    @Test
    public void renameWithPatternFailTest() {
        Pokemon PokeMonMock = EasyMock.createMock(Pokemon.class);
        PokeHandler testPokeHandler = new PokeHandler(PokeMonMock);
        String pattern = "pattern";
        EasyMock.expect(PokeMonMock.getNickname()).andReturn("pattern");
        EasyMock.replay(PokeMonMock);
        
        NicknamePokemonResponse.Result result = testPokeHandler.renameWithPattern(pattern,PokeMonMock);

        
        assertEquals(result,NicknamePokemonResponse.Result.UNSET);
        
        EasyMock.verify(PokeMonMock);
    }
    
    @Test
    public void renameWithPatternSuccessTest() {
        Pokemon PokeMonMock = EasyMock.createMock(Pokemon.class);
        PokeHandler testPokeHandler = new PokeHandler(PokeMonMock);
        String pattern = "pattern";
        
        EasyMock.expect(PokeMonMock.getNickname()).andReturn("NotPattern");
        try {
            EasyMock.expect(PokeMonMock.renamePokemon(pattern)).andReturn(NicknamePokemonResponse.Result.SUCCESS);
        } catch (LoginFailedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CaptchaActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RemoteServerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (HashException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        EasyMock.replay(PokeMonMock);
        
        NicknamePokemonResponse.Result result = testPokeHandler.renameWithPattern(pattern,PokeMonMock);

        
        assertEquals(result,NicknamePokemonResponse.Result.SUCCESS);
        
        EasyMock.verify(PokeMonMock);
    }
    
    @Test
    public void bulkRenameWithPattern2argTest() {
        Pokemon PokeMonMock = EasyMock.createMock(Pokemon.class);
        BiConsumer<NicknamePokemonResponse.Result, Pokemon> BiConsumerMock = EasyMock.createMock(BiConsumer.class);
        PokeHandler testPokeHandler = new PokeHandler(PokeMonMock);
        String pattern = "pattern";
        
        EasyMock.expect(PokeMonMock.getNickname()).andReturn("NotPattern");
        try {
            EasyMock.expect(PokeMonMock.renamePokemon(pattern)).andReturn(NicknamePokemonResponse.Result.SUCCESS);
        } catch (LoginFailedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CaptchaActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RemoteServerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (HashException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        EasyMock.replay(PokeMonMock);
        
        LinkedHashMap<Pokemon, NicknamePokemonResponse.Result> result = testPokeHandler.bulkRenameWithPattern(pattern,BiConsumerMock);
        
        assertNotNull(result);
        
        //EasyMock.verify(BiConsumerMock);
        EasyMock.verify(PokeMonMock);
       
    }
    
    @Test
    public void bulkRenameWithPattern1argTest() {
        Pokemon PokeMonMock = EasyMock.createMock(Pokemon.class);
        PokeHandler testPokeHandler = new PokeHandler(PokeMonMock);
        String pattern = "pattern";
        
        EasyMock.expect(PokeMonMock.getNickname()).andReturn("NotPattern");
        try {
            EasyMock.expect(PokeMonMock.renamePokemon(pattern)).andReturn(NicknamePokemonResponse.Result.SUCCESS);
        } catch (LoginFailedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CaptchaActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RemoteServerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (HashException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        EasyMock.replay(PokeMonMock);
        
        testPokeHandler.bulkRenameWithPattern(pattern);
        
        //EasyMock.verify(BiConsumerMock);
        EasyMock.verify(PokeMonMock);
       
    }
    
    @Test
    public void ReplacePatternNickTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        EasyMock.expect(testPokemon.getNickname()).andReturn("nickName");
        
        EasyMock.replay(testPokemon);
        
        ReplacePattern rep = ReplacePattern.valueOf("NICK");
        String testString = rep.get(testPokemon);
        
        assertEquals(testString,"nickName");
        
        EasyMock.verify(testPokemon);
    }
    
    @Test
    public void ReplacePatternNameTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        
        EasyMock.replay(testPokemon);
        
        ReplacePattern rep = ReplacePattern.valueOf("NAME");

        String testString = rep.get(testPokemon);

        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    @Test
    public void ReplacePatternName2Test() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        
        EasyMock.replay(testPokemon);
        
        ReplacePattern rep = ReplacePattern.valueOf("NAME_2");
        String testString = rep.get(testPokemon);

        assertNotNull(testString);

        
        EasyMock.verify(testPokemon);
    }
    
    @Test
    public void ReplacePatternName4Test() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        
        EasyMock.replay(testPokemon);
        
        ReplacePattern rep = ReplacePattern.valueOf("NAME_4");
        String testString = rep.get(testPokemon);

        assertNotNull(testString);

        
        EasyMock.verify(testPokemon);
    }
    
    @Test
    public void ReplacePatternName6Test() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        
        EasyMock.replay(testPokemon);
        
        ReplacePattern rep = ReplacePattern.valueOf("NAME_6");
        String testString = rep.get(testPokemon);

        assertNotNull(testString);

        
        EasyMock.verify(testPokemon);
    }
    
    @Test
    public void ReplacePatternName8Test() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        
        EasyMock.replay(testPokemon);
        
        ReplacePattern rep = ReplacePattern.valueOf("NAME_8");
        String testString = rep.get(testPokemon);

        assertNotNull(testString);

        
        EasyMock.verify(testPokemon);
    }
    
    @Test
    public void ReplacePatternCPTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        
        EasyMock.expect(testPokemon.getCp()).andReturn(13);
        
        EasyMock.replay(testPokemon);
        
        ReplacePattern rep = ReplacePattern.valueOf("CP");
        String testString = rep.get(testPokemon);

        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    @Test
    public void ReplacePatternCP_EVOLVEDTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        EasyMock.expect(testPokemon.getCp()).andReturn(13);

        EasyMock.expect(testPokemon.getCpAfterFullEvolve(testId)).andReturn(123);

        EasyMock.expect(testPokemon.getCp()).andReturn(13);
        
        EasyMock.replay(testPokemon);
        
        ReplacePattern rep = ReplacePattern.valueOf("CP_EVOLVED");
        String testString = rep.get(testPokemon);

        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    @Test
    public void ReplacePatternHPTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        
        EasyMock.expect(testPokemon.getMaxStamina()).andReturn(1234);
        
        EasyMock.replay(testPokemon);
        
        ReplacePattern rep = ReplacePattern.valueOf("HP");
        String testString = rep.get(testPokemon);

        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    @Test
    public void ReplacePatternIV_RATINGTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);

        EasyMock.expect(testPokemon.getIndividualAttack()).andReturn(123);
        EasyMock.expect(testPokemon.getIndividualDefense()).andReturn(23);
        EasyMock.expect(testPokemon.getIndividualStamina()).andReturn(12);
        EasyMock.replay(testPokemon);
        
        ReplacePattern rep = ReplacePattern.valueOf("IV_RATING");
        String testString = rep.get(testPokemon);

        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    @Test
    public void ReplacePatternIV_HEXTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);

        EasyMock.expect(testPokemon.getIndividualAttack()).andReturn(123);
        EasyMock.expect(testPokemon.getIndividualDefense()).andReturn(23);
        EasyMock.expect(testPokemon.getIndividualStamina()).andReturn(12);
        EasyMock.replay(testPokemon);
        
        ReplacePattern rep = ReplacePattern.valueOf("IV_HEX");
        String testString = rep.get(testPokemon);

        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    @Test
    public void ReplacePatternIV_ATTTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);

        EasyMock.expect(testPokemon.getIndividualAttack()).andReturn(123);
        EasyMock.replay(testPokemon);
        
        ReplacePattern rep = ReplacePattern.valueOf("IV_ATT");
        String testString = rep.get(testPokemon);

        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    @Test
    public void ReplacePatternIV_DEFTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);

        EasyMock.expect(testPokemon.getIndividualDefense()).andReturn(123);
        EasyMock.replay(testPokemon);
        
        ReplacePattern rep = ReplacePattern.valueOf("IV_DEF");
        String testString = rep.get(testPokemon);

        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    @Test
    public void ReplacePatternIV_STAMTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);

        EasyMock.expect(testPokemon.getIndividualStamina()).andReturn(123);
        EasyMock.replay(testPokemon);
        
        ReplacePattern rep = ReplacePattern.valueOf("IV_STAM");
        String testString = rep.get(testPokemon);

        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    @Test
    public void ReplacePatternIV_ATT_UNITest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);

        EasyMock.expect(testPokemon.getIndividualAttack()).andReturn(123);
        EasyMock.replay(testPokemon);
        
        ReplacePattern rep = ReplacePattern.valueOf("IV_ATT_UNI");
        String testString = rep.get(testPokemon);

        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    @Test
    public void ReplacePatternIV_DEF_UNITest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);

        EasyMock.expect(testPokemon.getIndividualDefense()).andReturn(123);
        EasyMock.replay(testPokemon);
        
        ReplacePattern rep = ReplacePattern.valueOf("IV_DEF_UNI");
        String testString = rep.get(testPokemon);

        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    @Test
    public void ReplacePatternIV_STAM_UNITest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);

        EasyMock.expect(testPokemon.getIndividualStamina()).andReturn(123);
        EasyMock.replay(testPokemon);
        
        ReplacePattern rep = ReplacePattern.valueOf("IV_STAM_UNI");
        String testString = rep.get(testPokemon);

        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    @Test
    public void ReplacePatternDUEL_ABILITYTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        PokemonMoveOuterClass.PokemonMove Move1 = PokemonMoveOuterClass.PokemonMove.AQUA_TAIL;
        PokemonMoveOuterClass.PokemonMove Move2 = PokemonMoveOuterClass.PokemonMove.DIG;
        
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        EasyMock.expect(testPokemon.getMove1()).andReturn(Move1);
        EasyMock.expect(testPokemon.getMove2()).andReturn(Move2);        
        EasyMock.expect(testPokemon.getIndividualAttack()).andReturn(123);
        EasyMock.expect(testPokemon.getIndividualDefense()).andReturn(23);
        EasyMock.expect(testPokemon.getIndividualStamina()).andReturn(12);
        EasyMock.replay(testPokemon);
        
        ReplacePattern rep = ReplacePattern.valueOf("DUEL_ABILITY");
        String testString = rep.get(testPokemon);

        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    @Test
    public void ReplacePatternGYM_OFFENSETest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        PokemonMoveOuterClass.PokemonMove Move1 = PokemonMoveOuterClass.PokemonMove.AQUA_TAIL;
        PokemonMoveOuterClass.PokemonMove Move2 = PokemonMoveOuterClass.PokemonMove.DIG;
        
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        EasyMock.expect(testPokemon.getMove1()).andReturn(Move1);
        EasyMock.expect(testPokemon.getMove2()).andReturn(Move2);        
        EasyMock.expect(testPokemon.getIndividualAttack()).andReturn(123);
        EasyMock.expect(testPokemon.getIndividualDefense()).andReturn(23);
        EasyMock.expect(testPokemon.getIndividualStamina()).andReturn(12);
        EasyMock.replay(testPokemon);
        
        ReplacePattern rep = ReplacePattern.valueOf("GYM_OFFENSE");
        String testString = rep.get(testPokemon);

        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
}
