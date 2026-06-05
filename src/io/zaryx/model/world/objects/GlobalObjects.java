package io.zaryx.model.world.objects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;

import io.zaryx.Server;
import io.zaryx.content.instances.InstancedArea;
import io.zaryx.content.wilderness.SpiderWeb;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Updated by Khaos
 */
public class GlobalObjects {

    private static final Logger logger = LoggerFactory.getLogger(GlobalObjects.class);

    /**
     * A collection of all existing objects.
     */
    Queue<GlobalObject> objects = new LinkedList<>();

    /**
     * A collection of all objects to be removed from the game.
     */
    Queue<GlobalObject> remove = new LinkedList<>();

    /**
     * Adds a new global object to the game world.
     *
     * @param object the object being added
     */
    public void add(GlobalObject object) {
        updateObject(object, object.getObjectId());
        objects.add(object);
        logger.debug("Add object {}", object);

        if (object.getRestoreId() > 0 && object.getTicksRemaining() > 0) {
            object.getRegionProvider().get(object.getX(), object.getY()).removeObject(
                    object.getRestoreId(),
                    object.getX(),
                    object.getY(),
                    object.getHeight(),
                    object.getType(),
                    object.getFace()
            );

            object.getRegionProvider().get(object.getX(), object.getY()).removeWorldObject(
                    object.withId(object.getRestoreId())
            );
        }

        if (object.getObjectId() == -1) {
            object.getRegionProvider().get(object.getX(), object.getY()).removeObject(
                    object.getObjectId(),
                    object.getX(),
                    object.getY(),
                    object.getHeight(),
                    object.getType(),
                    object.getFace()
            );

            object.getRegionProvider().get(object.getX(), object.getY()).removeWorldObject(object);
        } else {
            object.getRegionProvider().get(object.getX(), object.getY()).addObject(
                    object.getObjectId(),
                    object.getX(),
                    object.getY(),
                    object.getHeight(),
                    object.getType(),
                    object.getFace()
            );

            object.getRegionProvider().get(object.getX(), object.getY()).addWorldObject(object);
        }
    }

    public void remove(int id, int x, int y, int height) {
        remove(id, x, y, height, null);
    }

    /**
     * Removes a global object from the world.
     *
     * @param id the object id
     * @param x the x coordinate
     * @param y the y coordinate
     * @param height the height level
     * @param instance the instance area
     */
    public void remove(int id, int x, int y, int height, InstancedArea instance) {
        Optional<GlobalObject> existing = objects.stream()
                .filter(o -> o.getObjectId() == id
                        && o.getX() == x
                        && o.getY() == y
                        && o.getHeight() == height
                        && o.getInstance() == instance)
                .findFirst();

        if (existing.isPresent()) {
            remove(existing.get());
        } else {
            logger.debug(
                    "Attempted to remove object but no object exists: id={}, x={}, y={}, height={}, instance={}",
                    id,
                    x,
                    y,
                    height,
                    instance
            );
        }
    }

    public void remove(int id, InstancedArea instance) {
        List<GlobalObject> remove = objects.stream()
                .filter(o -> o.getObjectId() == id && o.getInstance() == instance)
                .collect(Collectors.toList());

        remove.forEach(it -> {
            remove(it);
            logger.debug("Removed object id={}, instance={}", id, instance);
        });
    }

    /**
     * Removes a global object from the world based on object reference.
     *
     * @param object the global object
     */
    public void remove(GlobalObject object) {
        if (!objects.contains(object)) {
            return;
        }

        updateObject(object, -1);
        remove.add(object);

        if (object.getObjectId() != -1) {
            object.getRegionProvider().get(object.getX(), object.getY()).removeObject(
                    object.getObjectId(),
                    object.getX(),
                    object.getY(),
                    object.getHeight(),
                    object.getType(),
                    object.getFace()
            );
        }
    }

    public void replace(GlobalObject remove, GlobalObject add) {
        remove(remove);
        add(add);
        logger.debug("Replace {} with {}", remove, add);
    }

    /**
     * Determines if an object exists in the game world.
     *
     * @param id the object id
     * @param x the x coordinate
     * @param y the y coordinate
     * @param height the height level
     * @return true if the object exists, otherwise false
     */
    public boolean exists(int id, int x, int y, int height) {
        return objects.stream().anyMatch(object ->
                object.getObjectId() == id
                        && object.getX() == x
                        && object.getY() == y
                        && object.getHeight() == height
        );
    }

    public boolean exists(int id, int height) {
        return objects.stream().anyMatch(object ->
                object.getObjectId() == id && object.getHeight() == height
        );
    }

    /**
     * Determines if any object exists in the game world at the specified location.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param height the height level
     * @return true if an object exists, otherwise false
     */
    public boolean anyExists(int x, int y, int height) {
        return objects.stream().anyMatch(object ->
                object.getX() == x
                        && object.getY() == y
                        && object.getHeight() == height
        );
    }

    /**
     * Determines if an object exists in the game world.
     *
     * @param id the object id
     * @param x the x coordinate
     * @param y the y coordinate
     * @return true if the object exists, otherwise false
     */
    public boolean exists(int id, int x, int y) {
        return exists(id, x, y, 0);
    }

    public GlobalObject get(int id, int x, int y, int height) {
        Optional<GlobalObject> obj = objects.stream()
                .filter(object -> object.getObjectId() == id
                        && object.getX() == x
                        && object.getY() == y
                        && object.getHeight() == height)
                .findFirst();

        return obj.orElse(null);
    }

    /**
     * Pulses global objects and restores temporary objects when their ticks expire.
     */
    public void pulse() {
        if (objects.size() == 0) {
            return;
        }

        Queue<GlobalObject> updated = new LinkedList<>();

        objects.removeAll(remove);
        remove.clear();

        GlobalObject object;

        while ((object = objects.poll()) != null) {
            if (object.getInstance() != null && object.getInstance().isDisposed()) {
                logger.debug("Remove global object because instance disposed {}", object);
                continue;
            }

            if (object.getTicksRemaining() < 0) {
                updated.add(object);
                continue;
            }

            object.removeTick();

            if (object.getTicksRemaining() == 0) {
                if (object.getObjectId() == SpiderWeb.RESTORE_ID) {
                    GlobalObject obj = object;
                    obj.setId(object.getRestoreId() == -1 ? object.getObjectId() : object.getRestoreId());
                    add(obj);
                } else {
                    placeObject(object, object.getRestoreId());
                }

                updateObject(object, object.getRestoreId());
            } else {
                updated.add(object);
            }
        }

        objects.addAll(updated);
    }

    /**
     * Updates a single global object with a new object id for nearby players.
     *
     * @param object the object being updated
     * @param objectId the new object id
     */
    public void updateObject(final GlobalObject object, final int objectId) {
        List<Player> players = PlayerHandler.nonNullStream()
                .filter(Objects::nonNull)
                .filter(player -> player.distanceToPoint(object.getX(), object.getY()) <= 60
                        && player.heightLevel == object.getHeight()
                        && object.getInstance() == player.getInstance())
                .collect(Collectors.toList());

        players.forEach(player ->
                player.getPA().object(
                        objectId,
                        object.getX(),
                        object.getY(),
                        object.getFace(),
                        object.getType(),
                        true
                )
        );
    }

    /**
     * For clipping and clicking support.
     */
    public void placeObject(final GlobalObject object, final int objectId) {
        object.getRegionProvider().get(object.getX(), object.getY()).removeWorldObject(object);

        if (objectId != -1) {
            object.getRegionProvider().get(object.getX(), object.getY()).addWorldObject(
                    object.withId(objectId)
            );
        }
    }

    /**
     * Updates all region objects for a specific player.
     *
     * @param player the player receiving the object updates
     */
    public void updateRegionObjects(Player player) {
        objects.stream()
                .filter(Objects::nonNull)
                .filter(object -> player.distanceToPoint(object.getX(), object.getY()) <= 60
                        && object.getHeight() == player.heightLevel)
                .forEach(object ->
                        player.getPA().object(
                                object.getObjectId(),
                                object.getX(),
                                object.getY(),
                                object.getFace(),
                                object.getType(),
                                true
                        )
                );
    }

    /**
     * Loads all global object information from global_objects.cfg.
     *
     * Expected format:
     * id x y height face type
     *
     * Supports both spaces and tabs.
     *
     * @throws IOException if the file cannot be read
     */
    public void loadGlobalObjectFile() throws IOException {
        File file = new File(Server.getDataDirectory() + "/cfg/obj/global_objects.cfg");

        System.out.println("Loading global objects from: " + file.getAbsolutePath());

        if (!file.exists()) {
            System.out.println("WARNING: global_objects.cfg does not exist at: " + file.getAbsolutePath());
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            int loaded = 0;
            int skipped = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                line = line.trim();

                if (line.isEmpty() || line.startsWith("//")) {
                    continue;
                }

                String[] data = line.split("\\s+");

                if (data.length != 6) {
                    skipped++;
                    System.out.println("WARNING: Invalid global object line " + lineNumber + ": " + line);
                    continue;
                }

                try {
                    int id = Integer.parseInt(data[0]);
                    int x = Integer.parseInt(data[1]);
                    int y = Integer.parseInt(data[2]);
                    int height = Integer.parseInt(data[3]);
                    int face = Integer.parseInt(data[4]);
                    int type = Integer.parseInt(data[5]);

                    add(new GlobalObject(id, x, y, height, face, type, -1));
                    loaded++;

                } catch (NumberFormatException nfe) {
                    skipped++;
                    System.out.println("WARNING: Unable to load global object from line " + lineNumber + ": " + line);
                }
            }

            System.out.println("Loaded " + loaded + " global objects. Skipped " + skipped + " invalid lines.");
        }
    }

    /**
     * Convenience method for testing object changes on a private host.
     *
     * @param player the player receiving the object updates
     * @throws IOException if the object file cannot be read
     */
    public void reloadObjectFile(Player player) throws IOException {
        objects.clear();
        loadGlobalObjectFile();
        updateRegionObjects(player);
    }

    @Override
    public String toString() {
        List<GlobalObject> copy = new ArrayList<>(objects);

        long matches = objects.stream()
                .filter(o -> copy.stream().anyMatch(m -> m.getX() == o.getX() && m.getY() == o.getY()))
                .count();

        StringBuilder sb = new StringBuilder();

        sb.append("GlobalObjects: <size: ")
                .append(objects.size())
                .append(", same spot: ")
                .append(matches)
                .append("> [");

        sb.append("\n");

        for (GlobalObject object : objects) {
            if (object == null) {
                continue;
            }

            sb.append("\t<id: ")
                    .append(object.getObjectId())
                    .append(", x: ")
                    .append(object.getX())
                    .append(", y: ")
                    .append(object.getY())
                    .append(">\n");
        }

        sb.append("]");

        return sb.toString();
    }
}