package me.corriekay.pokegoutil.DATA.models.operations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.sun.javafx.collections.ObservableListWrapper;

import me.corriekay.pokegoutil.DATA.models.PokemonModel;
import me.corriekay.pokegoutil.GUI.enums.OperationID;

@RunWith(value = Parameterized.class)
public class OperationTest {

    private OperationID operationID;
    private ObservableListWrapper<PokemonModel> pokemonList;

    @Parameters(name = "{index}: {0}")
    public static Collection<OperationID> data() {
        return Arrays.asList(OperationID.values());
    }

    public OperationTest(OperationID operationID) {
        this.operationID = operationID;

        // Create list with 1 null value for testing
        List<PokemonModel> list = new ArrayList<>();
        list.add(null);
        pokemonList = new ObservableListWrapper<>(list);
    }

    @Test
    public void TestGenerateOperations() {
        Operation.generateOperations(operationID, pokemonList);
    }
}
