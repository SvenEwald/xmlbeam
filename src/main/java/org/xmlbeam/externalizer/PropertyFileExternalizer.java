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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Properties;

import org.xmlbeam.exceptions.XBException;
import org.xmlbeam.exceptions.XBIOException;

/**
 * A full working example for an Externalizer implementation. This Externalizer reads the XPaths
 * from a property file instead from the projection annotations.
 */
public class PropertyFileExternalizer implements Externalizer {

    /**
     *
     */
    private static final long serialVersionUID = -2752447643606854521L;
    private final File propertyFile;
    private final Properties props = new Properties();
    private final boolean useXmlFormat;
    private long lastReadTS = 0;
    private String encodingName = "ISO8859-1";

    /**
     * Constructor for a given property file.
     *
     * @param propertyFile
     */
    public PropertyFileExternalizer(final File propertyFile) {
        this.propertyFile = propertyFile;
        this.useXmlFormat = false;
    }

    /**
     * Constructor for a given property file with the option to choose XML format.
     *
     * @param propertyFile
     * @param useXmlFormat
     */
    public PropertyFileExternalizer(final File propertyFile, final boolean useXmlFormat) {
        this.propertyFile = propertyFile;
        this.useXmlFormat = useXmlFormat;
    }

    /**
     * Setter for file encoding.
     *
     * @param encodingName
     * @return this for convenience
     */
    public PropertyFileExternalizer setEncoding(final String encodingName) {
        this.encodingName = encodingName;
        return this;
    }

    private void updateProps() {
        if (!propertyFile.canRead()) {
            throw new XBException("Can not read file '" + propertyFile + "'");
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
            @SuppressWarnings("resource")
            InputStreamReader reader = new InputStreamReader(inputStream, encodingName);
            props.load(reader);
        } catch (IOException e) {
            throw new XBIOException("Error while reading file '" + propertyFile + "'", e);
        } finally {
            lastReadTS = fileTS;
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new XBIOException("Can not close file '" + propertyFile + "'", e);
                }
            }
        }
    }

    @Override
    public String resolveXPath(final String annotationValue, final Method method, final Object[] args) {
        updateProps();
        return findProperty(annotationValue, method, args);
    }

    @Override
    public String resolveURL(final String annotationValue, final Method method, final Object[] args) {
        updateProps();
        return findProperty(annotationValue, method, args);
    }

    /**
     * @param key
     * @param method
     * @param args
     * @return
     */
    protected String findProperty(final String key, final Method method, final Object[] args) {
        String[] propNameCandidates = new String[] {//
        method.getDeclaringClass().getName() + "." + method.getName(),//
                method.getDeclaringClass().getSimpleName() + "." + method.getName(),//
                method.getName(),//
                key //
        };
        for (String propName : propNameCandidates) {
            if (props.containsKey(propName)) {
                return props.getProperty(propName);
            }
        }
        throw new IllegalArgumentException("Expected to find property with key '" + key + "' in file " + propertyFile.getAbsolutePath() + " for method " + method + ". But it does not exist.");
    }

}
