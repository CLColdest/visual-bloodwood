# Visual Bloodwood

Visual Bloodwood is a small RuneLite helper for Bloodwood trees and the engorged
Bloodwood tree.

## Bloodwood Trees

For regular Bloodwood trees, the plugin keeps track of the tree state while you
are working in the area:

- no bucket
- bucket placed
- chopping progress while opening a wound
- bleeding progress while sap is draining
- ready to collect
- nearby Bloodwood interaction spots

The overlay stays hidden while you are just passing through, then shows up once
Bloodwood activity starts.

## Engorged Bloodwood Tree

The engorged Bloodwood tree works differently from the regular ones, so it has
its own states.

The overlay shows when it is:

- ready
- missing empty bucket
- chopping
- waiting for the second click
- draining, with a 45 second countdown

There are optional notifications for the two easy-to-miss moments: when the first
phase is done, and when draining finishes. They use RuneLite's normal
notification settings, so you can handle sounds, flashing, focus behavior, and
everything else there.

## Letvek in a bucket

After collecting sap, a letvek can crawl into one of the player's buckets. The
plugin:

- highlights the `Letvek in a bucket` item in the inventory
- marks the engorged tree as `Shoo Letvek` until the letvek is removed
- can send a normal RuneLite notification
- supports clickbox or outline highlighting with separate outline and fill colors

Clicking the letvek remains the player's action. The plugin does not click the
item or use sap on it, and the letvek does not replace the sap that was collected.

## Session Stats

The side panel can show empty buckets, sap buckets, sap collected this session,
and sap per hour.

## Fair Play

Visual Bloodwood is only a visual helper. It does not automate input, change menu
options, modify clicks, or provide tick-cycle click guidance.

## Supporting

If this plugin has been useful to you, you can support my continued work on RuneLite plugins through [Patreon](https://www.patreon.com/Dazakio).

No pressure either way. Using the plugin, sharing feedback, or reporting issues already helps a lot.
