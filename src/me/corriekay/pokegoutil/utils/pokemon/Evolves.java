package me.corriekay.pokegoutil.utils.pokemon;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

import static java.util.stream.Collectors.*;

public class Evolves {
    private static class Evolve {
        String displayName;
        int candy;
        int toEvolve;
        int pokemons = 1;
        int count = -1;
        int transfers;
        int evolvedTransferred;

        Evolve(Pokemon p) {
            displayName = PokeHandler.getLocalPokeName(p.getMeta().getNumber());
            try {
                candy = p.getCandy();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            toEvolve = p.getCandiesToEvolve();
        }

        Evolve add(Evolve other) {
            pokemons++;
            return this;
        }

        void findCount() {
            count = 0;
            while (canEvolve()) evolve();
            while (canEvolveWithTransfers()) {
                transfer();
                if (canEvolve()) evolve();
            }
        }

        boolean canEvolve() {
            return candy >= toEvolve && pokemons > 0;
        }

        boolean canEvolveWithTransfers() {
            int transfersAvailable = pokemons - 1 + count - evolvedTransferred;
            return candy + transfersAvailable >= toEvolve && pokemons > 0;
        }

        void transfer() {
            if (evolvedTransferred < count) {
                evolvedTransferred++;
                candy++;
                return;
            }
            pokemons--;
            candy++;
            transfers++;
        }

        void evolve() {
            candy -= toEvolve;
            candy++;
            pokemons--;
            count++;
        }

        int count() {
            return count;
        }

        boolean possible() {
            if (count == -1) {
                findCount();
            }
            return count > 0;
        }

        @Override
        public String toString() {
            return displayName + " : " + count + (transfers > 0 ? ", transfer " + transfers + " in advance": "") +
                    (evolvedTransferred > 0 ? ", transfer " + evolvedTransferred + " evolved" : "");
        }

        static boolean isAbleTo(Pokemon p) {
            return !p.getIsEgg() && p.getEvolutionForm().getEvolutionStage() == 0 && p.getCandiesToEvolve() > 0;
        }
    }

    private static List<Evolve> calculate(PokemonGo go) throws LoginFailedException, RemoteServerException {
        return go.getInventories()
                 .getPokebank()
                 .getPokemons()
                 .stream()
                 .filter(Evolve::isAbleTo)
                 .collect(groupingBy(p -> p.getMeta().getNumber(), TreeMap::new, mapping(Evolve::new, reducing(Evolve::add))))
                 .values()
                 .stream()
                 .map(Optional::get)
                 .filter(Evolve::possible)
                 .collect(toList());
    }

    public static void print(PokemonGo go) throws LoginFailedException, RemoteServerException {
        List<Evolve> evolves = calculate(go);
        System.out.println("Evolves that can be done with Lucky Egg:");
        evolves.forEach(System.out::println);
        System.out.println("Total: " + evolves.stream().mapToInt(Evolve::count).sum());
    }
}
