package rodocraftprimary.rodocraftprimary;

import org.bukkit.plugin.java.JavaPlugin;
import rodocraftprimary.rodocraftprimary.database.MongoDBChangeStreamWhitelist;

public final class RodoCraftPrimary extends JavaPlugin {

    @Override
    public void onEnable() {
        System.out.println("Start");
        MongoDBChangeStreamWhitelist.getInstance().MongoDBChangeStreamWhitelistDAO();
        MongoDBChangeStreamWhitelist.getInstance().startChangeStreamMonitoring(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
