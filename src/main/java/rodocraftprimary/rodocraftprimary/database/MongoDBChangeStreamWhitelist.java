package rodocraftprimary.rodocraftprimary.database;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.UpdateDescription;
import org.bson.*;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginBase;
import rodocraftprimary.rodocraftprimary.Secret;

public class MongoDBChangeStreamWhitelist {
    private MongoClient mongoClient;
    private MongoCollection<Document> collection;

    private static MongoDBChangeStreamWhitelist instance = null;

    public static MongoDBChangeStreamWhitelist getInstance() {
        if (instance == null) {
            instance = new MongoDBChangeStreamWhitelist();
        }

        return instance;
    }

    public void MongoDBChangeStreamWhitelistDAO() {
        // Подключение к MongoDB
        MongoClientURI uri = new MongoClientURI(Secret.tokenDB);
        mongoClient = new MongoClient(uri);

        // Выбор базы данных и коллекции
        MongoDatabase database = mongoClient.getDatabase(uri.getDatabase());
        collection = database.getCollection("LinkDiscordWithMinecraft");
    }

    BsonDocument updatedFields;
    Document result;
    public  void startChangeStreamMonitoring(Plugin plugin) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();

                System.out.println(3);
                while (cursor.hasNext()) {
                    ChangeStreamDocument<Document> change = cursor.next();

                    updatedFields = change.getUpdateDescription().getUpdatedFields();

                    System.out.println(4);
                    if (change.getUpdateDescription().getUpdatedFields() == null) System.out.println("Zero");
                    else if (updatedFields.containsKey("whitelist")) {
                        System.out.println(5);
                        Document filter = new Document("_id", change.getDocumentKey().get("_id").asObjectId().getValue());
                        result = collection.find(filter).first();

                        Bukkit.getScheduler().runTask(plugin, () -> {
                            if (updatedFields.get("whitelist").asArray().contains(new BsonString("1"))) {
                                System.out.println(6);
                                addToWhitelist(result.getString("minecraftName"));
                            } else {
                                System.out.println(7);
                                removeFromWhitelist(result.getString("minecraftName"));
                            }
                        });
                    }
                }
            }catch (Error err){
                System.err.println(err);
            }
        });
    }

    private void addToWhitelist(String minecraftName) {
        try {
            OfflinePlayer player = Bukkit.getOfflinePlayer(minecraftName);

            System.out.println(player);
            System.out.println(player.getName());

            if(player != null){
//                Bukkit.getWhitelistedPlayers().add(player);
//                Bukkit.reloadWhitelist();

                player.setWhitelisted(true);
            }
            System.out.println(Bukkit.getWhitelistedPlayers());
        }catch (Error err) {
            System.err.println(err);
        }
    }

    private void removeFromWhitelist(String minecraftName) {
        try {
            OfflinePlayer player = Bukkit.getOfflinePlayer(minecraftName);

            System.out.println(player);
            System.out.println(player.getName());

            if (player != null) {
//                Bukkit.getWhitelistedPlayers().remove(player);
//                Bukkit.reloadWhitelist();

                player.setWhitelisted(false);
            }
            System.out.println(Bukkit.getWhitelistedPlayers());
        }catch (Error err) {
            System.err.println(err);
        }
    }
}
