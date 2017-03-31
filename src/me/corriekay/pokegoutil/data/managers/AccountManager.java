package me.corriekay.pokegoutil.data.managers;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.auth.CredentialProvider;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.request.LoginFailedException;
import com.pokegoapi.exceptions.request.RequestFailedException;

import me.corriekay.pokegoutil.data.enums.LoginType;
import me.corriekay.pokegoutil.data.models.BpmResult;
import me.corriekay.pokegoutil.data.models.LoginData;
import me.corriekay.pokegoutil.data.models.PlayerAccount;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.helpers.LoginHelper;
import okhttp3.OkHttpClient;

/**
 * This controller does the login/log off, and different account information (aka player data).
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

    /**
     * Deletes the specified LoginType from the config.json.
     * <p>
     * <p>Also SaveAuth settings will be deleted
     *
     * @param type the LoginType to be removed
     */
    private void deleteLoginData(final LoginType type) {
        deleteLoginData(type, true);
    }

    /**
     * Deletes the specific LoginType from config.json. If deleteSaveAuth is true, SaveAuth settings will be deleted
     *
     * @param type           the LoginType to be removed
     * @param deleteSaveAuth if true, SaveAuth will be deleted
     */
    private void deleteLoginData(final LoginType type, final boolean deleteSaveAuth) {
        if (deleteSaveAuth) {
            config.delete(ConfigKey.LOGIN_SAVE_AUTH);
        }

        switch (type) {
            case ALL:
                config.delete(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN);
                config.delete(ConfigKey.LOGIN_GOOGLE_APP_USERNAME);
                config.delete(ConfigKey.LOGIN_GOOGLE_APP_PASSWORD);
                config.delete(ConfigKey.LOGIN_PTC_USERNAME);
                config.delete(ConfigKey.LOGIN_PTC_PASSWORD);
                break;
            case GOOGLE_AUTH:
                config.delete(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN);
                break;
            case GOOGLE_APP_PASSWORD:
                config.delete(ConfigKey.LOGIN_GOOGLE_APP_USERNAME);
                config.delete(ConfigKey.LOGIN_GOOGLE_APP_PASSWORD);
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

    /**
     * Returns the logged in playerAccount.
     *
     * @return the logged in playerAccount
     */
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

    /**
     * Login to Pokemon Go with the login data.
     *
     * @param loginData the login data used to login
     * @return results of the login
     */
    public BpmResult login(final LoginData loginData) {
        switch (loginData.getLoginType()) {
            case GOOGLE_AUTH:
                if (loginData.isValidGoogleLogin()) {
                    return logOnGoogleAuth(loginData);
                }
                break;
            case PTC:
                if (loginData.isValidPtcLogin()) {
                    return logOnPtc(loginData);
                }
                break;
            default:
        }
        return new BpmResult("Invalid Login Type");
    }

    /**
     * Login using GoogleAuth.
     *
     * @param loginData the login data used to login
     * @return results of the login
     */
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
            final GoogleUserCredentialProvider provider;
            if (shouldRefresh) {
                provider = new GoogleUserCredentialProvider(http, authCode);
            } else {
                provider = new GoogleUserCredentialProvider(http);
                provider.login(authCode);
            }

            if (provider.isTokenIdExpired()) {
                throw new LoginFailedException();
            }

            cp = provider;
            if (saveAuth && !shouldRefresh) {
                config.setString(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN, provider.getRefreshToken());
            } else if (!saveAuth) {
                deleteLoginData(LoginType.GOOGLE_AUTH);
            }
        } catch (RequestFailedException e) {
            deleteLoginData(LoginType.GOOGLE_APP_PASSWORD);
            return new BpmResult(e.getMessage());
        }

        try {
            prepareLogin(cp, http);
            return new BpmResult();
        } catch (RequestFailedException e) {
            deleteLoginData(LoginType.ALL);
            return new BpmResult(e.getMessage());
        }
    }

    /**
     * Login using PTC.
     *
     * @param loginData the login data used to login
     * @return results of the login
     */
    private BpmResult logOnPtc(final LoginData loginData) {
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
        } catch (RequestFailedException e) {
            deleteLoginData(LoginType.PTC);
            return new BpmResult(e.getMessage());
        }

        try {
            prepareLogin(cp, http);
            return new BpmResult();
        } catch (RequestFailedException e) {
            deleteLoginData(LoginType.ALL);
            return new BpmResult(e.getMessage());
        }
    }

    /**
     * Do login process and initialize GUI.
     *
     * @param cp   contains the credential provider
     * @param http http client
     * @throws RequestFailedException request failed
     */
    private void prepareLogin(final CredentialProvider cp, final OkHttpClient http)
            throws RequestFailedException {
        go = new PokemonGo(http);
        LoginHelper.login(go, cp, api -> {
            playerAccount = new PlayerAccount(go.getPlayerProfile());
            initOtherControllers();
        });
    }

    /**
     * The value will be saved for saveAuth in config.json.
     *
     * @param shouldSave settings for saveAuth
     */
    public void setSaveLogin(final boolean shouldSave) {
        config.setBool(ConfigKey.LOGIN_SAVE_AUTH, shouldSave);
    }
}
