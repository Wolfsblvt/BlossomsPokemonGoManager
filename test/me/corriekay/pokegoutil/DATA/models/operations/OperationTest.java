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
import me.corriekay.pokegoutil.GUI.enums.OperationId;

@RunWith(value = Parameterized.class)
public class OperationTest {

    private OperationId operationId;
    private ObservableListWrapper<PokemonModel> pokemonList;

    @Parameters(name = "{index}: {0}")
    public static Collection<OperationId> data() {
        return Arrays.asList(OperationId.values());
    }

    public OperationTest(OperationId operationId) {
        this.operationId = operationId;

        // Create list with 1 null value for testing
        List<PokemonModel> list = new ArrayList<>();
        list.add(null);
        pokemonList = new ObservableListWrapper<>(list);
    }

    @Test
    public void TestGenerateOperations() {
        Operation.generateOperations(operationId, pokemonList);
    }
}
