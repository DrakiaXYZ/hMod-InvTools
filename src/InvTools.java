import java.util.logging.*;
import java.util.Map;
import java.util.HashMap;

public class InvTools extends Plugin {
	private Listener listener = new Listener();
	
	private Logger log;
	private String name = "InvTools";
	private String version = "1.0";
	Boolean groupPolicy;
	Integer repairPoint;
	Map<Integer, Boolean> tools;
	
	public void enable() {}
	public void disable() {}
	
	public void initialize() {
		log = Logger.getLogger("Minecraft");
		log.info(name + " v" + version + " initialized.");
		loadProperties();
		etc.getLoader().addListener(
				PluginLoader.Hook.BLOCK_BROKEN, 
				listener, 
				this, 
				PluginListener.Priority.MEDIUM);
	}
	
	public void loadProperties() {
		PropertiesFile prop = new PropertiesFile("InvTools.properties");
		try {
			groupPolicy = prop.getBoolean("groupPolicy", false);
			repairPoint = prop.getInt("RepairPoint", 30);
			
			// Load tools that are invincible. Convert to integers.
			tools = new HashMap<Integer, Boolean>();
			String[] tmp = prop.getString("Tools", "").split(",");
			for (String tool : tmp) {
				if (tool.equals("")) continue;
				tools.put(Integer.parseInt(tool), true);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Exception while loading InvTools.properties", e);
		}
	}

	public class Listener extends PluginListener {
		public boolean onBlockBreak(Player player, Block block) {
			int itemInHand = player.getItemInHand();
			Boolean inGroup = false;
			
			if (tools.containsKey(itemInHand)) {
				if (groupPolicy && player.canUseCommand("/InvTools")) inGroup = true;
				if (!groupPolicy || inGroup) {
					// Tool is invincible, recreate it in inventory.
					Inventory inv = player.getInventory();
					Item item = inv.getItemFromId(itemInHand);
					if (item.getDamage() >= repairPoint) {
						item.setDamage(0);
						inv.removeItem(itemInHand);
						inv.addItem(item);
						inv.update();
					}
				}
			}

			return false;
		}
	}
	
}
