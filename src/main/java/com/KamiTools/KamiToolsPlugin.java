//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.KamiTools;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.runelite.client.plugins.antidrag.*;

@PluginDescriptor(
		name = "KamiTools"
)
public class KamiToolsPlugin extends Plugin {
	private static final Logger log = LoggerFactory.getLogger(KamiToolsPlugin.class);
	private static final String PLUGIN_NAME = "Kami Screen Markers";
	private static final String CONFIG_GROUP = "kamiscreenmarkers";
	private static final String CONFIG_KEY = "kamimarkers";
	private static final String ICON_FILE = "panel_icon.png";
	private static final String DEFAULT_MARKER_NAME = "Marker";
	private static final Color DEFAULT_BOT_COLOR;
	private static final Color DEFAULT_BOT_FILL;
	private static final Dimension DEFAULT_SIZE;
	private NavigationButton navigationButton;
	@Inject
	private ConfigManager configManager;
	private final List<ScreenMarkerOverlay> screenMarkers = new ArrayList();
	@Inject
	private MouseManager mouseManager;
	@Inject
	private Client client;
	@Inject
	private Gson gson;
	@Inject
	private KamiToolsConfig config;
	@Inject
	private ColorPickerManager colorPickerManager;
	@Inject
	public OverlayManager overlayManager;
	@Inject
	private ClientToolbar clientToolbar;
	@Inject
	private ScreenMarkerCreationOverlay overlay;
	private ScreenMarkerMouseListener mouseListener;
	private ScreenMarkerPluginPanel pluginPanel;
	private ScreenMarker currentMarker;
	private boolean creatingScreenMarker = false;
	private boolean drawingScreenMarker = false;
	private Rectangle selectedWidgetBounds = null;
	private Point startLocation = null;
	@Inject
	private ScreenMarkerWidgetHighlightOverlay widgetHighlight;
	boolean DEBUG_FLAG = false;

	public KamiToolsPlugin() {
	}

	@Override
	protected void startUp() throws Exception {
		overlayManager.add(overlay);
		overlayManager.add(widgetHighlight);
		loadConfig(configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY)).forEach(screenMarkers::add);
		screenMarkers.forEach(overlayManager::add);

		pluginPanel = new ScreenMarkerPluginPanel(this);
		pluginPanel.rebuild();

		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), ICON_FILE);

		navigationButton = NavigationButton.builder()
				.tooltip(PLUGIN_NAME)
				.icon(icon)
				.priority(5)
				.panel(pluginPanel)
				.build();

		clientToolbar.addNavigation(navigationButton);

		mouseListener = new ScreenMarkerMouseListener(this);
	}

	protected void shutDown() throws Exception {
		log.info("KamiTools sopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged) {
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
			this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "KamiTools says " + this.config.greeting(), (String)null);
		}

	}

	public void startCreation(Point location) {
		this.startCreation(location, DEFAULT_SIZE);
		if (this.selectedWidgetBounds == null) {
			this.drawingScreenMarker = true;
		}

	}

	public void startCreation(Point location, Dimension size) {
		this.currentMarker = new ScreenMarker(Instant.now().toEpochMilli(), "Marker " + (this.screenMarkers.size() + 1), this.pluginPanel.getSelectedBorderThickness(), this.pluginPanel.getSelectedColor(), this.pluginPanel.getSelectedFillColor(), true, false);
		this.startLocation = location;
		this.overlay.setPreferredLocation(location);
		this.overlay.setPreferredSize(size);
		System.out.printf("p1x: %d, p1y: %d, p1h: :%d p1w: %d", location.x, location.y, 100, 100);
	}

	public void finishCreation(boolean aborted) {
		ScreenMarker marker = this.currentMarker;
		if (!aborted && marker != null) {
			ScreenMarkerOverlay screenMarkerOverlay = new ScreenMarkerOverlay(marker);
			screenMarkerOverlay.setPreferredLocation(this.overlay.getBounds().getLocation());
			screenMarkerOverlay.setPreferredSize(this.overlay.getBounds().getSize());
			System.out.printf("p1x: %d, p1y: %d, p1h: :%d p1w: %d", this.overlay.getBounds().getLocation().x, this.overlay.getBounds().getLocation().y, this.overlay.getBounds().getSize().height, this.overlay.getBounds().getSize().width);
			this.screenMarkers.add(screenMarkerOverlay);
			this.overlayManager.saveOverlay(screenMarkerOverlay);
			this.overlayManager.add(screenMarkerOverlay);
			this.pluginPanel.rebuild();
			this.updateConfig();
		}

		this.creatingScreenMarker = false;
		this.drawingScreenMarker = false;
		this.selectedWidgetBounds = null;
		this.startLocation = null;
		this.currentMarker = null;
		this.setMouseListenerEnabled(false);
		this.pluginPanel.setCreation(false);
	}

	public void dynamicCreation(Point location, Dimension size, Point location2, Dimension size2) {
		this.currentMarker = new ScreenMarker(Instant.now().toEpochMilli(), "Marker " + (this.screenMarkers.size() + 1), 3, DEFAULT_BOT_COLOR, DEFAULT_BOT_FILL, true, false);
		this.startLocation = location;
		ScreenMarker marker = this.currentMarker;
		ScreenMarkerOverlay screenMarkerOverlay = new ScreenMarkerOverlay(marker);
		screenMarkerOverlay.setPreferredLocation(location2);
		screenMarkerOverlay.setPreferredSize(size2);
		this.screenMarkers.add(screenMarkerOverlay);
		this.overlayManager.saveOverlay(screenMarkerOverlay);
		this.overlayManager.add(screenMarkerOverlay);
		this.pluginPanel.rebuild();
		this.updateConfig();
		this.creatingScreenMarker = false;
		this.drawingScreenMarker = false;
		this.selectedWidgetBounds = null;
		this.startLocation = null;
		this.currentMarker = null;
		this.setMouseListenerEnabled(false);
		this.pluginPanel.setCreation(false);
	}

	public void TEST_dynamiccreation() {
		this.dynamicCreation(new Point(this.config.posx(), this.config.posY()), new Dimension(this.config.widthX(), this.config.widthY()), new Point(this.config.posx2(), this.config.posY2()), new Dimension(this.config.widthX2(), this.config.widthY2()));
	}

	public void completeSelection() {
		this.pluginPanel.getCreationPanel().unlockConfirm();
	}

	public void deleteMarker(ScreenMarkerOverlay marker) {
		this.screenMarkers.remove(marker);
		this.overlayManager.remove(marker);
		this.overlayManager.resetOverlay(marker);
		this.pluginPanel.rebuild();
		this.updateConfig();
	}

	public void setMouseListenerEnabled(boolean enabled) {
		if (enabled) {
			this.mouseManager.registerMouseListener(this.mouseListener);
		} else {
			this.mouseManager.unregisterMouseListener(this.mouseListener);
		}

	}

	void resizeMarker(Point point) {
		this.drawingScreenMarker = true;
		Rectangle bounds = new Rectangle(this.startLocation);
		bounds.add(point);
		this.overlay.setPreferredLocation(bounds.getLocation());
		this.overlay.setPreferredSize(bounds.getSize());
	}

	public void updateConfig() {
		if (this.screenMarkers.isEmpty()) {
			this.configManager.unsetConfiguration("kamiscreenmarkers", "kamimarkers");
		} else {
			String json = this.gson.toJson(this.screenMarkers.stream().map(ScreenMarkerOverlay::getMarker).collect(Collectors.toList()));
			this.configManager.setConfiguration("kamiscreenmarkers", "kamimarkers", json);
		}
	}

	private Stream<ScreenMarkerOverlay> loadConfig(String json) {
		if (Strings.isNullOrEmpty(json)) {
			return Stream.empty();
		} else {
			List<ScreenMarker> screenMarkerData = (List)this.gson.fromJson(json, (new TypeToken<ArrayList<ScreenMarker>>() {
			}).getType());
			return screenMarkerData.stream().filter(Objects::nonNull).map(ScreenMarkerOverlay::new);
		}
	}

	@Provides
	KamiToolsConfig provideConfig(ConfigManager configManager) {
		return (KamiToolsConfig)configManager.getConfig(KamiToolsConfig.class);
	}

	public List<ScreenMarkerOverlay> getScreenMarkers() {
		return this.screenMarkers;
	}

	public ColorPickerManager getColorPickerManager() {
		return this.colorPickerManager;
	}

	ScreenMarker getCurrentMarker() {
		return this.currentMarker;
	}

	public boolean isCreatingScreenMarker() {
		return this.creatingScreenMarker;
	}

	public void setCreatingScreenMarker(boolean creatingScreenMarker) {
		this.creatingScreenMarker = creatingScreenMarker;
	}

	public boolean isDrawingScreenMarker() {
		return this.drawingScreenMarker;
	}

	public Rectangle getSelectedWidgetBounds() {
		return this.selectedWidgetBounds;
	}

	public void setSelectedWidgetBounds(Rectangle selectedWidgetBounds) {
		this.selectedWidgetBounds = selectedWidgetBounds;
	}

	static {
		DEFAULT_BOT_COLOR = Color.pink;
		DEFAULT_BOT_FILL = Color.pink;
		DEFAULT_SIZE = new Dimension(100, 100);
	}
}
