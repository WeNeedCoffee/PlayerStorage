package mrriegel.playerstorage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigHandler {

	public static Configuration config;

	public static boolean infiniteSpace, remote;
	public static Map<String, Unit2> apples = new HashMap<>() ;
	public static List<String> appleList = new ArrayList<String>(6) {
		public String get(int index) {
			if (index >= size())
				return "";
			return super.get(index);
		}
	};

	public static void refreshConfig(File file) {
		config = new Configuration(file);
		Gson gson = new Gson();
		infiniteSpace = config.getBoolean("infiniteSpace", Configuration.CATEGORY_GENERAL, false, "Enable infinite inventory.");
		remote = config.getBoolean("remote", Configuration.CATEGORY_GENERAL, false, "Enable remote item to access your inventory without pressing the key.");
		Property prop = config.get(Configuration.CATEGORY_GENERAL, "appleTiers", ImmutableList.builder()//
				.add(new Unit("blockIron", 3200, 32000), new Unit("blockGold", 25600, 256000), new Unit("blockDiamond", 102400, 1024000), new Unit("netherStar", 1000000, 10000000)).build().stream().map(gson::toJson).toArray(String[]::new));
		prop.setLanguageKey("appleTiers");
		prop.setComment("Tiers for apples");
		for (String s : prop.getStringList()) {
			@SuppressWarnings("serial")
			Unit unit = gson.fromJson(s, new TypeToken<Unit>() {
			}.getType());
			if (!apples.containsKey(unit.oreName))
				apples.put(unit.oreName, unit.entry);
			appleList.add(unit.oreName);
		}

		if (config.hasChanged()) {
			config.save();
		}
	}

	static class Unit {
		public String oreName;
		public Unit2 entry;

		public Unit(String oreName, int i1, int i2) {
			super();
			this.oreName = oreName;
			this.entry = new Unit2(i1, i2);
		}
	}

	static class Unit2 {
		public int itemLimit, fluidLimit;

		public Unit2(int itemLimit, int fluidLimit) {
			super();
			this.itemLimit = itemLimit;
			this.fluidLimit = fluidLimit;
		}
	}

}
