/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CompressionRatioViewer extends javax.swing.JDialog {

    public CompressionRatioViewer(java.awt.Frame parent, boolean modal, String filename) {
        super(parent, modal);
        initComponents();

        String toSetTitle = filename;
        setTitle("Compression ratios for: " + toSetTitle + "");
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width / 2 - getSize().width / 2, dim.height / 2 - getSize().height / 2);
        setResizable(false);
        try {
            setCompressionRatios(new AudioInfo(Files.readAllBytes(Paths.get(filename))));
        } catch (IOException ex) {
            Logger.getLogger(CompressionRatioViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setCompressionRatios(AudioInfo info) {
        byte[] dataByteArray = info.getDataByteArray();
        int originalSizeUsed = Byte.SIZE * dataByteArray.length;
        int newSizeUsed;

        // Process Huffman coding compression ratio.
        CodingProcessor codingProcessor;

        // Process Huffman coding compression ratio.
        codingProcessor = new HuffmanCodingProcessor();
        codingProcessor.addSamples(dataByteArray);
        byte[] huffmanData = codingProcessor.compress();
        newSizeUsed = Byte.SIZE * huffmanData.length;
        huffmanCodingRatioValueLabel.setText(Double.toString((double) ((double) (originalSizeUsed) / (double) (newSizeUsed))) + "%");

        // Process LZW coding compression ratio.
        codingProcessor = new LZWCodingProcessor();
        codingProcessor.addSamples(dataByteArray);
        byte[] lzwData = codingProcessor.compress();
        newSizeUsed = Byte.SIZE * lzwData.length;
        lzwCodingRatioValueLabel.setText(Double.toString((double) ((double) (originalSizeUsed) / (double) (newSizeUsed))) + "%");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titleLabel = new javax.swing.JLabel();
        huffmanCodingRatioLabelLabel = new javax.swing.JLabel();
        lzwCodingRatioLabelLabel = new javax.swing.JLabel();
        huffmanCodingRatioValueLabel = new javax.swing.JLabel();
        lzwCodingRatioValueLabel = new javax.swing.JLabel();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        titleLabel.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        titleLabel.setText("WAV Compression Ratios");

        huffmanCodingRatioLabelLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        huffmanCodingRatioLabelLabel.setText("Huffman coding compression ratio");

        lzwCodingRatioLabelLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lzwCodingRatioLabelLabel.setText("LZW coding compression ratio");

        huffmanCodingRatioValueLabel.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        huffmanCodingRatioValueLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        huffmanCodingRatioValueLabel.setText("[value]");

        lzwCodingRatioValueLabel.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        lzwCodingRatioValueLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lzwCodingRatioValueLabel.setText("[value]");

        closeButton.setText("close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titleLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lzwCodingRatioLabelLabel)
                            .addComponent(huffmanCodingRatioLabelLabel))
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(huffmanCodingRatioValueLabel)
                            .addComponent(lzwCodingRatioValueLabel))))
                .addContainerGap(107, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(closeButton)
                .addGap(49, 49, 49))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(titleLabel)
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(huffmanCodingRatioLabelLabel)
                    .addComponent(huffmanCodingRatioValueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lzwCodingRatioLabelLabel)
                    .addComponent(lzwCodingRatioValueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton)
                .addGap(0, 16, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CompressionRatioViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CompressionRatioViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CompressionRatioViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CompressionRatioViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {
            CompressionRatioViewer dialog = new CompressionRatioViewer(new javax.swing.JFrame(), true, null);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel huffmanCodingRatioLabelLabel;
    private javax.swing.JLabel huffmanCodingRatioValueLabel;
    private javax.swing.JLabel lzwCodingRatioLabelLabel;
    private javax.swing.JLabel lzwCodingRatioValueLabel;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
}
