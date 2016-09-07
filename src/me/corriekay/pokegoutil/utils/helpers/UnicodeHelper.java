package me.corriekay.pokegoutil.utils.helpers;

import com.pokegoapi.api.pokemon.PokemonType;

public enum UnicodeHelper {

	NUMBER_0(0x24FF,"0"),
	NUMBER_1(0x278A,"1"),
	NUMBER_2(0x278B,"2"),
	NUMBER_3(0x278C,"3"),
	NUMBER_4(0x278D,"4"),
	NUMBER_5(0x278E,"5"),
	NUMBER_6(0x278F,"6"),
	NUMBER_7(0x2790,"7"),
	NUMBER_8(0x2791,"8"),
	NUMBER_9(0x2792,"9"),
	NUMBER_10(0x2793,"10"),
	NUMBER_11(0x24EB,"11"),
	NUMBER_12(0x24EC,"12"),
	NUMBER_13(0x24ED,"13"),
	NUMBER_14(0x24EE,"14"),
	NUMBER_15(0x24EF,"15"),
	TYPE_BUG(0x2042,PokemonType.BUG.toString()),
	TYPE_DARK(0x263D,PokemonType.DARK.toString()),
	TYPE_DRAGON(0x26E9,PokemonType.DRAGON.toString()),
	TYPE_ELETRIC(0x2607,PokemonType.ELECTRIC.toString()),
	TYPE_FAIRY(0x2764,PokemonType.FAIRY.toString()),
	TYPE_FIGHTING(0x270A,PokemonType.FIGHTING.toString()),
	TYPE_FIRE(0x2668,PokemonType.FIRE.toString()),
	TYPE_FLYING(0x2708,PokemonType.FLYING.toString()),
	TYPE_GHOST(0x26B0,PokemonType.GHOST.toString()),
	TYPE_GRASS(0x2E19,PokemonType.GRASS.toString()),
	TYPE_GROUND(0x26F0,PokemonType.GROUND.toString()),
	TYPE_ICE(0x2744,PokemonType.ICE.toString()),
	TYPE_NORMAL(0x2734,PokemonType.NORMAL.toString()),
	TYPE_POISON(0x2620,PokemonType.POISON.toString()),
	TYPE_PSYCHIC(0x269B,PokemonType.PSYCHIC.toString()),
	TYPE_ROCK(0x25C9,PokemonType.ROCK.toString()),
	TYPE_STEEL(0x26D3,PokemonType.STEEL.toString()),
	TYPE_WATER(0x26C6,PokemonType.WATER.toString());

    private int hexaCode;
	private String identifier;

	UnicodeHelper(int hexaCode, String identifier) {
        this.hexaCode = hexaCode;
        this.identifier = identifier;
    }
	public static String get(String identifier)
	{
		for (UnicodeHelper uni : values())
		{
			if(uni.identifier.equals(identifier)) return Character.toString((char) uni.hexaCode);
		}
		return "";
	}
}
