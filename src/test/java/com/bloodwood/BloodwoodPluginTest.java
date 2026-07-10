package com.bloodwood;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class BloodwoodPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(BloodwoodPlugin.class);
		RuneLite.main(args);
	}
}
