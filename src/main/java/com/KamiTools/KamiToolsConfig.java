//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.KamiTools;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("KamiTools")
public interface KamiToolsConfig extends Config {
	@ConfigItem(
			keyName = "greeting",
			name = "Welcome Greeting",
			description = "The message to show to the user when they login"
	)
	default String greeting() {
		return "Heiiio";
	}

	@ConfigItem(
			keyName = "posX",
			name = "posX",
			description = "nyan",
			position = 1
	)
	default int posx() {
		return 100;
	}

	@ConfigItem(
			keyName = "posY",
			name = "posY",
			description = "nyan",
			position = 1
	)
	default int posY() {
		return 100;
	}

	@ConfigItem(
			keyName = "widthX",
			name = "widthX",
			description = "nyan",
			position = 1
	)
	default int widthX() {
		return 100;
	}

	@ConfigItem(
			keyName = "widthY",
			name = "widthY",
			description = "nyan",
			position = 1
	)
	default int widthY() {
		return 100;
	}

	@ConfigItem(
			keyName = "posX2",
			name = "posX2",
			description = "nyan",
			position = 1
	)
	default int posx2() {
		return 100;
	}

	@ConfigItem(
			keyName = "posY2",
			name = "posY2",
			description = "nyan",
			position = 1
	)
	default int posY2() {
		return 100;
	}

	@ConfigItem(
			keyName = "widthX2",
			name = "widthX2",
			description = "nyan",
			position = 1
	)
	default int widthX2() {
		return 100;
	}

	@ConfigItem(
			keyName = "widthY2",
			name = "widthY2",
			description = "nyan",
			position = 1
	)
	default int widthY2() {
		return 100;
	}
}
