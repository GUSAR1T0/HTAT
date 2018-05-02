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

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import store.vxdesign.htat.core.properties.MergedProperties;
import store.vxdesign.htat.hosts.general.GeneralHost;
import store.vxdesign.htat.hosts.general.GeneralHostProperties;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("singleton")
public class HostHolder extends AbstractHostHolder {
    private final Logger logger = LogManager.getLogger(this.getClass());

    @Getter
    private final List<GeneralHost> generalHosts = new ArrayList<>();

    @Getter
    private boolean isInitialized = false;

    public HostHolder(MergedProperties mergedProperties) {
        super(mergedProperties);
    }

    @Override
    public void initialize() {
        initializeGeneralHosts();
        if (!isInitialized) {
            isInitialized = true;
            logger.debug("The host holder was initialized successfully");
        } else {
            logger.trace("The host holder was reinitialized successfully");
        }
    }

    @Override
    public void stop() {
        if (isInitialized) {
            stopGeneralHosts();
            isInitialized = false;
            logger.debug("The host holder was stopped successfully");
        } else {
            logger.trace("The host holder has already stopped");
        }
    }

    private void initializeGeneralHosts() {
        if (isInitialized) {
            logger.trace("Stopping the last initialized hosts list");
            stopGeneralHosts();
        }
        logger.trace("Initializing general hosts list");
        generalHosts.addAll(initialize(GeneralHostProperties.class, GeneralHost::new));
    }

    private void stopGeneralHosts() {
        logger.trace("Closing the open connections from general hosts list");
        generalHosts.forEach(generalHost -> generalHost.getConnections().closeAllConnections());
        generalHosts.clear();
    }
}
