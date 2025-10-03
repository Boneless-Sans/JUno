package com.boneless.ref;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class UVEditorFrame extends JFrame {
    private final Simple3DEngine engine;
    private UVEditorPanel editorPanel;
    private JLabel faceLabel;
    private int selectedFace = 0;

    public UVEditorFrame(Simple3DEngine engine) {
        this.engine = engine;
        setTitle("UV Editor");
        setSize(550, 620);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        editorPanel = new UVEditorPanel();
        add(editorPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton loadTextureButton = new JButton("Load Texture for Face");
        faceLabel = new JLabel("Selected Face: 0");

        loadTextureButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fileChooser.getSelectedFile();
                    BufferedImage image = ImageIO.read(file);
                    if (image != null) {
                        editorPanel.setTexture(image);
                        engine.setFaceTexture(selectedFace, image);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Could not load image.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        controlPanel.add(faceLabel);
        controlPanel.add(loadTextureButton);
        add(controlPanel, BorderLayout.SOUTH);
    }

    public void setSelectedFace(int faceIndex) {
        this.selectedFace = faceIndex;
        faceLabel.setText("Selected Face: " + faceIndex);
        editorPanel.repaint();
    }

    private class UVEditorPanel extends JPanel {
        private BufferedImage texture;
        private int draggingVertex = -1;
        private final int HANDLE_SIZE = 10;

        public UVEditorPanel() {
            MouseAdapter adapter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    Point p = e.getPoint();
                    Simple3DEngine.Vec2[] uvs = engine.cubeUVs[selectedFace];
                    for (int i = 0; i < uvs.length; i++) {
                        Point uvPoint = uvToScreen(uvs[i]);
                        if (p.distance(uvPoint) < HANDLE_SIZE) {
                            draggingVertex = i;
                            return;
                        }
                    }
                }
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (draggingVertex != -1) {
                        Simple3DEngine.Vec2 newUv = screenToUv(e.getPoint());
                        engine.cubeUVs[selectedFace][draggingVertex].u = newUv.u;
                        engine.cubeUVs[selectedFace][draggingVertex].v = newUv.v;
                        repaint();
                        engine.repaint();
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    draggingVertex = -1;
                }
            };
            addMouseListener(adapter);
            addMouseMotionListener(adapter);
        }

        public void setTexture(BufferedImage texture) {
            this.texture = texture;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (texture != null) {
                g.drawImage(texture, 0, 0, getWidth(), getHeight(), null);
            } else {
                g2.setColor(Color.DARK_GRAY);
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(Color.WHITE);
                g2.drawString("Load a texture to begin", getWidth()/2 - 60, getHeight()/2);
            }

            // Draw UV mesh
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.RED);
            Simple3DEngine.Vec2[] uvs = engine.cubeUVs[selectedFace];
            Point[] points = new Point[uvs.length];
            for (int i = 0; i < uvs.length; i++) {
                points[i] = uvToScreen(uvs[i]);
            }
            for (int i = 0; i < points.length; i++) {
                g2.drawLine(points[i].x, points[i].y, points[(i + 1) % points.length].x, points[(i + 1) % points.length].y);
            }

            // Orange overlay for selected face
            g2.setColor(new Color(255, 165, 0, 100)); // semi-transparent orange
            Polygon poly = new Polygon();
            for (Point p : points) poly.addPoint(p.x, p.y);
            g2.fillPolygon(poly);

            // Draw draggable handles
            g2.setColor(Color.RED);
            for (Point p : points) {
                g2.fill(new Ellipse2D.Double(p.x - HANDLE_SIZE/2.0, p.y - HANDLE_SIZE/2.0, HANDLE_SIZE, HANDLE_SIZE));
            }
        }

        private Point uvToScreen(Simple3DEngine.Vec2 uv) {
            return new Point((int) (uv.u * getWidth()), (int) (uv.v * getHeight()));
        }

        private Simple3DEngine.Vec2 screenToUv(Point p) {
            double u = (double) p.x / getWidth();
            double v = (double) p.y / getHeight();
            return new Simple3DEngine.Vec2(u, v);
        }
    }
}