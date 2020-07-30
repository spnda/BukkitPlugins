package de.sean.splugin.util;

import java.util.HashMap;
import java.util.UUID;

public class SLockUtil {
    public static final String LOCK_ATTRIBUTE = "splugin_lock";

    public static final HashMap<UUID, LockData> locking = new HashMap<UUID, LockData>();
    public static final HashMap<UUID, GivePermData> givingPermission = new HashMap<UUID, GivePermData>();
    public static final HashMap<UUID, LockData> info = new HashMap<>();

    public static void addUserToAddLocking(UUID uuid) {
        if (locking.get(uuid) != null) locking.remove(uuid);
        locking.put(uuid, new LockData(System.currentTimeMillis(), uuid, true));
    }

    public static void addUserToRemoveLocking(UUID uuid) {
        if (locking.get(uuid) != null) locking.remove(uuid);
        locking.put(uuid, new LockData(System.currentTimeMillis(), uuid, false));
    }

    public static void removeUserFromLocking(UUID uuid) {
        if (locking.get(uuid) != null) locking.remove(uuid);
    }

    public static void addUserToBeAddedFromLocking(UUID uuid, UUID newUUID) {
        if (givingPermission.get(uuid) != null) givingPermission.remove(uuid);
        givingPermission.put(uuid, new GivePermData(System.currentTimeMillis(), uuid, newUUID, true));
    }

    public static void addUserToBeRemovedFromLocking(UUID uuid, UUID newUUID) {
        if (givingPermission.get(uuid) != null) givingPermission.remove(uuid);
        givingPermission.put(uuid, new GivePermData(System.currentTimeMillis(), uuid, newUUID, false));
    }

    public static void removeUserFromGiving(UUID uuid) {
        if (givingPermission.get(uuid) != null) givingPermission.remove(uuid);
    }

    public static void addUserToInfo(UUID uuid) {
        if (info.get(uuid) != null) info.remove(uuid);
        info.put(uuid, new LockData(System.currentTimeMillis(), uuid, false));
    }

    public static void removeUserFromInfo(UUID uuid) {
        if (info.get(uuid) != null) info.remove(uuid);
    }

    public static class LockData {
        public long timeRequested;
        public UUID uuid;
        public boolean action;

        public LockData(long timeRequested, UUID uuid, boolean action) {
            this.timeRequested = timeRequested;
            this.uuid = uuid;
            this.action = action;
        }
    }

    public static class GivePermData extends LockData {
        public UUID uuidToGive;

        public GivePermData(long timeRequested, UUID uuid, UUID uuidToGive, boolean action) {
            super(timeRequested, uuid, action);
            this.uuidToGive = uuidToGive;
        }
    }
}