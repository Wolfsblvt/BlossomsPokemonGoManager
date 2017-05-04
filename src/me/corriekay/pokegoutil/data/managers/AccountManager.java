package me.corriekay.pokegoutil.data.managers;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.auth.CredentialProvider;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.CaptchaActiveException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.exceptions.hash.HashException;

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
            deleteSavedLoginData();
        }

        switch (type) {
            case ALL:
                deleteGoogleAuthLoginData();
                deleteGoogleAppLoginData();
                deletePtcLoginData();
                break;
            case GOOGLE_AUTH:
                deleteGoogleAuthLoginData();
                break;
            case GOOGLE_APP_PASSWORD:
                deleteGoogleAppLoginData();
                break;
            case PTC:
                deletePtcLoginData();
                break;
            default:
        }
    }

    private void deleteGoogleAuthLoginData() {
        config.delete(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN);
    }

    private void deleteSavedLoginData() {
        config.delete(ConfigKey.LOGIN_SAVE_AUTH);
    }

    private void deletePtcLoginData() {
        config.delete(ConfigKey.LOGIN_PTC_USERNAME);
        config.delete(ConfigKey.LOGIN_PTC_PASSWORD);
    }

    private void deleteGoogleAppLoginData() {
        config.delete(ConfigKey.LOGIN_GOOGLE_APP_USERNAME);
        config.delete(ConfigKey.LOGIN_GOOGLE_APP_PASSWORD);
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
        PlayerProfile result = null;
        if(go != null)
            result = go.getPlayerProfile();
        
        return result;
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
        BpmResult result = new BpmResult("Invalid Login Type");
        switch (loginData.getLoginType()) {
            case GOOGLE_AUTH:
                if (loginData.isValidGoogleLogin()) {
                    result = logOnGoogleAuth(loginData);
                }
                break;
            case PTC:
                if (loginData.isValidPtcLogin()) {
                    result = logOnPtc(loginData);
                }
                break;
            default:
        }
        return result;
    }

    /**
     * Login using GoogleAuth.
     *
     * @param loginData the login data used to login
     * @return results of the login
     */
    private BpmResult logOnGoogleAuth(final LoginData loginData) {
        OkHttpClient http;
        CredentialProvider credentialProvider = null;
        http = new OkHttpClient();

        final String authCode = loginData.getToken();
        final boolean saveAuth = config.getBool(ConfigKey.LOGIN_SAVE_AUTH);

        BpmResult result = new BpmResult();
        
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

            credentialProvider = provider;
            if (saveAuth && !shouldRefresh) {
                config.setString(ConfigKey.LOGIN_GOOGLE_AUTH_TOKEN, provider.getRefreshToken());
            } else if (!saveAuth) {
                deleteLoginData(LoginType.GOOGLE_AUTH);
            }
        } catch (LoginFailedException | RemoteServerException | CaptchaActiveException e) {
            deleteLoginData(LoginType.GOOGLE_APP_PASSWORD);
            result =  new BpmResult(e.getMessage());
        }
        
        if (result.isSuccess()) {
            try {
                prepareLogin(credentialProvider, http);
            } catch (LoginFailedException | RemoteServerException | CaptchaActiveException | HashException e) {
                deleteLoginData(LoginType.ALL);
                result = new BpmResult(e.getMessage());
            }
        }
        
        return result;
    }

    /**
     * Login using PTC.
     *
     * @param loginData the login data used to login
     * @return results of the login
     */
    private BpmResult logOnPtc(final LoginData loginData) {
        OkHttpClient http;
        CredentialProvider credentialProvider;
        http = new OkHttpClient();

        final String username = loginData.getUsername();
        final String password = loginData.getPassword();
        final boolean saveAuth = config.getBool(ConfigKey.LOGIN_SAVE_AUTH);

        try {
            credentialProvider = new PtcCredentialProvider(http, username, password);
            config.setString(ConfigKey.LOGIN_PTC_USERNAME, username);
            if (saveAuth) {
                config.setString(ConfigKey.LOGIN_PTC_PASSWORD, password);
            } else {
                deleteLoginData(LoginType.PTC);
            }
        } catch (LoginFailedException | RemoteServerException | CaptchaActiveException e) {
            deleteLoginData(LoginType.PTC);
            return new BpmResult(e.getMessage());
        }

        try {
            prepareLogin(credentialProvider, http);
            return new BpmResult();
        } catch (LoginFailedException | RemoteServerException | CaptchaActiveException | HashException e) {
            deleteLoginData(LoginType.ALL);
            return new BpmResult(e.getMessage());
        }
    }

    /**
     * Do login process and initialize GUI.
     *
     * @param cp   contains the credential provider
     * @param http http client
     * @throws LoginFailedException  login failed
     * @throws RemoteServerException server error
     * @throws CaptchaActiveException captcha active error
     * @throws HashException 
     */
    private void prepareLogin(final CredentialProvider cp, final OkHttpClient http)
            throws LoginFailedException, RemoteServerException, CaptchaActiveException, HashException {
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
