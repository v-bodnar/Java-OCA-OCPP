package eu.chargetime.ocpp.model.core;

import eu.chargetime.ocpp.PropertyConstraintException;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.utilities.ModelUtil;

/*
 ChargeTime.eu - Java-OCA-OCPP
 Copyright (C) 2015-2016 Thomas Volden <tv@chargetime.eu>

 MIT License

 Copyright (c) 2016 Thomas Volden

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

/**
 * Sent by the the Central System to the Charge Point.
 */
public class GetConfigurationRequest implements Request {
    private String[] key;

    /**
     * List of keys for which the configuration value is requested.
     *
     * @return Array of key names.
     */
    public String[] getKey() {
        return key;
    }

    /**
     * Optional. List of keys for which the configuration value is requested.
     *
     * @param key Array of Strings, max 50 characters each, case insensitive.
     * @throws PropertyConstraintException At least one of the Strings exceeds 50 characters.
     */
    public void setKey(String[] key) throws PropertyConstraintException {
        if (!isValidKey(key))
            throw new PropertyConstraintException("key", key);

        this.key = key;
    }

    private boolean isValidKey(String[] keys) {
        boolean output = true;
        for(String key: keys) {
            if ((output = ModelUtil.validate(key, 50)) == false)
                break;
        }
        return output;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }
}
