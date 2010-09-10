/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2010 INRIA/University of
 *                              Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2
 * or a different license than the GPL.
 *
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */

package org.ow2.proactive.resourcemanager.authentication;

import java.io.File;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.ow2.proactive.authentication.LDAP2LoginModule;
import org.ow2.proactive.resourcemanager.core.properties.PAResourceManagerProperties;
import org.ow2.proactive.resourcemanager.utils.RMLoggers;


/**
 *
 * LDAP login module implementation for resource manager. Extracts LDAP configurations file from
 * resource manager configuration and uses it to authenticate users.
 *
 */
public class RMLDAP2LoginModule extends LDAP2LoginModule {

    /**
     * Returns LDAP configuration file name defined in resource manager configuration file
     */
    @Override
    protected String getLDAPConfigFileName() {
        String loginFile = PAResourceManagerProperties.RM_LDAP2_CONFIG.getValueAsString();
        //test that login file path is an absolute path or not
        if (!(new File(loginFile).isAbsolute())) {
            //file path is relative, so we complete the path with the prefix RM_Home constant
            loginFile = PAResourceManagerProperties.RM_HOME.getValueAsString() + File.separator + loginFile;
        }

        return loginFile;
    }

    /**
     * Returns login file name from resource manager configuration file
     * Used for authentication fall back.
     * @return login file name from resource manager configuration file     *
     */
    @Override
    protected String getLoginFileName() {

        String loginFile = PAResourceManagerProperties.RM_LOGIN_FILE.getValueAsString();
        //test that login file path is an absolute path or not
        if (!(new File(loginFile).isAbsolute())) {
            //file path is relative, so we complete the path with the prefix RM_Home constant
            loginFile = PAResourceManagerProperties.RM_HOME.getValueAsString() + File.separator + loginFile;
        }

        return loginFile;
    }

    /**
     * Returns group file name from resource manager configuration file
     * Used for group membership verification fall back.
     * @return group file name from resource manager configuration file
     */
    @Override
    protected String getGroupFileName() {
        String groupFile = PAResourceManagerProperties.RM_GROUP_FILE.getValueAsString();
        //test that group file path is an absolute path or not
        if (!(new File(groupFile).isAbsolute())) {
            //file path is relative, so we complete the path with the prefix RM_Home constant
            groupFile = PAResourceManagerProperties.RM_HOME.getValueAsString() + File.separator + groupFile;
        }

        return groupFile;
    }

    /**
     * Returns logger for authentication
     */
    public Logger getLogger() {
        return ProActiveLogger.getLogger(RMLoggers.CONNECTION);
    }
}