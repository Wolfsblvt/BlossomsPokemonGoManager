package me.corriekay.pokegoutil.DATA.models;

import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.api.player.PlayerProfile.Currency;
import com.pokegoapi.exceptions.InvalidCurrencyException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

public class PlayerAccount {

    private PlayerProfile playerProfile;

    public PlayerAccount(PlayerProfile playerProfile) {
        this.playerProfile = playerProfile;
    }
    
    public int getStardust() throws InvalidCurrencyException, LoginFailedException, RemoteServerException{
        return playerProfile.getCurrency(Currency.STARDUST);
    }
}
