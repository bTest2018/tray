package net.grandcentrix.tray.migration;

import net.grandcentrix.tray.accessor.Preference;
import net.grandcentrix.tray.accessor.TrayPreference;
import net.grandcentrix.tray.provider.TrayItem;

/**
 * Created by pascalwelsch on 2/26/15.
 */
public class TrayMigrator {

    private TrayPreference mTrayPreference;

    public TrayMigrator(final TrayPreference trayPreference) {
        mTrayPreference = trayPreference;
    }

    public void performMigration(TrayMigration... migrations) {
        for (TrayMigration migration : migrations) {
            if (!migration.shouldMigrate()) {
                continue;
            }

            boolean correctImported = false;
            final Object data = migration.getData();

            final boolean supportedDataType = Preference.isDataTypeSupported(data);
            if (supportedDataType) {
                final String key = migration.getTrayKey();
                final String migrationKey = migration.getPreviousKey();
                // save into tray
                mTrayPreference.getStorage().put(key, migrationKey, data);

                // check if data is really there.
                final TrayItem trayItem = mTrayPreference.getStorage().get(key);
                correctImported = (trayItem != null && trayItem.value().equals(data));
            }
            migration.onPostMigrate(correctImported);
        }
    }

    /**
     * checks if the given {@param migration} was imported before
     *
     * @param migration the import operation object
     * @return true if the item should be remigrated
     */
    /*private boolean isAlreadyImported(final TrayMigration migration) {
        // annotations are good but it's important to be sure
        // noinspection ConstantConditions
        if (migration.getPreviousKey() == null) {
            throw new IllegalArgumentException("the previousKey must not be null");
        }

        final String trayKey = migration.getTrayKey();
        final TrayItem item = mTrayPreference.getStorage().get(trayKey);
        if (item == null) {
            // item is unknown -> migrate
            return false;
        }

        if (item.migratedKey() == null) {
            // the tray item was available before the migration because it has no migrationKey
            return false;
        }

        final String migratedKey = migration.getPreviousKey();
        // for better documentation
        // noinspection RedundantIfStatement
        if (item.migratedKey().equals(migratedKey)) {
            // the keys are the same. so the item was imported before -> already imported
            return true;
        } else {
            // the key has changed since the last import. -> remigrate
            return false;
        }
    }*/
}
