package com.bloodwood;

import com.bloodwood.engorged.EngorgedBloodwoodPhase;
import com.bloodwood.engorged.EngorgedBloodwoodState;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import javax.inject.Inject;
import net.runelite.api.GameObject;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

class BloodwoodSceneOverlay extends Overlay
{
	private final BloodwoodPlugin plugin;
	private final BloodwoodConfig config;

	@Inject
	private BloodwoodSceneOverlay(BloodwoodPlugin plugin, BloodwoodConfig config)
	{
		this.plugin = plugin;
		this.config = config;
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPosition(OverlayPosition.DYNAMIC);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!plugin.shouldRenderTreeState() ||
			!config.showTreeState() && !config.showTreeSpots() && !config.showEngorgedTreeState())
		{
			return null;
		}

		if (config.showTreeState())
		{
			for (GameObject tree : plugin.getBloodwoodTrees())
			{
				BloodwoodTreeState state = plugin.getState(tree);
				renderClickbox(graphics, tree, getTreeColor(state));
				renderState(graphics, tree, state);
			}
		}

		if (config.showEngorgedTreeState())
		{
			for (GameObject tree : plugin.getEngorgedBloodwoodTrees())
			{
				EngorgedBloodwoodState state = plugin.getEngorgedState();
				renderClickbox(graphics, tree, getEngorgedTreeColor(state));
				renderEngorgedState(graphics, tree, state);
			}
		}

		if (config.showTreeSpots())
		{
			for (GameObject spot : plugin.getBloodwoodSpots())
			{
				renderTile(graphics, spot, config.spotColor());
			}
		}

		return null;
	}

	private static void renderTile(Graphics2D graphics, GameObject object, Color color)
	{
		Polygon polygon = object.getCanvasTilePoly();
		if (polygon != null)
		{
			OverlayUtil.renderPolygon(graphics, polygon, color);
		}
	}

	private static void renderClickbox(Graphics2D graphics, GameObject object, Color color)
	{
		Shape clickbox = object.getClickbox();
		if (clickbox == null)
		{
			renderTile(graphics, object, color);
			return;
		}

		graphics.setColor(color);
		graphics.draw(clickbox);
		graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(color.getAlpha(), 40)));
		graphics.fill(clickbox);
	}

	private Color getTreeColor(BloodwoodTreeState state)
	{
		if (state == null || state.getBucketPlaced() == 0)
		{
			return config.noBucketColor();
		}

		if (state.isCollectReady())
		{
			return config.collectColor();
		}

		if (state.getBleedingProgress() > 0)
		{
			return plugin.isBleedingDraining(state) ? config.bleedingColor() : config.stalledColor();
		}

		if (state.getChoppingProgress() > 0)
		{
			return config.choppingColor();
		}

		return config.bucketColor();
	}

	private Color getEngorgedTreeColor(EngorgedBloodwoodState state)
	{
		if (state.getPhase() == EngorgedBloodwoodPhase.NO_BUCKET)
		{
			return config.noBucketColor();
		}

		if (state.getPhase() == EngorgedBloodwoodPhase.DRAINING)
		{
			return config.engorgedDrainingColor();
		}

		if (state.getPhase() == EngorgedBloodwoodPhase.READY_TO_DRAIN)
		{
			return config.engorgedClickColor();
		}

		if (state.getPhase() == EngorgedBloodwoodPhase.BLEEDING)
		{
			return config.engorgedChoppingColor();
		}

		return config.engorgedReadyColor();
	}

	private void renderState(Graphics2D graphics, GameObject tree, BloodwoodTreeState state)
	{
		if (state == null)
		{
			return;
		}

		String text;
		if (state.getBucketPlaced() == 0)
		{
			text = "No bucket";
		}
		else if (state.isCollectReady())
		{
			text = "Collect";
		}
		else if (state.getBleedingProgress() > 0)
		{
			text = "Bleed " + state.getBleedingProgress();
		}
		else if (state.getChoppingProgress() > 0)
		{
			text = "Chops " + state.getChops();
			if (state.getLastChops() > 0)
			{
				text += " / " + state.getLastChops();
			}
		}
		else
		{
			text = "Bucket";
		}

		Point textLocation = tree.getCanvasTextLocation(graphics, text, 0);
		if (textLocation != null)
		{
			OverlayUtil.renderTextLocation(graphics, textLocation, text, Color.WHITE);
		}
	}

	private void renderEngorgedState(Graphics2D graphics, GameObject tree, EngorgedBloodwoodState state)
	{
		String text = getEngorgedText(state);

		Point textLocation = tree.getCanvasTextLocation(graphics, text, 0);
		if (textLocation != null)
		{
			OverlayUtil.renderTextLocation(graphics, textLocation, text, Color.WHITE);
		}
	}

	private static String getEngorgedText(EngorgedBloodwoodState state)
	{
		switch (state.getPhase())
		{
			case NO_BUCKET:
				return "No bucket";
			case BLEEDING:
				return "Chopping";
			case READY_TO_DRAIN:
				return "Click";
			case DRAINING:
				return "Drain " + getSecondsRemaining(state.getDrainingTicksRemaining()) + "s";
			case READY:
				return "Ready";
			default:
				return "Unknown";
		}
	}

	private static int getSecondsRemaining(int ticks)
	{
		return (ticks * 3 + 4) / 5;
	}
}
