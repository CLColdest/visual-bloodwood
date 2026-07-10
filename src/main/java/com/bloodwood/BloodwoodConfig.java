package com.bloodwood;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("bloodwood")
public interface BloodwoodConfig extends Config
{
	@ConfigItem(
		position = 1,
		keyName = "showTreeState",
		name = "Show tree state",
		description = "Displays passive state indicators for nearby bloodwood trees."
	)
	default boolean showTreeState()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "showTreeSpots",
		name = "Show tree spots",
		description = "Displays tile indicators for Bloodwood tree interaction spots."
	)
	default boolean showTreeSpots()
	{
		return true;
	}

	@ConfigItem(
		position = 3,
		keyName = "showSessionStats",
		name = "Show session stats",
		description = "Displays passive bloodwood sap session statistics."
	)
	default boolean showSessionStats()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		position = 4,
		keyName = "noBucketColor",
		name = "No bucket color",
		description = "Configures the color for bloodwood trees without a bucket."
	)
	default Color noBucketColor()
	{
		return new Color(160, 40, 40);
	}

	@Alpha
	@ConfigItem(
		position = 5,
		keyName = "bucketColor",
		name = "Bucket color",
		description = "Configures the color for bloodwood trees with a bucket placed."
	)
	default Color bucketColor()
	{
		return Color.CYAN;
	}

	@Alpha
	@ConfigItem(
		position = 6,
		keyName = "choppingColor",
		name = "Chopping color",
		description = "Configures the color for bloodwood trees while opening a wound."
	)
	default Color choppingColor()
	{
		return Color.ORANGE;
	}

	@Alpha
	@ConfigItem(
		position = 7,
		keyName = "bleedingColor",
		name = "Bleeding color",
		description = "Configures the color for bloodwood trees while sap is draining."
	)
	default Color bleedingColor()
	{
		return Color.GREEN;
	}

	@Alpha
	@ConfigItem(
		position = 8,
		keyName = "stalledColor",
		name = "Stalled color",
		description = "Configures the color for bloodwood trees when the bleeding value is no longer decreasing."
	)
	default Color stalledColor()
	{
		return Color.RED;
	}

	@Alpha
	@ConfigItem(
		position = 9,
		keyName = "collectColor",
		name = "Collect color",
		description = "Configures the color for bloodwood trees when a bucket appears ready to collect."
	)
	default Color collectColor()
	{
		return Color.MAGENTA;
	}

	@Alpha
	@ConfigItem(
		position = 10,
		keyName = "spotColor",
		name = "Spot color",
		description = "Configures the color for Bloodwood tree interaction spots."
	)
	default Color spotColor()
	{
		return new Color(230, 160, 40);
	}
}
