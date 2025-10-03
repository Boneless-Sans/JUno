package com.boneless.ref;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

public class Simple3DEngineOld extends JPanel {
    // Camera transform
    private Vec3 camPos = new Vec3(0, 0, -5); // Camera starts at (0,0,-5)
    private Vec3 camRot = new Vec3(0, 0, 0);   // Camera rotation

    // Cube transform
    private Vec3 cubePos = new Vec3(0, 0, 0);
    private Vec3 cubeRot = new Vec3(0, 0, 0); // pitch, yaw, roll in degrees

    // Control target and projection mode
    private String controlTarget = "Cube";
    private boolean isOrthographic = false;
    private final double orthoScale = 50.0; // Pixels per world unit for ortho mode

    public Simple3DEngineOld() {
        setBackground(Color.BLACK);
    }

    static class Vec3 {
        double x, y, z;
        Vec3(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
    }

    // Apply rotation to a point around origin
    private Vec3 rotatePoint(Vec3 v, Vec3 rot) {
        double x = v.x, y = v.y, z = v.z;

        // Convert to radians
        double pitch = Math.toRadians(rot.x);
        double yaw   = Math.toRadians(rot.y);
        double roll  = Math.toRadians(rot.z);

        // Rotate around X (pitch)
        double y1 = y * Math.cos(pitch) - z * Math.sin(pitch);
        double z1 = y * Math.sin(pitch) + z * Math.cos(pitch);
        y = y1; z = z1;

        // Rotate around Y (yaw)
        double x2 = x * Math.cos(yaw) + z * Math.sin(yaw);
        double z2 = -x * Math.sin(yaw) + z * Math.cos(yaw);
        x = x2; z = z2;

        // Rotate around Z (roll)
        double x3 = x * Math.cos(roll) - y * Math.sin(roll);
        double y3 = x * Math.sin(roll) + y * Math.cos(roll);
        x = x3; y = y3;

        return new Vec3(x, y, z);
    }

    /**
     * Transforms a point from world space to camera space.
     * This is done by applying the *inverse* of the camera's transformation.
     */
    private Vec3 transformPoint(Vec3 v) {
        // 1. Apply inverse camera translation
        double tx = v.x - camPos.x;
        double ty = v.y - camPos.y;
        double tz = v.z - camPos.z;
        Vec3 translated = new Vec3(tx, ty, tz);

        // 2. Apply inverse camera rotation
        Vec3 invRot = new Vec3(-camRot.x, -camRot.y, -camRot.z);
        return rotatePoint(translated, invRot);
    }

    /**
     * Projects a 3D point (in camera space) to a 2D screen coordinate.
     * Supports both Perspective and Orthographic projection.
     */
    private Point project(Vec3 v, int width, int height) {
        if (isOrthographic) {
            // No perspective, just scale and center the coordinates
            double x = v.x * orthoScale + width / 2.0;
            double y = -v.y * orthoScale + height / 2.0; // Screen Y is inverted
            return new Point((int)x, (int)y);
        } else { // Perspective Projection
            // The camera is at the origin looking down the +Z axis.
            // A point is visible if its Z is positive.
            double z = v.z < 0.1 ? 0.1 : v.z; // Avoid division by zero or negative
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

        // --- Cube (translated + rotated in world space) ---
        Vec3[] cube = {
                new Vec3(-1,-1,-1), new Vec3(1,-1,-1), new Vec3(1,1,-1), new Vec3(-1,1,-1),
                new Vec3(-1,-1, 1), new Vec3(1,-1, 1), new Vec3(1,1, 1), new Vec3(-1,1, 1)
        };
        int[][] edges = {
                {0,1},{1,2},{2,3},{3,0}, {4,5},{5,6},{6,7},{7,4},
                {0,4},{1,5},{2,6},{3,7}
        };

        g2.setColor(Color.WHITE);
        for (int[] e : edges) {
            // Apply cube's rotation first
            Vec3 v1_rot = rotatePoint(cube[e[0]], cubeRot);
            Vec3 v2_rot = rotatePoint(cube[e[1]], cubeRot);

            // Then apply cube's translation to get final world coordinates
            Vec3 v1_world = new Vec3(v1_rot.x + cubePos.x, v1_rot.y + cubePos.y, v1_rot.z + cubePos.z);
            Vec3 v2_world = new Vec3(v2_rot.x + cubePos.x, v2_rot.y + cubePos.y, v2_rot.z + cubePos.z);

            // The drawLine3D method will handle the camera transform and projection
            drawLine3D(g2, v1_world, v2_world, Color.WHITE, w,h);
        }
    }

    /**
     * Draws a line from a 3D world point v1 to v2.
     * It handles the camera transformation and projection to screen space.
     */
    private void drawLine3D(Graphics2D g2, Vec3 v1, Vec3 v2, Color color, int w, int h) {
        // Transform world points to camera space
        Vec3 v1CamSpace = transformPoint(v1);
        Vec3 v2CamSpace = transformPoint(v2);

        // Project camera space points to 2D screen
        Point p1 = project(v1CamSpace, w, h);
        Point p2 = project(v2CamSpace, w, h);

        g2.setColor(color);
        g2.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));
    }

    // --- Controls Window ---
    public void showControls() {
        JFrame controlFrame = new JFrame("Controls");
        controlFrame.setLayout(new BorderLayout());

        JPanel sliders = new JPanel(new GridLayout(6, 1));
        String[] labels = {"X Pos", "Y Pos", "Z Pos", "Pitch (X)", "Yaw (Y)", "Roll (Z)"};
        JSlider[] sliderArr = new JSlider[6];

        for (int i = 0; i < 6; i++) {
            JPanel row = new JPanel(new BorderLayout());
            JLabel l = new JLabel(labels[i]);
            sliderArr[i] = new JSlider(-100, 100, 0); // Increased range for more control
            row.add(l, BorderLayout.WEST);
            row.add(sliderArr[i], BorderLayout.CENTER);
            sliders.add(row);
        }

        JPanel topPanel = new JPanel();
        JComboBox<String> targetBox = new JComboBox<>(new String[]{"Cube", "Camera"});
        targetBox.addActionListener(e -> controlTarget = (String) targetBox.getSelectedItem());

        JCheckBox orthoCheck = new JCheckBox("Orthographic");
        orthoCheck.addActionListener(e -> {
            isOrthographic = orthoCheck.isSelected();
            repaint();
        });

        topPanel.add(new JLabel("Control Target:"));
        topPanel.add(targetBox);
        topPanel.add(orthoCheck);

        // Slider listeners
        for (int i = 0; i < 6; i++) {
            final int idx = i;
            sliderArr[i].addChangeListener(e -> {
                double posValue = sliderArr[idx].getValue() / 10.0; // Scale down for position
                double rotValue = sliderArr[idx].getValue() * 1.8;  // Scale up for rotation (range is now -180 to 180)

                if ("Camera".equals(controlTarget)) {
                    if (idx == 0) camPos.x = posValue;
                    if (idx == 1) camPos.y = posValue;
                    if (idx == 2) camPos.z = posValue - 5; // Start with an offset
                    if (idx == 3) camRot.x = rotValue;
                    if (idx == 4) camRot.y = rotValue;
                    if (idx == 5) camRot.z = rotValue;
                } else { // Cube
                    if (idx == 0) cubePos.x = posValue;
                    if (idx == 1) cubePos.y = posValue;
                    if (idx == 2) cubePos.z = posValue;
                    if (idx == 3) cubeRot.x = rotValue;
                    if (idx == 4) cubeRot.y = rotValue;
                    if (idx == 5) cubeRot.z = rotValue;
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
        Simple3DEngine panel = new Simple3DEngine();
        f.add(panel);
        f.setSize(800, 800);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);

        panel.showControls();
    }
}