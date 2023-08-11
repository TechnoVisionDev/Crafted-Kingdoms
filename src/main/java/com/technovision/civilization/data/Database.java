package com.technovision.civilization.data;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import com.technovision.civilization.data.objects.Civilization;
import com.technovision.civilization.data.objects.Resident;
import com.technovision.civilization.data.objects.Town;
import org.antlr.v4.runtime.misc.NotNull;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * Manages data between the plugin and the MongoDB database.
 *
 * @author TechnoVision
 */
public class Database {

    public static final ReplaceOptions UPSERT_REPLACE = new ReplaceOptions().upsert(true);
    public static final UpdateOptions UPSERT_UPDATE = new UpdateOptions().upsert(true);

    /** Collections */
    public static @NotNull MongoCollection<Civilization> civilizations;
    public static @NotNull MongoCollection<Town> towns;
    public static @NotNull MongoCollection<Resident> residents;

    /**
     * Connect to database using MongoDB URI and
     * initialize any collections that don't exist.
     *
     * @param name The database name.
     * @param uri MongoDB uri string.
     */
    public Database(String name, String uri) {
        // Setup MongoDB database with URI.
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .codecRegistry(codecRegistry)
                .build();
        MongoClient mongoClient = MongoClients.create(clientSettings);
        MongoDatabase database = mongoClient.getDatabase(name);

        // Initialize collections if they don't exist.
        civilizations = database.getCollection("civilizations", Civilization.class);
        towns = database.getCollection("towns", Town.class);
        residents = database.getCollection("residents", Resident.class);

        // Create collection indexes if they don't exist.
        Bson nameIndex = Indexes.descending("name");
        civilizations.createIndex(nameIndex);
        towns.createIndex(nameIndex);

        Bson playerIndex = Indexes.descending("playerID");
        residents.createIndex(playerIndex);
    }
}