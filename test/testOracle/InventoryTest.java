package testOracle;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import me.corriekay.pokegoutil.data.models.Inventory;

public class InventoryTest {
    
    private Inventory inventory;

    /**
     * Purpose: Construct and destroy Inventory class.
     * input: void
     * Expected:
     *          inventory is not null. ->
     *          inventory is null.
     */
    @Test
    public void testInventory() {
        inventory = new Inventory();
        assertNotNull(inventory);
        inventory = null;
        assertNull(inventory);
    }
}
