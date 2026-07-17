package com.bloodwood;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Notification;

@ConfigGroup("bloodwood")
public interface BloodwoodConfig extends Config
{
	@ConfigSection(
		position = 10,
		name = "Bloodwood Trees",
		description = "Settings for standard Bloodwood trees.",
		closedByDefault = false
	)
	String bloodwoodTreesSection = "bloodwoodTreesSection";

	@ConfigSection(
		position = 30,
		name = "Engorged Bloodwood Tree",
		description = "Settings for the engorged Bloodwood tree.",
		closedByDefault = false
	)
	String engorgedBloodwoodTreeSection = "engorgedBloodwoodTreeSection";

	@ConfigItem(
		position = 0,
		keyName = "showSessionStats",
		name = "Show session stats",
		description = "Displays passive bloodwood sap session statistics."
	)
	default boolean showSessionStats()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "showTreeState",
		name = "Show tree state",
		description = "Displays passive state indicators for nearby Bloodwood trees.",
		section = bloodwoodTreesSection
	)
	default boolean showTreeState()
	{
		return true;
	}

	@ConfigItem(
		position = 3,
		keyName = "showTreeSpots",
		name = "Show tree spots",
		description = "Displays tile indicators for Bloodwood tree interaction spots.",
		section = bloodwoodTreesSection
	)
	default boolean showTreeSpots()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		position = 4,
		keyName = "noBucketColor",
		name = "No bucket color",
		description = "Configures the color for Bloodwood trees without a bucket.",
		section = bloodwoodTreesSection
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
		description = "Configures the color for Bloodwood trees with a bucket placed.",
		section = bloodwoodTreesSection
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
		description = "Configures the color for Bloodwood trees while opening a wound.",
		section = bloodwoodTreesSection
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
		description = "Configures the color for Bloodwood trees while sap is draining.",
		section = bloodwoodTreesSection
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
		description = "Configures the color for Bloodwood trees when the bleeding value is no longer decreasing.",
		section = bloodwoodTreesSection
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
		description = "Configures the color for Bloodwood trees when a bucket appears ready to collect.",
		section = bloodwoodTreesSection
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
		description = "Configures the color for Bloodwood tree interaction spots.",
		section = bloodwoodTreesSection
	)
	default Color spotColor()
	{
		return new Color(230, 160, 40);
	}

	@ConfigItem(
		position = 21,
		keyName = "showEngorgedTreeState",
		name = "Show tree state",
		description = "Displays passive state indicators for the engorged Bloodwood tree.",
		section = engorgedBloodwoodTreeSection
	)
	default boolean showEngorgedTreeState()
	{
		return true;
	}

	@ConfigItem(
		position = 22,
		keyName = "engorgedClickNotification",
		name = "Click notification",
		description = "Notifies when the engorged Bloodwood tree needs another click.",
		section = engorgedBloodwoodTreeSection
	)
	default Notification engorgedClickNotification()
	{
		return Notification.ON;
	}

	@ConfigItem(
		position = 23,
		keyName = "engorgedDrainingCompleteNotification",
		name = "Draining complete notification",
		description = "Notifies when the engorged Bloodwood tree finishes draining.",
		section = engorgedBloodwoodTreeSection
	)
	default Notification engorgedDrainingCompleteNotification()
	{
		return Notification.ON;
	}

	@Alpha
	@ConfigItem(
		position = 25,
		keyName = "engorgedReadyColor",
		name = "Ready color",
		description = "Configures the color for the engorged Bloodwood tree when it is ready to chop.",
		section = engorgedBloodwoodTreeSection
	)
	default Color engorgedReadyColor()
	{
		return Color.CYAN;
	}

	@Alpha
	@ConfigItem(
		position = 26,
		keyName = "engorgedChoppingColor",
		name = "Chopping color",
		description = "Configures the color for the engorged Bloodwood tree while chopping.",
		section = engorgedBloodwoodTreeSection
	)
	default Color engorgedChoppingColor()
	{
		return Color.ORANGE;
	}

	@Alpha
	@ConfigItem(
		position = 27,
		keyName = "engorgedClickColor",
		name = "Click color",
		description = "Configures the color for the engorged Bloodwood tree when another click is needed.",
		section = engorgedBloodwoodTreeSection
	)
	default Color engorgedClickColor()
	{
		return Color.MAGENTA;
	}

	@Alpha
	@ConfigItem(
		position = 28,
		keyName = "engorgedDrainingColor",
		name = "Draining color",
		description = "Configures the color for the engorged Bloodwood tree while sap is draining.",
		section = engorgedBloodwoodTreeSection
	)
	default Color engorgedDrainingColor()
	{
		return Color.GREEN;
	}
}
