package me.corriekay.pokegoutil.utils;

import com.pokegoapi.api.device.DeviceInfos;

/**
 * A class that implements DeviceInfos and get all the configuration from the config.json file.
 * If you want different DeviceInfo passed to API, change the configuration in config file.
 * @author FernandoTBarros
 */
public class CustomDeviceInfo implements DeviceInfos {

    private static ConfigNew config = ConfigNew.getConfig();
    
    @Override
    public String getAndroidBoardName() {
        return config.getString(ConfigKey.DEVICE_INFO_ANDROID_BOARD_NAME);
    }

    @Override
    public String getAndroidBootloader() {
        return config.getString(ConfigKey.DEVICE_INFO_ANDROID_BOOTLOADER);
    }

    @Override
    public String getDeviceBrand() {
        return config.getString(ConfigKey.DEVICE_INFO_DEVICE_BRAND);
    }

    @Override
    public String getDeviceId() {
        return config.getString(ConfigKey.DEVICE_INFO_DEVICE_ID);
    }

    @Override
    public String getDeviceModel() {
        return config.getString(ConfigKey.DEVICE_INFO_DEVICE_MODEL);
    }

    @Override
    public String getDeviceModelIdentifier() {
        return config.getString(ConfigKey.DEVICE_INFO_DEVICE_MODELIDENTIFIER);
    }

    @Override
    public String getDeviceModelBoot() {
        return config.getString(ConfigKey.DEVICE_INFO_DEVICE_MODELBOOT);
    }

    @Override
    public String getHardwareManufacturer() {
        return config.getString(ConfigKey.DEVICE_INFO_HARDWARE_MANUFACUTER);
    }

    @Override
    public String getHardwareModel() {
        return config.getString(ConfigKey.DEVICE_INFO_HARDWARE_MODEL);
    }

    @Override
    public String getFirmwareBrand() {
        return config.getString(ConfigKey.DEVICE_INFO_FIRMWARE_BRAND);
    }

    @Override
    public String getFirmwareTags() {
        return config.getString(ConfigKey.DEVICE_INFO_FIRMWARE_TAGS);
    }

    @Override
    public String getFirmwareType() {
        return config.getString(ConfigKey.DEVICE_INFO_FIRMWARE_TYPE);
    }

    @Override
    public String getFirmwareFingerprint() {
        return config.getString(ConfigKey.DEVICE_INFO_FIRMWARE_FINGERPRINT);
    }

}
