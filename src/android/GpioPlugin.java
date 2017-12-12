package org.apache.cordova.things;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shinya Suefusa on 2017/12/10.
 */
public class GpioPlugin extends CordovaPlugin {

    private PeripheralManagerService service = new PeripheralManagerService();
    private Map<String, Gpio> gpioMap = new HashMap<>();

    @Override
    public void onDestroy() {
        for (String key : gpioMap.keySet()) {
            try {
                gpioMap.get(key).close();
            } catch(IOException e) {
                // Do nothing.
            }
        }
        gpioMap.clear();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if ("openGpio".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            Integer direction = args.length() > 1 ? args.getInt(1) : null;
            return openGpio(name, direction, callbackContext);
        } else if("close".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            return close(name, callbackContext);
        } else if("setValue".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            Integer value = args.length() > 1 ? args.getInt(1) : null;
            return setValue(name, value, callbackContext);
        } else if("getValue".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            return getValue(name, callbackContext);
        }

        return false;
    }

    private boolean openGpio(String name, Integer direction, CallbackContext callbackContext) {
        if (name == null) {
            return false;
        }
        if (gpioMap.containsKey(name)) {
            return true;
        }
        if (direction == null) {
            direction = Gpio.DIRECTION_OUT_INITIALLY_LOW;
        }
        try {
            Gpio gpio = service.openGpio(name);
            gpio.setDirection(direction);
            gpioMap.put(name, gpio);
        } catch(IOException e) {
            return false;
        }
        callbackContext.success();
        return true;
    }

    private boolean close(String name, CallbackContext callbackContext) {
        if (!gpioMap.containsKey(name)) {
            return false;
        }
        Gpio gpio = gpioMap.get(name);
        try {
            gpio.close();
        } catch(IOException e) {
            // Do nothing.
        }
        gpioMap.remove(gpio);
        callbackContext.success();
        return true;
    }

    private boolean setValue(String name, Integer value, CallbackContext callbackContext) {
        if (!gpioMap.containsKey(name)) {
            return false;
        }
        Gpio gpio = gpioMap.get(name);
        if (value == null) {
            value = 1;
        }
        try {
            gpio.setValue(value != 0);
        } catch(IOException e) {
            return false;
        }
        callbackContext.success();
        return true;
    }

    private boolean getValue(String name, CallbackContext callbackContext) {
        if (!gpioMap.containsKey(name)) {
            return false;
        }
        Gpio gpio = gpioMap.get(name);
        try {
            int value = gpio.getValue() ? 1 : 0;
            callbackContext.success(value);
        } catch(IOException e) {
            return false;
        }

        return true;
    }
}
