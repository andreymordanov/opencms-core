
package org.opencms.util.ant;

import java.io.File;

import org.apache.commons.digester.Digester;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Ant task for reading a property from a XML attribute or element.<p>
 * 
 * @author Michael Moossen  
 * 
 * @version $Revision$ 
 * 
 * @since 6.0.0 
 */
public class CmsAntTaskReadXMLProperty extends Task {

    /** target attribute. */
    private String m_attribute; // optional

    /** target element. */
    private String m_element; // required

    /** Destination property. */
    private String m_property; // required

    /** return value. */
    private String m_value = ",";

    /** absoulte path to the xml file. */
    private String m_xmlFile; // required

    /**
     * Default constructor.<p>
     */
    public CmsAntTaskReadXMLProperty() {

        super();
    }

    /**
     * Run the task.<p>
     * 
     * Sets the given property to <code>__ABORT__</code> if canceled,
     * or to a list of selected modules if not.<p>
     * 
     * @throws BuildException if something goes wrong
     *
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {

        boolean isAttr = (m_attribute != null && m_attribute.trim().length() > 0);
        
//        log("Xml File: " + m_xmlFile);
//        log("xPath: " + m_element + (isAttr?"#"+m_attribute:""));
//        log("property: " + m_property);

        // instantiate Digester and enable XML validation
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.setEntityResolver(null);
        digester.setRuleNamespaceURI(null);
        digester.setErrorHandler(new ErrorHandler() {

            public void error(SAXParseException exception) throws SAXException {

                log(exception.getMessage(), exception.getLineNumber());
            }

            public void fatalError(SAXParseException exception) throws SAXException {

                log(exception.getMessage(), exception.getLineNumber());
            }

            public void warning(SAXParseException exception) throws SAXException {

                log(exception.getMessage(), exception.getLineNumber());
            }

        });

        // add this class to the Digester
        digester.push(this);
        if (!isAttr) {
            digester.addCallMethod(m_element, "setValue", 0);
        } else {
            digester.addCallMethod(m_element, "setValue", 1);
            digester.addCallParam(m_element, 0, m_attribute);
        }
        // start the parsing process
        try {
            digester.parse(new File(getXmlFile()));
        } catch (Exception e) {
            throw new BuildException(e);
        }

        getProject().setProperty(m_property, m_value.substring(1));
    }

    /**
     * Returns the optional XML attribute.<p>
     *
     * @return the optional XML attribute
     */
    public String getAttribute() {

        return m_attribute;
    }

    /**
     * Returns the XML element path.<p>
     *
     * @return the XML element path
     */
    public String getElement() {

        return m_element;
    }

    /**
     * Returns the property to store the user selection.<p>
     * 
     * @return Returns the property
     */
    public String getProperty() {

        return m_property;
    }

    /**
     * Returns the return value.<p>
     *
     * @return the return value
     */
    public String getValue() {

        return m_value;
    }

    /**
     * Returns the xmlFile absolute path.<p>
     *
     * @return the xmlFile absolute path
     */
    public String getXmlFile() {

        return m_xmlFile;
    }

    /**
     * Sets the optional XML attribute.<p>
     *
     * @param attribute the optional XML attribute to set
     */
    public void setAttribute(String attribute) {

        m_attribute = attribute;
    }

    /**
     * Sets the XML element path.<p>
     *
     * @param element the XML element path to set
     */
    public void setElement(String element) {

        m_element = element;
    }

    /**
     * Sets the property for storing the selected value.
     * 
     * @param property The property to set
     */
    public void setProperty(String property) {

        m_property = property;
    }

    /**
     * Sets the return value.<p>
     *
     * @param value the return value to set
     */
    public void setValue(String value) {

        if (m_value.length() > 1) {
            m_value += ",";
        }
        m_value += value;
    }

    /**
     * Sets the xmlFile absolute path.<p>
     *
     * @param xmlFile the xmlFile absolute path to set
     */
    public void setXmlFile(String xmlFile) {

        m_xmlFile = xmlFile;
    }
}