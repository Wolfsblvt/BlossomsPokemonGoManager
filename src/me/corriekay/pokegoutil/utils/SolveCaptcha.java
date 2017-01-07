/*
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.corriekay.pokegoutil.utils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.util.CaptchaSolveHelper;
import com.pokegoapi.util.Log;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * Opens a window for captcha solving if needed.
 */
public class SolveCaptcha extends JFrame {

    private static final long serialVersionUID = -834873734847675970L;
    private final JFXPanel jfxPanel = new JFXPanel();
    private WebEngine engine;

    /**
     * Default constructor that creates the components and listeners.
     * @param api
     * @param challengeURL
     */
    public SolveCaptcha(final PokemonGo api, final String challengeURL) {
        super("Solve Captcha");
        initComponents(challengeURL);
        //Register listener to receive the token when the captcha has been solved from inside the WebView.
        CaptchaSolveHelper.Listener listener = new CaptchaSolveHelper.Listener() {
            @Override
            public void onTokenReceived(String token) {
                System.out.println("Token received: " + token + "!");
                //Remove this listener as we no longer need to listen for tokens, the captcha has been solved.
                CaptchaSolveHelper.removeListener(this);
                try {
                    //Close this window, it not valid anymore.
                    SolveCaptcha.this.setVisible(false);
                    SolveCaptcha.this.dispose();

                    if (api.verifyChallenge(token)) {
                        System.out.println("Captcha was correctly solved!");
                    } else {
                        System.out.println("Captcha was incorrectly solved! Please try again.");
                        /*
                                    Ask for a new challenge url, don't need to check the result,
                                    because the LoginListener will be called when this completed.
                         */
                        api.checkChallenge();
                    }
                } catch (Exception e) {
                    Log.e("Main", "Error while solving captcha!", e);
                }
            }
        };
        CaptchaSolveHelper.registerListener(listener);

        getContentPane().add(jfxPanel);
        setSize(500, 500);
        //Don't allow this window to be closed
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Please solve the captcha before closing the window!");
            }
        });
    }

    /**
     * Initialize the components after they are created.
     * @param challengeURL
     */
    private void initComponents(String challengeURL) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                WebView view = new WebView();
                engine = view.getEngine();
                //Set UserAgent so the captcha shows correctly in the WebView.
                engine.setUserAgent(CaptchaSolveHelper.USER_AGENT);
                engine.load(challengeURL);
                jfxPanel.setScene(new Scene(view));
            }
        });
    }
}
