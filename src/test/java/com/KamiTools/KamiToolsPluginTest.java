package com.KamiTools;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class KamiToolsPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(KamiToolsPlugin.class);
		RuneLite.main(args);
	}
}