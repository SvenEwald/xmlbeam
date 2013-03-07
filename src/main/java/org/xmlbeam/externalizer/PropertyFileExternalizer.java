/**
 *  Copyright 2013 Sven Ewald
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.xmlbeam.externalizer;

import java.lang.reflect.Method;

import java.util.Properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 */
public class PropertyFileExternalizer implements Externalizer {

    private final File propertyFile;
    private final Properties props = new Properties();
    private final boolean useXmlFormat;
    private long lastReadTS = 0;
    private String encodingName = "ISO 8859-1";

    PropertyFileExternalizer(File propertyFile) {
        this.propertyFile = propertyFile;
        this.useXmlFormat = false;
    }

    PropertyFileExternalizer(File propertyFile, boolean useXmlFormat) {
        this.propertyFile = propertyFile;
        this.useXmlFormat = useXmlFormat;
    }

    public PropertyFileExternalizer setEncoding(String encodingName) {
        this.encodingName=encodingName;
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    public String resolveString(String key) {
        updateProps();
        return props.getProperty(key);
    }

    private void updateProps() {
        if (!propertyFile.canRead()) {
            throw new RuntimeException("Can not read file '" + propertyFile + "'");
        }
        long fileTS = propertyFile.lastModified();
        if (lastReadTS > fileTS) {
            return;
        }
        props.clear();
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(propertyFile);
            if (useXmlFormat) {
                props.loadFromXML(inputStream);
                return;
            }
            InputStreamReader reader = new InputStreamReader(inputStream, encodingName);
            props.load(reader);
        } catch (IOException e) {
            throw new RuntimeException("Error while reading file '" + propertyFile + "'",e);
        } finally {
            lastReadTS=fileTS;
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException("Can not close file '" + propertyFile + "'",e);
                }
            }
        }
    }

    @Override
    public String resolveString(String key, Method method, Object[] args) {
        return resolveString(key);
    }

}
