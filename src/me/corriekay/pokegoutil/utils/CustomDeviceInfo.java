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
        return (String) config.getAsObject(ConfigKey.DEVICE_INFO_CUSTOM_ANDROID_BOARD_NAME);
    }

    @Override
    public String getAndroidBootloader() {
        return (String) config.getAsObject(ConfigKey.DEVICE_INFO_CUSTOM_ANDROID_BOOTLOADER);
    }

    @Override
    public String getDeviceBrand() {
        return (String) config.getAsObject(ConfigKey.DEVICE_INFO_CUSTOM_DEVICE_BRAND);
    }

    @Override
    public String getDeviceId() {
        return (String) config.getAsObject(ConfigKey.DEVICE_INFO_CUSTOM_DEVICE_ID);
    }

    @Override
    public String getDeviceModel() {
        return (String) config.getAsObject(ConfigKey.DEVICE_INFO_CUSTOM_DEVICE_MODEL);
    }

    @Override
    public String getDeviceModelIdentifier() {
        return (String) config.getAsObject(ConfigKey.DEVICE_INFO_CUSTOM_DEVICE_MODELIDENTIFIER);
    }

    @Override
    public String getDeviceModelBoot() {
        return (String) config.getAsObject(ConfigKey.DEVICE_INFO_CUSTOM_DEVICE_MODELBOOT);
    }

    @Override
    public String getHardwareManufacturer() {
        return (String) config.getAsObject(ConfigKey.DEVICE_INFO_CUSTOM_HARDWARE_MANUFACUTER);
    }

    @Override
    public String getHardwareModel() {
        return (String) config.getAsObject(ConfigKey.DEVICE_INFO_CUSTOM_HARDWARE_MODEL);
    }

    @Override
    public String getFirmwareBrand() {
        return (String) config.getAsObject(ConfigKey.DEVICE_INFO_CUSTOM_FIRMWARE_BRAND);
    }

    @Override
    public String getFirmwareTags() {
        return (String) config.getAsObject(ConfigKey.DEVICE_INFO_CUSTOM_FIRMWARE_TAGS);
    }

    @Override
    public String getFirmwareType() {
        return (String) config.getAsObject(ConfigKey.DEVICE_INFO_CUSTOM_FIRMWARE_TYPE);
    }

    @Override
    public String getFirmwareFingerprint() {
        return (String) config.getAsObject(ConfigKey.DEVICE_INFO_CUSTOM_FIRMWARE_FINGERPRINT);
    }

}
