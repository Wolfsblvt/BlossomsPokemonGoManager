package me.corriekay.pokegoutil.DATA.models;

import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.api.player.PlayerProfile.Currency;
import com.pokegoapi.exceptions.InvalidCurrencyException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

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
     * @throws LoginFailedException login failed
     * @throws RemoteServerException server error
     */
    public int getStardust() throws InvalidCurrencyException, LoginFailedException, RemoteServerException {
        return playerProfile.getCurrency(Currency.STARDUST);
    }
}
