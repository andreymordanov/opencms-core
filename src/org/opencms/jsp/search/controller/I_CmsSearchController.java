/*
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) Alkacon Software GmbH (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.jsp.search.controller;

import java.util.Map;

/** Interface all search controllers must implement. It consists of methods for query generation and state updates. */
public interface I_CmsSearchController {

    /** Add the request parameters that reflect the controllers current state (useful for link generation outside of a form).
     * @param parameters The request parameters reflecting the controllers currents state.
     */
    void addParametersForCurrentState(Map<String, String[]> parameters);

    /** Generate the Solr query part specific for the controller, e.g., the part for a field facet.
     * @return The Solr query part contstructed by the controller according to configuration and state. */
    String generateQuery();

    /** Update the controllers state in case the term that is search for (the query as given by the user) has changed. */
    void updateForQueryChange();

    /** Update the controllers state from the given request parameters.
     * @param parameters The request parameters. */
    void updateFromRequestParameters(Map<String, String[]> parameters);
}
