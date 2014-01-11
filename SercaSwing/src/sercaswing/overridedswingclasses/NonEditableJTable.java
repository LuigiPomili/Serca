/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sercaswing.overridedswingclasses;

import javax.swing.JTable;

/**
 *
 * @author tappof
 */
public class NonEditableJTable extends JTable{
    
    public NonEditableJTable() {
        super();
    }

    @Override
    public boolean isCellEditable(int i, int i1) {
        return false;
    }
}
