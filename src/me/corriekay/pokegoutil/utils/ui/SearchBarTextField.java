package me.corriekay.pokegoutil.utils.ui;

import java.util.function.Consumer;

import javax.swing.JTextField;

import me.corriekay.pokegoutil.utils.StringLiterals;
import me.corriekay.pokegoutil.utils.helpers.LDocumentListener;

/**
 * SearchBar component to encapsulate contents related to search mechanics.
 */
public class SearchBarTextField extends JTextField {

    private static final long serialVersionUID = -8207087504009285250L;
    
    /**
     * Default constructor for creating the JTextField with empty string.
     * @param filterMethod method that will be called whenever this textField changes.
     */
    public SearchBarTextField(final Consumer<String> filterMethod) {
        super("");
        
        LDocumentListener.addChangeListener(this, e -> {
            String search = getText().replaceAll(StringLiterals.SPACE, "").replaceAll(StringLiterals.UNDERSCORE, "").replaceAll("snek", "ekans")
                    .toLowerCase();
            if ("searchpokémon...".equals(search)) {
                search = "";
            }
            filterMethod.accept(search);
        });
        new GhostText(this, "Search Pokémon...");
    }

}
