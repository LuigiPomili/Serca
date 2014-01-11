/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sercaswing.overridedswingclasses;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author tappof
 */
public class OurDefaultTableModel extends DefaultTableModel {

    public OurDefaultTableModel(Object[] os, int i) {
        super(os, i);
    }

    @Override
    public Object getValueAt(int i, int i1) {
        Object obj = null;
        
        try {
            obj = super.getValueAt(i, i1);
        } catch (Exception e) {
            //System.err.println("CATCH IN OURDEFAULTTABLEMODEL");
            //e.printStackTrace();
            obj = null;
        }
        
        return obj;
    }
    
    
    
}
