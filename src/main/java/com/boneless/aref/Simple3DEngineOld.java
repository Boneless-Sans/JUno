package com.boneless.aref;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class Simple3DEngineOld extends JPanel implements KeyListener, MouseListener {
    // Camera
    private Vec3 camPos = new Vec3(0, 0, -5);
    private Vec3 camRot = new Vec3(0, 0, 0);

    // Scene objects
    private List<Cube> cubes = new ArrayList<>();
    private Cube selectedCube = null;
    private boolean cameraSelected = false;

    // Projection
    private boolean isOrthographic = false;
    private final double orthoScale = 50.0;

    public Simple3DEngineOld() {
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);

        // Add one initial cube
        cubes.add(new Cube(new Vec3(0,0,0), new Vec3(0,0,0)));
    }

    // Basic vector
    static class Vec3 {
        double x, y, z;
        Vec3(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
    }

    // Cube class
    static class Cube {
        Vec3 pos, rot;
        Cube(Vec3 pos, Vec3 rot) {
            this.pos = pos;
            this.rot = rot;
        }
    }

    // Apply rotation to a point around origin
    private Vec3 rotatePoint(Vec3 v, Vec3 rot) {
        double x = v.x, y = v.y, z = v.z;
        double pitch = Math.toRadians(rot.x);
        double yaw   = Math.toRadians(rot.y);
        double roll  = Math.toRadians(rot.z);

        // Rotate X
        double y1 = y * Math.cos(pitch) - z * Math.sin(pitch);
        double z1 = y * Math.sin(pitch) + z * Math.cos(pitch);
        y = y1; z = z1;

        // Rotate Y
        double x2 = x * Math.cos(yaw) + z * Math.sin(yaw);
        double z2 = -x * Math.sin(yaw) + z * Math.cos(yaw);
        x = x2; z = z2;

        // Rotate Z
        double x3 = x * Math.cos(roll) - y * Math.sin(roll);
        double y3 = x * Math.sin(roll) + y * Math.cos(roll);
        x = x3; y = y3;

        return new Vec3(x, y, z);
    }

    // World → Camera
    private Vec3 transformPoint(Vec3 v) {
        double tx = v.x - camPos.x;
        double ty = v.y - camPos.y;
        double tz = v.z - camPos.z;
        Vec3 translated = new Vec3(tx, ty, tz);
        Vec3 invRot = new Vec3(-camRot.x, -camRot.y, -camRot.z);
        return rotatePoint(translated, invRot);
    }

    // Camera → Screen
    private Point project(Vec3 v, int width, int height) {
        if (isOrthographic) {
            double x = v.x * orthoScale + width / 2.0;
            double y = -v.y * orthoScale + height / 2.0;
            return new Point((int)x, (int)y);
        } else {
            double z = v.z < 0.1 ? 0.1 : v.z;
            double x = (v.x / z) * width / 2.0 + width / 2.0;
            double y = (v.y / z) * height / 2.0 + height / 2.0;
            return new Point((int)x, (int)y);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth(), h = getHeight();
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- Axes ---
        drawLine3D(g2, new Vec3(0,0,0), new Vec3(2,0,0), Color.RED, w,h);
        drawLine3D(g2, new Vec3(0,0,0), new Vec3(0,2,0), Color.GREEN, w,h);
        drawLine3D(g2, new Vec3(0,0,0), new Vec3(0,0,2), Color.BLUE, w,h);

        // --- Floor Grid ---
        g2.setStroke(new BasicStroke(1));
        for (int x=-5; x<=5; x++) {
            drawLine3D(g2, new Vec3(x,-2,-5), new Vec3(x,-2,5), Color.DARK_GRAY, w,h);
        }
        for (int z=-5; z<=5; z++) {
            drawLine3D(g2, new Vec3(-5,-2,z), new Vec3(5,-2,z), Color.DARK_GRAY, w,h);
        }

        // --- Cubes ---
        for (int ci=0; ci<cubes.size(); ci++) {
            Cube cube = cubes.get(ci);
            drawCube(g2, cube, w, h);

            // Debug text at cube origin
            Vec3 camSpace = transformPoint(cube.pos);
            if (camSpace.z > 0.1) {
                Point p = project(camSpace, w, h);
                g2.setColor(Color.YELLOW);
                g2.setFont(new Font("Consolas", Font.PLAIN, 12));
                g2.drawString("Cube[" + ci + "]", p.x + 5, p.y - 5);
                g2.drawString(
                        String.format("Pos(%.1f,%.1f,%.1f)", cube.pos.x, cube.pos.y, cube.pos.z),
                        p.x + 5, p.y + 10
                );
                g2.drawString(
                        String.format("Rot(%.0f,%.0f,%.0f)", cube.rot.x, cube.rot.y, cube.rot.z),
                        p.x + 5, p.y + 25
                );
            }
        }

        // --- Camera overlay ---
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Consolas", Font.PLAIN, 14));
        int line = 20;
        g2.drawString("Camera Pos: ("+camPos.x+", "+camPos.y+", "+camPos.z+")", 10, line); line+=15;
        g2.drawString("Camera Rot: ("+camRot.x+", "+camRot.y+", "+camRot.z+")", 10, line);
    }

    private void drawCube(Graphics2D g2, Cube cube, int w, int h) {
        Vec3[] verts = {
                new Vec3(-1,-1,-1), new Vec3(1,-1,-1), new Vec3(1,1,-1), new Vec3(-1,1,-1),
                new Vec3(-1,-1, 1), new Vec3(1,-1, 1), new Vec3(1,1, 1), new Vec3(-1,1, 1)
        };
        int[][] edges = {
                {0,1},{1,2},{2,3},{3,0}, {4,5},{5,6},{6,7},{7,4},
                {0,4},{1,5},{2,6},{3,7}
        };

        Color outlineColor = (cube == selectedCube) ? Color.ORANGE : Color.WHITE;

        for (int[] e : edges) {
            Vec3 v1_rot = rotatePoint(verts[e[0]], cube.rot);
            Vec3 v2_rot = rotatePoint(verts[e[1]], cube.rot);

            Vec3 v1_world = new Vec3(v1_rot.x + cube.pos.x, v1_rot.y + cube.pos.y, v1_rot.z + cube.pos.z);
            Vec3 v2_world = new Vec3(v2_rot.x + cube.pos.x, v2_rot.y + cube.pos.y, v2_rot.z + cube.pos.z);

            drawLine3D(g2, v1_world, v2_world, outlineColor, w,h);
        }
    }

    private void drawLine3D(Graphics2D g2, Vec3 v1, Vec3 v2, Color color, int w, int h) {
        Vec3 v1CamSpace = transformPoint(v1);
        Vec3 v2CamSpace = transformPoint(v2);
        Point p1 = project(v1CamSpace, w, h);
        Point p2 = project(v2CamSpace, w, h);
        g2.setColor(color);
        g2.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));
    }

    // --- Keyboard ---
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == 'c') {
            cubes.add(new Cube(new Vec3(0,0,0), new Vec3(0,0,0)));
            repaint();
        }
    }
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    // --- Mouse Picking ---
    @Override
    public void mousePressed(MouseEvent e) {
        int w = getWidth(), h = getHeight();
        Point click = e.getPoint();
        Cube closest = null;
        double minDist = 20.0;

        for (Cube cube : cubes) {
            Vec3 camSpace = transformPoint(cube.pos);
            if (camSpace.z > 0.1) {
                Point p = project(camSpace, w, h);
                double dx = p.x - click.x;
                double dy = p.y - click.y;
                double dist = Math.sqrt(dx*dx + dy*dy);
                if (dist < minDist) {
                    minDist = dist;
                    closest = cube;
                }
            }
        }
        selectedCube = closest;
        cameraSelected = false;
        repaint();
    }
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    // --- Controls Window ---
    public void showControls() {
        JFrame controlFrame = new JFrame("Controls");
        controlFrame.setLayout(new BorderLayout());

        JPanel sliders = new JPanel(new GridLayout(6, 1));
        String[] labels = {"X Pos", "Z Pos (Forward/Back)", "Y Pos (Up/Down)", "Pitch (X)", "Yaw (Y)", "Roll (Z)"};
        JSlider[] sliderArr = new JSlider[6];

        for (int i = 0; i < 6; i++) {
            JPanel row = new JPanel(new BorderLayout());
            JLabel l = new JLabel(labels[i]);
            sliderArr[i] = new JSlider(-100, 100, 0);
            row.add(l, BorderLayout.WEST);
            row.add(sliderArr[i], BorderLayout.CENTER);
            sliders.add(row);
        }

        JPanel topPanel = new JPanel();
        JButton camButton = new JButton("Select Camera");
        camButton.addActionListener(e -> {
            cameraSelected = true;
            selectedCube = null;
        });

        JCheckBox orthoCheck = new JCheckBox("Orthographic");
        orthoCheck.addActionListener(e -> {
            isOrthographic = orthoCheck.isSelected();
            repaint();
        });

        topPanel.add(camButton);
        topPanel.add(orthoCheck);

        for (int i = 0; i < 6; i++) {
            final int idx = i;
            sliderArr[i].addChangeListener(e -> {
                double posValue = sliderArr[idx].getValue() / 10.0;
                double rotValue = sliderArr[idx].getValue() * 1.8;

                if (cameraSelected) {
                    if (idx == 0) camPos.x = posValue;
                    if (idx == 1) camPos.z = posValue;
                    if (idx == 2) camPos.y = posValue; // vertical
                    if (idx == 3) camRot.x = rotValue;
                    if (idx == 4) camRot.y = rotValue;
                    if (idx == 5) camRot.z = rotValue;
                } else if (selectedCube != null) {
                    if (idx == 0) selectedCube.pos.x = posValue;
                    if (idx == 1) selectedCube.pos.z = posValue;
                    if (idx == 2) selectedCube.pos.y = posValue; // vertical
                    if (idx == 3) selectedCube.rot.x = rotValue;
                    if (idx == 4) selectedCube.rot.y = rotValue;
                    if (idx == 5) selectedCube.rot.z = rotValue;
                }
                repaint();
            });
        }

        controlFrame.add(topPanel, BorderLayout.NORTH);
        controlFrame.add(sliders, BorderLayout.CENTER);
        controlFrame.setSize(400, 400);
        controlFrame.setVisible(true);
        controlFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Simple 3D Engine");
        Simple3DEngineOld panel = new Simple3DEngineOld();
        f.add(panel);
        f.setSize(800, 800);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);

        panel.showControls();
    }
}
