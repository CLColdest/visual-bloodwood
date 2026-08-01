package com.bloodwood;

import com.bloodwood.engorged.EngorgedBloodwoodIds;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.util.ImageUtil;

class BloodwoodInventoryOverlay extends WidgetItemOverlay
{
	private final BloodwoodConfig config;
	private final ItemManager itemManager;
	private int cachedQuantity = -1;
	private Color cachedOutlineColor;
	private Color cachedFillColor;
	private BufferedImage cachedItemOutline;
	private BufferedImage cachedItemFill;

	@Inject
	private BloodwoodInventoryOverlay(BloodwoodConfig config, ItemManager itemManager)
	{
		this.config = config;
		this.itemManager = itemManager;
		showOnInventory();
	}

	@Override
	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem item)
	{
		if (!config.showLetvekHighlight() || itemId != EngorgedBloodwoodIds.LETVEK_IN_A_BUCKET)
		{
			return;
		}

		Rectangle bounds = config.letvekHighlightMode() == LetvekHighlightMode.CLICKBOX
			? item.getWidget().getBounds()
			: item.getCanvasBounds();
		if (bounds == null)
		{
			return;
		}

		Color outlineColor = config.letvekHighlightOutlineColor();
		if (config.letvekHighlightMode() == LetvekHighlightMode.OUTLINE)
		{
			renderItemOutline(graphics, item, outlineColor, config.letvekHighlightFillColor());
			return;
		}

		OverlayUtil.renderPolygon(graphics, bounds, outlineColor, config.letvekHighlightFillColor(), new BasicStroke(2));
	}

	private void renderItemOutline(Graphics2D graphics, WidgetItem item, Color outlineColor, Color fillColor)
	{
		if (item.getQuantity() != cachedQuantity || !outlineColor.equals(cachedOutlineColor) || !fillColor.equals(cachedFillColor))
		{
			cachedQuantity = item.getQuantity();
			cachedOutlineColor = outlineColor;
			cachedFillColor = fillColor;
			cachedItemOutline = itemManager.getItemOutline(EngorgedBloodwoodIds.LETVEK_IN_A_BUCKET, item.getQuantity(), outlineColor);
			cachedItemFill = ImageUtil.fillImage(itemManager.getImage(EngorgedBloodwoodIds.LETVEK_IN_A_BUCKET, item.getQuantity(), false), fillColor);
		}

		Rectangle bounds = item.getCanvasBounds();
		graphics.drawImage(cachedItemOutline, bounds.x, bounds.y, null);
		graphics.drawImage(cachedItemFill, bounds.x, bounds.y, null);
	}
}
