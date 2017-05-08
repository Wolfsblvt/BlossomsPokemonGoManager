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
import com.pokegoapi.exceptions.NoSuchItemException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.exceptions.hash.HashException;

import POGOProtos.Enums.PokemonIdOuterClass;
import POGOProtos.Enums.PokemonMoveOuterClass;
import POGOProtos.Networking.Responses.NicknamePokemonResponseOuterClass.NicknamePokemonResponse;
import POGOProtos.Settings.Master.PokemonSettingsOuterClass;
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
    
    /**
     * Purpose: Init PokeHandler class
     * Input: PokeHandler  Make variable
     * Expected: PokeHandler variable; 
     */
    
    @Test
    public void PokeHandlerInitTest() {
        Pokemon PokeMonMock = EasyMock.createMock(Pokemon.class);
        PokeHandler testPokeHandler = new PokeHandler(PokeMonMock);

        EasyMock.replay(PokeMonMock);
        assertNotNull(testPokeHandler);
        EasyMock.verify(PokeMonMock);
    }
    
    /**
     * Purpose: generate PokemonNickname
     * Input: generatePokemonNickname null -> "pattern"
     * Expected:
     *          Return "pattern" 
     */

    @Test
    public void generatePokemonNicknameTest() {
        Pokemon PokeMonMock = EasyMock.createMock(Pokemon.class);
        PokeHandler testPokeHandler = new PokeHandler(PokeMonMock);
        String pattern = "pattern";
        
        PokeNick testNickA;
        testNickA = testPokeHandler.generatePokemonNickname(pattern,PokeMonMock);
        
        EasyMock.replay(PokeMonMock);

        assertEquals(testNickA.toString(),"pattern");
        
        EasyMock.verify(PokeMonMock);
    }
    
    /**
     * Purpose: rename With Pattern
     * Input: renameWithPattern "pattern" -> "pattern"
     * Expected:
     *          Return NicknamePokemonResponse.Result.UNSET 
     */


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
    
    /**
     * Purpose: rename With Pattern
     * Input: renameWithPattern "NotPattern" -> "pattern"
     * Expected:
     *          Return NicknamePokemonResponse.Result.SUCCESS 
     */

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
    
    /**
     * Purpose: Rename a bunch of Pokemon based on a pattern
     * Input: bulkRenameWithPattern "NotPattern" -> "pattern"
     * Expected:
     *          Return LinkedHashMap<Pokemon, NicknamePokemonResponse.Result> result
     */
    
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
    
    /**
     * Purpose: Rename a bunch of Pokemon based on a pattern
     * Input: bulkRenameWithPattern "NotPattern" -> "pattern"
     * Expected:
     *          void
     */
    
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
    
    /**
     * Purpose: Get Pokemon NickName to Replace Pattern
     * Input: ReplacePattern.NICK.get  get NickName "nickName"
     * Expected:
     *          Return "nickName"
     */
    
    @Test
    public void ReplacePatternNickTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        EasyMock.expect(testPokemon.getNickname()).andReturn("nickName");
        
        EasyMock.replay(testPokemon);
        String testString = ReplacePattern.NICK.get(testPokemon);

        assertEquals(testString,"nickName");
        
        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon Name to Replace Pattern
     * Input: ReplacePattern.NAME.get  get Name "Venusaur"
     * Expected:
     *          Return "Venusaur"
     */
    
    @Test
    public void ReplacePatternNameTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.NAME.get(testPokemon);

        assertEquals(testString,"Venusaur");

        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon Name 2 letters to Replace Pattern
     * Input: ReplacePattern.NAME_2.get  get Name "Ve"
     * Expected:
     *          Return "Ve"
     */
    
    @Test
    public void ReplacePatternName2Test() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.NAME_2.get(testPokemon);

        assertEquals(testString,"Ve");
        
        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon Name 4 letters to Replace Pattern
     * Input: ReplacePattern.NAME_4.get  get Name "Venu"
     * Expected:
     *          Return "Venu"
     */
    
    @Test
    public void ReplacePatternName4Test() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.NAME_4.get(testPokemon);

        assertEquals(testString,"Venu");

        
        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon Name 6 letters to Replace Pattern
     * Input: ReplacePattern.NAME_6.get  get Name "Venusa"
     * Expected:
     *          Return "Venusa"
     */
    
    @Test
    public void ReplacePatternName6Test() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.NAME_6.get(testPokemon);

        assertEquals(testString,"Venusa");
        
        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon Name 8 letters to Replace Pattern
     * Input: ReplacePattern.NAME_8.get  get Name "Venusaur"
     * Expected:
     *          Return "Venusaur"
     */
    
    @Test
    public void ReplacePatternName8Test() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.NAME_8.get(testPokemon);

        assertEquals(testString,"Venusaur");
        
        
        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon Combat Points(CP) to Replace Pattern
     * Input: ReplacePattern.CP.get  get CP "13"
     * Expected:
     *          Return "13"
     */
    
    @Test
    public void ReplacePatternCPTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        
        EasyMock.expect(testPokemon.getCp()).andReturn(13);
        
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.CP.get(testPokemon);

        assertEquals(testString,"13");

        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon Combat Points(CP) if pokemon was fully evolved to Replace Pattern
     * Input: ReplacePattern.CP_EVOLVED.get  get CP
     * Expected:
     *          Return "-"
     */
    
    @Test
    public void ReplacePatternCP_EVOLVEDTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.BUTTERFREE;
        
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        EasyMock.expect(testPokemon.getCp()).andReturn(13);

        EasyMock.expect(testPokemon.getCpAfterFullEvolve(testId)).andReturn(123);

        EasyMock.expect(testPokemon.getCp()).andReturn(13);
        
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.CP_EVOLVED.get(testPokemon);
        
        assertEquals(testString,"-");

        //EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon HP to Replace Pattern
     * Input: ReplacePattern.HP.get  get HP "1234"
     * Expected:
     *          Return "1234"
     */
    
    @Test
    public void ReplacePatternHPTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        
        EasyMock.expect(testPokemon.getMaxStamina()).andReturn(1234);
        
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.HP.get(testPokemon);
        
        assertEquals(testString,"1234");

        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon Level to Replace Pattern
     * Input: ReplacePattern.LEVEL.get  get LEVEL "12.0"
     * Expected:
     *          Return "12.0"
     */
    
    @Test
    public void ReplacePatternLEVELTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        
        EasyMock.expect(testPokemon.getLevel()).andReturn((float) 12);
        
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.LEVEL.get(testPokemon);

        assertEquals(testString,"12.0");

        EasyMock.verify(testPokemon);
    }
    
    
    /**
     * Purpose: Get Pokemon IV_RATING to Replace Pattern
     * Input: ReplacePattern.IV_RATING.get  get IV_RATING
     * Expected:
     *          Return "XX"
     */
    
    @Test
    public void ReplacePatternIV_RATINGTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);

        EasyMock.expect(testPokemon.getIndividualAttack()).andReturn(123);
        EasyMock.expect(testPokemon.getIndividualDefense()).andReturn(23);
        EasyMock.expect(testPokemon.getIndividualStamina()).andReturn(12);
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.IV_RATING.get(testPokemon);

        assertEquals(testString,"XX");


        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon IV Values in hexadecimal to Replace Pattern
     * Input: ReplacePattern.IV_HEX.get  
     *        IndividualAttack : 123 , IndividualDefense : 23, IndividualStamina : 12  ->  get IV_HEX "7B17C"
     * Expected:
     *          Return "7B17C"
     */
    
    @Test
    public void ReplacePatternIV_HEXTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);

        EasyMock.expect(testPokemon.getIndividualAttack()).andReturn(123);
        EasyMock.expect(testPokemon.getIndividualDefense()).andReturn(23);
        EasyMock.expect(testPokemon.getIndividualStamina()).andReturn(12);
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.IV_HEX.get(testPokemon);

        assertEquals(testString,"7B17C");


        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon IV ATACK to Replace Pattern
     * Input: ReplacePattern.IV_ATT.get  
     *        IndividualAttack : 123 ->  get IV_ATT
     * Expected:
     *          Return "XX"
     */
    
    @Test
    public void ReplacePatternIV_ATTTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);

        EasyMock.expect(testPokemon.getIndividualAttack()).andReturn(123);
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.IV_ATT.get(testPokemon);

        assertEquals(testString,"XX");


        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon IV DEF to Replace Pattern
     * Input: ReplacePattern.IV_DEF.get  
     *        IndividualDefense : 123 ->  get IV_DEF
     * Expected:
     *          Return "XX"
     */
    
    
    @Test
    public void ReplacePatternIV_DEFTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);

        EasyMock.expect(testPokemon.getIndividualDefense()).andReturn(123);
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.IV_DEF.get(testPokemon);

        assertEquals(testString,"XX");

        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon IV STAM to Replace Pattern
     * Input: ReplacePattern.IV_STAM.get  
     *        IndividualStamina : 123 ->  get IV_STAM
     * Expected:
     *          Return "XX"
     */
    
    @Test
    public void ReplacePatternIV_STAMTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);

        EasyMock.expect(testPokemon.getIndividualStamina()).andReturn(123);
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.IV_STAM.get(testPokemon);

        assertEquals(testString,"XX");


        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon IV ATT UNI to Replace Pattern
     * Input: ReplacePattern.IV_ATT_UNI.get  
     *        IndividualAttack : 123 ->  get IV_ATT_UNI
     * Expected:
     *          Return UNICODE
     */
    
    @Test
    public void ReplacePatternIV_ATT_UNITest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);

        EasyMock.expect(testPokemon.getIndividualAttack()).andReturn(123);
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.IV_ATT_UNI.get(testPokemon);
       
        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon IV DEF UNI to Replace Pattern
     * Input: ReplacePattern.IV_DEF_UNI.get  
     *        IndividualDefense : 123 ->  get IV_DEF_UNI
     * Expected:
     *          Return UNICODE
     */
    
    @Test
    public void ReplacePatternIV_DEF_UNITest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);

        EasyMock.expect(testPokemon.getIndividualDefense()).andReturn(123);
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.IV_DEF_UNI.get(testPokemon);
        
        assertNotNull(testString);
        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon IV STAM UNI to Replace Pattern
     * Input: ReplacePattern.IV_STAM_UNI.get  
     *        IndividualStamina : 123 ->  get IV_STAM_UNI
     * Expected:
     *          Return UNICODE
     */
    
    @Test
    public void ReplacePatternIV_STAM_UNITest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);

        EasyMock.expect(testPokemon.getIndividualStamina()).andReturn(123);
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.IV_STAM_UNI.get(testPokemon);
        
        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon DUEL ABILITY to Replace Pattern
     * Input: ReplacePattern.DUEL_ABILITY.get  
     *        Id : Venusaur, move1 : Aqua Tail, move2 : Dig, Attack : 123, Defense : 23, Stamina : 12 ->  get DUEL_ABILITY
     * Expected:
     *          Return DUEL_ABILITY
     */
    
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
        
        String testString = ReplacePattern.DUEL_ABILITY.get(testPokemon);
        
        assertNotNull(testString);


        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon GYM OFFENSE to Replace Pattern
     * Input: ReplacePattern.GYM_OFFENSE.get  
     *        Id : Venusaur, move1 : Aqua Tail, move2 : Dig, Attack : 123, Defense : 23, Stamina : 12 ->  get GYM_OFFENSE
     * Expected:
     *          Return GYM_OFFENSE
     */
    
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
        
        String testString = ReplacePattern.GYM_OFFENSE.get(testPokemon);
        
        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon GYM DEFENSE to Replace Pattern
     * Input: ReplacePattern.GYM_DEFENSE.get  
     *        Id : Venusaur, move1 : Aqua Tail, move2 : Dig, Attack : 123, Defense : 23, Stamina : 12 ->  get GYM_DEFENSE
     * Expected:
     *          Return GYM_DEFENSE
     */
    
    @Test
    public void ReplacePatternGYM_DEFENSETest() {
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
        
        String testString = ReplacePattern.GYM_DEFENSE.get(testPokemon);
        
        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon DUEL ABILITY RATING to Replace Pattern
     * Input: ReplacePattern.DUEL_ABILITY_RATING.get  
     *        Id : Venusaur, move1 : Aqua Tail, move2 : Dig, Attack : 123, Defense : 23, Stamina : 12 ->  get DUEL_ABILITY_RATING
     * Expected:
     *          Return DUEL_ABILITY_RATING
     */
    
    @Test
    public void ReplacePatternDUEL_ABILITY_RATINGTest() {
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
        
        String testString = ReplacePattern.DUEL_ABILITY_RATING.get(testPokemon);
        
        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon GYM OFFENSE RATING to Replace Pattern
     * Input: ReplacePattern.GYM_OFFENSE_RATING.get  
     *        Id : Venusaur, move1 : Aqua Tail, move2 : Dig, Attack : 123 ->  get GYM_OFFENSE_RATING
     * Expected:
     *          Return GYM_OFFENSE_RATING
     */
    
    @Test
    public void ReplacePatternGYM_OFFENSE_RATINGTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        PokemonMoveOuterClass.PokemonMove Move1 = PokemonMoveOuterClass.PokemonMove.AQUA_TAIL;
        PokemonMoveOuterClass.PokemonMove Move2 = PokemonMoveOuterClass.PokemonMove.DIG;
        
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        EasyMock.expect(testPokemon.getMove1()).andReturn(Move1);
        EasyMock.expect(testPokemon.getMove2()).andReturn(Move2);        
        EasyMock.expect(testPokemon.getIndividualAttack()).andReturn(123);
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.GYM_OFFENSE_RATING.get(testPokemon);
        
        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon GYM DEFENSE RATING to Replace Pattern
     * Input: ReplacePattern.GYM_DEFENSE_RATING.get  
     *        Id : Venusaur, move1 : Aqua Tail, move2 : Dig, Attack : 123, Defense : 23, Stamina : 12 ->  get GYM_DEFENSE_RATING
     * Expected:
     *          Return GYM_DEFENSE_RATING
     */
    
    @Test
    public void ReplacePatternGYM_DEFENSE_RATINGTest() {
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
        
        String testString = ReplacePattern.GYM_DEFENSE_RATING.get(testPokemon);
        
        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon MAX CP to Replace Pattern
     * Input: ReplacePattern.MAX_CP.get  
     *        MaxCp : 123 ->  get MaxCp "123"
     * Expected:
     *          Return "123"
     */
    
    @Test
    public void ReplacePatternMAX_CPTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        
        try {
            EasyMock.expect(testPokemon.getMaxCp()).andReturn(123);
        } catch (NoSuchItemException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.MAX_CP.get(testPokemon);
        
        assertEquals(testString,"123");

        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon MOVE 1 TYPE to Replace Pattern
     * Input: ReplacePattern.MOVE_TYPE_1.get  
     *        Id : Venusaur, move1 : Aqua Tail ->  get MOVE_TYPE_1
     * Expected:
     *          Return MOVE_TYPE_1
     */
    
    @Test
    public void ReplacePatternMOVE_TYPE_1Test() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        PokemonMoveOuterClass.PokemonMove Move1 = PokemonMoveOuterClass.PokemonMove.AQUA_TAIL;
        
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        EasyMock.expect(testPokemon.getMove1()).andReturn(Move1);
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.MOVE_TYPE_1.get(testPokemon);

        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon MOVE 2 TYPE to Replace Pattern
     * Input: ReplacePattern.MOVE_TYPE_2.get  
     *        Id : Venusaur, move2 : Aqua Tail ->  get MOVE_TYPE_2
     * Expected:
     *          Return MOVE_TYPE_2
     */
    
    @Test
    public void ReplacePatternMOVE_TYPE_2Test() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        PokemonMoveOuterClass.PokemonMove Move2 = PokemonMoveOuterClass.PokemonMove.AQUA_TAIL;
        
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        EasyMock.expect(testPokemon.getMove2()).andReturn(Move2);
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.MOVE_TYPE_2.get(testPokemon);

        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon MOVE 1 TYPE UNI to Replace Pattern
     * Input: ReplacePattern.MOVE_TYPE_1_UNI.get  
     *        Id : Venusaur, move1 : Aqua Tail ->  get MOVE_TYPE_1_UNI
     * Expected:
     *          Return MOVE_TYPE_1_UNI
     */
    
    @Test
    public void ReplacePatternMOVE_TYPE_1_UNITest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        PokemonMoveOuterClass.PokemonMove Move1 = PokemonMoveOuterClass.PokemonMove.AQUA_TAIL;
        
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        EasyMock.expect(testPokemon.getMove1()).andReturn(Move1);
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.MOVE_TYPE_1_UNI.get(testPokemon);

        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon MOVE 2 TYPE UNI to Replace Pattern
     * Input: ReplacePattern.MOVE_TYPE_2_UNI.get  
     *        Id : Venusaur, move2 : Aqua Tail ->  get MOVE_TYPE_2_UNI
     * Expected:
     *          Return MOVE_TYPE_2_UNI
     */
    
    @Test
    public void ReplacePatternMOVE_TYPE_2_UNITest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        PokemonMoveOuterClass.PokemonMove Move2 = PokemonMoveOuterClass.PokemonMove.AQUA_TAIL;
        
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        EasyMock.expect(testPokemon.getMove2()).andReturn(Move2);
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.MOVE_TYPE_2_UNI.get(testPokemon);

        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon DPS Move 1 to Replace Pattern
     * Input: ReplacePattern.DPS_1.get  
     *        Id : Venusaur, move1 : Aqua Tail, move2 : Dig ->  get DPS_1
     * Expected:
     *          Return DPS_1
     */
    
    @Test
    public void ReplacePatternDPS_1Test() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        PokemonMoveOuterClass.PokemonMove Move1 = PokemonMoveOuterClass.PokemonMove.AQUA_TAIL;
        PokemonMoveOuterClass.PokemonMove Move2 = PokemonMoveOuterClass.PokemonMove.DIG;
        
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        EasyMock.expect(testPokemon.getMove1()).andReturn(Move1);
        EasyMock.expect(testPokemon.getMove2()).andReturn(Move2);
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.DPS_1.get(testPokemon);
        
        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon DPS Move 2 to Replace Pattern
     * Input: ReplacePattern.DPS_2.get  
     *        Id : Venusaur, move1 : Aqua Tail, move2 : Dig ->  get DPS_2
     * Expected:
     *          Return DPS_2
     */
    
    @Test
    public void ReplacePatternDPS_2Test() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        PokemonMoveOuterClass.PokemonMove Move1 = PokemonMoveOuterClass.PokemonMove.AQUA_TAIL;
        PokemonMoveOuterClass.PokemonMove Move2 = PokemonMoveOuterClass.PokemonMove.DIG;
        
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        EasyMock.expect(testPokemon.getMove1()).andReturn(Move1);
        EasyMock.expect(testPokemon.getMove2()).andReturn(Move2);
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.DPS_2.get(testPokemon);
        
        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon MOVE 1 RATING to Replace Pattern
     * Input: ReplacePattern.MOVE_1_RATING.get  
     *        Id : Venusaur, move1 : Aqua Tail, move2 : Dig ->  get MOVE_1_RATING
     * Expected:
     *          Return MOVE_1_RATING
     */
    
    @Test
    public void ReplacePatternMOVE_1_RATINGTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        PokemonMoveOuterClass.PokemonMove Move1 = PokemonMoveOuterClass.PokemonMove.AQUA_TAIL;
        PokemonMoveOuterClass.PokemonMove Move2 = PokemonMoveOuterClass.PokemonMove.DIG;
        
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        EasyMock.expect(testPokemon.getMove1()).andReturn(Move1);
        EasyMock.expect(testPokemon.getMove2()).andReturn(Move2);
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.MOVE_1_RATING.get(testPokemon);
        
        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon MOVE 2 RATING to Replace Pattern
     * Input: ReplacePattern.MOVE_2_RATING.get  
     *        Id : Venusaur, move1 : Aqua Tail, move2 : Dig ->  get MOVE_2_RATING
     * Expected:
     *          Return MOVE_2_RATING
     */
    
    @Test
    public void ReplacePatternMOVE_2_RATINGTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        PokemonMoveOuterClass.PokemonMove Move1 = PokemonMoveOuterClass.PokemonMove.AQUA_TAIL;
        PokemonMoveOuterClass.PokemonMove Move2 = PokemonMoveOuterClass.PokemonMove.DIG;
        
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        EasyMock.expect(testPokemon.getMove1()).andReturn(Move1);
        EasyMock.expect(testPokemon.getMove2()).andReturn(Move2);
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.MOVE_2_RATING.get(testPokemon);
        
        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon TYPE 1 to Replace Pattern
     * Input: ReplacePattern.TYPE_1.get  
     *        Id : Venusaur, Settings : null->  get TYPE_1
     * Expected:
     *          Return TYPE_1
     */
    
    @Test
    public void ReplacePatternTYPE_1Test() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        PokemonSettingsOuterClass.PokemonSettings testSetting = null; 
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        EasyMock.expect(testPokemon.getSettings()).andReturn(testSetting);
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.TYPE_1.get(testPokemon);
        
        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon TYPE 2 to Replace Pattern
     * Input: ReplacePattern.TYPE_2.get  
     *        Id : Venusaur, Settings = null ->  get TYPE_2
     * Expected:
     *          Return TYPE_2
     */
    
    @Test
    public void ReplacePatternTYPE_2Test() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        PokemonSettingsOuterClass.PokemonSettings testSetting = null; 
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        EasyMock.expect(testPokemon.getSettings()).andReturn(testSetting);
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.TYPE_2.get(testPokemon);
        
        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon TYPE 1 UNI to Replace Pattern
     * Input: ReplacePattern.TYPE_1_UNI.get  
     *        Id : Venusaur, Settings : null->  get TYPE_1_UNI
     * Expected:
     *          Return TYPE_1_UNI
     */
    
    @Test
    public void ReplacePatternTYPE_1_UNITest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        PokemonSettingsOuterClass.PokemonSettings testSetting = null; 
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        EasyMock.expect(testPokemon.getSettings()).andReturn(testSetting);
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.TYPE_1_UNI.get(testPokemon);
        
        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon TYPE 2 UNI to Replace Pattern
     * Input: ReplacePattern.TYPE_2_UNI.get  
     *        Id : Venusaur, Settings : null->  get TYPE_2_UNI
     * Expected:
     *          Return TYPE_2_UNI
     */
    
    @Test
    public void ReplacePatternTYPE_2_UNITest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR;
        PokemonSettingsOuterClass.PokemonSettings testSetting = null; 
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        EasyMock.expect(testPokemon.getSettings()).andReturn(testSetting);
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.TYPE_2_UNI.get(testPokemon);
        
        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon SWORD UNICODE to Replace Pattern
     * Input: ReplacePattern.SWORD_UNICODE.get  
     *        "sword" ->  get SWORD_UNICODE
     * Expected:
     *          Return SWORD_UNICODE
     */
    
    @Test
    public void ReplacePatternSWORD_UNICODETest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.SWORD_UNICODE.get(testPokemon);

        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon SHIELD UNICODE to Replace Pattern
     * Input: ReplacePattern.SHIELD_UNICODE.get  
     *        "shield" ->  get SHIELD_UNICODE
     * Expected:
     *          Return SHIELD_UNICODE
     */
    
    @Test
    public void ReplacePatternSHIELD_UNICODETest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.SHIELD_UNICODE.get(testPokemon);

        assertNotNull(testString);

        EasyMock.verify(testPokemon);
    }
    
    /**
     * Purpose: Get Pokemon ID to Replace Pattern
     * Input: ReplacePattern.ID.get  
     *        Id : Venusaur ->  get ID "003"
     * Expected:
     *          Return "003"
     */
    
    @Test
    public void ReplacePatternIDTest() {
        Pokemon testPokemon = EasyMock.createMock(Pokemon.class);
        PokemonIdOuterClass.PokemonId testId = PokemonIdOuterClass.PokemonId.VENUSAUR; 
        EasyMock.expect(testPokemon.getPokemonId()).andReturn(testId);
        EasyMock.replay(testPokemon);
        
        String testString = ReplacePattern.ID.get(testPokemon);

        assertEquals(testString,"003");

        EasyMock.verify(testPokemon);
    }
}
