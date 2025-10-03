package com.boneless.engines;

import javax.swing.*;

public class Uno3DEngine extends JPanel {

    static class Vec3 {
        double x, y, z;
        Vec3(double x, double y, double z) {this.x = x; this.y = y; this.z = z; }
    }

    private Vec3 camPos = new Vec3(0, 0, 0);
    private Vec3 camRot = new Vec3(0, 0, 0);

    public Uno3DEngine() {

    }
}
