/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SpritesPackerWindow.java
 *
 * Created on Jun 2, 2011, 9:56:41 AM
 */
package spritespacker;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.io.File;
import java.io.FilenameFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import packer.Pack;
import packer.Sheet;
import packer.SheetPanel;
import packer.Sprite;

/**
 *
 * @author dals
 */
public class SpritesPackerWindow extends javax.swing.JFrame {

    /** The panel showing the currently generated sprite sheet */
    private SheetPanel sheetPanel;
    /** The sprites currently displayed */
    private DefaultListModel sprites = new DefaultListModel();

    /** The width of the texture being generated */
    private int twidth;
    /** The height of the texture being generated */
    private int theight;
//    /** The width texture sizes model */
//    private DefaultComboBoxModel sizeWidth = new DefaultComboBoxModel();
//    /** The height texture sizes model */
//    private DefaultComboBoxModel sizeHeight = new DefaultComboBoxModel();
    /** The chooser used to select sprites */
    private JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
    /** The chooser used to save the sprite sheet */
    
    public String lastSaveLocation = System.getProperty("user.home");
    private JFileChooser saveChooser = new JFileChooser(lastSaveLocation);
    /** The packing tool */
    private Pack pack = new Pack();
    
    
    private FileDialog openFileDialog = new FileDialog(this, "Choose a sprites", FileDialog.LOAD);
    private FileDialog saveFileDialog = new FileDialog(this, "Save spritesheet as", FileDialog.SAVE);

    /** Creates new form SpritesPackerWindow */
    public SpritesPackerWindow() {
        initObjects();
        initComponents();
        initValues();
        setIconImage(Toolkit.getDefaultToolkit().getImage("/icon.png"));
    }

    public void initObjects() {
        sheetPanel = new SheetPanel(this);

        saveChooser.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                return (f.getName().endsWith(".png"));
            }
            public String getDescription() {
                return "PNG Images (*.png)";
            }
        });
        
        
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                return (f.getName().endsWith(".png")
                        || f.getName().endsWith(".jpg")
                        || f.getName().endsWith(".gif"));
            }

            public String getDescription() {
                return "Images (*.jpg, *.png, *.gif)";
            }
        });       
        
    }

    public void initValues() 
    {
        twidth = Integer.parseInt(widthBox.getSelectedItem().toString());
        theight = Integer.parseInt(heightBox.getSelectedItem().toString());     
        
        sheetPanel.setTextureSize(twidth, theight);
        
        spritesList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                    Object[] values = spritesList.getSelectedValues();
                    ArrayList spritesArray = new ArrayList();
                    for (int i=0;i<values.length;i++) {
                            spritesArray.add(values[i]);
                    }

                    spritesList.removeListSelectionListener(this);
                    select(spritesArray);
                    spritesList.addListSelectionListener(this);
            }
        });
        
        
        

    }
    /**
     * Get the sprite a given location on the current sheet
     * 
     * @param x The x coordinate to look for the sprite
     * @param y The y coordinate to look for the sprite
     * @return The sprite found at the given location or null if no sprite can be found
     */
    public Sprite getSpriteAt(int x, int y) {
        for (int i = 0; i < sprites.size(); i++) {
            if (((Sprite) sprites.get(i)).contains(x, y)) {
                return ((Sprite) sprites.get(i));
            }
        }

        return null;
    }

    /**
     * Select a series of sprites
     * 
     * @param selection The series of sprites to be selected (Sprite objects)
     */
    public void select(ArrayList selection) {
        spritesList.clearSelection();
        int[] selected = new int[selection.size()];
        for (int i = 0; i < selection.size(); i++) {
            selected[i] = sprites.indexOf(selection.get(i));
        }
        spritesList.setSelectedIndices(selected);
        sheetPanel.setSelection(selection);
    }

    /**
     * Save the sprite sheet
     */
    private void save() throws Exception {
//        int resp = saveChooser.showSaveDialog(this);
//        if (resp == JFileChooser.APPROVE_OPTION) {
//            File out = saveChooser.getSelectedFile();
//
//            ArrayList list = new ArrayList();
//            for (int i = 0; i < sprites.size(); i++) {
//                list.add(sprites.elementAt(i));
//            }
//
//            try {
//                
//                int b = ((Integer) border.getValue()).intValue();
//                pack.packImages(list, twidth, theight, b, out);
//            } catch (IOException e) {
//                // shouldn't happen 
//                e.printStackTrace();
//                JOptionPane.showMessageDialog(this, "Failed to write output");
//            }
//        }
        int resp = saveChooser.showSaveDialog(this);
        if (resp == JFileChooser.APPROVE_OPTION) {
                                    
            File out = saveChooser.getSelectedFile();
            if(!out.getName().toLowerCase().endsWith(".png")) {
                out = new File(out.getAbsolutePath()+".png");
            }
                        
            ArrayList list = new ArrayList();
            for (int i=0;i<sprites.size();i++) {
                list.add(sprites.elementAt(i));
            }
            
            try {
                int b = ((Integer) border.getValue()).intValue();
                pack.packImages(list, twidth, theight, b, out);
                
                lastSaveLocation = out.getParent();
            } catch (Exception e) {
                // shouldn't happen
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to write output");
            }
        }
    }

    /**
     * Regenerate the sprite sheet that is being displayed
     */
    private void regenerate() {
        try {
            ArrayList list = new ArrayList();
            for (int i = 0; i < sprites.size(); i++) {
                list.add(sprites.elementAt(i));
            }

            int b = ((Integer) border.getValue()).intValue();
            Sheet sheet = pack.packImages(list, twidth, theight, b, null);
            sheetPanel.setImage(sheet);
        } catch (Exception ex) {
            Logger.getLogger(SpritesPackerWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * A list cell renderer to show just the plain names
     * 
     * @author kevin
     */
    private class FileListRenderer extends DefaultListCellRenderer {

        /**
         * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
         */
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
                    cellHasFocus);

            Sprite sprite = (Sprite) value;
            label.setText(sprite.getName());

            return label;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        spritesScrollPane = new javax.swing.JScrollPane();
        spritesList = new javax.swing.JList(sprites);
        btnAdd = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        sheetScrollPane = new javax.swing.JScrollPane(sheetPanel);
        spritesLabel = new javax.swing.JLabel();
        widthBox = new javax.swing.JComboBox();
        heightBox = new javax.swing.JComboBox();
        wLabel = new javax.swing.JLabel();
        hLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        border = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        widthText = new javax.swing.JTextField();
        heightText = new javax.swing.JTextField();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        saveMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sprites Packer");
        setResizable(false);
        setSize(new java.awt.Dimension(1024, 768));

        spritesList.setCellRenderer(new FileListRenderer());
        spritesScrollPane.setViewportView(spritesList);

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/plus.png"))); // NOI18N
        btnAdd.setBorderPainted(false);
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/minus.png"))); // NOI18N
        btnRemove.setBorderPainted(false);
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        sheetScrollPane.setAutoscrolls(true);

        spritesLabel.setText("Included sprites:");

        widthBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "128", "256", "512", "1024", "2048", "4096" }));
        widthBox.setSelectedIndex(2);
        widthBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                widthBoxActionPerformed(evt);
            }
        });

        heightBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "128", "256", "512", "1024", "2048", "4096" }));
        heightBox.setSelectedIndex(2);
        heightBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                heightBoxActionPerformed(evt);
            }
        });

        wLabel.setText("WIdth:");

        hLabel.setText("Height:");

        jLabel1.setText("Border:");

        border.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                borderStateChanged(evt);
            }
        });

        jLabel2.setText("Generated spritesheet:");

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/save.png"))); // NOI18N
        btnSave.setBorderPainted(false);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        widthText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                widthTextFocusLost(evt);
            }
        });

        heightText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                heightTextFocusLost(evt);
            }
        });

        fileMenu.setMnemonic('f');
        fileMenu.setText("File");

        saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveMenuItem.setMnemonic('s');
        saveMenuItem.setText("Save");
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveMenuItem);

        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sheetScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 889, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(spritesLabel)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(54, 54, 54)
                                .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(spritesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(hLabel)
                                    .addComponent(wLabel)
                                    .addComponent(jLabel1)
                                    .addComponent(widthText, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(widthBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, 129, Short.MAX_VALUE)
                                    .addComponent(heightBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, 129, Short.MAX_VALUE)
                                    .addComponent(border, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                                    .addComponent(heightText, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(31, 31, 31))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(spritesLabel))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(spritesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(heightText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(widthText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(border, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(widthBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(wLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(heightBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(hLabel)))
                    .addComponent(sheetScrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 621, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed

        Object[] selected = spritesList.getSelectedValues();
        for (int i=0;i<selected.length;i++) {
                sprites.removeElement(selected[i]);
        }
        regenerate();

    }//GEN-LAST:event_btnRemoveActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        int resp = chooser.showOpenDialog(this);
        if (resp == JFileChooser.APPROVE_OPTION) {
                File[] selected = chooser.getSelectedFiles();
                for (int i=0;i<selected.length;i++) {
                        try {
                                sprites.addElement(new Sprite(selected[i]));
                        } catch (IOException x) {
                                x.printStackTrace();
                                JOptionPane.showMessageDialog(this, "Unable to load: "+selected[i].getName());
                        }
                }
        }
        
        
        regenerate();
    }//GEN-LAST:event_btnAddActionPerformed

    private void widthBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_widthBoxActionPerformed
        twidth = Integer.parseInt(widthBox.getSelectedItem().toString());
        sheetPanel.setTextureSize(twidth, theight);
        widthText.setText(twidth+"");
        regenerate();
    }//GEN-LAST:event_widthBoxActionPerformed

    private void heightBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_heightBoxActionPerformed
        theight = Integer.parseInt(heightBox.getSelectedItem().toString());
        sheetPanel.setTextureSize(twidth, theight);
        heightText.setText(theight+"");
        regenerate();
    }//GEN-LAST:event_heightBoxActionPerformed

    private void borderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_borderStateChanged
        regenerate();
    }//GEN-LAST:event_borderStateChanged

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
}//GEN-LAST:event_exitMenuItemActionPerformed

    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
        try {
            save();
        } catch (Exception ex) {
            Logger.getLogger(SpritesPackerWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_saveMenuItemActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        try {
            save();
        } catch (Exception ex) {
            Logger.getLogger(SpritesPackerWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void widthTextFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_widthTextFocusLost
        twidth = Integer.parseInt(widthText.getText());
        sheetPanel.setTextureSize(twidth, theight);
        regenerate();
    }//GEN-LAST:event_widthTextFocusLost

    private void heightTextFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_heightTextFocusLost
        theight = Integer.parseInt(heightText.getText());
        sheetPanel.setTextureSize(twidth, theight);
        regenerate();
    }//GEN-LAST:event_heightTextFocusLost
    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//
//            public void run() {
//                new SpritesPackerWindow().setVisible(true);
//            }
//        });
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner border;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnSave;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JLabel hLabel;
    private javax.swing.JComboBox heightBox;
    private javax.swing.JTextField heightText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JScrollPane sheetScrollPane;
    private javax.swing.JLabel spritesLabel;
    private javax.swing.JList spritesList;
    private javax.swing.JScrollPane spritesScrollPane;
    private javax.swing.JLabel wLabel;
    private javax.swing.JComboBox widthBox;
    private javax.swing.JTextField widthText;
    // End of variables declaration//GEN-END:variables
}
