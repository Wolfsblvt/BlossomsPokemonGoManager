package testOracle;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.pokemon.Pokemon;

import javafx.collections.ObservableList;
import me.corriekay.pokegoutil.data.models.PokemonBag;
import okhttp3.OkHttpClient;

public class PokemonBagTest {
    private PokemonBag pokemonBag;
    
    //TODO
    @Test
    public void testConstructor() {
        List<Pokemon> list = new ArrayList<Pokemon>();
        pokemonBag = new PokemonBag(list);
        assertEquals(0, pokemonBag.getNumberPokemon());
    }

    @Test
    public void testGetAllPokemon() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetNumberPokemon() {
        fail("Not yet implemented");
    }

}
