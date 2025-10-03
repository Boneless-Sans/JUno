package com.boneless.aref;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class Simple3DEngine extends JPanel {
    // Scene and Camera
    private Vec3 camPos = new Vec3(0, -8, 2);
    private Vec3 camRot = new Vec3(0, 0, 0);
    private double fov = 90;

    // Cube
    private Vec3 cubePos = new Vec3(0, 0, 0);
    private Vec3 cubeRot = new Vec3(0, 0, 0);
    private final Map<Integer, BufferedImage> faceTextures = new ConcurrentHashMap<>();
    public final Vec2[][] cubeUVs = new Vec2[6][4]; // Public for editor access

    // Controls
    private String controlTarget = "Cube";
    private boolean isOrthographic = false;
    private boolean isWireframe = false; // Start in solid mode to show textures
    private final double orthoScale = 50.0;
    private UVEditorFrame uvEditor;

    // Rendering Buffer
    private BufferedImage framebuffer;
    private int[] framebufferPixels;
    private double[] zBuffer;

    public Simple3DEngine() {
        setBackground(Color.BLACK);
        initDefaultUVs();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseClick(e.getPoint());
            }
        });
    }

    // --- Core Data Structures ---
    static class Vec2 { double u, v; Vec2(double u, double v) { this.u = u; this.v = v; } }
    static class Vec3 { double x, y, z; Vec3(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }}
    static class RenderableFace implements Comparable<RenderableFace> {
        int index; double avgDepth; Polygon polygon; Vec3[] camSpaceVerts;
        RenderableFace(int i, double d, Polygon p, Vec3[] v) { index=i; avgDepth=d; polygon=p; camSpaceVerts=v; }
        @Override public int compareTo(RenderableFace other) { return Double.compare(other.avgDepth, this.avgDepth); }
    }

    private void initDefaultUVs() {
        for (int i = 0; i < 6; i++) {
            cubeUVs[i][0] = new Vec2(0, 1);
            cubeUVs[i][1] = new Vec2(1, 1);
            cubeUVs[i][2] = new Vec2(1, 0);
            cubeUVs[i][3] = new Vec2(0, 0);
        }
    }

    // --- 3D Math and Transformations (unchanged) ---
    private Vec3 rotatePoint(Vec3 v, Vec3 rot) {
        double x = v.x, y = v.y, z = v.z;
        double pitch = Math.toRadians(rot.x); double roll = Math.toRadians(rot.y); double yaw = Math.toRadians(rot.z);
        double y1 = y * Math.cos(pitch) - z * Math.sin(pitch); double z1 = y * Math.sin(pitch) + z * Math.cos(pitch); y = y1; z = z1;
        double x2 = x * Math.cos(roll) + z * Math.sin(roll); double z2 = -x * Math.sin(roll) + z * Math.cos(roll); x = x2; z = z2;
        double x3 = x * Math.cos(yaw) - y * Math.sin(yaw); double y3 = x * Math.sin(yaw) + y * Math.cos(yaw); x = x3; y = y3;
        return new Vec3(x, y, z);
    }
    private Vec3 transformPoint(Vec3 v) {
        double tx = v.x - camPos.x; double ty = v.y - camPos.y; double tz = v.z - camPos.z;
        return rotatePoint(new Vec3(tx, ty, tz), new Vec3(-camRot.x, -camRot.y, -camRot.z));
    }
    private Point project(Vec3 v, int width, int height) {
        double depth = v.y < 0.1 ? 0.1 : v.y;
        if (isOrthographic) {
            double x = v.x * orthoScale + width / 2.0; double y = -v.z * orthoScale + height / 2.0;
            return new Point((int)x, (int)y);
        } else {
            double scale = (width / 2.0) / Math.tan(Math.toRadians(fov / 2.0));
            double x = (v.x * scale / depth) + width / 2.0; double y = (-v.z * scale / depth) + height / 2.0;
            return new Point((int)x, (int)y);
        }
    }

    // --- Rendering Pipeline ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth(); int h = getHeight();
        if (framebuffer == null || framebuffer.getWidth() != w || framebuffer.getHeight() != h) {
            framebuffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            framebufferPixels = ((DataBufferInt) framebuffer.getRaster().getDataBuffer()).getData();
            zBuffer = new double[w * h];
        }

        // Clear buffers
        Graphics2D g2 = framebuffer.createGraphics();
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, w, h);
        Arrays.fill(zBuffer, Double.POSITIVE_INFINITY);

        // Render scene to buffer
        renderScene(g2, w, h);

        // Draw the final buffer to the screen
        g.drawImage(framebuffer, 0, 0, null);
        g2.dispose();
    }

    private void renderScene(Graphics2D g2, int w, int h) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(2));
        drawLine3D(g2, new Vec3(0,0,0), new Vec3(2,0,0), Color.RED, w,h);
        drawLine3D(g2, new Vec3(0,0,0), new Vec3(0,2,0), Color.GREEN, w,h);
        drawLine3D(g2, new Vec3(0,0,0), new Vec3(0,0,2), Color.BLUE, w,h);
        g2.setStroke(new BasicStroke(1));
        for (int i = -5; i <= 5; i++) {
            drawLine3D(g2, new Vec3(i, -5, 0), new Vec3(i, 5, 0), Color.DARK_GRAY, w,h);
            drawLine3D(g2, new Vec3(-5, i, 0), new Vec3(5, i, 0), Color.DARK_GRAY, w,h);
        }

        if (isWireframe) {
            drawCubeWireframe(g2, w, h);
        } else {
            drawCubeSolid(w, h);
        }
    }

    private void drawCubeWireframe(Graphics2D g2, int w, int h) {
        Vec3[] vertices = getCubeWorldVertices();
        for (int[] edge : CUBE_EDGES) {
            drawLine3D(g2, vertices[edge[0]], vertices[edge[1]], Color.WHITE, w, h);
        }
    }

    private void drawCubeSolid(int w, int h) {
        List<RenderableFace> facesToRender = getVisibleFaces(w, h);
        Collections.sort(facesToRender); // Painter's algorithm
        for (RenderableFace face : facesToRender) {
            rasterizeTexturedFace(face, w, h);
        }
    }

    private void rasterizeTexturedFace(RenderableFace face, int w, int h) {
        BufferedImage texture = faceTextures.get(face.index);

        Vec3 v0 = face.camSpaceVerts[0];
        Vec3 v1 = face.camSpaceVerts[1];
        Vec3 v2 = face.camSpaceVerts[2];
        Vec3 v3 = face.camSpaceVerts[3];
        Vec2 uv0 = cubeUVs[face.index][0];
        Vec2 uv1 = cubeUVs[face.index][1];
        Vec2 uv2 = cubeUVs[face.index][2];
        Vec2 uv3 = cubeUVs[face.index][3];

        if (texture == null) {
            // Draw solid white if no texture
            Graphics2D g2 = framebuffer.createGraphics();
            g2.setColor(Color.WHITE);
            g2.fillPolygon(face.polygon);
            g2.dispose();
            return;
        }

        // Split quad into two triangles for textured rendering
        rasterizeTriangle(v0, v1, v2, uv0, uv1, uv2, texture, w, h);
        rasterizeTriangle(v0, v2, v3, uv0, uv2, uv3, texture, w, h);
    }

    private void rasterizeTriangle(Vec3 v0, Vec3 v1, Vec3 v2, Vec2 uv0, Vec2 uv1, Vec2 uv2, BufferedImage tex, int w, int h) {
        Point p0 = project(v0, w, h);
        Point p1 = project(v1, w, h);
        Point p2 = project(v2, w, h);

        int minX = Math.max(0, Math.min(p0.x, Math.min(p1.x, p2.x)));
        int maxX = Math.min(w - 1, Math.max(p0.x, Math.max(p1.x, p2.x)));
        int minY = Math.max(0, Math.min(p0.y, Math.min(p1.y, p2.y)));
        int maxY = Math.min(h - 1, Math.max(p0.y, Math.max(p1.y, p2.y)));

        double area = (p1.y - p2.y) * (p0.x - p2.x) + (p2.x - p1.x) * (p0.y - p2.y);
        if (Math.abs(area) < 1e-5) return;

        int texW = tex.getWidth();
        int texH = tex.getHeight();

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                double w0 = ((p1.y - p2.y) * (x - p2.x) + (p2.x - p1.x) * (y - p2.y)) / area;
                double w1 = ((p2.y - p0.y) * (x - p2.x) + (p0.x - p2.x) * (y - p2.y)) / area;
                double w2 = 1.0 - w0 - w1;

                if (w0 >= 0 && w1 >= 0 && w2 >= 0) {
                    double z_inv = w0 / v0.y + w1 / v1.y + w2 / v2.y;
                    double z = 1.0 / z_inv;
                    int bufferIndex = y * w + x;

                    if (z < zBuffer[bufferIndex]) {
                        zBuffer[bufferIndex] = z;

                        double u = (w0 * uv0.u / v0.y + w1 * uv1.u / v1.y + w2 * uv2.u / v2.y) * z;
                        double v = (w0 * uv0.v / v0.y + w1 * uv1.v / v1.y + w2 * uv2.v / v2.y) * z;

                        int texX = (int) (u * texW) % texW;
                        int texY = (int) (v * texH) % texH;
                        if (texX < 0) texX += texW;
                        if (texY < 0) texY += texH;

                        framebufferPixels[bufferIndex] = tex.getRGB(texX, texY);
                    }
                }
            }
        }
    }


    private void drawLine3D(Graphics2D g2, Vec3 v1, Vec3 v2, Color color, int w, int h) {
        Vec3 v1Cam = transformPoint(v1); Vec3 v2Cam = transformPoint(v2);
        if (v1Cam.y < 0.1 && v2Cam.y < 0.1) return;
        Point p1 = project(v1Cam, w, h); Point p2 = project(v2Cam, w, h);
        g2.setColor(color);
        g2.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));
    }


    // --- User Interaction ---
    private void handleMouseClick(Point clickPoint) {
        if (isWireframe) return;
        List<RenderableFace> faces = getVisibleFaces(getWidth(), getHeight());
        Collections.sort(faces, (a, b) -> Double.compare(b.avgDepth, a.avgDepth) * -1);

        for (RenderableFace face : faces) {
            if (face.polygon.contains(clickPoint)) {
                if (uvEditor != null) {
                    uvEditor.setSelectedFace(face.index);
                } else {
                    JOptionPane.showMessageDialog(this, "Please open the UV Editor first to assign a texture.", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
                return;
            }
        }
    }

    public void setFaceTexture(int faceIndex, BufferedImage texture) {
        faceTextures.put(faceIndex, texture);
        repaint();
    }


    // --- Helper and Data Methods ---
    private List<RenderableFace> getVisibleFaces(int w, int h) {
        List<RenderableFace> facesToRender = new ArrayList<>();
        Vec3[] worldVertices = getCubeWorldVertices();
        for (int i = 0; i < CUBE_FACES.length; i++) {
            int[] faceIndices = CUBE_FACES[i];
            Vec3[] faceCamVerts = new Vec3[4];
            double totalDepth = 0;
            for (int j = 0; j < 4; j++) {
                faceCamVerts[j] = transformPoint(worldVertices[faceIndices[j]]);
                totalDepth += faceCamVerts[j].y;
            }
            Vec3 vA = new Vec3(faceCamVerts[1].x - faceCamVerts[0].x, faceCamVerts[1].y - faceCamVerts[0].y, faceCamVerts[1].z - faceCamVerts[0].z);
            Vec3 vB = new Vec3(faceCamVerts[2].x - faceCamVerts[0].x, faceCamVerts[2].y - faceCamVerts[0].y, faceCamVerts[2].z - faceCamVerts[0].z);
            Vec3 normal = new Vec3(vA.y*vB.z - vA.z*vB.y, vA.z*vB.x - vA.x*vB.z, vA.x*vB.y - vA.y*vB.x);
            if (normal.x*faceCamVerts[0].x + normal.y*faceCamVerts[0].y + normal.z*faceCamVerts[0].z >= 0) continue;
            Polygon p = new Polygon();
            for (Vec3 v : faceCamVerts) { Point pt = project(v, w, h); p.addPoint(pt.x, pt.y); }
            facesToRender.add(new RenderableFace(i, totalDepth / 4.0, p, faceCamVerts));
        }
        return facesToRender;
    }
    private Vec3[] getCubeWorldVertices() {
        Vec3[] vertices = new Vec3[8];
        for (int i=0; i<8; i++) {
            Vec3 v = CUBE_VERTICES[i];
            v = new Vec3(v.x, v.y, v.z + 1);
            v = rotatePoint(v, cubeRot);
            v = new Vec3(v.x + cubePos.x, v.y + cubePos.y, v.z + cubePos.z);
            vertices[i] = v;
        }
        return vertices;
    }

    private static final Vec3[] CUBE_VERTICES = { new Vec3(-1,-1,-1), new Vec3(1,-1,-1), new Vec3(1,1,-1), new Vec3(-1,1,-1), new Vec3(-1,-1, 1), new Vec3(1,-1, 1), new Vec3(1,1, 1), new Vec3(-1,1, 1) };
    private static final int[][] CUBE_EDGES = { {0,1},{1,2},{2,3},{3,0}, {4,5},{5,6},{6,7},{7,4}, {0,4},{1,5},{2,6},{3,7} };
    private static final int[][] CUBE_FACES = { {0, 3, 2, 1}, {4, 5, 6, 7}, {0, 1, 5, 4}, {2, 3, 7, 6}, {1, 2, 6, 5}, {3, 0, 4, 7} }; // Corrected winding order

    // --- Controls Window ---
    public void showControls() {
        JFrame controlFrame = new JFrame("Controls");
        controlFrame.setLayout(new BorderLayout());
        JPanel sliders = new JPanel(new GridLayout(6, 1));
        String[] labels = {"X Pos", "Depth (Y)", "Height (Z)", "Pitch (X)", "Roll (Y)", "Yaw (Z)"};
        JSlider[] sliderArr = new JSlider[6];
        for (int i = 0; i < 6; i++) {
            JPanel row = new JPanel(new BorderLayout());
            row.add(new JLabel(labels[i]), BorderLayout.WEST); sliderArr[i] = new JSlider(-100, 100, 0);
            row.add(sliderArr[i], BorderLayout.CENTER); sliders.add(row);
        }
        JPanel topPanel = new JPanel();
        JComboBox<String> targetBox = new JComboBox<>(new String[]{"Cube", "Camera"});
        JCheckBox orthoCheck = new JCheckBox("Orthographic"); JCheckBox wireframeCheck = new JCheckBox("Wireframe", isWireframe);
        topPanel.add(new JLabel("Control:")); topPanel.add(targetBox); topPanel.add(orthoCheck); topPanel.add(wireframeCheck);
        JButton uvButton = new JButton("Open UV Editor");
        topPanel.add(uvButton);
        JPanel fovPanel = new JPanel(new BorderLayout());
        JSlider fovSlider = new JSlider(30, 140, (int)fov);
        fovPanel.add(new JLabel("Field of View"), BorderLayout.WEST); fovPanel.add(fovSlider, BorderLayout.CENTER); fovPanel.setVisible(false);
        targetBox.addActionListener(e -> { controlTarget = (String) targetBox.getSelectedItem(); fovPanel.setVisible("Camera".equals(controlTarget) && !isOrthographic); });
        orthoCheck.addActionListener(e -> { isOrthographic = orthoCheck.isSelected(); fovPanel.setVisible(!isOrthographic && "Camera".equals(controlTarget)); repaint(); });
        wireframeCheck.addActionListener(e -> { isWireframe = wireframeCheck.isSelected(); repaint(); });
        uvButton.addActionListener(e -> { if (uvEditor == null || !uvEditor.isShowing()) { uvEditor = new UVEditorFrame(this); } uvEditor.setVisible(true); });
        fovSlider.addChangeListener(e -> { fov = fovSlider.getValue(); repaint(); });
        for (int i = 0; i < 6; i++) {
            final int idx = i;
            sliderArr[i].addChangeListener(e -> {
                double pVal = sliderArr[idx].getValue()/10.0, rVal = sliderArr[idx].getValue()*1.8;
                if ("Camera".equals(controlTarget)) { if (idx == 0) camPos.x = pVal; if (idx == 1) camPos.y = pVal-8; if (idx == 2) camPos.z = pVal+2; if (idx == 3) camRot.x=rVal; if (idx == 4) camRot.y=rVal; if (idx == 5) camRot.z=rVal; }
                else { if (idx == 0) cubePos.x = pVal; if (idx == 1) cubePos.y = pVal; if (idx == 2) cubePos.z = pVal; if (idx == 3) cubeRot.x=rVal; if (idx == 4) cubeRot.y=rVal; if (idx == 5) cubeRot.z=rVal; }
                repaint();
            });
        }
        JPanel mainControls = new JPanel(new BorderLayout());
        mainControls.add(sliders, BorderLayout.CENTER); mainControls.add(fovPanel, BorderLayout.SOUTH);
        controlFrame.add(topPanel, BorderLayout.NORTH); controlFrame.add(mainControls, BorderLayout.CENTER);
        controlFrame.pack(); controlFrame.setMinimumSize(controlFrame.getSize());
        controlFrame.setVisible(true); controlFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> {
        JFrame f = new JFrame("Simple 3D Engine");
        Simple3DEngine panel = new Simple3DEngine();
        f.add(panel);
        f.setSize(800, 800);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
        panel.showControls();
    });}
}