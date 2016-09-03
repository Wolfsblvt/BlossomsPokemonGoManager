package me.corriekay.pokegoutil.DATA.managers;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.auth.CredentialProvider;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import me.corriekay.pokegoutil.DATA.enums.LoginType;
import me.corriekay.pokegoutil.DATA.models.BpmResult;
import me.corriekay.pokegoutil.DATA.models.LoginData;
import me.corriekay.pokegoutil.DATA.models.PlayerAccount;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import okhttp3.OkHttpClient;

/*
 * This controller does the login/log off, and different account information (aka player data)
 */
public final class AccountManager {

    private static AccountManager instance;

    private final ConfigNew config = ConfigNew.getConfig();
    private PokemonGo go;
    private PlayerAccount playerAccount;

    public static AccountManager getInstance() {
        if (instance == null) {
            instance = new AccountManager();
            // DO any required initialization stuff here
        }
        return instance;
    }

    private AccountManager() {

    }

    private void deleteLoginData(final LoginType type) {
        deleteLoginData(type, false);
    }

    private void deleteLoginData(final LoginType type, final boolean justCleanup) {
        if (!justCleanup) {
            config.delete(ConfigKey.LOGIN_SAVE_AUTH);
        }

        switch (type) {
            case BOTH:
                config.delete(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN);
                config.delete(ConfigKey.LOGIN_PTC_USERNAME);
                config.delete(ConfigKey.LOGIN_PTC_PASSWORD);
                break;
            case GOOGLE:
                config.delete(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN);
                break;
            case PTC:
                config.delete(ConfigKey.LOGIN_PTC_USERNAME);
                config.delete(ConfigKey.LOGIN_PTC_PASSWORD);
                break;
            default:
        }
    }

    public LoginData getLoginData() {
        final LoginData loginData = new LoginData(
                config.getString(ConfigKey.LOGIN_PTC_USERNAME),
                config.getString(ConfigKey.LOGIN_PTC_PASSWORD),
                config.getString(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN));

        if (loginData.isValidGoogleLogin()) {
            loginData.setSavedToken(true);
        }

        return loginData;
    }

    public PlayerAccount getPlayerAccount() {
        return playerAccount;
    }

    public PlayerProfile getPlayerProfile() {
        return go != null ? go.getPlayerProfile() : null;
    }

    private void initOtherControllers() {
        InventoryManager.initialize(go);
        PokemonBagManager.initialize(go);
        ProfileManager.initialize(go);
    }

    public BpmResult login(final LoginData loginData) {
        switch (loginData.getLoginType()) {
            case GOOGLE:
                if (loginData.isValidGoogleLogin()) {
                    return logOnGoogleAuth(loginData);
                }
                break;
            case PTC:
                if (loginData.isValidPTCLogin()) {
                    return logOnPTC(loginData);
                }
                break;
            default:
        }
        return new BpmResult("Invalid Login Type");
    }

    private BpmResult logOnGoogleAuth(final LoginData loginData) {
        OkHttpClient http;
        CredentialProvider cp;
        http = new OkHttpClient();

        final String authCode = loginData.getToken();
        final boolean saveAuth = config.getBool(ConfigKey.LOGIN_SAVE_AUTH);

        boolean shouldRefresh = false;
        if (loginData.isSavedToken() && saveAuth) {
            shouldRefresh = true;
        }

        try {
            final GoogleUserCredentialProvider provider = new GoogleUserCredentialProvider(http);
            if (shouldRefresh) {
                provider.refreshToken(authCode);
            } else {
                provider.login(authCode);
            }

            if (provider.isTokenIdExpired()) {
                throw new LoginFailedException();
            }

            cp = provider;
            if (saveAuth && !shouldRefresh) {
                config.setString(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN, provider.getRefreshToken());
            } else {
                deleteLoginData(LoginType.GOOGLE);
            }
        } catch (LoginFailedException | RemoteServerException e) {
            deleteLoginData(LoginType.GOOGLE);
            return new BpmResult(e.getMessage());
        }

        try {
            prepareLogin(cp, http);
            return new BpmResult();
        } catch (LoginFailedException | RemoteServerException e) {
            deleteLoginData(LoginType.BOTH);
            return new BpmResult(e.getMessage());
        }
    }

    private BpmResult logOnPTC(final LoginData loginData) {
        OkHttpClient http;
        CredentialProvider cp;
        http = new OkHttpClient();

        final String username = loginData.getUsername();
        final String password = loginData.getPassword();
        final boolean saveAuth = config.getBool(ConfigKey.LOGIN_SAVE_AUTH);

        try {
            cp = new PtcCredentialProvider(http, username, password);
            config.setString(ConfigKey.LOGIN_PTC_USERNAME, username);
            if (saveAuth) {
                config.setString(ConfigKey.LOGIN_PTC_PASSWORD, password);
            } else {
                deleteLoginData(LoginType.PTC);
            }
        } catch (final Exception e) {
            deleteLoginData(LoginType.PTC);
            return new BpmResult(e.getMessage());
        }

        try {
            prepareLogin(cp, http);
            return new BpmResult();
        } catch (LoginFailedException | RemoteServerException e) {
            deleteLoginData(LoginType.BOTH);
            return new BpmResult(e.getMessage());
        }
    }

    private void prepareLogin(final CredentialProvider cp, final OkHttpClient http)
            throws LoginFailedException, RemoteServerException {
        go = new PokemonGo(cp, http);
        playerAccount = new PlayerAccount(go.getPlayerProfile());
        initOtherControllers();
    }

    public void setSaveLogin(final boolean save) {
        config.setBool(ConfigKey.LOGIN_SAVE_AUTH, save);
    }
}
