/*
 * Copyright (c) 2018 Roman Mashenkin <xromash@vxdesign.store>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package store.vxdesign.htat.hosts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import store.vxdesign.htat.core.exceptions.InstanceInitializationException;
import store.vxdesign.htat.core.properties.MergedProperties;
import store.vxdesign.htat.hosts.common.Host;
import store.vxdesign.htat.hosts.common.HostConnections;
import store.vxdesign.htat.hosts.common.HostProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractHostHolder {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final MergedProperties mergedProperties;

    AbstractHostHolder(MergedProperties mergedProperties) {
        this.mergedProperties = mergedProperties;
    }

    <P extends HostProperties, T extends Host<P, ? extends HostConnections>> List<T> initialize(Class<P> propertiesClass, Function<P, T> hostInstanceGenerator) {
        try {
            List<P> propertiesList = mergedProperties.getPropertiesList(propertiesClass);
            return propertiesList.stream().map(hostInstanceGenerator).collect(Collectors.toList());
        } catch (NullPointerException e) {
            logger.trace("List of instances is not set in properties files");
        }

        P properties = mergedProperties.getProperties(propertiesClass);
        if (properties != null) {
            return new ArrayList<>(Collections.singletonList(hostInstanceGenerator.apply(properties)));
        } else {
            logger.trace("Single instance is not set in properties files");
            throw new InstanceInitializationException("Failed to initialize list of instances or single instance: %s.", propertiesClass.getName());
        }
    }

    public abstract void initialize();

    public abstract void stop();
}
