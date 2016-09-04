package me.corriekay.pokegoutil.DATA.models.operations;

import static org.hamcrest.CoreMatchers.is;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.sun.javafx.collections.ObservableListWrapper;

import me.corriekay.pokegoutil.DATA.models.PokemonModel;
import me.corriekay.pokegoutil.GUI.enums.OperationId;

@RunWith(value = Parameterized.class)
public class OperationTest {

    private final OperationId operationId;
    private final ObservableListWrapper<PokemonModel> pokemonList;

    @Parameters(name = "{index}: {0}")
    public static Collection<OperationId> data() {
        return Arrays.asList(OperationId.values());
    }

    /**
     * Instantiate a OperationTest using the parameters from data().
     *
     * @param operationId operationId to test
     */
    public OperationTest(final OperationId operationId) {
        this.operationId = operationId;

        // Create list with 1 null value for testing
        final List<PokemonModel> list = new ArrayList<>();
        list.add(null);
        pokemonList = new ObservableListWrapper<>(list);
    }

    @Test
    public void testGenerateOperations() {
        final List<Operation> operations = Operation.generateOperations(operationId, pokemonList);
        Assert.assertThat("Created correct operation", operations.get(0).getOperationId(), is(operationId));
    }
}
