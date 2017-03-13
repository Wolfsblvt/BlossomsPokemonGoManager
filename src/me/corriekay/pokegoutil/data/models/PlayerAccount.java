package me.corriekay.pokegoutil.data.models;

import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.api.player.PlayerProfile.Currency;
import com.pokegoapi.exceptions.InvalidCurrencyException;

public class PlayerAccount {

    private final PlayerProfile playerProfile;

    /**
     * Instantiate PlayerAccount controller.
     *
     * @param playerProfile player profile of logged on user
     */
    public PlayerAccount(final PlayerProfile playerProfile) {
        this.playerProfile = playerProfile;
    }

    /**
     * Get number of stardust.
     *
     * @return number of stardust
     * @throws InvalidCurrencyException invalid currency
     */
    public int getStardust() throws InvalidCurrencyException {
        return playerProfile.getCurrency(Currency.STARDUST);
    }
}
