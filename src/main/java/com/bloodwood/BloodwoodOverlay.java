package com.bloodwood;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

class BloodwoodOverlay extends OverlayPanel
{
	private final BloodwoodPlugin plugin;
	private final BloodwoodConfig config;

	@Inject
	private BloodwoodOverlay(BloodwoodPlugin plugin, BloodwoodConfig config)
	{
		super(plugin);
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.TOP_LEFT);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showSessionStats() || !plugin.isBloodwoodActive())
		{
			return null;
		}

		panelComponent.getChildren().add(TitleComponent.builder()
			.text("Bloodwood")
			.color(Color.RED)
			.build());

		BloodwoodSession session = plugin.getSession();
		if (session != null && session.getSapCollected() > 0)
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Sap collected:")
				.right(Integer.toString(session.getSapCollected()))
				.build());

			if (session.getSapPerHour() > 0)
			{
				panelComponent.getChildren().add(LineComponent.builder()
					.left("Sap/hr:")
					.right(Integer.toString(session.getSapPerHour()))
					.build());
			}
		}

		panelComponent.getChildren().add(LineComponent.builder()
			.left("Empty buckets:")
			.right(Integer.toString(plugin.getEmptyBuckets()))
			.build());

		panelComponent.getChildren().add(LineComponent.builder()
			.left("Sap buckets:")
			.right(Integer.toString(plugin.getSapBuckets()))
			.build());

		if (plugin.isInventoryFull())
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Inventory:")
				.right("Full")
				.rightColor(Color.ORANGE)
				.build());
		}

		return super.render(graphics);
	}
}
