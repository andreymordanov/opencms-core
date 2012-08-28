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

package org.opencms.gwt.client.ui.input;

import org.opencms.gwt.client.I_CmsHasInit;
import org.opencms.gwt.client.ui.CmsPushButton;
import org.opencms.gwt.client.ui.CmsScrollPanel;
import org.opencms.gwt.client.ui.I_CmsAutoHider;
import org.opencms.gwt.client.ui.css.I_CmsLayoutBundle;
import org.opencms.gwt.client.ui.input.category.CmsDataValue;
import org.opencms.gwt.client.ui.input.form.CmsWidgetFactoryRegistry;
import org.opencms.gwt.client.ui.input.form.I_CmsFormWidgetFactory;
import org.opencms.gwt.client.ui.tree.CmsTreeItem;
import org.opencms.gwt.shared.CmsCategoryTreeEntry;
import org.opencms.gwt.shared.CmsListInfoBean;
import org.opencms.util.CmsStringUtil;

import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;

/**
 * Basic category widget for forms.<p>
 * 
 * @since 8.0.0
 * 
 */
public class CmsCategoryField extends Composite implements I_CmsFormWidget, I_CmsHasInit {

    /** Selection handler to handle check box click events and double clicks on the list items. */
    protected abstract class A_SelectionHandler implements ClickHandler, DoubleClickHandler {

        /** The reference to the checkbox. */
        private CmsCheckBox m_checkBox;

        /** The the select button, can be used instead of a double click to select and search. */
        private CmsPushButton m_selectButton;

        /**
         * Constructor.<p>
         * 
         * @param checkBox the item check box
         */
        protected A_SelectionHandler(CmsCheckBox checkBox) {

            m_checkBox = checkBox;
        }

        /**
         * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
         */
        public void onClick(ClickEvent event) {

            if (event.getSource().equals(m_selectButton)) {
                m_checkBox.setChecked(true);
                onSelectionChange();
            } else if (event.getSource().equals(m_checkBox)) {
                onSelectionChange();
            }
        }

        /**
         * @see com.google.gwt.event.dom.client.DoubleClickHandler#onDoubleClick(com.google.gwt.event.dom.client.DoubleClickEvent)
         */
        public void onDoubleClick(DoubleClickEvent event) {

            m_checkBox.setChecked(true);
            onSelectionChange();
            event.stopPropagation();
            event.preventDefault();
        }

        /**
         * Sets the select button, can be used instead of a double click to select and search.<p>
         * 
         * @param button the select button
         */
        public void setSelectButton(CmsPushButton button) {

            m_selectButton = button;
        }

        /**
         * Returns the check box.<p>
         * 
         * @return the check box
         */
        protected CmsCheckBox getCheckBox() {

            return m_checkBox;
        }

        /**
         * Executed on selection change. Either when the check box was clicked or on double click on a list item.<p>
         */
        protected abstract void onSelectionChange();
    }

    /** The widget type identifier for this widget. */
    private static final String WIDGET_TYPE = "categoryField";

    /** The panel contains all the categories. */
    FlowPanel m_categories = new FlowPanel();

    /** The default rows set. */
    int m_defaultHeight;

    /** The root panel containing the other components of this widget. */
    Panel m_panel = new FlowPanel();

    /** The container for the text area. */
    CmsScrollPanel m_scrollPanel = GWT.create(CmsScrollPanel.class);

    /** The error display for this widget. */
    private CmsErrorWidget m_error = new CmsErrorWidget();

    /**
     * Categoryfield widgets for ADE forms.<p>
     */
    public CmsCategoryField() {

        super();
        initWidget(m_panel);
        m_panel.add(m_scrollPanel);
        m_scrollPanel.getElement().getStyle().setHeight(50, Unit.PX);
        m_scrollPanel.add(m_categories);

        m_panel.add(m_error);
        m_scrollPanel.addStyleName(I_CmsLayoutBundle.INSTANCE.generalCss().cornerAll());
    }

    /**
     * Initializes this class.<p>
     */
    public static void initClass() {

        // registers a factory for creating new instances of this widget
        CmsWidgetFactoryRegistry.instance().registerFactory(WIDGET_TYPE, new I_CmsFormWidgetFactory() {

            /**
             * @see org.opencms.gwt.client.ui.input.form.I_CmsFormWidgetFactory#createWidget(java.util.Map)
             */
            public I_CmsFormWidget createWidget(Map<String, String> widgetParams) {

                return new CmsCategoryField();
            }
        });
    }

    /**
     * @param treeEntries
     * @param selectedCategories
     */
    public void buildCategoryTree(List<CmsCategoryTreeEntry> treeEntries, List<String> selectedCategories) {

        m_categories.removeFromParent();
        m_categories.clear();

        if ((treeEntries != null) && !treeEntries.isEmpty()) {
            // add the first level and children
            for (CmsCategoryTreeEntry category : treeEntries) {
                // set the category tree item and add to list 
                CmsTreeItem treeItem = buildTreeItem(category, selectedCategories);
                if (treeItem.isOpen()) {
                    addChildren(treeItem, category.getChildren(), selectedCategories);
                }

            }
        }
        m_scrollPanel.add(m_categories);
        m_scrollPanel.onResize();

    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#getApparentValue()
     */
    public String getApparentValue() {

        // TODO: Auto-generated method stub
        return null;
    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#getFieldType()
     */
    public FieldType getFieldType() {

        return I_CmsFormWidget.FieldType.STRING;
    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#getFormValue()
     */
    public Object getFormValue() {

        // TODO: Auto-generated method stub
        return null;
    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#getFormValueAsString()
     */
    public String getFormValueAsString() {

        // TODO: Auto-generated method stub
        return null;
    }

    /**
     * Returns the scroll panel of this widget.<p>
     * 
     * @return the scroll panel
     */
    public CmsScrollPanel getScrollPanel() {

        return m_scrollPanel;
    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#isEnabled()
     */
    public boolean isEnabled() {

        return false;
    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#reset()
     */
    public void reset() {

        //TODO implement reset();
    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#setAutoHideParent(org.opencms.gwt.client.ui.I_CmsAutoHider)
     */
    public void setAutoHideParent(I_CmsAutoHider autoHideParent) {

        // nothing to do
    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled) {

        //TODO implement setEnabled;
    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#setErrorMessage(java.lang.String)
     */
    public void setErrorMessage(String errorMessage) {

        m_error.setText(errorMessage);
    }

    /**
     * @see org.opencms.gwt.client.ui.input.I_CmsFormWidget#setFormValueAsString(java.lang.String)
     */
    public void setFormValueAsString(String value) {

        // TODO: Auto-generated method stub

    }

    /**
     * Sets the height of this category field.<p>
     * 
     * @param height the height of this category field
     */
    public void setHeight(int height) {

        m_defaultHeight = height;
        m_scrollPanel.setHeight(m_defaultHeight + "px");
        m_scrollPanel.setDefaultHeight(m_defaultHeight);
        m_scrollPanel.onResize();
    }

    /**
     * Sets the value of the widget.<p>
     * 
     * @param value the new value 
     */
    public void setSelected(Object value) {

        // nothing to do
    }

    /**
     * Set the selected categories.<p>
     * 
     * @param newValue String of selected categories separated by '|'
     */
    public void setSelectedAsString(String newValue) {

        setSelected(newValue);
    }

    /**
     * @see com.google.gwt.user.client.ui.Composite#onAttach()
     */
    @Override
    protected void onAttach() {

        super.onAttach();
    }

    /**
     * Adds children item to the category tree and select the categories.<p>
     * 
     * @param parent the parent item 
     * @param children the list of children
     * @param selectedCategories the list of categories to select
     */
    private void addChildren(CmsTreeItem parent, List<CmsCategoryTreeEntry> children, List<String> selectedCategories) {

        if (children != null) {
            for (CmsCategoryTreeEntry child : children) {
                // set the category tree item and add to parent tree item
                CmsTreeItem treeItem = buildTreeItem(child, selectedCategories);
                if ((selectedCategories != null) && selectedCategories.contains(child.getPath())) {
                    addChildren(treeItem, child.getChildren(), selectedCategories);
                    parent.addChild(treeItem);
                }

            }
        }
    }

    /**
     * Builds a tree item for the given category.<p>
     * 
     * @param category the category
     * @param selectedCategories the selected categories
     * 
     * @return the tree item widget
     */
    private CmsTreeItem buildTreeItem(CmsCategoryTreeEntry category, List<String> selectedCategories) {

        CmsListInfoBean categoryBean = new CmsListInfoBean(
            category.getTitle(),
            CmsStringUtil.isNotEmptyOrWhitespaceOnly(category.getDescription())
            ? category.getDescription()
            : category.getPath(),
            null);
        // set the list item widget

        CmsDataValue categoryTreeItem = new CmsDataValue(
            500,
            4,
            categoryBean.getTitle(),
            categoryBean.getSubTitle(),
            categoryBean.getLockIconTitle());
        categoryTreeItem.setTitle(categoryBean.getSubTitle());
        CmsTreeItem treeItem = new CmsTreeItem(false, categoryTreeItem);
        treeItem.setId(category.getPath());
        if ((selectedCategories != null) && selectedCategories.contains(category.getPath())) {
            m_categories.add(treeItem);
            treeItem.setOpen(true);
        }
        return treeItem;
    }
}
